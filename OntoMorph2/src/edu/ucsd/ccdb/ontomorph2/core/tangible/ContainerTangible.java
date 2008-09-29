package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

/**
 * Base class for any tangible that is allowed to contain another tangible.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class ContainerTangible extends Tangible {


	public void setRelativePosition(PositionVector pos, boolean flagChanged) {
		if (pos != null) {
			Vector3f oldPosition = getSpatial().getLocalTranslation();
			getSpatial().setLocalTranslation(pos);
			
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
