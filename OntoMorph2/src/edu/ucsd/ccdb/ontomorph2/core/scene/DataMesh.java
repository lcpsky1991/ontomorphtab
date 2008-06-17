package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines a polygonal mesh object that represents a 3D object segmented from 
 * microscopy data. Also is made aware of semantics objects.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISemanticsAware
 */
public class DataMesh extends SceneObjectImpl implements ISemanticsAware{
	
	URL maxFile = null;
	URL objFile = null;
	
	public DataMesh() {
	}
	
	
	public void setMaxMeshURL(URL maxFilePath) {
		this.maxFile = maxFilePath;
	}
	
	public void setObjMeshURL(URL objFilePath) {
		objFile = objFilePath;
	}
	
	public URL getMaxMeshURL() {
		return maxFile;
	}
	
	public URL getObjMeshURL() {
		return objFile;
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
