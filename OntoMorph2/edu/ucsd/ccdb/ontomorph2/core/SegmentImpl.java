package edu.ucsd.ccdb.ontomorph2.core;

import java.math.BigInteger;

public class SegmentImpl implements ISegment {

	BigInteger _id;
	float[] _proxPoint;
	float[] _distPoint;
	float _proxRad;
	float _distRad;
	BigInteger _segGroupId;
	
	public SegmentImpl(BigInteger id, float[] proximalPoint, float[] distalPoint, 
			           float proxRadius, float distRadius, BigInteger segGroupId) {
		_id = id;
		_proxPoint = proximalPoint;
		_distPoint = distalPoint;
		_proxRad = proxRadius;
		_distRad = distRadius;
		_segGroupId = segGroupId;
	}
	
	public float[] getProximalPoint() {
		return _proxPoint;
	}

	public float[] getDistalPoint() {
		return _distPoint;
	}

	public float getProximalRadius() {
		return _proxRad;
	}

	public float getDistalRadius() {
		return _distRad;
	}

	public BigInteger getSegmentGroupId() {
		return _segGroupId;
	}
	
}
