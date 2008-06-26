package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

import edu.ucsd.ccdb.ontomorph2.core.scene.objects.DataMesh;

/**
 * Visualizes a mesh.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MeshViewImpl {


	//OMTDiscreteLodNode object = null;
	Node object = null;
	DataMesh mesh = null;
	public MeshViewImpl(DataMesh mesh) {
		this.mesh = mesh;
		if (mesh.getMaxMeshURL() != null) {
			loadMaxFile(mesh.getMaxMeshURL());
		} else if (mesh.getObjMeshURL() != null) {
			loadObjFile(mesh.getObjMeshURL());
		}
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
				object = n;
			}
			/*
			object.updateModelBound();
			Vector3f translateToOrigin = new Vector3f();
			if (object.getWorldBound() != null) {
				 translateToOrigin = object.getWorldBound().getCenter().subtract(new Vector3f(0,0,0));
				object.setLocalTranslation(translateToOrigin);
			}*/
			if (mesh.getAbsolutePosition() != null)
				object.setLocalTranslation(mesh.getAbsolutePosition().asVector3f());
			if (mesh.getAbsoluteRotation() != null)
				object.setLocalRotation(mesh.getAbsoluteRotation().asMatrix3f());
			object.setLocalScale(mesh.getRelativeScale());
			
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
}
