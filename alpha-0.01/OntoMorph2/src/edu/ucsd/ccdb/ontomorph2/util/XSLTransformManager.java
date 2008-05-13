package edu.ucsd.ccdb.ontomorph2.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Represents a singleton.
 */

public class XSLTransformManager {
	
	/**
	 * Holds singleton instance
	 */
	private static XSLTransformManager instance;
	URL _morphmlToX3dXSL = null;

	public InputStream convertMorphMLToX3D(URL morphMLDocument) {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = tFactory.newTransformer(new StreamSource(_morphmlToX3dXSL.getFile()));

//			 Perform the transformation from a StreamSource to a StreamResult;
			try {
				transformer.transform(new StreamSource(morphMLDocument.getFile()), new StreamResult(new FileOutputStream("temp.xml")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
	    } catch (TransformerException e) {
			e.printStackTrace();
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream("temp.xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}
		
	public void init() {
		String key = "javax.xml.transform.TransformerFactory";
		String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);
		
		_morphmlToX3dXSL = XSLTransformManager.class.getClassLoader().getResource("etc/NeuroML_Level3_v1.7.1_X3D.xsl");
	}

	/**
	 * prevents instantiation
	 */
	private XSLTransformManager() {
		// prevent creation
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public XSLTransformManager getInstance() {
		if (instance == null) {
			instance = new XSLTransformManager();
			instance.init();
		}
		return instance;
	}
}
