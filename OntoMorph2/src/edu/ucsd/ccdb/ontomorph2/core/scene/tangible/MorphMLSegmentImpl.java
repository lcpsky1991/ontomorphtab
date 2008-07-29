package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import neuroml.generated.Segment;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Implementation of ISegment.  Also aware of semantic tags.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegment
 * @see ISemanticsAware
 *
 */
public class MorphMLSegmentImpl extends Tangible implements ISegment, ISemanticsAware {

	Segment s = null;
	MorphMLNeuronMorphology parentCell;
	
	public MorphMLSegmentImpl(MorphMLNeuronMorphology parentCell, Segment s) {
		this.s = s;
		this.parentCell = parentCell;
	}
	
	public float[] getProximalPoint() {
		//if there is no proximal point defined for this segment, look up 
		//the parent segment and get its distal point instead.
		if (s.getProximal() == null){
			Segment ps = this.parentCell.getSegmentFromId(s.getParent());
			float[] array = {(float)ps.getDistal().getX(), (float)ps.getDistal().getY(), (float)ps.getDistal().getZ()};
			return array;
		}
		float[] array = {(float)s.getProximal().getX(), (float)s.getProximal().getY(), (float)s.getProximal().getZ()};
		return array;
		
	}

	public float[] getDistalPoint() {
		float[] array = {(float)s.getDistal().getX(), (float)s.getDistal().getY(), (float)s.getDistal().getZ()};
		return array;
	}

	public float getProximalRadius() {
//		if there is no proximal point defined for this segment, look up 
		//the parent segment and get its distal radius instead.
		if (s.getProximal() == null){
			Segment ps = this.parentCell.getSegmentFromId(s.getParent());
			return ps.getDistal().getDiameter().floatValue();
		}
		return s.getProximal().getDiameter().floatValue();
	}

	public float getDistalRadius() {
		return s.getDistal().getDiameter().floatValue();
	}

	public BigInteger getSegmentGroupId() {
		return s.getCable();
	}

	public void select() 
	{
		//TODO: call super?
		System.out.println("Not finished seleted for SegmentImpl");
		//getParentCell().select();
		//getParentCell().selectSegment(this);
	}

	public void unselect() 
	{
//		TODO: call super?
		System.out.println("Not finished seleted for SegmentImpl");
		//getParentCell().unselectSegment(this);
		//getParentCell().unselect();
	}

	public boolean isSelected() 
	{
		return getParentCell().isSelected();
	}

	public MorphMLNeuronMorphology getParentCell() {
		return this.parentCell;
	}
}
