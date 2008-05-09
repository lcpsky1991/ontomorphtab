package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

public class SegmentGroupImpl implements ISegmentGroup, ISemanticsAware{

	BigInteger id;
	List<ISegment> segments = new ArrayList<ISegment>();
	List<String> tags = new ArrayList<String>();
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	Color color = null;
	INeuronMorphology parentCell = null;
	
	public SegmentGroupImpl(INeuronMorphology parentCell, BigInteger id, List<ISegment> segments, List<String> tags) {
		this.id = id;
		
		this.segments.addAll(segments);
		this.tags.addAll(tags);
		this.parentCell = parentCell;
	}
	
	public BigInteger getId() {
		return id;
	}

	public List<ISegment> getSegments() {
		for (ISegment s : segments) {
			s.setColor(color);
		}
		return segments;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	
	public List<ISemanticThing> getSemanticThings() {
		return this.semanticThings;
	}
	
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(this.semanticThings);
		for (ISegment sg : this.getSegments()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
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

	public void select() {
		getParentCell().selectSegmentGroup(this);
	}

	public void unselect() {
		getParentCell().unselectSegmentGroup(this);
	}

	public INeuronMorphology getParentCell() {
		return this.parentCell;
	}

	public boolean isSelected() {
		return getParentCell().getSelectedSegmentGroups().contains(this);
	}

}
