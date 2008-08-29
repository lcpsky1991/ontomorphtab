package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ICable;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
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
				
			}
			else if (arg.equals(Scene.CHANGED_PART))
			{
				msg = "reloading part (all)";
				reloadAll(scene);
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
				System.err.println("Warning in WBC SceneObserver: argument supplied for update scene not accounted for (" + arg +")");
				
			}
		
			Log.warn("Performance Mesg: " + msg);
		}
		
		else if (o instanceof ISemanticThing)
		{
			ISemanticThing st = (ISemanticThing) o;
			for (ISemanticsAware sa : st.getSemanticsAwareAssociations())
			{
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
				
				brv.update();
			}
		}
		
		else if (o instanceof CurveAnchorPoint)
		{
			//TODO: Curve3D now has a getChildrenCells, use that to query cells instead of this
			CurveAnchorPoint point = (CurveAnchorPoint) o;	
			Curve3D changed = point.getParentCurve();
			TangibleView tv = null; //used for updating 

			//now update the anchorpoint itself
			tv = TangibleViewManager.getInstance().getTangibleViewFor((Tangible) o);
			if ( tv != null) tv.update();	//update the anchorpoint
				
			for ( NeuronMorphology c: changed.getChildrenCells())
			{
				c.positionAlongCurve(c.getCurve(), c.getTime());
				tv = TangibleViewManager.getInstance().getTangibleViewFor(c);
				if (tv != null)	tv.update();
			}
		} 
		else if (o instanceof ICable) {

			if (arg.equals(Tangible.CHANGED_SELECT)) {
				ICable cable = (ICable)o;
				NeuronMorphology parent = ((ICable)o).getParent();
				NeuronMorphologyView nmv = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(parent);
				if (nmv != null) {
					nmv.highlightCable(cable.getId());
				}
			} else if (arg.equals(Tangible.CHANGED_UNSELECT)) {
				ICable cable = (ICable)o;
				NeuronMorphology parent = ((ICable)o).getParent();
				NeuronMorphologyView nmv = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(parent);
				if (nmv != null) {
					nmv.unhighlightCable(cable.getId());
				}
			}
		}

		//catch all method for any leftover tangibles
		else if (o instanceof Tangible)
		{
			TangibleView tv = TangibleViewManager.getInstance().getTangibleViewFor((Tangible) o);
			if (tv != null)
			{
				//tv.updateRenderState();
				tv.update();
			}
		}
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

		setCamera(s);
	}
	
	private void setCamera(Scene s) {
		switch(s.getCameraPosition()) {
		case Scene.CAMERA_SLIDE_POSITION :
			_view.getCamera().setToSlideView();
			break;
		case Scene.CAMERA_CELLS_POSITION :
			_view.getCamera().smoothlyZoomToCellView();
			break;
		case Scene.CAMERA_SUBCELLULAR_POSITION :
			_view.getCamera().smoothlyZoomToSubcellularView();
			break;
		case Scene.CAMERA_LATERAL_POSITION :
			_view.getCamera().setToAtlasLateralView();
			break;
		case Scene.CAMERA_MEDIAL_POSITION :
			_view.getCamera().setToAtlasMedialView();
			break;
		case Scene.CAMERA_CEREBELLUM_POSITION :
			_view.getCamera().smoothlyZoomToSlideCerebellumView();
		}
		
	}
	
}
