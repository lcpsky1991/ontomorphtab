package edu.ucsd.ccdb.ontomorph2.core.spatial;

public interface IBrainAtlasCoordinateSystem extends ICoordinateSystem {

	
	
	/**
	 * Returns a string that provides the anatomical name for the brain region
	 * at the point <x,y,z>
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public String getBrainRegionName(float x, float y, float z);
	
	
	/**
	 * Returns a string that indicates the SI type of unit that the coordinates
	 * are expressed in (millimeters, micrometers, nanometers).
	 * 
	 * Ideally this method will return a type rather than a free string
	 * 
	 * @return SI unit of coordinate system.
	 */
	public String getUnits();
}
