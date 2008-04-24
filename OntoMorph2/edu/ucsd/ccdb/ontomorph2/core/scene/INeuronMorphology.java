package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;
import java.util.List;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.misc.IPrototype;
import edu.ucsd.ccdb.ontomorph2.core.misc.MorphologyRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

/**
 * @$comment Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space (MorphML?)
 */

public interface INeuronMorphology {
	
	//render options.
	public final static String RENDER_AS_LINES = "lines";
	public final static String RENDER_AS_CYLINDERS = "cylinders";
	public final static String SPHERES_AT_JOINTS = "spheres";

			public edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation lnkIRotation = null;

		public edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition lnkIPosition = null;

	public ISegment lnkSegment = null;

	/**
	 * @associates SegmentGroup
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Collection lnkSegmentGroup = null;

	public IPrototype lnkPrototype = null;

	public MorphologyRepository lnkMorphologyRepository = null;

	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getMorphMLURL();

	/**
	 * get the IRotation that defines this INeuronMorphology's rotation
	 * @return - the IRotation
	 */
	public IRotation getRotation();

	/**
	 * get the IPosition that defines this INeuronMorphology's position 
	 * @return
	 */
	public IPosition getPosition();
	
	/** 
	 * set the IPosition for this INeuronMorphology
	 * @param pos - desired position
	 */
	public void setPosition(IPosition pos);
	
	/**
	 * set the IRotation for this INeuronMorphology
	 * @param rot - desiredRotation
	 */
	public void setRotation(IRotation rot);

	/**
	 * set the scale for this INeuronMorphology
	 * @param f - scale
	 */
	public void setScale(float f);
	
	public void setRenderOption(String s);
	
	public String getRenderOption();

	public float getScale();
	
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
	
}