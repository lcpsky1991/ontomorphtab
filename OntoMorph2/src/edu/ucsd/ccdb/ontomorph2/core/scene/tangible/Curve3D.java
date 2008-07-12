package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;

import com.jme.curve.BezierCurve;
import com.jme.curve.Curve;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

/**
 * Defines a Bezier curve in the framework.
 * 
 *  
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class Curve3D extends Tangible{

	BezierCurve theCurve = null;
	BezierCurve absoluteCurve = null; // copy of the curve for coordinate systems
	ColorRGBA color = null;
	float delta = 0.1f;
	private Vector3f _modelBinormal = null;
	CoordinateSystem sys = null;
	OMTVector[] controlPoints = null;
	boolean seeAnchorPoints = true;
		
	
	public Curve3D(String arg0, OMTVector[] arg1) {
		theCurve = new BezierCurve(arg0, arg1);
		controlPoints = arg1;
	}

	public Curve3D(String string, OMTVector[] array, CoordinateSystem d) {
		this(string, array);
		setCoordinateSystem(d);
	}
	
	public void setCoordinateSystem(CoordinateSystem sys) {
		this.sys = sys;
		
	}
	
	public CoordinateSystem getCoordinateSystem() {
		return this.sys;
	}

	/**
	 * Set the color of the curve.
	 */
	public void setColor(Color color) {
		this.color = ColorUtil.convertColorToColorRGBA(color);
		theCurve.setSolidColor(this.color);
	}
	
	/**
	 * compute a very small vector that is 
	 * approximately tangent to the point of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getTangent(float time) {
		Vector3f p1 = theCurve.getPoint(getTimeMinusDelta(time));
		Vector3f p2 = theCurve.getPoint(getTimePlusDelta(time));
		return p2.subtract(p1).normalize();
	}
	
	/**
	 * computes a very small vector that is approximately normal 
	 * to the point of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getNormal(float time) {
		
		Vector3f px = theCurve.getPoint(getTimeMinusDelta(time));
		Vector3f py = theCurve.getPoint(getTimePlusDelta(time));
		
		Vector3f pe = theCurve.getPoint(time);
		Vector3f pf = new Vector3f((py.x - px.x)/2+px.x, (py.y - px.y)/2+px.y, (py.z - px.z)/2+px.z);

		return pf.subtract(pe).normalize();
	}
	
	private float getTimeMinusDelta(float time) {
		if (time - delta < 0) {
			return 0;
		}
		return time-delta;
	}
	
	private float getTimePlusDelta(float time) {
		if (time + delta >= 1) {
			return 0.999f;
		}
		return time+delta;
	}

	/**
	 * Give a copy of this Bezier Curve as a JME Curve class
	 * @return a copy of this Curve3D
	 * @see Curve
	 */
	public Curve asBezierCurve() {
		Curve copy = copyBezierCurve(this.controlPoints);
		return copy;
	}
	
	private BezierCurve copyBezierCurve(OMTVector[] controlPoints) {
		BezierCurve copy = new BezierCurve(theCurve.getName(), controlPoints);
		copy.setSolidColor(this.color);
		
		//apply coordinate system to this curve.
		if (this.getCoordinateSystem() != null) {
			this.getCoordinateSystem().applyToSpatial(copy);
		}
		return copy;
	}
	
	/**
	 * The model binormal sets a vector direction that is 
	 * defined as the standard for object that are rotated
	 * with respect to this curve.  Because the dot and cross 
	 * products are only defined within 0-180 degrees, this is 
	 * necessary to keep cells rotating around curves in the
	 * appropriate manner
	 * 
	 * @param binormal
	 */
	public void setModelBinormal(Vector3f binormal) {
		this._modelBinormal = binormal;
	}
	
	/**
	 * Sets the model binormal simply by defining the up vector and the time
	 * 
	 * @param up
	 * @param time
	 * @see setModelBinormal
	 */
	public void setModelBinormalWithUpVector(Vector3f up, float time) {
		this._modelBinormal = this.getTangent(time).cross(up).normalize();
	}
	
	/**
	 * returns the orientation of an object that is following this curve, 
	 * at a given time and precision, given an up vector
	 */
	public Matrix3f getOrientation(float time, float precision, Vector3f up) {
		if (up == null) {
			return theCurve.getOrientation(time, precision);
		}
		Matrix3f rotation = new Matrix3f();

		//calculate tangent
		Vector3f tangent = theCurve.getPoint(time).subtract(theCurve.getPoint(time + precision));
		tangent = tangent.normalize();

		//calculate binormal
		Vector3f binormal = tangent.cross(up);
		binormal = binormal.normalize();
		if (_modelBinormal != null) {
			if (_modelBinormal.angleBetween(binormal) < FastMath.PI / 2) {
				binormal.negateLocal();
			}
		}

		//calculate normal
		Vector3f normal = binormal.cross(tangent);
		normal = normal.normalize();

		rotation.setColumn(0, tangent);
		rotation.setColumn(1, normal);
		rotation.setColumn(2, binormal);

		return rotation;
	}

	/**
	 * Gives a 3D PositionVector that corresponds to the time parameter
	 * @param time - 0 to 1 representation of the length of the Curve from beginning to end
	 * @return a PositionVector on this Curve3D at time.
	 */
	public PositionVector getPoint(float time) {
		return new PositionVector(theCurve.getPoint(time));
	}

	/**
	 * Sets the visibility of the anchor points, which govern the shape of the curve.
	 * 
	 * @param visible - if true, make points visible, if false, make invisible
	 */
	public void setAnchorPointsVisibility(boolean visible) {
		this.seeAnchorPoints = visible;
		setChanged();
	}
	
	/**
	 * Gets the visibility state of the anchor points, which govern the shape of the curve
	 * @return true if visible, false if invisible
	 */
	public boolean getAnchorPointsVisibility() {
		return this.seeAnchorPoints;
	}
	
	/**
	 * Gives the control points that define this curve
	 * @return an array of OMTVectors with all the control points in order.
	 */
	public OMTVector[] getControlPoints() {
		return this.controlPoints;
	}
	
	/**
	 * Sets the control points for this curve
	 * 
	 * @param points
	 */
	public void setControlPoints(OMTVector[] points) {
		this.theCurve = copyBezierCurve(points);
		changed();
	}

	/**
	 * Set a single control point for this curve.
	 * @param i
	 * @param pos
	 */
	public void setControlPoint(int i, OMTVector pos) {
		this.controlPoints[i] =  pos;
		setControlPoints(this.controlPoints);
	}
	
}
