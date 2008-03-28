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
import java.util.ArrayList;

import org.w3c.dom.Document;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.LightState;

import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;
import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;

public class Structure3DImpl extends Node implements IStructure3D {
	
	
	public Structure3DImpl(IMorphology morph) {
		InputStream input = XSLTransformManager.getInstance().convertMorphMLToX3D(morph.getMorphML());

		this.setX3DNeuron(input, morph.getPosition(), morph.getRotation(), morph.getScale());
	}


	public Node getNode() {
		return this;
	}
	
	public void setX3DNeuron(InputStream input, IPosition _position, IRotation _rotation, float _scale) {
		try {
			X3DLoader converter = new X3DLoader();
			Spatial scene = converter.loadScene(input, null, null);
			if (scene instanceof Node) {
				Node sceneNode = (Node) scene;
				sceneNode.setLightCombineMode(LightState.INHERIT);
				Line l = (Line)sceneNode.getChild("X3D_LineSet");
				Cylinder c = new Cylinder("cyl", 10,10 , 3.0f, 3.0f);
				c.setLocalTranslation(l.getLocalTranslation());
				c.setLocalRotation(l.getLocalRotation());
				c.setLightCombineMode(LightState.INHERIT);
				c.setRandomColors();
				sceneNode.attachChild(c);
				
				/*
				for (Spatial s : sceneNode.getChildren()) {
					
					if (s instanceof Line) {
						Line l = (Line) s;
						l.setDefaultColor(ColorRGBA.yellow);
						
						l.setRandomColors();
					}
				}
				*/
			}
		
			this.detachAllChildren();
			this.attachChild(scene);
			
			if (_position != null) {
				this.setLocalTranslation(_position.asVector3f());
			}
			if (_rotation != null) {
				this.setLocalRotation(_rotation.asQuaternion());
			}
			if (_scale != 1) {
				this.setLocalScale(_scale);
			}
		} catch (Exception e) {
			//logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
	}
	
	public void setX3DNeuron(URL structureLoc) {
		try {
			this.setX3DNeuron(new FileInputStream(structureLoc.getFile()), null, null, 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
