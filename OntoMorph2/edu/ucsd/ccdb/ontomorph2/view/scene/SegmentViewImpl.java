package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

public class SegmentViewImpl implements ISegmentView {

	private ISegment seg = null;
	private ISegmentGroup sg = null;
	private Geometry currentGeometry = null;
	
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

	private Vector3f getBase() {
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
	
	private Vector3f getApex() {
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
	
	public Line getLine() {
		
		Vector3f base = getBase();
    	Vector3f apex = getApex();
		
		float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
		
		//Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, colorBuffer, null);
		Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, null, null);
		this.currentGeometry = l;
		this.setToDefaultColor();
		return l;
	}
	
	public void setToDefaultColor() {
		if (correspondsToSegment()) {
		this.currentGeometry.setSolidColor(ColorUtil.convertColorToColorRGBA(getCorrespondingSegment().getColor())); 
		} else if (correspondsToSegmentGroup()) {
			this.currentGeometry.setSolidColor(ColorUtil.convertColorToColorRGBA(getCorrespondingSegmentGroup().getColor()));
		}
	}
	
	public Cylinder getCylinder() {
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
		cyl.updateModelBound();
		
		Quaternion q = new Quaternion();
		q.lookAt(unit, Vector3f.UNIT_Y);
		
		cyl.setLocalRotation(q);
		
		cyl.setLocalTranslation(center);
		this.currentGeometry = cyl;
		this.setToDefaultColor();
		return cyl;
	}
	
	public AreaClodMesh getClodMeshCylinder() {
		AreaClodMesh out =  getClodMeshFromGeometry(getCylinder());
		this.currentGeometry = out;
		this.setToDefaultColor();
		return out;
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
	
	public Geometry getCurrentGeometry() {
		return this.currentGeometry;
	}

}
