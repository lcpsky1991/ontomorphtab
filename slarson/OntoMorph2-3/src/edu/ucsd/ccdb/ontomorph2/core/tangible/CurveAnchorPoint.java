/**
 * 
 */
package edu.ucsd.ccdb.ontomorph2.core.tangible;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * Represents a single point in a Bezier curve.  When it gets repositioned,
 * it updates that point in its parent BezierCurve automatically.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class CurveAnchorPoint extends Tangible {
	int i = 0;
	Curve3D parentCurve = null;
	
	/**
	 * 
	 * @param curve - the parent curve for this anchor point
	 * @param position - the position of the point
	 * @param i - the index of the point in the curve
	 */
	public CurveAnchorPoint(Curve3D curve, OMTVector position, int i) {
		super(curve.getName() + ", point " + i);
		this.parentCurve = curve;
		this.setPosition(new PositionVector(position));
		this.i = i;	
	}
	
	public boolean delete()
	{
		return (parentCurve.removeControlPoint(this.i) && super.delete());
	}
	
	
	/**
	 * 
	 */
	public PositionVector move(float dx, float dy, int mx, int my)
	{
		PositionVector p = super.move(dx, dy, mx, my);
		//parentCurve.setControlPoint(this.i, this.getRelativePosition());
		return p;
	}
	
	public void execPostManipulate(Tangible target)
	{
		this.parentCurve.reapply();
	}
	
	public PositionVector getDeltafromCenter()
	{
		//return (thisPosition - center)
		return new PositionVector(this.getPosition().asVector3f().subtract(parentCurve.getCenterPoint().asVector3f()));
	}
	
	
	/**
	 * Meant to be used to access the functionality of the curve this anchor point belongs to
	 * Should not be used for creating new curves, but ok for creating new anchor points
	 * @return reference to the tangible of the curve3d associated with this point
	 */
	public Curve3D getParentCurve()
	{
		return this.parentCurve;
	}
	
	/**
	 * 
	 * @return i the index (time) of the point along the curve)
	 */
	public int getIndex()
	{
		return i;
	}
	
	/**
	 * Approximate 'time' of this point along the parentCurve
	 * NOT RELIABLE - this is only an estimate
	 * @return (0-1) the time, as a float between 0 and 1 that represents the position along the curve.
	 */
	public float aproxTime()
	{
		float count = parentCurve.controlPoints.length;
		return ((float) i / count);
	}
	/**
	 * Null implementation overriding super class functionality 
	 * to prevent rotation
	 */
	public void rotate(float dx, float dy, OMTVector constraint)
	{
		//CurveAnchorPoints shouldn't rotate
	}
	
	/**
	 * Null implementation overriding super class functionality 
	 * to prevent scaling
	 */
	public void scale(float dx, float dy, OMTVector constraint) {
		//CurveAnchorPoints shouldn't scale
	}
	
	public String getName()
	{
		String info="";
		info = super.getName();
		
		if (info == null || info == "")
		{
			return "P" + this.i + " of " + this.getParentCurve().getName();
		}
		return info;	//if it has a name return it
	}
	
	/**
	 * Create a new anchor point at a location relative to this point.
	 *
	 */
	public void createPoint()
	{
		int i = getIndex() + 1;
		float t = aproxTime();
		float delta = 0.05f;
		OMTVector place = null;
		
		
		//place = new OMTVector(src.getRelativePosition()); //original
		//find out what 'time' the current point is at, incriment that ammount a small ammount
		//find out the tangent of the current point, then add the tangent unit to the position
		//DOes not work well with the end points
		place = new OMTVector(getParentCurve().getPoint(t+delta));

		CurveAnchorPoint capt =	getParentCurve().addControlPoint(i, place);
		capt.select();
		
		getParentCurve().reapply(); //TODO: remove this line
	}
	

}