package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.math.BigInteger;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines the smallest unit of an INeuronMorphology.  A segment has a proximal and a distal point,
 * and each has a radius.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface ISegment extends ISemanticsAware{
	
	/**
	 * 
	 * @return a 3-dimensional float array defining the proximal point.  x=0, y=1, z=2.
	 */
	public float[] getProximalPoint();
	
	/**
	 * 
	 * @return a 3-dimensional float array defining the distal point.  x=0, y=1, z=2.
	 */
	public float[] getDistalPoint();
	
	/**
	 * 
	 * @return a float value representing the proximal radius
	 */
	public float getProximalRadius();
	
	/**
	 * 
	 * @return a float value representing the distal radius
	 */
	public float getDistalRadius();
	
	/**
	 * 
	 * @return the id of the larger segment group this segment belongs to
	 */
	public BigInteger getSegmentGroupId();

	public Color getColor();
	
	public void setColor(Color c);
	
	public NeuronMorphology getParentCell();

}
