package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.Set;

import com.jme.scene.Geometry;
import com.jme.scene.Spatial;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines a volume of space, identified by the boundaries of a box or a sphere.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface IVolume extends ISceneObject {

	public static final int BOX_SHAPE = 0;
	public static final int SPHERE_SHAPE = 1;
	
	/**
	 * 
	 * @return Any ISemanticsAware objects that are geometrically inside this IVolume
	 */
	public Set<ISemanticsAware> getContainedSemanticalObjects();
	
	/**
	 * Get the shape of this IVolume
	 * @return A shape token defined in IVolume
	 */
	public int getShape();
	
	/**
	 * Set the shape of this IVolume
	 * @param shape - defined in IVolume
	 */
	public void setShape(int shape);
	
	/**
	 * Explicitly set the shape of this volume
	 * @param g - a geometrical shape for this volume
	 */
	public void setExplicitShape(Geometry g);
	
	/**
	 * Get the explicit shape of this volume
	 * @return - the explicit shape
	 */
	public Geometry getExplicitShape();
	
	/**
	 * Determine if this shape is represented explicitly or not
	 * @return true if explicit, false if not
	 */
	public boolean isExplicit();
	
	public boolean containsObject(Spatial s);

	public boolean isVisible();
	
	public void setVisible(boolean b);
}
