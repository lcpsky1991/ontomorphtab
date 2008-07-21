/**
 * 
 */
package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;

/**
 * Visualizes a CurveAnchorPoint as a sphere.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
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
		
		List<Geometry> ll = new ArrayList<Geometry>();
		ll.add(s);
		this.registerGeometries(ll);
		
		this.attachChild(s);
		
		this.update();
	}

	public void update(){
		this.setLocalTranslation(getModel().getAbsolutePosition());
		
		if (this.getModel().isSelected()) 
		{
			this.highlight();
		}
		else 
		{
			this.unhighlight();
		}
		
		s.updateModelBound();
		s.updateRenderState();
		s.updateGeometricState(5f, true);
	}
	
	public void doHighlight() 
	{
		this.s.setDefaultColor(TangibleViewManager.highlightSelectedColor);
	}
	
	public void doUnhighlight() 
	{
		this.s.setDefaultColor(ColorRGBA.white);
	}
}