package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

import edu.ucsd.ccdb.ontomorph2.core.scene.IMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.MeshImpl;
import edu.ucsd.ccdb.ontomorph2.util.OMTDiscreteLodNode;

public class MeshViewImpl {


	//OMTDiscreteLodNode object = null;
	Node object = null;
	IMesh mesh = null;
	public MeshViewImpl(IMesh mesh) {
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
			if (mesh.getPosition() != null)
				object.setLocalTranslation(mesh.getPosition().asVector3f());
			if (mesh.getRotation() != null)
				object.setLocalRotation(mesh.getRotation().asMatrix3f());
			object.setLocalScale(mesh.getScale());
			
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
