package edu.ucsd.ccdb.ontomorph2.view.scene;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;

/**
 * The visual representation of a segment or segment group.  Currently this can either be
 * as a line segment, or as a cylinder.  
 * 
 * @author stephen
 *
 */
public interface ISegmentView {

	public boolean correspondsToSegment();
	public boolean correspondsToSegmentGroup();
	public ISegment getCorrespondingSegment();
	public ISegmentGroup getCorrespondingSegmentGroup();
}
