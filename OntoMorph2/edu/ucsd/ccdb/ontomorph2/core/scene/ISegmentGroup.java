package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.math.BigInteger;
import java.util.List;

public interface ISegmentGroup {

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
	public INeuronMorphology getParentCell();

}