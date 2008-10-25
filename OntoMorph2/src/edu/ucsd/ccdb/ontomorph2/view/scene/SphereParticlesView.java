package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.fenggui.Widget;
import org.fenggui.render.Binding;
import org.fenggui.render.Cursor;
import org.fenggui.render.CursorFactory;
import org.fenggui.text.TextView;
import org.fenggui.util.Point;

import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleMesh;

import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.BasicSearchWidget;
/**
 * Creates Texture and Particles to be displayed as search query indicators.
 * @author jrmartin
 *
 */
public class SphereParticlesView extends TangibleView{

	SphereParticles particles = null;
	private DisplaySystem display = null;
	ParticleGeometry pMesh;
	Quad quad;
	BasicSearchWidget basic = new BasicSearchWidget();
	
	public SphereParticlesView(SphereParticles particles) 
	{
		super(particles);
		this.pickPriority = TangibleView.P_HIGHEST;
		//System.out.println("sphere particles");
		this.particles = particles;
		init();
	}
private AlphaState getAlphaState(){
		
		//System.out.println("alpha state");
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	    as1.setBlendEnabled(true);
	    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	    as1.setDstFunction(AlphaState.DB_ONE);
	    as1.setTestEnabled(true);
	    as1.setTestFunction(AlphaState.TF_GREATER);
	    as1.setEnabled(true);
	    
	    return as1;
	}
	private TextureState getTextureState() 
	{
		//System.out.println("texture staet");
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
		this.detachChild(pMesh);
	
		TextureState st = getTextureState();
		//System.out.println("init");
		pMesh = ParticleFactory.buildParticles("particles", 500);
	    pMesh.setOriginOffset(particles.getPosition());
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
	    this.setRenderState(st);
	    this.setRenderState(getAlphaState());
	                ZBufferState zstate = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
	                zstate.setEnabled(false);
	                pMesh.setRenderState(zstate);
	    pMesh.setModelBound(new BoundingSphere());
	    pMesh.updateModelBound();
	    
//		update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps

		
	    this.registerGeometry(pMesh);
	    this.attachChild(pMesh);

		this.updateRenderState();
		//this.update();
	}
	
	public void setDisplay(DisplaySystem disp) {
		this.display = disp;
		System.out.println("display");
	}
	
	
	//Will do highlight when Mouse Rollover is on top of SphereParticles
	public void doHighlight() 
	{
		this.pMesh.setStartColor(ColorRGBA.orange);
		this.pMesh.setStartColor(ColorRGBA.orange);
	}


	//Will do unhighlist when Mouse is not Rollover on top of SphereParticles
	public void doUnhighlight() 
	{
		this.pMesh.setSolidColor(ColorUtil.convertColorToColorRGBA(this.getModel().getColor()));
		//this.b.setDefaultColor(ColorUtil.convertColorToColorRGBA(((Curve3D)getModel()).getHighlightedColor()));
	}
	
}
