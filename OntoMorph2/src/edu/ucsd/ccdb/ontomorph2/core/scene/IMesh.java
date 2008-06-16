package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

/**
 * Defines a polygonal mesh object that represents a 3D object segmented from 
 * microscopy data.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface IMesh extends ISceneObject{

	public URL getMaxMeshURL();
	
	public URL getObjMeshURL();
	
	public void setMaxMeshURL(URL maxFilePath);
	
	public void setObjMeshURL(URL objFilePath);
}
