package edu.ucsd.ccdb.ontomorph2.core.spatial;

/**
 * Interface that defines a coordinate system
 * 
 * @author stephen
 *
 */
public interface ICoordinateSystem {

	/* Getter methods to determine the bounds of the coordinate system.
	 * Ideally this is returned from a backend server
	 */
	public float getMinimumXCoordinate();
	public float getMinimumYCoordinate();
	public float getMinimumZCoordinate();
	public float getMaximumXCoordinate();
	public float getMaximumYCoordinate();
	public float getMaximumZCoordinate();

	
	/*
	 * Returns a length 3 float array that contains x, y, and z coordinates
	 * for a unit vector that points in the +X direction for this coordinate 
	 * system
	 */
	public float[] getXDirection();
	
	/*
	 * Returns a length 3 float array that contains x, y, and z coordinates
	 * for a unit vector that points in the +Y direction for this coordinate system
	 */
	public float[] getYDirection();
	
	/*
	 * Returns a length 3 float array that contains x, y, and z coordinates
	 * for a unit vector that points in the +Z direction for this coordinate system
	 */
	public float[] getZDirection();
	
	/*
	 * Returns a length 3 float array that contains x, y, and z coordinates
	 * for the origin of this coordinate system, relative to the absolute 
	 * world coordinates of the rendering system
	 */
	public float[] getOrigin();
}
