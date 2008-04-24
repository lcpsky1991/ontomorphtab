package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.Set;

import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;

/**
 * @$comment Describes the 3D structure of a biological object 
 * in a format that can be easily visualized in a 3D viewer (X3D?)
 */

public interface INeuronMorphologyView {
	
	public Node getNode();
	public INeuronMorphology getMorphology();
	public void updateSelectedSegments(Set<ISegment> segs);
	public void updateSelectedSegmentGroups(Set<ISegmentGroup> sgs);
	public void updateSelected(boolean selected);
}