package edu.ucsd.ccdb.ontomorph2.core.tangible;

import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.scene.SphereParticlesView;



public class SphereParticles extends Tangible {
	SphereParticles particles = null;
	private DisplaySystem display = null;
	ParticleMesh pMesh;
	Quad quad;
	
	public SphereParticles(String name, Vector3f position) {
		super();
		this.setRelativePosition(new PositionVector(position));
		setName(name);	
	}
		
}
