package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;

/**
 * The visual representation of a segment or segment group.  Currently this can either be
 * as a line segment, or as a cylinder.  
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface ISegmentView {

	public boolean correspondsToSegment();
	public boolean correspondsToSegmentGroup();
	public Vector3f getBase();
	public Vector3f getApex();
	public ISegment getCorrespondingSegment();
	public ISegmentGroup getCorrespondingSegmentGroup();
	
	/**
	 * Switch the visualization of this ISegmentView to indicate that it has been selected
	 *
	 */
	public void highlight();
	
	/**
	 * Switch the visualization of this ISegmentView to indicate it is not selected
	 *
	 */
	public void unhighlight();
	
	/**
	 * Return true if this visualization is highlighted, false if it is not
	 * @return
	 */
	public boolean isHighlighted();
	
	/**
	 * Tests if the Geometry g is inside the current visualization of this ISegmentView
	 * @param g
	 * @return true if g is currently visible
	 */
	public boolean containsCurrentGeometry(Geometry g);
	
	/**
	 * Return a node that contains the geometries to visualize this ISegmentView
	 * 
	 * @param renderOption - an option to determine how the ISegmentView should be rendered
	 * @return
	 */
	public Node getViewNode(String renderOption);
}
