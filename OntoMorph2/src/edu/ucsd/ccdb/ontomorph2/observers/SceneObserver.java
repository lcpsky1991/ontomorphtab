package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.SegmentGroupImpl;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveAnchorPointView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveView;
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
			
//			setting arg to not be null simplifies error checking (dont need to check for null cases)
			if (arg == null) arg = Scene.CHANGED_UNKNOWN;  
			
				//========= CURVES	=======================
			if ( arg.equals(Scene.CHANGED_CURVE))
			{
				Scene origscene = (Scene) o;
				_view.getView3D().setCurves(origscene.getCurves());
				msg = "reloading curves";
			}
				//============ LOAD ==============
			else if (arg.equals(Scene.CHANGED_LOAD) ) 
			{
				//(this is used for load in prototype)
				Scene scene = (Scene) o;
				_view.getView3D().setVolumes(scene.getVolumes());
				_view.getView3D().setSlides(scene.getSlides());
				_view.getView3D().setCells(scene.getCells());
				for (NeuronMorphology c : scene.getCells())
				{
					NeuronMorphology mi = (NeuronMorphology) c;
					mi.addObserver(this);
				}
				_view.getView3D().setCurves(scene.getCurves());
				_view.getView3D().setSurfaces(scene.getSurfaces());
				msg = "reloading entire scene";
			}
				//======== DEFAULT ===========
			else	
			{
				//Default case for reloading entire scene 
				System.err.println("Warning in WBC SceneObserver: argument supplied for update scene not accounted for (" + arg +")");
			}
			
			System.out.println("Performance Mesg: " + msg);
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

		else if (o instanceof Tangible)
		{
			// catch all method for any leftover tangibles
			TangibleView tv = TangibleViewManager.getInstance().getTangibleViewFor((Tangible) o);
			if (tv != null)
			{
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

}
