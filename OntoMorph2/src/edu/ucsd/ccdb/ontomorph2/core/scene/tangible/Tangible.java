package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.observers.SemanticObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines an object that can be viewed in the scene.  This object can be made relative
 * to an arbitrary coordinate system.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICoordinateSystem
 *
 */
public abstract class Tangible extends Observable implements ISemanticsAware{

	public static final String CHANGED_SET_COORDINATE_SYSTEM = "coordinate system";
	public static final String CHANGED_SELECT = "select";
	public static final String CHANGED_UNSELECT = "unselect";
	public static final String CHANGED_VISIBLE = "visible";
	public static final String CHANGED_COLOR = "color";
	public static final String CHANGED_HIGHLIGHTED_COLOR = "highlighted color";
	public static final String CHANGED_ADD_SEMANTIC_THING = "add semantic thing";
	public static final String CHANGED_REMOVE_SEMANTIC_THING = "remove semantic thing";
	public static final String CHANGED_SCALE = "scale";
	public static final String CHANGED_ROTATE = "rotate";
	public static final String CHANGED_MOVE = "move";
	public static final String CHANGED_NAME = "name";
	public static final String CHANGED_ADD_PART = "add part";
	public static final String CHANGED_LOADED = "loaded";
	public static final String CHANGED_CONTAINS = "contains";
	
	
	private PositionVector _position = new PositionVector();
	private RotationVector _rotation = new RotationVector();
	private CoordinateSystem sys = null;
	private Node theSpatial = new Node();
	private boolean _visible = false;
	private List<SemanticClass> semanticThings = new ArrayList<SemanticClass>();
	private SemanticClass mainSemanticClass = null;
	private SemanticInstance mainSemanticInstance = null;
	private Set<Tangible> previousContainerTangibles = null;
	

	private Color c = null;
	private Color highlightedColor = Color.yellow;
	
	private String name;
	
	
	public Tangible() {
		TangibleManager.getInstance().addTangible(this);
		this.addObserver(SceneObserver.getInstance());
		this.addObserver(SemanticObserver.getInstance());
		//by default, all objects ought to be associated with an instance.
		//the least specific instance that can be created is one of bfo:entity.
		//addSemanticThing(GlobalSemanticRepository.getInstance().createNewInstanceOfClass("bfo:entity"));
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
		return new RotationVector(_rotation);
	}

	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system. 
	 * @return - the PositionVector
	 * @see CoordinateSystem
	 */
	public PositionVector getRelativePosition()
	{
		_position.set(theSpatial.getLocalTranslation());
		return new PositionVector(_position);
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
		this.setRelativePosition(pos, true);
	}
	
	public void setRelativePosition(PositionVector pos, boolean flagChanged) {
		if (pos != null) {
			Vector3f oldPosition = theSpatial.getLocalTranslation();
			theSpatial.setLocalTranslation(pos);
			
			Vector3f displacement = oldPosition.subtract(pos);
			//in order for contained objects to travel along with its parent, must
			//also set their relative positions
			for (Tangible t: this.getContainedTangibles()) {
				Vector3f newContainedPosition = t.getRelativePosition().subtract(displacement);
				t.setRelativePosition(new PositionVector(newContainedPosition));
			}
			
			if (flagChanged) changed(CHANGED_MOVE);
		}
	}
	
	public void setRelativePosition(float x, float y, float z) {
		this.setRelativePosition(new PositionVector(x,y,z));
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
			changed(CHANGED_ROTATE);
		}
	}

	/**
	 * Set the scale for this INeuronMorphology using a 3D vector
	 * @param v - scale
	 * @see CoordinateSystem
	 */
	public void setRelativeScale(OMTVector v) {
		theSpatial.setLocalScale(v);
		changed(CHANGED_SCALE);
	}
	
	/**
	 * Set the scale for this INeuronMorphology using a float
	 * @param v - scale
	 * @see CoordinateSystem
	 */
	public void setRelativeScale(float f) {
		theSpatial.setLocalScale(f);
		changed(CHANGED_SCALE);
	}
	

	/**
	 * Returns a vector representing the normal of the XY plane of the parent tangible
	 * Takes the cross of the X and Y and then rotates it about the {@link CoordinateSystem} of the source {@link Tangible}
	 * The vector returned is in Absoltue World Coordinates
	 * @return {@link OMTVector} represrnting the normal in JME world coordinates
	 */
	public OMTVector getWorldNormal()
	{
		OMTVector v = null;
		
		CoordinateSystem coords = this.getCoordinateSystem();	//get the coordinate system
		Quaternion rotOrig = null;
		
		//find the rotation of the coordinate system
		if (coords != null)
		{
			rotOrig = coords.getRotationFromAbsolute();	
		}
		else
		{
			rotOrig = new Quaternion().fromAngleAxis(0, new Vector3f(0,0,0)); //empty quaternion
		}
		
		Vector3f xyplane = new Vector3f(0,0,1); //an apopgraph of X.cross(Y), Z is normal
		
		//now rotate that vector that is the normal to be aligned with the coordinates system
		v = new OMTVector(OMTUtility.rotateVector(xyplane, rotOrig));
		
		return v;
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
		
		if (this.getCoordinateSystem() == null) 
		{
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

	
	public void changed() {
		changed(null);
	}
	
	protected void changed(String argument) {
		this.setChanged();
		if (argument == null) {
			notifyObservers();
		} else {
			notifyObservers(argument);
		}
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
		changed(CHANGED_SET_COORDINATE_SYSTEM);
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
	
	/**
	 * puts the tangibles that called this member function on the selected list
	 */
	public void select() 
	{
		TangibleManager.getInstance().select(this);
		changed(CHANGED_SELECT);
	}
	
	public boolean isSelected() 
	{
		return TangibleManager.getInstance().isSelected(this);
	}

	public void unselect() 
	{
		TangibleManager.getInstance().unselect(this);
		changed(CHANGED_UNSELECT);
	}
	
	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean b) {
		_visible = b;
		changed(CHANGED_VISIBLE);
	}
	
	public void setColor(Color c) {
		this.c = c;
		changed(CHANGED_COLOR);
	}
	
	public Color getColor() {
		return this.c;
	}
	
	protected void setHighlightedColor(Color c) {
		this.highlightedColor = c;
		changed(CHANGED_HIGHLIGHTED_COLOR);
	}
	
	public Color getHighlightedColor() {
		return this.highlightedColor;
	}
	
	public List<SemanticClass> getSemanticClasses() {
		return semanticThings;
	}
	
	public List<SemanticClass> getAllSemanticClasses() {
		return getSemanticClasses();
	}
	
	public void addSemanticClass(SemanticClass thing) {
		this.semanticThings.add(thing);
		thing.addSemanticsAwareAssociation(this);
		changed(CHANGED_ADD_SEMANTIC_THING);
	}
	
	public void removeSemanticClass(SemanticClass thing) {
		this.semanticThings.remove(thing);
		thing.removeSemanticsAwareAssociation(this);
		changed(CHANGED_REMOVE_SEMANTIC_THING);
	}
	
	public SemanticClass getMainSemanticClass() {
		if (mainSemanticClass != null) {
			return mainSemanticClass;
		}
		
		//if we haven't assigned the main semantic class explicitly
		//pick the first one from the list of semantic classes
		//if the semantic classes list is empty, return null
		for (ISemanticThing thing : getSemanticClasses()) {
			if (thing instanceof SemanticClass) {
				this.mainSemanticClass = (SemanticClass)thing;
				break;
			}
		}
		return this.mainSemanticClass;
	}
	
	public SemanticInstance getMainSemanticInstance() {
		if (mainSemanticInstance != null) {
			return mainSemanticInstance;	
		}
		
		//if the main semantic instance for this tangible has not yet been defined
		//then create a new instance for this tangible from the main semantic class
		mainSemanticInstance = getMainSemanticClass().createInstance();
		
		return mainSemanticInstance;
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
		changed(CHANGED_SCALE);
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
		changed(CHANGED_ROTATE);
	}
	
	
	/**
	 * Changes the local translation this Tangible
	 * @param manip
	 * @param dx delta X
	 * @param dy delta Y
	 * @param mx position of mouse now X
	 * @param my position of mouse now Y
	 * @return {@link PositionVector} of displacement
	 * @author Stephen Larson, @author caprea
	 */
	public void move(float dx, float dy, int mx, int my)
	{
	
		PositionVector desired = pointUnderMouse(mx,my);
		this.setRelativePosition(desired, true); //implicitly calls changed()
	
	}
	
	private PositionVector pointUnderMouse(int mx, int my)
	{
//		this method returns the position that the mouse would be located in
		//the world if it had the same distance away from the camera that the selected objects have.
		//takes advantage of JME's Camera.getWorldCoordinates() method.
		
		Tangible manip = this;
		
		//designed to replace manip.move() - which needs to be overwritten in NeuronMorphology
		Quaternion rot = new Quaternion(0,0,0,1);		//for non-coordinated Tangibles
		Quaternion inv = new Quaternion();				//inverse rotation of coordinate system
		Vector3f offset = new Vector3f(0,0,0); 			//offset of origin of coordinate system
		Camera cam = View.getInstance().getCameraNode().getCamera();
		
		
		//Find the rotation of the coordinate system and the offset of the origin 
		//
		if (manip.getCoordinateSystem() != null) 
		{
			rot = manip.getCoordinateSystem().getRotationFromAbsolute();
			offset = manip.getCoordinateSystem().getOriginVector();
		}
		inv = new Quaternion(rot.inverse());	//inverse the rotation to put things into regular world coordinates
		
		//if the mouse were at the same z-position (relative to the camera) as the average 
		//position of all selected objects, 
		//find its world position in absolute coordinates.
		
		Vector3f fromPos = new Vector3f(manip.getAbsolutePosition());
		//note: previously used getRelativePosition and inverted its rotation, this caused jumps along Z - dont know why
		
		float dist = cam.getScreenCoordinates(fromPos).getZ();
		
		//a 3D mouse needs an X, Y and a distance where distance is [0,1] where 0 is close
		//detemine that position based on the X,Y and the predicted distance away from the camera
		Vector3f mWorldPos = cam.getWorldCoordinates(new Vector2f(mx, my), dist);
		Vector3f toPos = mWorldPos;
		
		//adjust the new position by the adjustments in the coordinate system
		toPos = toPos.subtract(offset);
		toPos = OMTUtility.rotateVector(toPos, inv);
		
		//put it in it's place
		PositionVector place = new PositionVector(toPos);
		
		return place;
	}
	
	
	
	/**
	 * Changes the local translation this Tangible
	 * The dimensions of freedom allow the 2D movement of the mouse to map to the 3D movement intended
	 * @param constraint Specifies what dimensions to allow movement based on mouse input. 
	 * Will typically range from (0,0,0) to (1,1,1). Where (1,1,0) corresponds to 2D movement on 
	 * the current X,Y plane
	 */
	/*
	public void move(float dx, float dy, OMTVector constraint)
	{
		//get changes in mouse movement
		//TODO: calculate the viewing angle and apply to constraint
		
		dx = dx * constraint.getX();
		dy = dy * constraint.getY();
		float dz = dy * constraint.getZ();
		
		//get the position, add the change, store the new position
		PositionVector np = new PositionVector( this.getRelativePosition().asVector3f().add(dx,dy,dz) );
		//PositionVector np = new PositionVector( this.getAbsolutePosition().asVector3f().add(dx,dy,dz) );
		
		//apply the movement
		this.setRelativePosition( np );
		changed(CHANGED_MOVE);
	}
	*/
	
	/*
	public void move(float dx, float dy, float dz)
	{
	//Use setRelative Position instead?
		//get the position, add the change, store the new position
		PositionVector np = new PositionVector( this.getRelativePosition().asVector3f().add(dx,dy,dz) );
				
		//apply the movement
		this.setRelativePosition( np );
		changed(CHANGED_MOVE);
	}
	*/
	
	
	public int hashCode() {
		return super.hashCode() + theSpatial.hashCode();
	}
	
	public String getName() 
	{
		String strName = "";
		
		if (this.name == null)
		{
			strName = "(" + this.getClass().getSimpleName() + ")";
		}
		else
		{	
			strName = this.name;
		}
		return strName;
	}
	
	public void setName(String name) {
		this.name = name;
		changed(CHANGED_NAME);
	}
	
	/**
	 * Returns the SemanticClass corresponding to this Tangible.
	 * @return {@link SemanticClass}
	 */
	public SemanticClass getSemanticClass() {
		return getSemanticInstance().getSemanticClass();
	}

	public SemanticInstance getSemanticInstance() {
		return null;//GlobalSemanticRepository.getInstance().getSemanticInstance("");
	}

	public void addContainedTangible(Tangible contained) {
		TangibleManager.getInstance().addContainedTangible(this, contained);
		changed(CHANGED_CONTAINS);
	}
	
	public Set<Tangible> getContainedTangibles() {
		return TangibleManager.getInstance().getContainedTangibles(this);
	}
	
	public Set<Tangible> getContainerTangibles() {
		return TangibleManager.getInstance().getContainerTangibles(this);
	}
	
	public void removeContainedTangible(Tangible t) {
		TangibleManager.getInstance().removeContainedTangible(this, t);
		changed(CHANGED_CONTAINS);
	}

	public Set<Tangible> getPreviousContainerTangibles() {
		return previousContainerTangibles;
	}
	
	public void setPreviousContainerTangibles(Set<Tangible> t) {
		previousContainerTangibles = t;
	}
	
	public void updateContainerTangibles(Set<Tangible> containerTangibles) 
	{
		Set<Tangible> currentContainerTangibles = TangibleManager.getInstance().getContainerTangibles(this);
		previousContainerTangibles = currentContainerTangibles;

		boolean changed = false; //one boolean to test if a change has happened
		
		//tangibles to remove contains those elements in the list of current containers
		//that do not appear in the update list.  these must go away
		Set<Tangible> tangiblesToRemove = new HashSet<Tangible>(currentContainerTangibles);
		tangiblesToRemove.removeAll(containerTangibles);
		for (Tangible toRemove : tangiblesToRemove) {
			TangibleManager.getInstance().removeContainedTangible(toRemove, this);
			changed = true;
		}
		
		//tangibles to add contains those elements in the update list that do not 
		//appear in the list of current containers.  these must be added.
		Set<Tangible> tangiblesToAdd = new HashSet<Tangible>(containerTangibles);
		tangiblesToAdd.removeAll(currentContainerTangibles);
		for (Iterator it = tangiblesToAdd.iterator(); it.hasNext();) {
			TangibleManager.getInstance().addContainedTangible((Tangible)it.next(), this);
			changed = true;
		}
		
		if (changed) {
			changed(CHANGED_CONTAINS);
		}

	}
	
	public Node getSpatial() {
		return theSpatial;
	}

}
