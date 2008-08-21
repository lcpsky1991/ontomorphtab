package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.morphml.morphml.schema.Segment;
import org.morphml.morphml.schema.Cell.SegmentsType;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * Implementation of ISegment.  
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegment
 * @see ISemanticsAware
 *
 */
public class MorphMLSegmentImpl extends Tangible implements ISegment{
	/**************************************************
	 * Be careful with instances of this class.  The constructor has been made protected
	 * in order to avoid careless instantiation.  This class is basically
	 * just a proxy for the underling Segment representation that ought to be
	 * persisted in the database.  We want to throw away instances of this class 
	 * as quickly as possible, and simply use the setMorphMLSegment method to
	 * get the data about the underlying Segment representation.
	 ***************************************************/

	Segment s = null;
	MorphMLNeuronMorphology parentCell;
	
	protected MorphMLSegmentImpl(MorphMLNeuronMorphology parentCell, Segment s) {
		this.s = s;
		this.parentCell = parentCell;
	}
	
	public float[] getProximalPoint() {
		//if there is no proximal point defined for this segment, look up 
		//the parent segment and get its distal point instead.
		if (s.getProximal() == null){
			Segment ps = this.getSegmentFromId(s.getParent());
			float[] array = {(float)ps.getDistal().getX(), (float)ps.getDistal().getY(), 
					(float)ps.getDistal().getZ()};
			return array;
		}
		float[] array = {(float)s.getProximal().getX(), (float)s.getProximal().getY(), 
				(float)s.getProximal().getZ()};
		return array;
		
	}

	public float[] getDistalPoint() {
		float[] array = {(float)s.getDistal().getX(), (float)s.getDistal().getY(), 
				(float)s.getDistal().getZ()};
		return array;
	}

	public float getProximalRadius() {
//		if there is no proximal point defined for this segment, look up 
		//the parent segment and get its distal radius instead.
		if (s.getProximal() == null){
			Segment ps = this.getSegmentFromId(s.getParent());
			return (float)ps.getDistal().getDiameter() / 2;
		}
		return (float)s.getProximal().getDiameter() / 2;
	}

	public float getDistalRadius() {
		return (float)s.getDistal().getDiameter() / 2;
	}

	/**
	 * Get the id for the cable this segment belongs to
	 */
	public BigInteger getCableId() {
		return s.getCable();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.INeuronMorphologyPart#getId()
	 */
	public BigInteger getId() {
		return s.getId();
	}

	public void select() 
	{
		//select the cable that contains this segment, rather than the segment itself
		this.getParent().getCable(this.getCableId()).select();
	}

	public void unselect() 
	{
		this.getParent().getCable(this.getCableId()).unselect();
	}

	public boolean isSelected() 
	{
		return this.getParent().getCable(this.getCableId()).isSelected();
	}

	public MorphMLNeuronMorphology getParent() {
		return this.parentCell;
	}
	
	public float getBaseRadius() {
		float proximalRadius = 0;
		proximalRadius = this.getProximalRadius();
		return proximalRadius;
	}
	
	public float getApexRadius() {
		float distalRadius = 0;
    	distalRadius = this.getDistalRadius();	
		return distalRadius;
	}
	
	/**
	 * Gets a vector corresponding to the bottom of this segment or the bottom
	 * of the bottom most segment, if multiple segments are used.
	 * @return
	 */
	public OMTVector getBase() {
		OMTVector base = new OMTVector(this.getProximalPoint()[0], 
				this.getProximalPoint()[1], this.getProximalPoint()[2]);

		return base;
	}
	
	/**
	 * Gets a vector corresponding to the top of this segment, or the top
	 * of the top most segment, if multiple segments are used.
	 * @return
	 */
	public OMTVector getApex() {
		OMTVector apex = new OMTVector(this.getDistalPoint()[0], 
				this.getDistalPoint()[1], this.getDistalPoint()[2]);

		return apex;
	}

	public void setMorphMLSegment(Segment s2) {
		this.s = s2;
	}

	/**
	 * Returns 0 beecause a segment does not have any sub parts 
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.INeuronMorphologyPart#getSubPartCount()
	 */
	public int getSubPartCount() {
		return 0;
	}

	/**
	 * Returns null becuause a segment does not have any sub parts
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.INeuronMorphologyPart#getSubPart(int)
	 */
	public INeuronMorphologyPart getSubPart(int i) {
		return null;
	}
	
	private Segment getSegmentFromId(BigInteger id) {
		List segments = this.getSegmentsList();
		for(int i = 0; i < segments.size(); i++ ) {
			Segment s = (Segment)segments.get(i);
			if (s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}
	
	private List getSegmentsList() {
		List segs = new ArrayList();
		List segments = this.getParent().getMorphMLCell().getSegments();
		for (int i = 0; i < segments.size(); i++) {
			segs.addAll(((SegmentsType)segments.get(i)).getSegment());
		}
		return segs;
	}

}
