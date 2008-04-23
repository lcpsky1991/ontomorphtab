package edu.ucsd.ccdb.ontomorph2.core;

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
	public List<ISemanticThing> getSemanticThings();
	public void addSemanticThing(ISemanticThing thing);
	public void removeSemanticThing(ISemanticThing thing);
	public void setColor(Color color);
	public Color getColor();
	public ICell getParentCell();
	public void select();
	public void unselect();

}