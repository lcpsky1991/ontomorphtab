package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.util.Set;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * Base class for any tangible that is allowed to contain another tangible.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class ContainerTangible extends Tangible {
	
	public ContainerTangible(String name) {
		super(name);
	}


	public void setPosition(PositionVector pos, boolean flagChanged) {
		if (pos != null) {
			OMTVector oldPosition = new PositionVector(getSpatial().getPosition());
			getSpatial().setPosition(pos.toPoint3D());
			
			Vector3f displacement = oldPosition.subtract(pos);
			//in order for contained objects to travel along with its parent, must
			//also set their relative positions
			for (Tangible t: this.getContainedTangibles()) {
				Vector3f newContainedPosition = t.getPosition().subtract(displacement);
				t.setPosition(new PositionVector(newContainedPosition));
			}
			
			if (flagChanged) changed(CHANGED_MOVE);
		}
	}
	
	/**
	 * Adds the parameter to be consider contained within this ContainerTangible
	 * @param contained
	 */
	public void addContainedTangible(Tangible contained) {
		TangibleManager.getInstance().addContainedTangible(this, contained);
		changed(CHANGED_CONTAINS);
	}
	
	/**
	 * Returns the list of tangibles that are contained within this ContainerTangible
	 * @return
	 */
	public Set<Tangible> getContainedTangibles() {
		return TangibleManager.getInstance().getContainedTangibles(this);
	}
	
	/**
	 * Gets rid of a tangible that is currently contained within this ContainerTangible
	 * @param t
	 */
	public void removeContainedTangible(Tangible t) {
		TangibleManager.getInstance().removeContainedTangible(this, t);
		changed(CHANGED_CONTAINS);
	}
	
	
}
