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
	
	/**
	 * compute a very small vector that is 
	 * approximately tangent to the point of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getTangent(float time) {
		Vector3f p1 = getPoint(time-0.01f);
		Vector3f p2 = getPoint(time+0.01f);
		return p2.subtract(p1).normalize();
	}
	
	/**
	 * computes a very small vector that is approximately normal 
	 * to the poit of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getNormal(float time) {
		
		Vector3f px = getPoint(time-0.01f);
		Vector3f py = getPoint(time+0.01f);
		
		Vector3f pe = getPoint(time);
		Vector3f pf = new Vector3f((py.x - px.x)/2+px.x, (py.y - px.y)/2+px.y, (py.z - px.z)/2+px.z);

		return pf.subtract(pe).normalize();
	}

}
