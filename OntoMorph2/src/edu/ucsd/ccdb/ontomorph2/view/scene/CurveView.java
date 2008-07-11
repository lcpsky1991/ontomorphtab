package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;

public class CurveView extends TangibleView {

	Node anchors = null;
	
	public CurveView(Curve3D curve) {
		super.setModel(curve);

		update();
	}

	@Override
	protected void refreshColor() {
		// TODO Auto-generated method stub

	}
	
	public void update() {
		//remove everything at the beginning because we can't be guaranteed to have
		//the same BezierCurve from Curve since it gives us a copy every time.
		//(see Curve3D.asBezierCurve() comment.
		this.detachAllChildren();
		
		Curve3D curve = ((Curve3D)getModel());
		this.attachChild(curve.asBezierCurve());
		
		if (curve.getAnchorPointsVisibility()) {
			renderAnchorPoints(curve);
		}
		//no need to remove anchor points because this is done 
		//automatically already
		
		this.updateModelBound();
	}

	private void renderAnchorPoints(Curve3D curve) {
		if (anchors == null) {
			anchors = new Node();
		}
		OMTVector[] anchorPoints = curve.getControlPoints();
		
		for (OMTVector v : anchorPoints) {
			anchors.attachChild(new CurveAnchorPoint(v));
		}
		anchors.updateModelBound();
		this.attachChild(anchors);
	}
	
	protected class CurveAnchorPoint extends TangibleView {
		
		Sphere s = null;
		
		public CurveAnchorPoint(OMTVector position) {
			this.s = new Sphere("curve anchor point", position, 5, 5, 1f);
			s.setModelBound(new BoundingBox());
			s.updateModelBound();
			this.attachChild(s);
		}
		
		public void setHighlighted(boolean highlight) {
			if (highlight) {
				this.s.setSolidColor(ColorRGBA.yellow);
			} else {
				this.s.setSolidColor(ColorRGBA.cyan);
			}
		}

		@Override
		protected void refreshColor() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}
	}
	
}
