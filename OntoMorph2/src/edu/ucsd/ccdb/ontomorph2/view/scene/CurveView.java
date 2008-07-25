package edu.ucsd.ccdb.ontomorph2.view.scene;


import com.jme.bounding.BoundingBox;
import com.jme.curve.BezierCurve;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;

public class CurveView extends TangibleView {

	Node anchors = null;
	BezierCurve b = null;
	
	public CurveView(Curve3D curve) {
		super(curve);
		super.setName("Curve3DView");
		update();
	}

	
	public void update() {
		
	
		//remove everything at the beginning because we can't be guaranteed to have
		//the same BezierCurve from Curve since it gives us a copy every time.
		//(see Curve3D.asBezierCurve() comment.
		this.detachChild(this.b);
		
		Curve3D curve = ((Curve3D)getModel());
		this.b = (BezierCurve)curve.getCurve();
		
		
		this.attachChild(this.b);
		
		

		if (curve.getAnchorPointsVisibility()) {
			renderAnchorPoints(curve);
		} else {
			this.detachChild(anchors);
			anchors = null;
		}
		//no need to remove anchor points because this is done 
		//automatically already
		if (this.getModel().isSelected()) 
		{
			this.highlight();
		}
		else 
		{
			this.unhighlight();
		}
		
		this.b.setModelBound(new BoundingBox());
		this.b.updateModelBound();
		this.b.updateRenderState();
		this.b.updateGeometricState(5f, true);
		
		if (anchors != null) {
			this.anchors.updateModelBound();
			this.anchors.updateRenderState();
			this.anchors.updateGeometricState(5f, true);
		}
		
		this.updateModelBound();
	    this.updateRenderState();
	    this.updateGeometricState(5f, true);
	}

	private void renderAnchorPoints(Curve3D curve) {
		if (anchors == null) {
			anchors = new Node();
			
			for (CurveAnchorPoint c : curve.getAnchorPoints()) {
				this.attachChild(new CurveAnchorPointView(c));
			}
			this.attachChild(anchors);
		}
	}


	@Override
	public void doHighlight() 
	{
		this.b.setSolidColor(TangibleViewManager.highlightSelectedColor);
	}


	@Override
	public void doUnhighlight() 
	{
		this.b.setSolidColor(ColorUtil.convertColorToColorRGBA(this.getModel().getColor()));
		//this.b.setDefaultColor(ColorUtil.convertColorToColorRGBA(((Curve3D)getModel()).getHighlightedColor()));
	}
	
}
