package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

/**
 * A base class for the view classes that display scene objects.
 * 
 * Need to implement right-click bringing up a context menu for each TangibleView
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class TangibleView extends Node {
	private boolean highlighted = false;
	private Tangible model = null;
	
	public void setModel(Tangible model) {
		this.model = model;
	}
	
	public Tangible getModel() {
		return this.model;
	}
	
	/**
	 * Return true if this visualization is highlighted, false if it is not
	 * @return
	 */
	public boolean isHighlighted() {
		return this.highlighted;
	}
	
	/**
	 * Switch the visualization of this ISegmentView to indicate that it has been selected
	 *
	 */
	public void highlight() {
		highlighted = true;
		refreshColor();
	}

	/**
	 * Switch the visualization of this ISegmentView to indicate it is not selected
	 *
	 */
	public void unhighlight() {
		highlighted = false;
		refreshColor();
	}

	protected abstract void refreshColor();
	
}
