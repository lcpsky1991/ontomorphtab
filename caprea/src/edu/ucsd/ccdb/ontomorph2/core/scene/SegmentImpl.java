package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Implementation of ISegment.  Also aware of semantic tags.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegment
 * @see ISemanticsAware
 *
 */
public class SegmentImpl implements ISegment, ISemanticsAware {

	BigInteger _id;
	float[] _proxPoint;
	float[] _distPoint;
	float _proxRad;
	float _distRad;
	BigInteger _segGroupId;
	Color c = null;
	INeuronMorphology parentCell;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	
	public SegmentImpl(INeuronMorphology parentCell, BigInteger id, float[] proximalPoint, float[] distalPoint, 
			           float proxRadius, float distRadius, BigInteger segGroupId) {
		_id = id;
		_proxPoint = proximalPoint;
		_distPoint = distalPoint;
		_proxRad = proxRadius;
		_distRad = distRadius;
		_segGroupId = segGroupId;
		this.parentCell = parentCell;
	}
	
	public float[] getProximalPoint() {
		return _proxPoint;
	}

	public float[] getDistalPoint() {
		return _distPoint;
	}

	public float getProximalRadius() {
		return _proxRad;
	}

	public float getDistalRadius() {
		return _distRad;
	}

	public BigInteger getSegmentGroupId() {
		return _segGroupId;
	}

	public Color getColor() {
		return this.c;
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

	public void select() {
		getParentCell().selectSegment(this);
	}

	public void unselect() {
		getParentCell().unselectSegment(this);
	}


	public INeuronMorphology getParentCell() {
		return this.parentCell;
	}

	public boolean isSelected() {
		return getParentCell().getSelectedSegments().contains(this);
	}

	public void setColor(Color c) {
		this.c = c;
	}
}
