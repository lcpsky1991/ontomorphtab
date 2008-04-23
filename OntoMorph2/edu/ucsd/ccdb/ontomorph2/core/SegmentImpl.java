package edu.ucsd.ccdb.ontomorph2.core;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SegmentImpl implements ISegment, ISemanticsAware {

	BigInteger _id;
	float[] _proxPoint;
	float[] _distPoint;
	float _proxRad;
	float _distRad;
	BigInteger _segGroupId;
	Color c = null;
	ICell parentCell;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	
	public SegmentImpl(ICell parentCell, BigInteger id, float[] proximalPoint, float[] distalPoint, 
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

	public ICell getParentCell() {
		return this.parentCell;
	}

	public List<ISemanticThing> getSemanticThings() {
		return semanticThings;
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
		getParentCell().getMorphology().selectSegment(this);
	}

	public void unselect() {
		getParentCell().getMorphology().unselectSegment(this);
	}
}
