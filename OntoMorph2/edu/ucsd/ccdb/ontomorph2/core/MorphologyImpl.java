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

	//render options.  don't forget to update the test in the constructor when adding to this
	public final static String RENDER_AS_LINES = "lines";
	public final static String RENDER_AS_CYLINDERS = "cylinders";
	
	URL _morphLoc = null;
	IPosition _position = null;
	IRotation _rotation = null;
	float _scale = 1F;
	String _renderOption = RENDER_AS_LINES; //default render option
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation) {
		_morphLoc = morphLoc;
		_position = position;
		_rotation = rotation;
	}
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation, String renderOption) {
		this(morphLoc, position, rotation);
		if (RENDER_AS_LINES.equals(renderOption) || RENDER_AS_CYLINDERS.equals(renderOption)) {
			_renderOption = renderOption;
		}
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
	
	public String getRenderOption() {
		return _renderOption;
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
