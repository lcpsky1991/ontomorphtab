package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


/**
 * A coordinate system that defines the particular coordinates of a demo scene.  This will
 * eventually be removed.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DemoCoordinateSystem extends CoordinateSystem {

	public DemoCoordinateSystem() {
		this.setScale(1.0f, 1.0f, 1.0f);
		this.setTranslation(300f, -113f, -180f);
		this.setRotationQuaternion(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Y));
	}

	public float[] getOrigin() {
		float[] origin = {300f, -113f, -180f};
		return origin;
	}
	
	public Quaternion getOriginRotation()
	{
		return new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Y);	
	}

}
