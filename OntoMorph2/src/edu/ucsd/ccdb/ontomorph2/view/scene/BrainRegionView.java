package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.net.URL;

import org.lwjgl.opengl.Display;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Manages the visual rendering of a BrainRegion.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class BrainRegionView extends TangibleView{

	LightState lightState = null;
	TriMesh mesh = null;
	int defaultRenderQueueMode = 0;
	
	/**
	 * Create a new BrainRegionView based on a BrainRegion model that will be associated with
	 * it and the parentNode that it ought to be contained in
	 * @param br
	 * @param parentNode
	 */
	public BrainRegionView(BrainRegion br) {
		super(br);
		super.setName("BrainRegionView");
		
		this.mesh = br.getData();
		this.mesh.setName("Trimesh for brain region: " + br.getAbbreviation());
		//mesh.setSolidColor(ColorRGBA.blue);
		mesh.setModelBound(new BoundingBox());
		VBOInfo nfo = new VBOInfo(true);
		mesh.setVBOInfo(nfo);
		mesh.setCullMode(SceneElement.CULL_DYNAMIC);
		
		this.lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        this.lightState.setEnabled(true);
        
        MaterialState ms = View.getInstance().getDisplaySystem().getRenderer().createMaterialState();
        
        ColorRGBA brColor = ColorUtil.convertColorToColorRGBA(br.getColor());
        
        ms.setDiffuse(brColor);
        ms.setSpecular(brColor);
        ms.setAmbient(brColor);
        ms.setEmissive(brColor);
        ms.setShininess(0.5f);
		//mesh.setRenderState(ms);
        
        
        //===== make it appealing ===========
        //applies a ghost effect to the mesh
		try
		{
			mesh.clearRenderState(0);
			GLSLShaderObjectsState xray = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
	        			
	        URL frag = this.getClass().getResource("xray.frag");
	        URL vert = this.getClass().getResource("xray.vert");
	        
	        xray.load(vert,  frag);  
	        xray.setEnabled(true);
	        xray.setUniform("edgefalloff", 1f);

	        mesh.setLightCombineMode(LightState.COMBINE_FIRST);
	        mesh.setRenderState(xray);
		}
        catch (Exception e) 
        {
        	System.out.println("Xray failed"); 
        	e.printStackTrace();
		}

        
        //==================================
        
        
        //register the geometries
		//update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps
        this.registerGeometry(this.mesh);
        
        this.defaultRenderQueueMode = this.getRenderQueueMode();
        
		this.pickPriority = P_UNPICKABLE;
		
		this.update();
	}
	
	public BrainRegion getBrainRegion() {
		return (BrainRegion)this.getModel();
	}
	
	/**
	 * Refresh this view based on the current state of the associated BrainRegion model.
	 *
	 */
	public void update() {
		super.update();
		if (mesh != null) {
			switch(getBrainRegion().getVisibility()) {
			case BrainRegion.VISIBLE:
				makeVisible();
				makeSolid();
				
				break;
			case BrainRegion.INVISIBLE:
				//make invisible
				this.detachChild(this.mesh);
				
				break;
			case BrainRegion.TRANSPARENT:
				makeVisible();
				makeTransparent();
				break;
			}
			
			
			if (getBrainRegion().isSelected()) {
				this.highlight();
			} else {
				this.unhighlight();
			}
			

			this.mesh.updateModelBound();
			this.mesh.updateRenderState();
			this.mesh.updateGeometricState(0.5f, true);
		}
	}
	
	private void makeVisible() {
		this.detachChild(this.mesh);
		this.attachChild(this.mesh);
	}
	
	private void makeTransparent() {
		//disable writing to zbuffer
		ZBufferState zb = View.getInstance().getRenderer().createZBufferState();
		zb.setWritable(false);
		zb.setEnabled(true);
		this.setRenderState(zb);
		
		//enable alpha blending
		AlphaState as = View.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);      
	      as.setDstFunction(AlphaState.DB_ONE);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    this.setRenderState(as);
	    this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	}
	
	private void makeSolid() {
		//enable writing to zbuffer
		ZBufferState zb = View.getInstance().getRenderer().createZBufferState();
		zb.setWritable(true);
		zb.setFunction(ZBufferState.CF_LEQUAL);
		zb.setEnabled(true);
		this.setRenderState(zb);
		
		//create new alpha state with blending disabled
		AlphaState as = View.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(false);
	      as.setTestEnabled(false);
	      as.setEnabled(false);
	    this.setRenderState(as);
		//this.setRenderState(lightState);  	    
		this.setRenderQueueMode(this.defaultRenderQueueMode);
	}
	
	
	/*
	public BatchMesh getMesh() {
		if (mesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			loader.setColor(this.color);
			this.mesh = loader.loadByAbbreviation(this.getAbbreviation());

			if (this.getCoordinateSystem() != null) {
				if (this.getAbsolutePosition() != null) {
					this.mesh.setLocalTranslation(this.getAbsolutePosition());
				} 
				if (this.getAbsoluteRotation() != null) {
					this.mesh.setLocalRotation(this.getAbsoluteRotation());
				}
				if (this.getAbsoluteScale() != null) {
					this.mesh.setLocalScale(this.getAbsoluteScale());
				}
			}
		}
		return mesh;
	}

	public AreaClodMesh getClodMesh() {
		if (aMesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			loader.setColor(this.color);
			this.aMesh = loader.loadClodMeshByAbbreviation(this.getAbbreviation());
			if (this.getCoordinateSystem() != null) {
				if (this.getAbsolutePosition() != null) {
					this.aMesh.setLocalTranslation(this.getAbsolutePosition());
				} 
				if (this.getAbsoluteRotation() != null) {
					this.aMesh.setLocalRotation(this.getAbsoluteRotation());
				}
				if (this.getAbsoluteScale() != null) {
					this.aMesh.setLocalScale(this.getAbsoluteScale());
				}
			}
		}
		return aMesh;
	}*/
	
	

	@Override
	public void doHighlight() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUnhighlight() {
		// TODO Auto-generated method stub
		
	}

}
