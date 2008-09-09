package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.math.BigInteger;

/**
 * Defines the smallest unit of an INeuronMorphology.  A segment has a proximal and a distal point,
 * and each has a radius.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface ISegment extends INeuronMorphologyPart{
	
	/**
	 * 
	 * @return the id of the cable this segment belongs to
	 */
	public BigInteger getCableId();

}
