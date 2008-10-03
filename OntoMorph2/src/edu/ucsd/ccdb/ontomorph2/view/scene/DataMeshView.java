package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.RenderState;

import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;

/**
 * Visualizes a mesh.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataMeshView extends TangibleView{

	Node standinObject = null;
	
	public DataMeshView(DataMesh mesh) {
		super(mesh);
		super.setName("DataMeshView for " + this.getModel().getName());
		
		this.pickPriority = TangibleView.P_HIGHEST;
	}
	
	public void init() {
	
		standinObject = new Node();
		DataMesh dataMesh = (DataMesh)getModel();
		Object o = dataMesh.getData();
		
		if (o instanceof TriMesh) {
			TriMesh mesh = (TriMesh)o;
			mesh.setSolidColor(ColorRGBA.orange);
			
			this.registerGeometry(mesh);
			mesh.setModelBound(new BoundingBox());
			mesh.updateModelBound();
			standinObject.attachChild(mesh);
			
		} else if (o instanceof Node) {
			Node n = (Node)o;
			
			for(Spatial s : n.getChildren()) {
				if (s instanceof Geometry) {
					this.registerGeometry((Geometry)s);
				}
			}
			n.setModelBound(new BoundingBox());
			n.updateModelBound();
			standinObject = n;
		}
		this.attachChild(standinObject);
		this.update();
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

	@Override
	public void doHighlight() {
		for (Spatial s : standinObject.getChildren()) {
			if (s instanceof Geometry) {
				((Geometry)s).setSolidColor(ColorRGBA.yellow);
			}
		}
	}

	@Override
	public void doUnhighlight() {
		for (Spatial s : standinObject.getChildren()) {
			if (s instanceof Geometry) {
				((Geometry)s).setSolidColor(ColorRGBA.orange);
			}
		}
	}
}
