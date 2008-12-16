package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.net.URL;

import com.jme.scene.Node;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

public abstract class EffectsUtility 
{

	
	/**
	 * Makes the target node have an XRay or Ghost effect to it
	 * @param target
	 * @return True if the apply was successful, False is unsuccessful
	 */
	public static boolean applyEffectGhost(Node target)
	{
		try
		{
			GLSLShaderObjectsState xray = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
	        			
	        URL frag = target.getClass().getResource("xray.frag");
	        URL vert = target.getClass().getResource("xray.vert");
	        
	        xray.load(vert,  frag);  
	        xray.setEnabled(true);
	        xray.setUniform("edgefalloff", 1f);

	        //target.setLightCombineMode(LightState.COMBINE_FIRST);
	        target.setRenderState(xray);
		}
        catch (Exception e) 
        {
        	e.printStackTrace();
        	return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	
	
	
}
