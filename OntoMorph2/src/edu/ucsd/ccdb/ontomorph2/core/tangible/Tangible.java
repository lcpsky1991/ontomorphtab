package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Set;

import org.morphml.neuroml.schema.XWBCTangible;
import org.morphml.neuroml.schema.impl.XWBCTangibleImpl;

import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.observers.SemanticObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

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
	public static final String CHANGED_DELETE = "delete";
	public static final String CHANGED_ADD_PART = "add part";
	public static final String CHANGED_LOADED = "loaded";
	public static final String CHANGED_CONTAINS = "contains";

	protected XWBCTangible theSpatial = null;
	private boolean _visible = true;
	private List<SemanticClass> semanticThings = new ArrayList<SemanticClass>();
	private SemanticClass mainSemanticClass = null;
	private SemanticInstance mainSemanticInstance = null;

	private Set<ContainerTangible> previousContainerTangibles = null;
	

	private Color c = null;
	private Color highlightedColor = Color.yellow;
	
	
	/**
	 * Construct a Tangible.  Must have a name that is unique throughout the system
	 * @param name
	 */
	public Tangible(String name) 
	{
		if (name == null) 
		{
			throw new OMTException("Cannot construct a tangible with a null name!");
		}
		initializeTangible(name, new XWBCTangibleImpl());
		TangibleManager.getInstance().addTangible(this);
		this.addObserver(SceneObserver.getInstance());
		this.addObserver(SemanticObserver.getInstance());
		//by default, all objects ought to be associated with an instance.
		//the least specific instance that can be created is one of bfo:entity.
		//addSemanticThing(GlobalSemanticRepository.getInstance().createNewInstanceOfClass("bfo:entity"));
	}
	
	protected void initializeTangible(String name, XWBCTangible t) {
		theSpatial = t;
		theSpatial.setPosition(new PositionVector().toPoint3D());
		theSpatial.setRotation(new RotationQuat().toWBCQuat());
		theSpatial.setScale(new OMTVector(1f, 1f, 1f).toPoint3D());
		theSpatial.setName(name);
		theSpatial.setId(BigInteger.valueOf(new Random().nextLong()));
	}
	
	
	/**
	 * 
	 * @return True if successfully deleted this tangible
	 */
	public boolean delete()
	{
		//System.out.println("Deleting disabled");
		//remove all the view instances of this tangible, first
		this.changed(CHANGED_DELETE);
		//return TangibleManager.getInstance().removeTangible(this);
		return true;
	}
	

	/**
	 * get the RotationQuat that defines this INeuronMorphology's rotation.
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @return - the RotationQuat
	 * @see CoordinateSystem
	 */
	public RotationQuat getRotation() {
		return new RotationQuat(theSpatial.getRotation());
	}

	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system. 
	 * @return - the PositionVector
	 * @see CoordinateSystem
	 */
	public PositionVector getPosition()
	{
		return new PositionVector(theSpatial.getPosition());
	}

	/** 
	 * set the position for this Tangible
	 * This is relative to the coordinate system that this Tangible has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param pos - desired position
	 * @see CoordinateSystem
	 */
	public void setPosition(PositionVector pos) {
		this.setPosition(pos, true);
	}
	
	/**
	 * Set the position, with an option to not fire the changed behavior
	 * @param pos - new relative position for this Tangible
	 * @param flagChanged - if true, fire the changed behavior, if false, do not fire.
	 */
	public void setPosition(PositionVector pos, boolean flagChanged) 
	{
		if (pos != null) 
		{
			theSpatial.setPosition(pos.toPoint3D());
			if (flagChanged) 
			{
				this.save();
				changed(CHANGED_MOVE);
				
			}
		}
	}
	
	public void setPosition(float x, float y, float z) {
		this.setPosition(new PositionVector(x,y,z));
	}
	
	/**
	 * set the RotationQuat for this INeuronMorphology
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param rot - desiredRotation
	 * @see CoordinateSystem
	 */
	public void setRotation(RotationQuat rot) {
		if (rot != null) {
			theSpatial.setRotation(rot.toWBCQuat());
			this.save();
			changed(CHANGED_ROTATE);
		}
	}

	/**
	 * Set the scale for this INeuronMorphology using a 3D vector
	 * @param v - scale
	 * @see CoordinateSystem
	 */
	public void setScale(OMTVector v) {
		theSpatial.setScale(v.toPoint3D());
		this.save();
		changed(CHANGED_SCALE);
	}
	
	/**
	 * Set the scale for this INeuronMorphology using a float
	 * @param v - scale
	 */
	public void setScale(float f) {
		OMTVector v = new OMTVector(f, f, f);
		this.setScale(v);
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
		
		Quaternion rotOrig = null;
		
		
		rotOrig = new Quaternion().fromAngleAxis(0, new Vector3f(0,0,0)); //empty quaternion

		Vector3f xyplane = new Vector3f(0,0,1); //an apopgraph of X.cross(Y), Z is normal
		
		//now rotate that vector that is the normal to be aligned with the coordinates system
		v = new OMTVector(OMTVector.rotateVector(xyplane, rotOrig));
		
		return v;
	}
	
	protected XWBCTangible getSpatial() {
		return this.theSpatial;
	}
	
	/**
	 * 
	 * @see CoordinateSystem
	 */
	public OMTVector getScale() {
		return new OMTVector(theSpatial.getScale());
	}
	
	/**
	 * puts the tangibles that called this member function on the selected list
	 */
	public void select() 
	{
		TangibleManager.getInstance().select(this);
		changed(CHANGED_SELECT);
	}
	
	public Tangible selectRollover(){
		Tangible rollover  = TangibleManager.getInstance().selectRollover(this);
		//changed(CHANGED_SELECT);
		return rollover;
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
		if (this.c == null) {
			this.c = Color.WHITE; // default color if it hasn't been set
		}
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
		changed(CHANGED_ADD_SEMANTIC_THING);
	}
	
	public void removeSemanticClass(SemanticClass thing) {
		this.semanticThings.remove(thing);
		changed(CHANGED_REMOVE_SEMANTIC_THING);
	}
	
	/**
	 * Returns the SemanticClass corresponding to this Tangible.
	 * If there are multiple semantic classes, pick the first one.
	 * @return {@link SemanticClass}
	 */
	public SemanticClass getSemanticClass() {
		if (mainSemanticClass != null) {
			return mainSemanticClass;
		}
		
		//if we haven't assigned the main semantic class explicitly
		//pick the first one from the list of semantic classes
		//if the semantic classes list is empty, return null
		for (SemanticThing thing : getSemanticClasses()) {
			if (thing instanceof SemanticClass) {
				this.mainSemanticClass = (SemanticClass)thing;
				break;
			}
		}
		return this.mainSemanticClass;
	}
	
	/**
	 * Creates a SemanticInstance of this Tangible in the SemanticRepository.
	 * Uses the class that is returned from getSemanticClass()
	 */
	public SemanticInstance getSemanticInstance() {
		if (mainSemanticInstance == null) {
			//if the main semantic instance for this tangible has not yet been defined
			//then create a new instance for this tangible from the main semantic class
			mainSemanticInstance = getSemanticClass().createInstance();
			//associate the SemanticInstance with this Tangible
			mainSemanticInstance.setSemanticsAwareAssociation(this);
		}
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
		
		OMTVector current = this.getScale();
		OMTVector nscale = new OMTVector(current.add(delta,delta,delta));
		
		//do NOT scale if the new scale will 'flip' the object
		if ( !(nscale.getX() < 0 || nscale.getY() < 0 || nscale.getZ() < 0 ) )
		{
			this.setScale(nscale);	
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
		
		end = this.getRotation().mult(more);
		
		this.setRotation( new RotationQuat(end) );
		changed(CHANGED_ROTATE);
	}
	
	
	/**
	 * Changes the local translation this Tangible
	 * @param manip
	 * @param dx delta X
	 * @param dy delta Y
	 * @param mx position of mouse now X
	 * @param my position of mouse now Y
	 * @return Vector representing the displacement of this movement
	 * @author Stephen Larson, @author caprea
	 */
	public PositionVector move(float dx, float dy, int mx, int my)
	{
	
		Vector3f previous = this.getPosition();
		Vector3f desired = pointUnderMouse(mx,my);
		this.setPosition(new PositionVector(desired), true); //implicitly calls changed()
		
		return new PositionVector(desired.subtract(previous));
	
	}
	
	private Vector3f pointUnderMouse(int mx, int my)
	{
//		this method returns the position that the mouse would be located in
		//the world if it had the same distance away from the camera that the selected objects have.
		//takes advantage of JME's Camera.getWorldCoordinates() method.
		
		Tangible manip = this;
		
		//designed to replace manip.move() - which needs to be overwritten in NeuronMorphology
		Quaternion rot = new Quaternion(0,0,0,1);		//for non-coordinated Tangibles
		Quaternion inv = new Quaternion();				//inverse rotation of coordinate system
		Vector3f offset = new Vector3f(0,0,0); 			//offset of origin of coordinate system
		Camera cam = View.getInstance().getCameraView().getCamera();
		
		inv = new Quaternion(rot.inverse());	//inverse the rotation to put things into regular world coordinates
		
		//if the mouse were at the same z-position (relative to the camera) as the average 
		//position of all selected objects, 
		//find its world position in absolute coordinates.
		
		Vector3f fromPos = new Vector3f(manip.getPosition());
		//note: previously used getRelativePosition and inverted its rotation, this caused jumps along Z - dont know why
		
		float dist = cam.getScreenCoordinates(fromPos).getZ();
		
		//a 3D mouse needs an X, Y and a distance where distance is [0,1] where 0 is close
		//detemine that position based on the X,Y and the predicted distance away from the camera
		Vector3f mWorldPos = cam.getWorldCoordinates(new Vector2f(mx, my), dist);
		Vector3f toPos = mWorldPos;
		
		//adjust the new position by the adjustments in the coordinate system
		toPos = toPos.subtract(offset);
		toPos = OMTVector.rotateVector(toPos, inv);
		
		//put it in it's place
		//PositionVector place = new PositionVector(toPos);
		
		return toPos.clone();
	}
	
	
	public String getName() 
	{
		return theSpatial.getName();
	}
	
	/**
	 * meant for calling from on mouse-UP event (such as dragging one tangible onto another) and clean-up code
	 * Not intended for calling changed() or update() - rethink architecture if putting that in here
	 * @param newTarget
	 */
	public void execPostManipulate(Tangible newTarget)
	{
		/** this is NOT meant for an update() or change() **/ //that's not change() we can beleive in, that's more of the same! - Obama '08
	
	}
	
	public void setName(String name) 
	{
		
		this.theSpatial.setName(name);
		changed(CHANGED_NAME);
	}
	

	/**
	 * Returns a list of ContainerTangibles that this Tangible is contained within
	 * @return
	 */
	public Set<ContainerTangible> getContainerTangibles() {
		return TangibleManager.getInstance().getContainerTangibles(this);
	}
	
	/**
	 * Gets the old list of ContainerTangibles for this Tangible
	 * @return
	 */
	public Set<ContainerTangible> getPreviousContainerTangibles() {
		return previousContainerTangibles;
	}
	
	protected void setPreviousContainerTangibles(Set<ContainerTangible> t) {
		previousContainerTangibles = t;
	}
	
	/**
	 * For this Tangible, update the containment information based on a list of ContainerTangibles that
	 * are reporting that they contain this Tangible
	 * @param containerTangibles
	 */
	public void updateContainment(Set<ContainerTangible> containerTangibles) 
	{
		Set<ContainerTangible> currentContainerTangibles = 
			TangibleManager.getInstance().getContainerTangibles(this);
		setPreviousContainerTangibles(currentContainerTangibles);

		boolean changed = false; //one boolean to test if a change has happened
		
		//tangiblesToRemove contains those elements in the list of current containers
		//that do not appear in the update list.  these must go away
		Set<Tangible> tangiblesToRemove = new HashSet<Tangible>(currentContainerTangibles);
		tangiblesToRemove.removeAll(containerTangibles);
		for (Tangible toRemove : tangiblesToRemove) {
			TangibleManager.getInstance().removeContainedTangible(toRemove, this);
			changed = true;
		}
		
		//tangiblesToAdd contains those elements in the update list that do not 
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
	
	public void save()
	{
		DataRepository.getInstance().saveFileToDB(theSpatial);
	}
	
	public void changed() {
		changed(null);
	}

	
	public void changed(String argument) 
	{
		this.setChanged();
		if (argument == null) {
			notifyObservers();
		} else {
			notifyObservers(argument);
		}
	}

}
