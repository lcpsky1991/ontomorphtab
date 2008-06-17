package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;
import java.util.List;
import java.util.Set;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

/**
 * Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space (MorphML?)
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public interface INeuronMorphology extends ISceneObject{
	
	//render options.
	public final static String RENDER_AS_LINES = "lines";
	public final static String RENDER_AS_CYLINDERS = "cylinders";
	public final static String RENDER_AS_LOD = "lod";
	public final static String RENDER_AS_LOD_2 = "lod2";
	public final static String SPHERES_AT_JOINTS = "spheres";

	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getMorphMLURL();

	public void setRenderOption(String s);
	
	public String getRenderOption();

	public List<ISegment> getSegments();
	
	public void selectSegmentGroup(ISegmentGroup g);
	
	public void unselectSegmentGroup(ISegmentGroup g);
	
	/** 
	 * Convenience method to select a segment within this INeuronMorphology
	 * @param s - the segment to select
	 */
	public void selectSegment(ISegment s);
	
	/** 
	 * Convenience method to unselect a segment within this INeuronMorphology
	 * @param s - the segmen to unselect
	 */
	public void unselectSegment(ISegment s);
	
	/**
	 * 
	 * @return all ISegment's that are currently selected
	 */
	public Set<ISegment> getSelectedSegments();
	
	/**
	 * 
	 * @return all ISegmentGroups that are currently selected
	 */
	public Set<ISegmentGroup> getSelectedSegmentGroups();
	
	/**
	 * 
	 * @return true if this INeuronMorphology has ISegmentGroups that are selected, false otherwise
	 */
	public boolean hasSelectedSegmentGroups();
	
	/**
	 * 
	 * @return the ISegmentGroups that are associated with this INeuronMorphology
	 */
	public Set<ISegmentGroup> getSegmentGroups();
	
	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c
	 *
	 */
	public void positionAlongCurve(Curve3D c, float time);
	
	/**
	 * Rotates the NeuronMorphology to aim its 'up' direction towards p
	 * @param p
	 */
	public void lookAt(PositionVector p);
	
	public PositionVector getLookAtPosition();

	public String getName();

	/**
	 * Get the ICurve that this INeuronMorphology has been associated with
	 * @return
	 */
	public Curve3D getCurve();
	
	/**
	 * Retrieves the "time" along the curve that this INeuronMorphology is positioned at
	 * @return
	 */
	public float getTime();
	
	public void setUpVector(Vector3f vector3f);
	
	public Vector3f getUpVector();

}
