package edu.ucsd.ccdb.ontomorph2.view;

import java.util.List;

import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.ISegmentGroup;

/**
 * @$comment Describes the 3D structure of a biological object 
 * in a format that can be easily visualized in a 3D viewer (X3D?)
 */

public interface IStructure3D {
	
	public Node getNode();
	public IMorphology getMorphology();
	public void updateSelectedSegments(List<ISegment> segs);
	public void updateSelectedSegmentGroups(List<ISegmentGroup> sgs);
	public void updateSelected(boolean selected);
}