package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * A coordinate system that defines the system used by the Allen Brain Atlas
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class AllenCoordinateSystem extends CoordinateSystem implements  IBrainAtlasCoordinateSystem{
/*
The meshes were extracted from a sagittally-oriented volume 
with 25 micron voxel spacing and dimensions 528 x 320 x 456 
voxels.  The voxel ordering is 528 voxels rostral to caudal, 
320 voxels dorsal to ventral, and 456 voxels lateral left to 
right.  Bregma is at 213, 41, 223.
 */
	
	public AllenCoordinateSystem() {
		this.MAX_X = 528;
		this.MIN_X = 0;
		this.MAX_Y = 320;
		this.MIN_Y = 0;
		this.MAX_Z = 456;
		this.MIN_Z = -456;
		
		this.setRotationQuaternion(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*180, 
				OMTVector.UNIT_X));
	}
		

	public String getBrainRegionName(float x, float y, float z) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	public float[] getXDirection() {
		float[] direction = {Vector3f.UNIT_Z.x, 
				Vector3f.UNIT_Z.y, Vector3f.UNIT_Z.z};
		return direction;
	}
	

	public float[] getYDirection() {
		float[] direction = {Vector3f.UNIT_Y.x, 
				Vector3f.UNIT_Y.y, Vector3f.UNIT_Y.z};
		return direction;
	}
	

	public float[] getZDirection() {
		float[] direction = {Vector3f.UNIT_X.x, 
				Vector3f.UNIT_X.y, Vector3f.UNIT_X.z};
		return direction;
	}
	

	public float[] getOrigin() {
		float[] origin = {0f, 0f, 0f};
		return origin;
	}
	

}
