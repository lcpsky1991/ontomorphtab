package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.objects.SceneObject;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

public abstract class SceneObjectView extends Node {
	private boolean highlighted = false;
	private SceneObject model = null;
	
	public void setModel(SceneObject model) {
		this.model = model;
	}
	
	public SceneObject getModel() {
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
