package edu.ucsd.ccdb.ontomorph2.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.URL;

import org.w3c.dom.Document;

import com.jme.scene.Node;
import com.jme.scene.Spatial;

import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;
import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;

public class Structure3DImpl extends Node implements IStructure3D {
	
	
	public Structure3DImpl(IMorphology morph) {
		InputStream input = XSLTransformManager.getInstance().convertMorphMLToX3D(morph.getMorphML());

		this.setX3DNeuron(input, morph.getPosition(), morph.getRotation());
	}


	public Node getNode() {
		return this;
	}
	
	public void setX3DNeuron(InputStream input, IPosition _position, IRotation _rotation) {
		try {
			X3DLoader converter = new X3DLoader();
			Spatial scene = converter.loadScene(input, null, null);
		
			this.detachAllChildren();
			this.attachChild(scene);
			
			if (_position != null) {
				this.setLocalTranslation(_position.asVector3f());
			}
			if (_rotation != null) {
				this.setLocalRotation(_rotation.asQuaternion());
			}
		} catch (Exception e) {
			//logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
	}
	
	public void setX3DNeuron(URL structureLoc) {
		try {
			this.setX3DNeuron(new FileInputStream(structureLoc.getFile()), null, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
