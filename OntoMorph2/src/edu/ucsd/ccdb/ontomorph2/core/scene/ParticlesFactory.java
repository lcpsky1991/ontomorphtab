package edu.ucsd.ccdb.ontomorph2.core.scene;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.View;

public class ParticlesFactory {

	private static ParticlesFactory instance = null;
	Vector3f location;
	
	public static ParticlesFactory getInstance() {
		if (instance == null) {
			instance = new ParticlesFactory();
		}
		return instance;
	}
	
	private ParticlesFactory(){
		
	}
	
	/**
	 * Create particles at the given location
	 * 
	 * @param location
	 * @return
	 */
	public SphereParticles createParticles(Vector3f location)
	{
		//System.out.println("create Particles");
		this.location = location;
		SphereParticles particles = new SphereParticles("Go Here Particles", this.location);
		particles.setColor(java.awt.Color.blue);
		particles.setVisible(true);
		particles.addObserver(SceneObserver.getInstance());
		
		//cap.setColor(java.awt.Color.orange);
	//	cap.setVisible(true);
		//cap.setModelBinormalWithUpVector(towardcam, 0.01f);	
		//cap.addObserver(SceneObserver.getInstance());
		//cap.changed();
		
		//redraw the scene, but not the whole scene, let observer know the curves have changed
		View.getInstance().getScene().changed(Scene.CHANGED_CURVE);
		return particles;

	}
}
