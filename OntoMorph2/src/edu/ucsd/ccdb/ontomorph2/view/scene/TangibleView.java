package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Box;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.geom.Debugger;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * A base class for the view classes that display Tangibles.  
 * TangibleViews are managed by the TangibleViewManager.  
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class TangibleView extends Node 
{
	private Tangible model = null;
	Node nEffects = new Node("effects");	//for cursor over, highlighting, animation, etc
	
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
		super("UnInited Tangible View");
		
		if (model != null) this.setName("Tangible View for " + model.getName());
		
		this.setModel(model);
		pickPriority = P_UNKNOWN;
		
		//register this instance with the TangibleViewManager
		TangibleViewManager.getInstance().addTangibleView(this);
		
		//add the effects node to this
		this.attachChild(nEffects);
		
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
	
	
	public void showBounds()
	{
		return;
		
		//Debugger.drawBounds(this, View.getInstance().getRenderer(), false);
		
		//BoundingBox bounds = (BoundingBox) this.getWorldBound();

		//System.out.println(this.getLocalTranslation());
		//Debugger.drawBounds(this, View.getInstance().getRenderer(), true);
		
		
		/*
		Vector3f size = new Vector3f();
		if (bounds != null)
		{
			float scale = this.getWorldScale().length();
			size = bounds.getExtent(size);
			
			
			Box wirebox = new Box("bbox", new Vector3f(), size.x /scale, size.y /scale , size.z /scale);
			
			wirebox.setLocalRotation(this.getLocalRotation());
			
			
			ZBufferState zt = View.getInstance().getRenderer().createZBufferState();
			WireframeState wt = View.getInstance().getRenderer().createWireframeState();
			
			wirebox.setRenderState(wt);
			wirebox.setRenderState(zt);
	        wirebox.updateRenderState();
	        nEffects.attachChild(wirebox);	
		}
		*/
        
	}
	
	/**
	 * Switch the visualization of this TangibleView to indicate that it has been selected
	 *
	 */
	public final void highlight() 
	{
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
			showBounds();
		}
		else 
		{
			this.unhighlight();
			nEffects.detachAllChildren();
		}
		
		//FIXME: setlocals may need to be changed to setWorld
	   
		if (this.getModel().getScale() != null) {
			setLocalScale(this.getModel().getScale());
		}
		
		if (this.getModel().getPosition() != null) 
		{
			setLocalTranslation(this.getModel().getPosition());
		}
		
		if (this.getModel().getRotation() != null) {
			setLocalRotation(this.getModel().getRotation());
		}
			
		//make it invisible if needbe
		if ( this.getModel().isVisible() )
		{
			this.setCullMode(SceneElement.CULL_DYNAMIC); //visible
		}
		else
		{
			this.setCullMode(SceneElement.CULL_ALWAYS); //inviisble
		}

				
		this.updateModelBound();
		this.updateRenderState();
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
	
	public boolean equals(Object o) 
	{
		return this.hashCode() == o.hashCode();
	}
	
	public int hashCode() 
	{
		return super.hashCode() + getModel().hashCode();
	}
}
