package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.util.OMTDiscreteLodNode;

public class SegmentViewImpl implements ISegmentView {

	private ISegment seg = null;
	private ISegmentGroup sg = null;
	//list of lists of geometries.  
	private boolean highlighted = false;
	private OMTDiscreteLodNode node = null;
	
	public SegmentViewImpl(ISegment seg) {
		assert seg != null;
		this.seg = seg;
	}
	
	public SegmentViewImpl(ISegmentGroup sg) {
		assert sg != null;
		this.sg = sg;
	}
	
	public boolean correspondsToSegment() {
		return (this.seg != null);
	}

	public boolean correspondsToSegmentGroup() {
		return (this.sg != null);
	}

	public ISegment getCorrespondingSegment() {
		return this.seg;
	}

	public ISegmentGroup getCorrespondingSegmentGroup() {
		return this.sg;
	}
	
	private float getBaseRadius() {
		float proximalRadius = 0;
		if (correspondsToSegment()) {
			proximalRadius = seg.getProximalRadius();
		} else if (correspondsToSegmentGroup()){
			ISegment firstSeg = sg.getSegments().get(0);
			
			proximalRadius = firstSeg.getProximalRadius();
		}
		return proximalRadius;
	}
	
	private float getApexRadius() {
		float distalRadius = 0;
		if (correspondsToSegment()) {
	    	distalRadius = seg.getDistalRadius();
		} else if (correspondsToSegmentGroup()){
			ISegment lastSeg = sg.getSegments().get(sg.getSegments().size() - 1);
			distalRadius = lastSeg.getDistalRadius();
		}
		return distalRadius;
	}

	public Vector3f getBase() {
		Vector3f base = new Vector3f();
		if (correspondsToSegment()) {
	    	base.x = seg.getProximalPoint()[0];
	    	base.y = seg.getProximalPoint()[1];
	    	base.z = seg.getProximalPoint()[2];
		} else if (correspondsToSegmentGroup()){
			ISegment firstSeg = sg.getSegments().get(0);
			float[] proximalPoint = firstSeg.getProximalPoint();
			base.x = proximalPoint[0];
			base.y = proximalPoint[1];
			base.z = proximalPoint[2];
		}
		return base;
	}
	
	public Vector3f getApex() {
		Vector3f apex = new Vector3f();
		if (correspondsToSegment()) {
	    	apex.x = seg.getDistalPoint()[0];
	    	apex.y = seg.getDistalPoint()[1];
	    	apex.z = seg.getDistalPoint()[2];
		} else if (correspondsToSegmentGroup()){
			ISegment lastSeg = sg.getSegments().get(sg.getSegments().size() - 1);
			float[] distalPoint = lastSeg.getDistalPoint();
			apex.x = distalPoint[0];
			apex.y = distalPoint[1];
			apex.z = distalPoint[2];
		}
		return apex;
	}
	
	// render this SegmentViewImpl as a Line
	private Line getLine() {
		
		Vector3f base = getBase();
    	Vector3f apex = getApex();
		
		float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
		
		//Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, colorBuffer, null);
		Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, null, null);
		setCurrentGeometry(l);
		return l;
	}
	
	/**
	 * @return True if this segment finds itself inside some IVolume
	 */
	public boolean insideVolume() {
		for (VolumeViewImpl vol : ViewImpl.getInstance().getView3D().getVolumes()) {
			for (Geometry g : this.getCurrentGeometries()) {
				if (vol.getVolume().containsObject(g)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void setToDefaultColor(Geometry g) {
		if (insideVolume()) {
			g.setSolidColor(ColorRGBA.red);
		} else if (correspondsToSegment()) {
			g.setSolidColor(ColorUtil.convertColorToColorRGBA(getCorrespondingSegment().getColor()));
		} else if (correspondsToSegmentGroup()) {
			g.setSolidColor(ColorUtil.convertColorToColorRGBA(getCorrespondingSegmentGroup().getColor()));
		}
	}
	
	//Render this SegmentViewImpl as a Cylinder
	private Cylinder getCylinder() {
		Vector3f base = getBase();
    	Vector3f apex = getApex();
    	
//		calculate new center
    	float xCenter = (float)((apex.x - base.x)/2 + base.x);
    	float yCenter = (float)((apex.y - base.y)/2 + base.y);
    	float zCenter = (float)((apex.z - base.z)/2 + base.z);
    	
    	Vector3f center = new Vector3f(xCenter, yCenter, zCenter);
    	
    	Vector3f unit = new Vector3f();
    	unit = apex.subtract(base); // unit = apex - base;
    	float height = unit.length();
    	unit = unit.normalize();
    	
		Cylinder cyl = new Cylinder("neuron_cyl", 2, 4, 0.5f, height);
		cyl.setRadius1(getBaseRadius());
		cyl.setRadius2(getApexRadius());
		//cyl.setColorBuffer(2, colorBuffer);
		
		AlphaState as = ViewImpl.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	      as.setDstFunction(AlphaState.DB_ONE);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    cyl.setRenderState(as);
	    cyl.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
				
		Quaternion q = new Quaternion();
		q.lookAt(unit, Vector3f.UNIT_Y);
		
		cyl.setLocalRotation(q);
		
		cyl.setLocalTranslation(center);
		
		setCurrentGeometry(cyl);
		return cyl;
	}
	
	//render this SegmentViewImpl as a ClodMeshCylinder
	private AreaClodMesh getClodMeshCylinder() {
		AreaClodMesh out =  getClodMeshFromGeometry(getCylinder());
		setCurrentGeometry(out);
		return out;
	}
	
	//this doesn't work right 
	private Node getCurveFromSegGroup() {
		Node n = new Node();
		ArrayList<Vector3f> l = new ArrayList<Vector3f>();
		if (correspondsToSegmentGroup()) {
			for (ISegment seg : this.getCorrespondingSegmentGroup().getSegments()) {
				SegmentViewImpl s = new SegmentViewImpl(seg);
				if (l.size() == 0) {
					l.add(s.getBase());
				} 
				l.add(s.getApex());
			}
		}
		Vector3f[] array = new Vector3f[l.size()];
		array = l.toArray(array);
		CurveImpl c = new CurveImpl("name", array);
		n.attachChild(c);
		setCurrentGeometry(c);
		return n;
	}
	
	//Render this SegmentViewImpl as a series of cylinders corresponding to the underlying
	//individual segments of this segment group
	private List<Geometry> getCylindersFromSegGroup() {
		List<Geometry> l = new ArrayList<Geometry>();
		
		if (correspondsToSegmentGroup()) {
			for (ISegment seg : this.getCorrespondingSegmentGroup().getSegments()) {
				SegmentViewImpl sv = new SegmentViewImpl(seg);
				Cylinder c = sv.getCylinder();
				l.add(c);
			}
		}
		
		return l;
	}
	
	private AreaClodMesh getClodMeshFromGeometry(Geometry cylinder) {
		AreaClodMesh acm = new AreaClodMesh(cylinder.getName(),
                (TriMesh) cylinder, null);
        acm.setLocalTranslation(cylinder.getLocalTranslation());
        acm.setLocalRotation(cylinder.getLocalRotation());
        acm.setModelBound(new BoundingSphere());
        acm.updateModelBound();
        // Allow 1/2 of a triangle in every pixel on the screen in
        // the bounds.
        acm.setTrisPerPixel(.5f);
        // Force a move of 2 units before updating the mesh geometry
        acm.setDistanceTolerance(2);
        // Give the clodMe sh node the material state that the
        // original had.
        //acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]); //Note: Deprecated
        acm.setRenderState(cylinder.getRenderState(RenderState.RS_MATERIAL));
        // Attach clod node.
        return acm;
	}
	
	private List<Geometry> getCurrentGeometries() {
		if (node != null) {
			return this.node.getActiveGeometries();
		} else {
			return new ArrayList<Geometry>();
		}
	}
	
	private void setCurrentGeometry(Geometry g) {
		g.setModelBound(new BoundingBox());
		g.updateModelBound();
		this.chooseColor(g);
	}
	
	private void setCurrentGeometries(List<Geometry> l) {
		for (Geometry g : l) {
			setCurrentGeometry(g);
		}
	}
	
	public void highlight() {
		highlighted = true;
		refreshColor();
	}
	
	private void chooseColor(Geometry g) {
		if (highlighted) {
			g.setSolidColor(ColorRGBA.yellow);
		} else {
			this.setToDefaultColor(g);	
		}
	}
	
	private void refreshColor() {
		for (Geometry g : this.node.getAllGeometries()) {
			chooseColor(g);
		}
	}

	public void unhighlight() {
		highlighted = false;
		refreshColor();
	}

	public boolean containsCurrentGeometry(Geometry g) {
		for (Geometry ge: this.getCurrentGeometries()) {
			if (ge == g) { 
				return true;
			}
		}
		return false;
	}

	public boolean isHighlighted() {
		return this.highlighted;
	}

	
	public Node getViewNode(String renderOption) {
		if (this.node == null) {
			this.node = new OMTDiscreteLodNode(new DistanceSwitchModel(10));
			
			if (renderOption.equals(INeuronMorphology.RENDER_AS_LINES)) {
				this.node.attachChild(this.getLine());
			} else if (renderOption.equals(INeuronMorphology.RENDER_AS_CYLINDERS)) {
				//node.attachChild(((SegmentViewImpl)seg).getClodMeshCylinder());
				this.node.attachChild(this.getCylinder());
			} else if (renderOption.equals(INeuronMorphology.RENDER_AS_LOD)) {
				
				this.node.addDiscreteLodNodeChild(this.getCylinder(), 0, 1000);
				this.node.addDiscreteLodNodeChild(this.getLine(), 1000, 10000);
				
			} else if (renderOption.equals(INeuronMorphology.RENDER_AS_LOD_2)){
				
				this.node.addDiscreteLodNodeChild(this.getCylindersFromSegGroup(), 0, 800);
				this.node.addDiscreteLodNodeChild(this.getLine(), 800, 10000);
				
			}
		}
		return this.node;
	}

}
