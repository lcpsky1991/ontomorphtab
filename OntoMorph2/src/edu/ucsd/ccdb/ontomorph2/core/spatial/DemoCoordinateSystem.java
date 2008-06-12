package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

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

}
