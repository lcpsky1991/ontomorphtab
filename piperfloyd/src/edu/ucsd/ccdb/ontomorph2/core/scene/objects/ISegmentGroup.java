package edu.ucsd.ccdb.ontomorph2.core.scene.objects;

import java.awt.Color;
import java.math.BigInteger;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines a group of segments in a neuron morphology.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegment
 * @see INeuronMorphology
 *
 */
public interface ISegmentGroup extends ISemanticsAware{

	/**
	 * @link aggregation
	 * @associates Segment
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkSegment = null;
	
	public BigInteger getId();
	public List<ISegment> getSegments();
	public List<String> getTags();
	public void setColor(Color color);
	public Color getColor();
	public NeuronMorphology getParentCell();

}
