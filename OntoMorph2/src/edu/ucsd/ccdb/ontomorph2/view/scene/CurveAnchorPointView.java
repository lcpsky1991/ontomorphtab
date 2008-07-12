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
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

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
		this.s =  new Sphere("curve anchor point", capt.getAbsolutePosition(), 6, 6, 0.5f);
					
		s.setModelBound(new BoundingBox());
		s.updateModelBound();
		
		List<Geometry> ll = new ArrayList<Geometry>();
		ll.add(s);
		this.registerGeometries(ll);
		
		this.attachChild(s);
	}

	public void update(){
		super.update();
		s.setLocalTranslation(this.getModel().getRelativePosition());
	}
	
	public void doHighlight() {
		this.s.setSolidColor(ColorUtil.convertColorToColorRGBA(this.getModel().getColor()));
	}
	
	public void doUnhighlight() {
		this.s.setSolidColor(ColorUtil.convertColorToColorRGBA(this.getModel().getHighlightedColor()));
	}
}