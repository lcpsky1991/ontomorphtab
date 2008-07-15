package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Implements an ISegmentGroup. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegmentGroup
 *
 */
public class SegmentGroupImpl extends Tangible implements ISegmentGroup{

	BigInteger id;
	List<ISegment> segments = new ArrayList<ISegment>();
	List<String> tags = new ArrayList<String>();
	NeuronMorphology parentCell = null;
	
	public SegmentGroupImpl(NeuronMorphology parentCell, BigInteger id, List<ISegment> segments, 
			List<String> tags) {
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
			s.setColor(this.getColor());
		}
		return segments;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(this.getSemanticThings());
		for (ISegment sg : this.getSegments()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}

	public NeuronMorphology getParentCell() {
		return this.parentCell;
	}
	
	public void select() {
		super.select();
		SegmentGroupImpl sg = (SegmentGroupImpl)this;
		//this is getting called more times than it should
		String infoString = sg.getTags().toString() + "\n";
		for (ISemanticThing s: sg.getSemanticThings()) {
			infoString += (s.toString() + "\n"); 
		}
		infoString += ((NeuronMorphology)sg.getParentCell()).getSemanticThings();
		View.getInstance().getView2D().setInfoText(infoString);
	}


}
