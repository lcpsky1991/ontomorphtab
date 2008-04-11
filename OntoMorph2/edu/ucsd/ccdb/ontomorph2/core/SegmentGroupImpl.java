package edu.ucsd.ccdb.ontomorph2.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SegmentGroupImpl implements ISegmentGroup {

	BigInteger id;
	ArrayList<ISegment> segments = new ArrayList<ISegment>();
	ArrayList<String> tags = new ArrayList<String>();
	
	public SegmentGroupImpl(BigInteger id, List<ISegment> segments, List<String> tags) {
		this.id = id;
		this.segments.addAll(segments);
		this.tags.addAll(tags);
	}
	
	public BigInteger getId() {
		return id;
	}

	public List<ISegment> getSegments() {
		return segments;
	}
	
	public List<String> getTags() {
		return tags;
	}

}
