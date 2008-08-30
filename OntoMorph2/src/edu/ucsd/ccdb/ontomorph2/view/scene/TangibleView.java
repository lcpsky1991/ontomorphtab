package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.List;

import com.jme.scene.Geometry;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;

/**
 * A base class for the view classes that display Tangibles.
 * 
 * Need to implement right-click bringing up a context menu for each TangibleView
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class TangibleView extends Node {
	private Tangible model = null;
	
	


//	==============================
//	 Picking Constants
//	==============================
	public static final int P_UNPICKABLE = -1;
	public static final int P_UNKNOWN = 0;
	public static final int P_BACKGROUND = 1; //Lowest
	public static final int P_LOW = 2;
	public static final int P_MEDIUM = 3;
	public static final int P_HIGH = 4;
	public static final int P_HIGHEST = 5;
	
	public int pickPriority = P_UNKNOWN;
	
	public TangibleView(Tangible model) 
	{
		super("Tangible View for " + model.getName());
		this.setModel(model);
		pickPriority = P_UNKNOWN;
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
		doHighlight();
	}

	/**
	 * Switch the visualization of this ISegmentView to indicate it is not selected
	 *
	 */
	public final void unhighlight() 
	{
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
	   
		setLocalScale(this.getModel().getAbsoluteScale());
		setLocalTranslation(this.getModel().getAbsolutePosition());
		setLocalRotation(this.getModel().getAbsoluteRotation());
			

		this.updateRenderState();
		this.updateModelBound();
		this.updateGeometricState(0.5f, false);
	}
	
	/**
	 * Take any Geometry instance and associate it with this instance
	 * of TangibleView.  This allows this TangibleView to be retrieved
	 * from the TangibleViewManager when the Geometry is picked.
	 * @param g
	 * @see TangibleViewManager#getTangibleView(Geometry)
	 */
	public void registerGeometry(Geometry g) {
		TangibleViewManager.getInstance().addToGeometryTangibleViewMap(g, this);
	}
	
	public void registerGeometries(List<Geometry> b) {
		for (Geometry gb : b) {
			this.registerGeometry(gb);
		}
	}
	
	public int hashCode() {
		return super.hashCode() + getModel().hashCode();
	}
}
