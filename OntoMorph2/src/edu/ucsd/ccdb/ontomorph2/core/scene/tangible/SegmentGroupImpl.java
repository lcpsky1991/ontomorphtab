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
	
	public void select() 
	{
		// if this item has already been selected then we are interested in the CELL, so deslect the segmentgroup and select the cell instead 
		if (this.isSelected())
		{
			super.unselect();
			this.getParentCell().select();	
		}
		else
		{
			
			super.select(); //this.select() is recursion, super.select() to use default selection
		}

		//TODO: move this to the cell select
		String infoString = "";
		try
		{
			SegmentGroupImpl sg = (SegmentGroupImpl)this;

			if (sg != null)
			{
	//			this is getting called more times than it should
				infoString = sg.getTags().toString() + "\n";
				for (ISemanticThing s : sg.getSemanticThings())
				{
					infoString += (s.getLabel() + "\n"); 
				}
				infoString += ((NeuronMorphology)sg.getParentCell()).getSemanticThings();
			}
		}
		catch(Exception e)
		{
			infoString += "\n Exception: " + e.getMessage();
		}
		
		View.getInstance().getView2D().setInfoText(infoString);
	}


}
