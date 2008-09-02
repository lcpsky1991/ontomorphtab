package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.RenderState;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.DataMesh;

/**
 * Visualizes a mesh.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MeshViewImpl extends TangibleView{


	//OMTDiscreteLodNode object = null;
	Node object = null;
	
	public MeshViewImpl(DataMesh mesh) {
		super(mesh);
		if (mesh.getMaxMeshURL() != null) {
			loadMaxFile(mesh.getMaxMeshURL());
		} else if (mesh.getObjMeshURL() != null) {
			loadObjFile(mesh.getObjMeshURL());
		}
		
		this.pickPriority = TangibleView.P_HIGHEST;
	}
	
	private void loadFile(FormatConverter converter, URL model) {
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		// This will read the .jme format and convert it into a scene graph
		BinaryImporter jbr = new BinaryImporter();
		
		try {
			// Use the format converter to convert .obj to .jme
			converter.convert(model.openStream(), BO);
			// Load the binary .jme format into a scene graph
			
			//object = new OMTDiscreteLodNode(new DistanceSwitchModel(10));
			object = new Node();
			Object o = jbr.load(new ByteArrayInputStream(BO.toByteArray()));
			if (o instanceof TriMesh) {
				TriMesh mesh = (TriMesh)o;
				mesh.setSolidColor(ColorRGBA.orange);
				
				//object.addDiscreteLodNodeChild(mesh, 0, 1000);
				
				
				//this works but adds a significant time overhead to loading the program
				//object.attachChild(this.getClodMeshFromGeometry(mesh));
				
				this.registerGeometry(mesh);
				mesh.setModelBound(new BoundingBox());
				mesh.updateModelBound();
				object.attachChild(mesh);
			} else if (o instanceof Node) {

				Node n = (Node)o;
				/*
				List<Geometry> g = new ArrayList<Geometry>();
				for (Spatial s : n.getChildren()) {
					if (s instanceof Geometry) {
						g.add((Geometry)s);
						((Geometry)s).setSolidColor(ColorRGBA.orange);
					}
				}
				object.addDiscreteLodNodeChild(n, g, 0, 1000);
				*/
				//object.attachChild(n);
				for(Spatial s : n.getChildren()) {
					if (s instanceof Geometry) {
						this.registerGeometry((Geometry)s);
					}
				}
				n.setModelBound(new BoundingBox());
				n.updateModelBound();
				object = n;
			}

			if (getModel().getAbsolutePosition() != null)
				object.setLocalTranslation(getModel().getAbsolutePosition().asVector3f());
			if (getModel().getAbsoluteRotation() != null)
				object.setLocalRotation(getModel().getAbsoluteRotation().asMatrix3f());
			object.setLocalScale(getModel().getRelativeScale());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadMaxFile(URL model) {
		FormatConverter converter = new MaxToJme();
		loadFile(converter, model);
	}
	
	private void loadObjFile(URL model) {
		// Create something to convert .obj format to .jme
		FormatConverter converter = new ObjToJme();
		loadFile(converter, model);
	}

	public Node getNode() {
		return object;
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
		for (Spatial s : object.getChildren()) {
			if (s instanceof Geometry) {
				((Geometry)s).setSolidColor(ColorRGBA.yellow);
			}
		}
	}

	@Override
	public void doUnhighlight() {
		for (Spatial s : object.getChildren()) {
			if (s instanceof Geometry) {
				((Geometry)s).setSolidColor(ColorRGBA.orange);
			}
		}
	}
}
