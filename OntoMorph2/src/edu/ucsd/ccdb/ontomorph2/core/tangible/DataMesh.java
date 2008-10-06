package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

/**
 * Defines a polygonal mesh object that represents a 3D object segmented from 
 * microscopy data. Also is made aware of semantics objects.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class DataMesh extends ContainerTangible{
	
	URL maxFile = null;
	URL objFile = null;
	Object data = null;
	
	public DataMesh(URL url) throws IOException {
		super(url.toString());
		setObjMeshURL(url);
		if (getMaxMeshURL() != null) {
			loadMaxFile(getMaxMeshURL());
		} else if (getObjMeshURL() != null) {
			loadObjFile(getObjMeshURL());
		}
	}
	
	public Object getData() {
		return data;
	}
	

	private void loadFile(FormatConverter converter, URL model) throws IOException{
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		// This will read the .jme format and convert it into a scene graph
		BinaryImporter jbr = new BinaryImporter();
		
		// Use the format converter to convert .obj to .jme
		converter.convert(model.openStream(), BO);
		// Load the binary .jme format into a scene graph
		
		data = jbr.load(new ByteArrayInputStream(BO.toByteArray()));
	}
	
	private void loadMaxFile(URL model) throws IOException{
		FormatConverter converter = new MaxToJme();
		loadFile(converter, model);
	}
	
	private void loadObjFile(URL model) throws IOException{
		// Create something to convert .obj format to .jme
		FormatConverter converter = new ObjToJme();
		loadFile(converter, model);
	}
	
	public void setMaxMeshURL(URL maxFilePath) {
		this.maxFile = maxFilePath;
		this.changed();
	}
	
	public void setObjMeshURL(URL objFilePath) {
		objFile = objFilePath;
		this.changed();
	}
	
	public URL getMaxMeshURL() {
		return maxFile;
	}
	
	public URL getObjMeshURL() {
		return objFile;
	}
	

}
