package edu.ucsd.ccdb.ontomorph2.core;

/**
 * Defines the smallest unit of an IMorphology.  A segment has a proximal and a distal point,
 * and each has a radius.
 * 
 * @author stephen
 *
 */
public interface ISegment {
	
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
}