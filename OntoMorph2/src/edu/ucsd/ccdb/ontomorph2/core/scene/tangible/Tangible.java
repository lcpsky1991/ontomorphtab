package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;

/**
 * Defines an object that can be viewed in the scene.  This object can be made relative
 * to an arbitrary coordinate system.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICoordinateSystem
 *
 */
public abstract class Tangible extends Observable implements ISemanticsAware{

	private PositionVector _position = new PositionVector();
	private RotationVector _rotation = new RotationVector();
	private CoordinateSystem sys = null;
	private Node theSpatial = new Node();
	private boolean selected = false;
	private boolean _visible = true;
	private List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();

	private Color c = null;
	private Color highlightedColor = null;
	
	public Tangible() {
		//by default, all objects ought to be associated with an instance.
		//the least specific instance that can be created is one of bfo:entity.
		//addSemanticThing(SemanticRepository.getInstance().createNewInstanceOfClass("bfo:entity"));
	}
	
	/**
	 * get the RotationVector that defines this INeuronMorphology's rotation.
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @return - the RotationVector
	 * @see CoordinateSystem
	 */
	public RotationVector getRelativeRotation() {
		_rotation.set(theSpatial.getLocalRotation());
		return _rotation;
	}

	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system. 
	 * @return - the PositionVector
	 * @see CoordinateSystem
	 */
	public PositionVector getRelativePosition() {
		_position.set(theSpatial.getLocalTranslation());
		return _position;
	}

	/** 
	 * set the PositionVector for this INeuronMorphology
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param pos - desired position
	 * @see CoordinateSystem
	 */
	public void setRelativePosition(PositionVector pos) {
		if (pos != null) {
			theSpatial.setLocalTranslation(pos);
			changed();
		}
	}
	
	/**
	 * set the RotationVector for this INeuronMorphology
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param rot - desiredRotation
	 * @see CoordinateSystem
	 */
	public void setRelativeRotation(RotationVector rot) {
		if (rot != null) {
			theSpatial.setLocalRotation(rot);
			changed();
		}
	}

	/**
	 * Set the scale for this INeuronMorphology using a 3D vector
	 * @param v - scale
	 * @see CoordinateSystem
	 */
	public void setRelativeScale(OMTVector v) {
		theSpatial.setLocalScale(v);
		changed();
	}
	
	/**
	 * Set the scale for this INeuronMorphology using a float
	 * @param v - scale
	 * @see CoordinateSystem
	 */
	public void setRelativeScale(float f) {
		theSpatial.setLocalScale(f);
	}
	

	/**
	 * 
	 * @see CoordinateSystem
	 */
	public OMTVector getRelativeScale() {
		return new OMTVector(theSpatial.getLocalScale());
	}
	
	/**
	 * get the RotationVector that defines this INeuronMorphology's rotation.
	 * This is always in the absolute world coordinate system.
	 * @return - the RotationVector
	 * @see CoordinateSystem
	 */
	public RotationVector getAbsoluteRotation() {
		if (this.getCoordinateSystem() == null) {
			return this.getRelativeRotation();
		}
		Quaternion v = this.getRelativeRotation().multLocal(this.getCoordinateSystem().getRotation((Quaternion)null));
		if (v != null) {
			return new RotationVector(v);
		}
		return (RotationVector)null;
	}
	
	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is always in the absolute world coordinate system.
	 * @see CoordinateSystem
	 */
	public PositionVector getAbsolutePosition() {
		
		if (this.getCoordinateSystem() == null) {
			return this.getRelativePosition();
		}
		Vector3f v = this.getCoordinateSystem().multPoint(this.getRelativePosition());
		if (v != null) {
			return new PositionVector(v);
		}
		return (PositionVector)null;
	}

	public OMTVector getAbsoluteScale() {
		if (this.getCoordinateSystem() == null) {
			return this.getRelativeScale();
		}
		
		Vector3f v = this.getRelativeScale().multLocal(this.getCoordinateSystem().getScale(null));
		if (v != null) {
			return new OMTVector(v);
		}
		return (OMTVector)null;
	}

	
	protected void changed() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Sets the CoordinateSystem by which the relative position, rotation, and scale of this
	 * scene object will be defined.
	 * 
	 * @param sys
	 * @see CoordinateSystem
	 */
	public void setCoordinateSystem(CoordinateSystem sys) {
		this.sys = sys;
	}
	
	/**
	 * Gets the CoordinateSystem by which the relative position, rotation, and scale
	 * of this scene object will be defined.
	 * @return
	 * @see CoordinateSystem
	 */
	public CoordinateSystem getCoordinateSystem() {
		return this.sys;
	}
	
	public void select() 
	{
		TangibleManager.getInstance().select(this);
		changed();
	}
	
	public boolean isSelected() 
	{
		return TangibleManager.getInstance().isSelected(this);
	}

	public void unselect() 
	{
		TangibleManager.getInstance().unselect(this);
		changed();
	}
	
	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean b) {
		_visible = b;
		changed();
	}
	
	public void setColor(Color c) {
		this.c = c;
		changed();
	}
	
	public Color getColor() {
		return this.c;
	}
	
	public void setHighlightedColor(Color c) {
		this.highlightedColor = c;
		changed();
	}
	
	public Color getHighlightedColor() {
		return this.highlightedColor;
	}
	
	public List<ISemanticThing> getSemanticThings() {
		return semanticThings;
	}
	
	public List<ISemanticThing> getAllSemanticThings() {
		return getSemanticThings();
	}
	
	public void addSemanticThing(ISemanticThing thing) {
		this.semanticThings.add(thing);
		thing.addSemanticsAwareAssociation(this);
	}
	
	public void removeSemanticThing(ISemanticThing thing) {
		this.semanticThings.remove(thing);
		thing.removeSemanticsAwareAssociation(this);
	}
	

	/**
	 * Scales this Tangible in the dimensions of constraint
	 * @param morph the item(s) to be rotated
	 * @param constraint the dimensions that the morphology will be scaled in
	 */
	public void scale(float dx, float dy, OMTVector constraint)
	{
	
		float delta = 0.01f * dx;
		
		OMTVector current = this.getRelativeScale();
		
		OMTVector nscale = new OMTVector(current.add(delta,delta,delta));
		
		
		//do NOT scale if the new scale will 'flip' the object
		if ( !(nscale.getX() < 0 || nscale.getY() < 0 || nscale.getZ() < 0 ) )
		{
			this.setRelativeScale(nscale);	
		}
	}
	
	
	
	/**
	 * Changes the rotation of this Tangible 
	 * @param morph the item(s) to be rotated
	 * @param constraint the axis (or axes) on which to rotate the object. 
	 * For example, if constraint is (1,0,0) the object will rotates about it's own X axis 
	 * (not the world's X axis)
	 * @author caprea
	 */
	public void rotate(float dx, float dy, OMTVector constraint)
	{
		float delta = dx;
		
		Quaternion more = new Quaternion();
		Quaternion end = new Quaternion();
		
		more.fromAngleAxis(0.1f * delta, constraint); //rotate with horitonzal mouse movement
		
		end = this.getRelativeRotation().mult(more);
		
		this.setRelativeRotation( new RotationVector(end) );
		
	}
	
	/**
	 * Changes the local translation this Tangible
	 * The dimensions of freedom allow the 2D movement of the mouse to map to the 3D movement intended
	 * @param constraint Specifies what dimensions to allow movement based on mouse input. 
	 * Will typically range from (0,0,0) to (1,1,1). Where (1,1,0) corresponds to 2D movement on 
	 * the current X,Y plane
	 */
	//TODO: impliment the constraint
	public void move(float dx, float dy, OMTVector constraint)
	{
		//get changes in mouse movement
		
		
		//TODO: calculate the viewing angle and apply to constraint
		
		dx = dx * constraint.getX();
		dy = dy * constraint.getY();
		float dz = dy * constraint.getZ();
		
		//get the position, add the change, store the new position
		PositionVector np = new PositionVector( this.getRelativePosition().asVector3f().add(dx,dy,dz) );
		
		//apply the movement
		this.setRelativePosition( np );
	}
}
