package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.batch.GeomBatch;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * A base class for the view classes that display Tangibles.
 * 
 * Need to implement right-click bringing up a context menu for each TangibleView
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class TangibleView extends Node {
	//TODO: remove
	//private boolean highlighted = false; //nodes should not be tracking their own selection (ask the manager)
	private Tangible model = null;
	
	public TangibleView(Tangible model) {
		super("Tangible View");
		this.setModel(model);
		//register this instance with the TangibleViewManager
		TangibleViewManager.getInstance().addTangibleView(this);
	}
	/**
	 * Sets the model that corresponds to this TangibleView
	 * @param model
	 */
	private void setModel(Tangible model) {
		this.model = model;
	}
	
	/**
	 * Gets the model that corresponds to this TangibleView
	 * @return
	 */
	public Tangible getModel() {
		return this.model;
	}
	
	/**
	 * Switch the visualization of this ISegmentView to indicate that it has been selected
	 *
	 */
	public final void highlight() {
		//TangibleManager.getInstance().select(this.getModel()); //causes inf recursiveness
		doHighlight();
	}

	/**
	 * Switch the visualization of this ISegmentView to indicate it is not selected
	 *
	 */
	public final void unhighlight() 
	{
		//TangibleManager.getInstance().unselect(this.getModel()); //causes inf recursiveness
		doUnhighlight();
	}
	
	public abstract void doHighlight();
	public abstract void doUnhighlight();

	
	public void update() 
	{
		if (this.getModel().isSelected()) 
		{
			this.highlight();
		}
		else 
		{
			this.unhighlight();
		}
		
		//FIXME: setlocals may need to be changed to setWorld
		setLocalTranslation(this.getModel().getAbsolutePosition());
		setLocalScale(this.getModel().getAbsoluteScale());
		setLocalRotation(this.getModel().getAbsoluteRotation());
	}
	
	public void registerGeometries(List<Geometry> b) {
		for (Geometry gb : b) {
			TangibleViewManager.getInstance().addToGeometryTangibleViewMap(gb, this);
		}
	}
	
	public int hashCode() {
		return super.hashCode() + getModel().hashCode();
	}
}
