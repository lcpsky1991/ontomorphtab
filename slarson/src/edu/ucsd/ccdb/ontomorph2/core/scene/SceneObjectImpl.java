package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.Observable;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;

public abstract class SceneObjectImpl extends Observable implements ISceneObject{

	private PositionVector _position = new PositionVector();
	private RotationVector _rotation = new RotationVector();
	CoordinateSystem sys = null;
	Node theSpatial = new Node();
	private boolean selected = false;
	private boolean _visible = true;
	
	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getRotation()
	 */
	public RotationVector getRelativeRotation() {
		_rotation.set(theSpatial.getLocalRotation());
		return _rotation;
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getPosition()
	 */
	public PositionVector getRelativePosition() {
		_position.set(theSpatial.getLocalTranslation());
		return _position;
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setPosition(src.edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition)
	 */
	public void setRelativePosition(PositionVector pos) {
		if (pos != null) {
			theSpatial.setLocalTranslation(pos);
			changed();
		}
	}
	
	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setRotation(src.edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector)
	 */
	public void setRelativeRotation(RotationVector rot) {
		if (rot != null) {
			theSpatial.setLocalRotation(rot);
			changed();
		}
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setScale(float)
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
	}
}
