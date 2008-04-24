package edu.ucsd.ccdb.ontomorph2.core.spatial;

import java.awt.Color;

import com.jme.curve.BezierCurve;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

public class CurveImpl extends BezierCurve implements ICurve{

	ColorRGBA color = null;
	
	public CurveImpl(String arg0, Vector3f[] arg1) {
		super(arg0, arg1);
	}

	/**
	 * Set the color of the curve.
	 */
	public void setColor(Color color) {
		this.setSolidColor(ColorUtil.convertColorToColorRGBA(color));
	}

}
