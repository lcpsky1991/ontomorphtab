package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

public interface IMesh extends ISceneObject{

	public URL getMaxMeshURL();
	
	public URL getObjMeshURL();
	
	public void loadMaxFile(String maxFilePath);
	
	public void loadObjFile(String objFilePath);
}