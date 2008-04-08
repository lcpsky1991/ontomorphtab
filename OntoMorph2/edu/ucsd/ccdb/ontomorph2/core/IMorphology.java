package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.view.IStructure3D;

/**
 * @$comment Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space (MorphML?)
 */

public interface IMorphology {
	
	//render options.
	public final static String RENDER_AS_LINES = "lines";
	public final static String RENDER_AS_CYLINDERS = "cylinders";

			public edu.ucsd.ccdb.ontomorph2.core.IRotation lnkIRotation = null;

		public edu.ucsd.ccdb.ontomorph2.core.IPosition lnkIPosition = null;

	public ISegment lnkSegment = null;

	/**
	 * @associates SegmentGroup
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Collection lnkSegmentGroup = null;

	public IPrototype lnkPrototype = null;

	public MorphologyRepository lnkMorphologyRepository = null;

	public URL getMorphML();

	public IRotation getRotation();

	public IPosition getPosition();
	
	public void setPosition(IPosition pos);
	
	public void setRotation(IRotation rot);

	public void setScale(float f);
	
	public void setRenderOption(String s);
	
	public String getRenderOption();

	public float getScale();
}