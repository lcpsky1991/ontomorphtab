package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

public class MeshImpl extends SceneObjectImpl implements IMesh, ISemanticsAware, ISelectable{
	
	URL maxFile = null;
	URL objFile = null;
	
	public MeshImpl() {
	}
	
	public void loadMaxFile(String maxFilePath) {
		this.maxFile =
			MeshImpl.class.getClassLoader().
			getResource(maxFilePath);
	}
	
	public void loadObjFile(String objFilePath) {
		objFile =
			MeshImpl.class.getClassLoader().
			getResource(objFilePath);
	}
	
	public URL getMaxMeshURL() {
		return maxFile;
	}
	
	public URL getObjMeshURL() {
		return objFile;
	}
	
	
	public void select() {
		// TODO Auto-generated method stub
		
	}

	public void unselect() {
		// TODO Auto-generated method stub
		
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<ISemanticThing> getSemanticThings() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ISemanticThing> getAllSemanticThings() {
		return getSemanticThings();
	}
	
	public void addSemanticThing(ISemanticThing thing) {
		// TODO Auto-generated method stub
		
	}

	public void removeSemanticThing(ISemanticThing thing) {
		// TODO Auto-generated method stub
		
	}

}
