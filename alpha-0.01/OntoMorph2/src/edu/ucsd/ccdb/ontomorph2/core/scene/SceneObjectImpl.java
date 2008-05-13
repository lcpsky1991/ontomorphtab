package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.Observable;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

public abstract class SceneObjectImpl extends Observable implements ISceneObject{

	IPosition _position = null;
	IRotation _rotation = null;
	float _scale = 1F;
	
	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getRotation()
	 */
	public IRotation getRotation() {
		return _rotation;
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getPosition()
	 */
	public IPosition getPosition() {
		return _position;
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setPosition(src.edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition)
	 */
	public void setPosition(IPosition pos) {
		_position = pos;
		changed();
	}
	
	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setRotation(src.edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation)
	 */
	public void setRotation(IRotation rot) {
		_rotation = rot;
		changed();
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#setScale(float)
	 */
	public void setScale(float f) {
		_scale = f;
		changed();
	}
	

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject#getScale()
	 */
	public float getScale() {
		return _scale;
	}

	
	protected void changed() {
		setChanged();
		notifyObservers();
	}
	

}
