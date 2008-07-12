package edu.ucsd.ccdb.ontomorph2.view.scene;


import com.jme.curve.BezierCurve;
import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;

public class CurveView extends TangibleView {

	Node anchors = null;
	BezierCurve b = null;
	
	public CurveView(Curve3D curve) {
		super(curve);
		
		update();
	}

	
	public void update() {
		//remove everything at the beginning because we can't be guaranteed to have
		//the same BezierCurve from Curve since it gives us a copy every time.
		//(see Curve3D.asBezierCurve() comment.
		this.detachAllChildren();
		
		Curve3D curve = ((Curve3D)getModel());
		this.b = (BezierCurve)curve.asBezierCurve();
		this.attachChild(this.b);
		
		if (curve.getAnchorPointsVisibility()) {
			renderAnchorPoints(curve);
		}
		//no need to remove anchor points because this is done 
		//automatically already
		
		
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
		}
		OMTVector[] anchorPoints = curve.getControlPoints();
		
		int i = 0;
		for (OMTVector v : anchorPoints) {
			this.attachChild(new CurveAnchorPointView(new CurveAnchorPoint(curve, v, i++)));
		}
		
		this.attachChild(anchors);
	}


	@Override
	public void doHighlight() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void doUnhighlight() {
		// TODO Auto-generated method stub
		
	}
	
}
