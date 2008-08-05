package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.morphml.morphml.schema.Cable;


import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Implements an ICable. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICable
 *
 */
public class MorphMLCableImpl extends Tangible implements ICable{

	Cable c = null;
	MorphMLNeuronMorphology parentCell = null;
	
	public MorphMLCableImpl(MorphMLNeuronMorphology parentCell, Cable c) {
		this.parentCell = parentCell;
		this.c = c;
	}
	
	public BigInteger getId() {
		return c.getId();
	}

	public List<ISegment> getSegments() {
		List<ISegment> segments = this.parentCell.getMorphMLSegmentsForCableId(this.getId());
		for (ISegment s : segments) {
			s.setColor(this.getColor());
		}
		return segments;
	}
	
	public List<String> getTags() {
		return this.c.getGroup();
	}
	
	
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(this.getSemanticThings());
		for (ISegment sg : this.getSegments()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}

	public MorphMLNeuronMorphology getParentCell() {
		return this.parentCell;
	}
	
	public void select() 
	{
		super.select(); //this.select() is recursion, super.select() to use default selection

		//TODO: move this to the cell select
		String infoString = "";
		try
		{
			MorphMLCableImpl sg = (MorphMLCableImpl)this;

			if (sg != null)
			{
	//			this is getting called more times than it should
				infoString = sg.getTags().toString() + "\n";
				for (ISemanticThing s : sg.getSemanticThings())
				{
					infoString += (s.toString() + "\n"); 
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
