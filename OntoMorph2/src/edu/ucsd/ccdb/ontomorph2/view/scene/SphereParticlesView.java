package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.TiledSlide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.URISlide;
import edu.ucsd.ccdb.ontomorph2.view.View;

public class SphereParticlesView extends TangibleView{

	SphereParticles particles = null;
	private DisplaySystem display = null;
	ParticleMesh pMesh;
	Quad quad;
	
	public SphereParticlesView(SphereParticles particles) {
		super(particles);
		this.particles = particles;
		init();
	}

	public SphereParticlesView(SphereParticles particles, DisplaySystem disp) {
		super(particles);
		this.particles = particles;
		setDisplay(disp);
		init();
	}
	
	private AlphaState getAlphaState(){
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	    as1.setBlendEnabled(true);
	    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	    as1.setDstFunction(AlphaState.DB_ONE);
	    as1.setTestEnabled(true);
	    as1.setTestFunction(AlphaState.TF_GREATER);
	    as1.setEnabled(true);
	    
	    return as1;
	}
	private TextureState getTextureState() {
		
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    ts.setTexture(
	        TextureManager.loadTexture(
	        SphereParticlesView.class.getClassLoader().getResource(
	        "jmetest/data/texture/flaresmall.jpg"),
	        Texture.MM_LINEAR_LINEAR,
	        Texture.FM_LINEAR));
	    ts.setEnabled(true);
		return ts;
	}
	
	private void init() {
		pMesh = ParticleFactory.buildParticles("particles", 500);
	    //Mesh.setOriginOffset();
	    pMesh.setEmissionDirection(new Vector3f(0,1,0));
	    pMesh.setInitialVelocity(.006f);
	    pMesh.setStartSize(2.5f);
	    pMesh.setEndSize(2.5f);
	    pMesh.setMinimumLifeTime(1200f);
	    pMesh.setMaximumLifeTime(1400f);
	    pMesh.setStartColor(new ColorRGBA(1, 1, 1, 1));
	    pMesh.setEndColor(new ColorRGBA(0, 1, 1, 0));
	    pMesh.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
	    pMesh.getParticleController().setControlFlow(false);
	    pMesh.warmUp(60);
	    quad.setRenderState(getTextureState());
	    quad.setRenderState(getAlphaState());
	                ZBufferState zstate = display.getRenderer().createZBufferState();
	                zstate.setEnabled(false);
	                pMesh.setRenderState(zstate);
	    pMesh.setModelBound(new BoundingSphere());
	    pMesh.updateModelBound();
	    
	    if (particles.isVisible()) {
			this.attachChild(quad);
		}
		
		this.updateRenderState();
		
//		update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps
		this.registerGeometry(quad);
		
		this.update();
	}
	
	public void setDisplay(DisplaySystem disp) {
		this.display = disp;
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
