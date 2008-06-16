package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.WireframeState;

import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;

/**
 * Visualizes an IVolume
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see IVolume
 */
public class VolumeViewImpl {

	IVolume _vol = null;
	
	public VolumeViewImpl(IVolume vol) {
		_vol = vol;
	}

	public Spatial getNode() {
		Node n = new Node();
		if (_vol.isExplicit()) {
			Geometry g = _vol.getExplicitShape();
			/*
			AlphaState as = ViewImpl.getInstance().getRenderer().createAlphaState();
			as.setBlendEnabled(true);
			as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
			as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
			as.setTestEnabled(true);
			as.setTestFunction(AlphaState.TF_GREATER);
			g.setRenderState(as);
			*/
			
			WireframeState ws = ViewImpl.getInstance().getRenderer().createWireframeState();
	        ws.setEnabled(true);

	        g.setRenderState(ws);
			
			if (_vol.isVisible()) {
				n.attachChild(g);
			}
		} else {
			Vector3f p = _vol.getAbsolutePosition().asVector3f();
			if (_vol.getShape() == IVolume.BOX_SHAPE) {
				Box b = new Box("my box", p, 1, 1, 1);
				n.attachChild(b);
			} else if (_vol.getShape() == IVolume.SPHERE_SHAPE) {
				Sphere s = new Sphere("my sphere", p, 10, 10, 1);
				n.attachChild(s);
			}
		}
		return n;
	}

	
	public IVolume getVolume() {
		return _vol;
	}
}
