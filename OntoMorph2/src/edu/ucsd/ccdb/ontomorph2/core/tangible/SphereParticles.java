package edu.ucsd.ccdb.ontomorph2.core.tangible;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;
import com.jmex.effects.particles.ParticleMesh;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;



public class SphereParticles extends Tangible 
{
	
	public SphereParticles(String name, Vector3f position) 
	{
		super(name);
		this.setPosition(new PositionVector(position));
	}
		
}
