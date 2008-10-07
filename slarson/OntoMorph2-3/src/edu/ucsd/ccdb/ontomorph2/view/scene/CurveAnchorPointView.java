/**
 * 
 */
package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;

/**
 * Visualizes a CurveAnchorPoint as a sphere.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 * @see CurveAnchorPoint
 */
public class CurveAnchorPointView extends TangibleView {
	
	Sphere s = null;
	
	public CurveAnchorPointView(CurveAnchorPoint capt) {
		super(capt);
		super.setName("CurveAnchorPointView");
		this.s =  new Sphere("curve anchor point", new OMTVector(0,0,0), 6, 6, 0.5f);
		s.setModelBound(new BoundingBox());
		s.updateModelBound();	
		this.pickPriority = P_HIGH;
		this.registerGeometry(s);
		this.attachChild(s);
		this.update();
	}

	
	public void doHighlight() 
	{
		this.s.setSolidColor(TangibleViewManager.highlightSelectedColor);	
	}
	
	public void doUnhighlight() 
	{
		this.s.setSolidColor(ColorRGBA.white);
	}
}