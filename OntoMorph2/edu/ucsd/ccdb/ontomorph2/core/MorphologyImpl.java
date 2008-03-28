package edu.ucsd.ccdb.ontomorph2.core;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

import org.w3c.dom.Document;

import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;
import edu.ucsd.ccdb.ontomorph2.view.IStructure3D;
import edu.ucsd.ccdb.ontomorph2.view.Structure3DImpl;

public class MorphologyImpl implements IMorphology  {

	URL _morphLoc = null;
	IPosition _position = null;
	IRotation _rotation = null;
	float _scale = 1F;
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation) {
		_morphLoc = morphLoc;
		_position = position;
		_rotation = rotation;
	}

	public URL getMorphML() {
		return _morphLoc;
	}

	public IRotation getRotation() {
		return _rotation;
	}

	public IPosition getPosition() {
		return _position;
	}
	
	public float getScale() {
		return _scale;
	}

	public void setPosition(IPosition pos) {
		_position = pos;
	}
	
	public void setRotation(IRotation rot) {
		_rotation = rot;
	}
	
	public void setScale(float f) {
		_scale = f;
	}

}
