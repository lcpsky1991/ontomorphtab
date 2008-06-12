package edu.ucsd.ccdb.ontomorph2.util;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

public abstract class AbstractVegetation extends Node {
	protected Camera cam;
	protected float viewDistance;

	public AbstractVegetation( String string, Camera cam, float viewDistance ) {
		super( string );
		this.cam = cam;
		this.viewDistance = viewDistance;
	}

	public void initialize() {
	}

	public abstract void addVegetationObject( Spatial target, Vector3f translation, Vector3f scale, Quaternion rotation );

	public void setup() {
	}
}