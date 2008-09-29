package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.jme.curve.Curve;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;



//TODO: use setModelBound on spatial, maybe this will fix the problem 

/**
 * Defines a CatmullRom curve in the framework that is manipulable by anchor points.
 * 
 * @author caprea
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class Curve3D extends Tangible{

	CatmullRomCurve theCurve = null;
	//BezierCurve theCurve = null;
	float delta = 0.1f;
	private Vector3f _modelBinormal = null;
	OMTVector[] controlPoints = null;
	boolean seeAnchorPoints = false;
	List<CurveAnchorPoint> anchors = null;
	
	public Curve3D(String arg0, OMTVector[] arg1) {
		setName(arg0);
		controlPoints = arg1;
	}

	public Curve3D(String string, OMTVector[] array, CoordinateSystem d) {
		this(string, array);
		setCoordinateSystem(d);
	}
	
	/**
	 * Get a list of the CurveAnchorPoints for this Curve
	 * @return
	 */
	public List<CurveAnchorPoint> getAnchorPoints() {
		if (anchors == null) 
		{
			anchors = new ArrayList<CurveAnchorPoint>();
			for (int i = 0; i< controlPoints.length; i++) {
				anchors.add(new CurveAnchorPoint(this, controlPoints[i], i));
			}
		}
		return anchors;
	}
	
	/**
	 * Returns a list of cells that are associated with this curve
	 * @author caprea
	 * @return array list of {@link NeuronMorphology}s that 'belong' to this curve. This list is not ordered by anything reasonable (such as time along curve)
	 * 
	 */
	public List<NeuronMorphology> getChildrenCells()
	{
		 Set<NeuronMorphology> all = TangibleManager.getInstance().getCells();
		 List<NeuronMorphology> kids = new ArrayList<NeuronMorphology>();
		 kids.clear();
		 
		 Iterator i = all.iterator();
		 
		 //loops through all possible cells, if they are part of this curve then add those cells to the list to return
		 while ( i.hasNext() )
		 {
			 NeuronMorphology consider = (NeuronMorphology)i.next();
			 Curve3D c = consider.getCurve();
			 if (this == c)
			 {
				 kids.add(consider);
			 }
		 }
		 
		 return kids;
	}
	/**
	 * Counts the number of controlpoints that define this curve
	 * @return the number of points defining this curve
	 */
	public int getAnchorCount()
	{
		return controlPoints.length;
	}
	
	/**
	 * compute a very small vector that is 
	 * approximately tangent to the point of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getTangent(float time) {
		Vector3f p1 = getCurve().getPoint(getTimeMinusDelta(time));
		Vector3f p2 = getCurve().getPoint(getTimePlusDelta(time));
		return p2.subtract(p1).normalize();
	}
	
	/**
	 * computes a very small vector that is approximately normal 
	 * to the point of 'time' given
	 * @param time
	 * @return - the tangent Vector3f
	 */
	public Vector3f getNormal(float time) {
		
		Vector3f px = getCurve().getPoint(getTimeMinusDelta(time));
		Vector3f py = getCurve().getPoint(getTimePlusDelta(time));
		
		Vector3f pe = getCurve().getPoint(time);
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
	public Curve getCurve() {
		if (theCurve == null) {
			theCurve = copyBezierCurve();
		}
		return theCurve;
	}
	
	//have to copy the curve because JME BezierCurve class 
	//does not allow modification of the control points
	//private BezierCurve copyBezierCurve()
	private CatmullRomCurve copyBezierCurve()
	{
		//BezierCurve copy = new BezierCurve(this.getName(), this.controlPoints);
		CatmullRomCurve copy = new CatmullRomCurve(this.getName(), this.controlPoints);

		//apply coordinate system to this curve.
		if (this.getCoordinateSystem() != null) 
		{
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
			return getCurve().getOrientation(time, precision);
		}
		Matrix3f rotation = new Matrix3f();

		//calculate tangent
		Vector3f tangent = getCurve().getPoint(time).subtract(getCurve().getPoint(time + precision));
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
		return new PositionVector(getCurve().getPoint(time));
	}

	/**
	 * Sets the visibility of the anchor points, which govern the shape of the curve.
	 * 
	 * @param visible - if true, make points visible, if false, make invisible
	 */
	public void setAnchorPointsVisibility(boolean visible)
	{
		this.seeAnchorPoints = visible;
		this.changed();
	}
	
	
	public void reapply()
	{
		//redraw the curve
		for (CurveAnchorPoint p : getAnchorPoints())
		{
			setControlPoint(p.getIndex(), p.getRelativePosition());
		}
		
	}
	
	public PositionVector getCenterPoint()
	{
		Vector3f mean=new Vector3f(0,0,0);
		for (CurveAnchorPoint p: getAnchorPoints())
		{
			mean = mean.add(p.getRelativePosition().asVector3f());
		}
		
		mean = mean.divide(getAnchorCount()); //divie to get eh average
		return (new PositionVector(mean));
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
	protected OMTVector[] getControlPoints() {
		return this.controlPoints;
	}

	/**
	 * Set a single control point for this curve.
	 * @param i
	 * @param pos
	 */
	protected void setControlPoint(int i, OMTVector pos) {
		this.controlPoints[i] =  pos;
		changed();
	}
	
	
	
	/**
	 * Adds a new anchorpoint to the curve. If index is -1 or less, anchor point will be appended to the end of curve
	 * @param i the index of which to insert the control point, 
	 * @param pos
	 */
	public void addControlPoint(int index, OMTVector pos)
	{
		
		OMTVector modlist[] = new OMTVector[controlPoints.length+1]; //make the new list one element larger 
		
		//if no index supplied append a new control point
		if ( index < 0 )
		{
			index = 0;	
		}
		else if (index >= controlPoints.length)
		{
			index = controlPoints.length - 1; //length is a conveiniant way to get the [ (last element index)+1 ]
		}
		
		//copy over the control points for points before index
		for (int x=0; x < index; x++)
		{
			modlist[x] = controlPoints[x];
		}
		
		//insert the new control point
		modlist[index] = pos;		
		
		//now append the rest of the points
		for (int x=index; x < controlPoints.length; x++)
		{
			modlist[x+1] = controlPoints[x]; //copy over the values from original into ind+1 of modified list
		}
		
		//now copy the list back to be saved
		controlPoints = modlist;
		
		anchors = null; //forces recreation of anchor-list next time getAnchors is called
		anchors = getAnchorPoints();
		
		//deselect the previous point and select the newly created one
		CurveAnchorPoint prev = anchors.get(index-1);
		CurveAnchorPoint curr = anchors.get(index);
		prev.unselect();
		curr.setVisible(true);
		curr.select();
		
		curr.changed();
		
		//update the scene
		//changed();
		
	}
	
	
	public boolean removeControlPoint(int index)
	{
		if (true) return true;
		
		if (controlPoints.length > 2)
		{
			OMTVector modpoints[] = new OMTVector[controlPoints.length-1];
			
			//copy over the points that preceed the index-to-remove
			for (int i = 0; i < index; i++)
			{
				modpoints[i] = controlPoints[i];
			}
			
			//copy over the points that follow the index-to-remove
			for (int j = index+1; j < controlPoints.length; j++)
			{
				modpoints[j-1] = controlPoints[j];
			}
			controlPoints = modpoints.clone();
			
			
			//update the anchor points (their Is)
			anchors.remove(index);
			for (int i=0; i < controlPoints.length; i++)
			{
				anchors.get(i).i = i;
				System.out.println(i + ":" + anchors.get(i).getIndex());
			}
			
			//System.out.println("anchors " + anchors.size());
			//System.out.println("apc " +  getAnchorPoints().size());
			return true;
		}		
		
		return true;
	}
	
	
	public void changed() 
	{
		//null theCurve to force a getCurve to recreate the 
		// underlying curve instance
		this.theCurve = null;
		View.getInstance().getScene().changed(Scene.CHANGED_CURVE);  //FIXME: possible site of tension with model/view/controller
		super.changed();
	}

	
	public void select()
	{ 
		
		boolean ms = TangibleManager.getInstance().getMultiSelect(); //get previous state of multiselect
		super.select();
		
		TangibleManager.getInstance().setMultiSelect(true);
		anchors = getAnchorPoints();
		
		for (int i=0; i < anchors.size(); i++)
		{
			anchors.get(i).select();
		}
		//puts the curve as the most recently selected item		
		TangibleManager.getInstance().setMultiSelect(ms);	//restore multiselect to however it was befpre
	}
	

	public void unselect()
	{
		super.unselect();
		for (int i=0; i < anchors.size(); i++)
		{
			anchors.get(i).unselect();
		}
		reapply();
	}
}

