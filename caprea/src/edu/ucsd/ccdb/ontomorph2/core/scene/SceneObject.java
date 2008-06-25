package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;

/**
 * Defines an object that can be viewed in the scene.  This object can be made relative
 * to an arbitrary coordinate system.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICoordinateSystem
 *
 */
public abstract class SceneObject extends Observable implements ISelectable, ISemanticsAware{

	private PositionVector _position = new PositionVector();
	private RotationVector _rotation = new RotationVector();
	private CoordinateSystem sys = null;
	private Node theSpatial = new Node();
	private boolean selected = false;
	private boolean _visible = true;
	private List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();

	private Color c = null;
	private Color highlightedColor = null;
	
	public SceneObject() {
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
	 * @return
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
	 */
	public void setRelativeRotation(RotationVector rot) {
		if (rot != null) {
			theSpatial.setLocalRotation(rot);
			changed();
		}
	}

	/**
	 * set the scale for this INeuronMorphology
	 * @param v - scale
	 */
	public void setRelativeScale(OMTVector v) {
		theSpatial.setLocalScale(v);
		changed();
	}
	
	public void setRelativeScale(float f) {
		theSpatial.setLocalScale(f);
	}
	

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getScale()
	 */
	public OMTVector getRelativeScale() {
		return new OMTVector(theSpatial.getLocalScale());
	}
	
	/**
	 * get the RotationVector that defines this INeuronMorphology's rotation.
	 * This is always in the absolute world coordinate system.
	 * @return - the RotationVector
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
	 * @return
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
	
	public void setCoordinateSystem(CoordinateSystem sys) {
		this.sys = sys;
	}
	
	public CoordinateSystem getCoordinateSystem() {
		return this.sys;
	}
	
	public void select() {
		this.selected = true;		
		changed();
	}
	
	public boolean isSelected() {
		//return SceneObjectManager.getInstance().isSelected(this);
		return this.selected;
	}

	public void unselect() {
		this.selected = false;
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
}
