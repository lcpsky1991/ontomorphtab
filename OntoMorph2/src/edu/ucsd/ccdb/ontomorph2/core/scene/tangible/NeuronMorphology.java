package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.math.BigInteger;
import java.util.Set;

import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.spatial.AllenCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public abstract class NeuronMorphology extends Tangible{

	public static final String RENDER_AS_LINES = "lines";
	public static final String RENDER_AS_CYLINDERS = "cylinders";
	public static final String RENDER_AS_LOD = "LOD";
	public static final String RENDER_AS_LOD_2 = "LOD2";
	public static final String RENDER_AS_DETAILED_BOXES = "detailed_boxes";

	//public static final String RENDER_AS_CYLINDER_BATCH = "cylinder_batch";

	String _renderOption = RENDER_AS_LINES; //default render option
	
	Set<ICable> segmentGroupList = null;
	PositionVector lookAtPosition = null;
	
	Curve3D _curve = null;
	float _time = 0.0f;
	private Vector3f _upVector;
	
	/**
	 * Get the Curve that this NeuronMorphology has been associated with
	 * @return
	 */
	public Curve3D getCurve() {
		return _curve;
	}
	
	public abstract String getFilename();
	
	/**
	 * Disassociates a curve with this morphology and attaches this morphology to a different curve
	 * @param c
	 * @return True if successful. False if failed.
	 */
	public boolean attachTo(Curve3D c)
	{
		if (c != null)
		{
			_curve = c;
			this.setCoordinateSystem(c.getCoordinateSystem());
			positionAlongCurve(_curve, _time);
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the "time" along the curve that this INeuronMorphology is positioned at
	 * @return
	 */
	public float getTime() {
		return _time;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getRenderOption()
	 */
	public String getRenderOption() {
		return _renderOption;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#setRenderOption(java.lang.String)
	 */
	public void setRenderOption(String renderOption) {
			_renderOption = renderOption;
	}

	/*
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(getSemanticThings());
		for (ICable sg : this.getSegmentGroups()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}*/
	
	/*
	public void addSemanticClass(String classURI) {
		semanticThings.add(GlobalSemanticRepository.getInstance().getSemanticClass(classURI));
	}*/

	
	/**
	 * Tells whether this cell is attached to a parent curve
	 * @return True if this cell has NO parent curve
	 */
	public boolean isFreeFloating()
	{
		if (_curve != null) return false;
		return true;
	}
	
	
	/**
	 * Overrides the general Tangible move() because most cells will be attached to a curve
	 * therefore, in most cases cells only move along a curve's time
	 */
	
	public PositionVector move(float dx, float dy, int mx, int my)
	{
		//get changes in mouse movement
		//if this cell is a free-floating cell, then move it as normal
		PositionVector p = null;
		if (this.isFreeFloating())
		{
			p = super.move(dx,dy, mx, my);
		}
		else 
		{
			//the cell is attached to a curve
			//move the cell by changing it's time signature on the parent curve
			PositionVector prev = this.getRelativePosition();
			
			this._time += 0.001f * dx; //the dx passed may be negative
			
			if (_time <= 0 ) _time = 0.001f;
			if (_time >= 1) _time = 0.999f;
			this.positionAlongCurve(_curve,_time);
			//p = new PositionVector(prev.asVector3f().subtract(this.getRelativePosition().asVector3f())); //return the displacement
			
		}
		
		//apply the movement
		changed(CHANGED_MOVE);
		return p;
	}
	
	
	
	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c, or does nothing if it is a free-floating neuron
	 * @return True if moved successfully, returns false if it does not move (free-floating neurons)
	 * @param c The curve to position this neuron along
	 * @param time The time to place it along the curve from [0,1]
	 */
	public boolean positionAlongCurve(Curve3D c, float time) 
	{
		if ( _curve != null)
		{
			setRelativePosition(new PositionVector(((Curve3D)c).getPoint(time)));
			return true;
		}
		return false;
	}

	/**
	 * Rotates the NeuronMorphology to aim its 'up' direction towards p
	 * @param p
	 */
	public void lookAt(PositionVector p) {
		lookAtPosition = p;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getLookAtPosition()
	 */
	public PositionVector getLookAtPosition() {
		return lookAtPosition;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getName()
	 */
	//public abstract String getName();

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#setUpVector(com.jme.math.Vector3f)
	 */
	public void setUpVector(Vector3f vector3f) {
		_upVector = vector3f;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getUpVector()
	 */
	public Vector3f getUpVector() {
		if (_upVector != null) {
			return _upVector;
		}
		return Vector3f.UNIT_Y;
	}
	
	/**
	 * Returns the BrainRegion that this neuron morphology is currently located in
	 * @return
	 */
	public BrainRegion getEnclosingBrainRegion(){
		if (this.getCoordinateSystem() != null && this.getCoordinateSystem() instanceof AllenCoordinateSystem) {
			OMTVector position = this.getRelativePosition();
			return ReferenceAtlas.getInstance().getBrainRegionByVoxel((int)position.x, (int)position.y, (int)position.z);
		} 
		throw new OMTException("Cannot get encolosing brain region from a NeuronMorphology that is not set to the AllenCoordinateSystem", null);
	}

	/**
	 * Get the number of cables this NeuronMorphology has
	 * @return
	 */
	public abstract int getCableCount();

	/**
	 * Get a cable by its numerical position 
	 * @param i
	 * @return
	 */
	public abstract ICable getCable(int i);
	
	/**
	 * Get a cable by its id, indepedent of its numerical position
	 * @param id
	 * @return
	 */
	public abstract ICable getCable(BigInteger id);

}