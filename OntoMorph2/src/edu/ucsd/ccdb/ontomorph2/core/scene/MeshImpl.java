package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

public class MeshImpl extends SceneObjectImpl implements IMesh, ISemanticsAware, ISelectable{
	
	URL maxFile = null;
	URL objFile = null;
	
	public MeshImpl() {
	}
	
	public void loadMaxFile(URL maxFilePath) {
		this.maxFile = maxFilePath;
	}
	
	public void loadObjFile(URL objFilePath) {
		objFile = objFilePath;
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
