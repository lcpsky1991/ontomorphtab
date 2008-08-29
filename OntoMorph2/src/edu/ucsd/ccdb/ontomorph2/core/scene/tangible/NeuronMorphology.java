package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.math.BigInteger;
import java.util.Set;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.spatial.AllenCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

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
	public void move(float dx, float dy, OMTVector constraint)
	{
		//get changes in mouse movement
		
		//if this cell is a free-floating cell, then move it as normal
		if (isFreeFloating())
		{
			super.move(dx,dy, constraint);
		}
		else 
		{
			//the cell is attached to a curve
			//move the cell by changing it's time signature on the parent curve
			this._time += 0.002f * dx;
			if (_time <= 0 ) _time = 0.001f;
			if (_time >= 1) _time = 0.999f;
			this.positionAlongCurve(_curve,_time);
		}
		
		//apply the movement
		changed(CHANGED_MOVE);
	}
	
	
	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c
	 *
	 */
	public void positionAlongCurve(Curve3D c, float time) {
		setRelativePosition(new PositionVector(((Curve3D)c).getPoint(time)));
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
	public abstract String getName();

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