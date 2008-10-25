package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * Creates Geometry and Selections for Particles
 * 
 * @author jrmartin
 *
 */
public class SphereParticles extends Tangible{
	
	private boolean selectedSphere = false;
	private Tangible previousSelected = null;
	private TangibleManager selected = null;
	public SphereParticles(String name, Vector3f position) 
	{
		
		super(name);
		
		this.setPosition(new PositionVector(position));
		//MouseInput.get().addListener(FocusManager.getInstance());
	}
	
		
	public void select()
	{
		super.select();
		if(this.isVisible()){
			this.setVisible(false);
			previousSelected = this;
		}		
		
		if(previousSelected !=null){
			//previousSelected = selected.getSelectedRecent();
			System.out.println(previousSelected.getName());
			this.previousSelected.setVisible(true);
		}

		View.getInstance().getCameraView().searchZoomTo(TangibleManager.getInstance().getSelectedRecent().getPosition());
	}
	

}
