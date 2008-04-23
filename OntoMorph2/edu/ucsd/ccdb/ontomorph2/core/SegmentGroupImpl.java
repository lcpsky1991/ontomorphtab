package edu.ucsd.ccdb.ontomorph2.core;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SegmentGroupImpl implements ISegmentGroup, ISemanticsAware {

	BigInteger id;
	List<ISegment> segments = new ArrayList<ISegment>();
	List<String> tags = new ArrayList<String>();
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	Color color = null;
	ICell parentCell = null;
	
	public SegmentGroupImpl(ICell parentCell, BigInteger id, List<ISegment> segments, List<String> tags) {
		this.id = id;
		this.segments.addAll(segments);
		this.tags.addAll(tags);
		this.parentCell = parentCell;
	}
	
	public BigInteger getId() {
		return id;
	}

	public List<ISegment> getSegments() {
		return segments;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public List<ISemanticThing> getSemanticThings(){
		return this.semanticThings;
	}
	
	public void addSemanticThing(ISemanticThing thing) {
		this.semanticThings.add(thing);
		thing.addSemanticsAwareAssociation(this);
	}
	
	public void removeSemanticThing(ISemanticThing thing) {
		this.semanticThings.remove(thing);
		thing.removeSemanticsAwareAssociation(this);
	}

	public void setColor(Color color) {
		this.color = color;		
	}
	
	public Color getColor() {
		return this.color;
	}

	public ICell getParentCell() {
		return this.parentCell;
	}

	public void select() {
		getParentCell().getMorphology().selectSegmentGroup(this);
	}

	public void unselect() {
		getParentCell().getMorphology().unselectSegmentGroup(this);
	}

}
