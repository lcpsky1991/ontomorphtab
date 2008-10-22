package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.INeuronMorphologyPart;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * This main observer is triggered when any scene object changes and updates the
 * view accordingly.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class SceneObserver implements Observer {

	View _view;

	/**
	 * Holds singleton instance
	 */
	private static SceneObserver instance;

	public void update(Observable o, Object arg)
	{
		if (o instanceof Scene)
		{
			String msg = "";
			Scene scene = (Scene) o;
			
			//System.out.println(" o instance of Scene");
			_view.getView3D().addParticles(scene.getParticles());
//			setting arg to not be null simplifies error checking (dont need to check for null cases)
			if (arg == null) arg = Scene.CHANGED_UNKNOWN;  
			
				//========= CURVES	=======================
			if ( arg.equals(Scene.CHANGED_CURVE))
			{
				_view.getView3D().setCurves(scene.getCurves());
				msg = "reloading curves";
			}
				//============ LOAD ==============
			else if (arg.equals(Scene.CHANGED_LOAD) ) 
			{
				msg = "(re)loading entire scene";
				reloadAll(scene);
			}
			else if (arg.equals(Scene.CHANGED_CELL))
			{
				msg = "adding cells";
				_view.getView3D().setCells(scene.getCells());
			}
			else if (arg.equals(Scene.CHANGED_PART))
			{
				msg = "reloading part (all)";
				reloadAll(scene);
			}
			else if (arg.equals(Scene.CHANGED_SLIDE))
			{
				//System.out.println("reloading slides");
				_view.getView3D().setSlides(scene.getSlides());
				msg = "reloading slides";
			}
			else if (arg.equals(Scene.CHANGED_TEST))
			{
				_view.getView3D().setCurves(scene.getCurves());
				_view.getView3D().setCells(scene.getCells());
				msg = "reloading test";
			}
			
				//======== DEFAULT ===========
			else	
			{
				//Default case for reloading entire scene 
				Log.warn("Warning in WBC SceneObserver: argument supplied for update scene not accounted for (" + arg +")");
				
			}
		
			Log.warn("Performance Mesg: " + msg);
		}
		
		else if (o instanceof SemanticThing)
		{
			SemanticThing st = (SemanticThing) o;
			
			ISemanticsAware sa = st.getSemanticsAwareAssociation();
			if (sa != null) {
				
				if (st.isSelected())
				{
					sa.select();
				}
				else if (!st.isSelected())
				{
					sa.unselect();
				}
			}
		}
		else if (o instanceof BrainRegion)
		{
			BrainRegionView brv = (BrainRegionView) TangibleViewManager
			        .getInstance().getTangibleViewFor((BrainRegion) o);
			if (Tangible.CHANGED_VISIBLE.equals(arg))
			{
				if (brv == null) {
					Set s = new HashSet();
					s.add(o);
					View.getInstance().getView3D().addBrainRegions(s);
					
					brv = (BrainRegionView) TangibleViewManager
			        .getInstance().getTangibleViewFor((BrainRegion) o);
				} 
				
				//brv.update(); //will be called as the code falls-through to Tangible case
			}
		}
		

		else if (o instanceof INeuronMorphologyPart) {

			if (Tangible.CHANGED_SELECT.equals(arg)) {
				INeuronMorphologyPart cable = (INeuronMorphologyPart)o;
				NeuronMorphology parent = ((INeuronMorphologyPart)o).getParent();
				NeuronMorphologyView nmv = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(parent);
				if (nmv != null) {
					nmv.highlightCable(cable.getId());
				}
			} else if (Tangible.CHANGED_UNSELECT.equals(arg)) {
				INeuronMorphologyPart cable = (INeuronMorphologyPart)o;
				NeuronMorphology parent = ((INeuronMorphologyPart)o).getParent();
				NeuronMorphologyView nmv = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(parent);
				if (nmv != null) {
					nmv.unhighlightCable(cable.getId());
				}
			}
		}
		else if (o instanceof Slide)
		{
			SlideView sv = (SlideView) TangibleViewManager.getInstance().getTangibleViewFor((Slide)o);
			
			if ( sv != null && Tangible.CHANGED_COLOR.equals(arg))
			{
				sv.redrawTexture();
			}
		}
		
		
		//catch all method for any leftover tangibles
		if (o instanceof Tangible)
		{
			//System.out.println("tangible sceneobserver");
			Tangible t = (Tangible)o;
			
			//get the tangible view manager that holds on to the list of tangible views
			TangibleViewManager tvm = TangibleViewManager.getInstance();
			
			//get the tangible view that corresponds to the current tangible
			TangibleView tv = tvm.getTangibleViewFor(t);
			
			if (tv == null) 
			{
				Log.warn(("TV for " + t.getName() + " not found"));
				return; 
			}
			
			//if we have moved, test to see if any tangibles contain any other tangibles now
			//this code is required to do containment operations.   We need to find
			//another way of improving performance beyond commenting it out because it
			//is core functionality.
			if (Tangible.CHANGED_MOVE.equals(arg)) 
			{
				Set<ContainerTangible> containerTangibles = tvm.getContainerTangibles(tv);
				t.updateContainment(containerTangibles);				
			}
			
			//remove ttangible
			if (Tangible.CHANGED_DELETE.equals(arg))
			{
				TangibleManager.getInstance().removeTangible(t);
				TangibleViewManager.getInstance().removeTangibleView(tv);
				tv.detachAllChildren();
				tv.removeFromParent();
				//t = null;
				//tv = null;			
				//return; //do not execute the update() as usual because the object is null
			}
			
			tv.update();
		}
		
		
		
		//probably good to do this on every change
		//_view.getView3D().updateRoot(); //commented out to drasticly improve curve reloading performance
	}

	/**
	 * prevents instantiation
	 */
	private SceneObserver()
	{
		// prevent creation
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	static public SceneObserver getInstance()
	{
		if (instance == null)
		{
			instance = new SceneObserver();
		}
		return instance;
	}

	public void setView(View view)
	{
		_view = view;
	}

	public void reloadAll(Scene s)
	{
		
		_view.getView3D().setSlides(s.getSlides());
		_view.getView3D().setVolumes(s.getVolumes());
		_view.getView3D().setCells(s.getCells());
		_view.getView3D().setCurves(s.getCurves());
		_view.getView3D().setSurfaces(s.getSurfaces());
		_view.getView3D().setMeshes(s.getMeshes());
		_view.getView3D().addParticles(s.getParticles());
		setCamera(s);
		_view.getView3D().updateNode(_view.getView3D());
	}
	
	private void setCamera(Scene s) {
		switch(s.getCameraPosition()) {
		case Scene.CAMERA_SLIDE_POSITION :
			_view.getCameraView().setToSlideView();
			break;
		case Scene.CAMERA_CELLS_POSITION :
			_view.getCameraView().smoothlyZoomToCellView();
			break;
		case Scene.CAMERA_SUBCELLULAR_POSITION :
			_view.getCameraView().smoothlyZoomToSubcellularView();
			break;
		case Scene.CAMERA_LATERAL_POSITION :
			_view.getCameraView().setToAtlasLateralView();
			break;
		case Scene.CAMERA_MEDIAL_POSITION :
			_view.getCameraView().setToAtlasMedialView();
			break;
		case Scene.CAMERA_CEREBELLUM_POSITION :
			_view.getCameraView().smoothlyZoomToSlideCerebellumView();
		}
		
	}
	
}
