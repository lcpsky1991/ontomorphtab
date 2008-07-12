package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
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
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * This main observer is triggered when any scene object changes and updates
 * the view accordingly.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class SceneObserver implements Observer{

	View _view;
	/**
	 * Holds singleton instance
	 */
	private static SceneObserver instance;

	public void update(Observable o, Object arg) {
		if (o instanceof Scene) {
			Scene scene = (Scene)o;
			_view.getView3D().setVolumes(scene.getVolumes());
			_view.getView3D().setSlides(scene.getSlides());
			_view.getView3D().setCells(scene.getCells());
			for (NeuronMorphology c: scene.getCells()) {
				NeuronMorphology mi = (NeuronMorphology)c;
				mi.addObserver(this);
			}
			_view.getView3D().setCurves(scene.getCurves());
			_view.getView3D().setSurfaces(scene.getSurfaces());
			_view.getView3D().setMeshes(scene.getMeshes());
			
		} else if (o instanceof NeuronMorphology) { //if an NeuronMorphology is changed
			NeuronMorphologyView struct3d = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor((NeuronMorphology)o);
			if (struct3d != null) {
				NeuronMorphology morph = (NeuronMorphology)struct3d.getMorphology();
				if (morph == o) { // find the one that matches this NeuronMorphology and update it
					
					//Moves the segment to see if this is how to update model
					PositionVector p = (PositionVector)struct3d.getMorphology().getAbsolutePosition();
					struct3d.getNode().setLocalTranslation(p);
					
					RotationVector r = (RotationVector)struct3d.getMorphology().getAbsoluteRotation();
					struct3d.getNode().setLocalRotation(r);
					
					OMTVector s = struct3d.getMorphology().getAbsoluteScale();
					struct3d.getNode().setLocalScale(s);
				
					struct3d.updateSelected(morph.isSelected());
					struct3d.updateSelectedSegments(morph.getSelectedSegments());
					struct3d.updateSelectedSegmentGroups(morph.getSelectedSegmentGroups());
					
					struct3d.updateModelBound();
					struct3d.updateWorldBound();
					struct3d.updateRenderState();
	
				}
			}
		} else if (o instanceof ISemanticThing) {
			ISemanticThing st = (ISemanticThing)o;
			for(ISemanticsAware sa : st.getSemanticsAwareAssociations()) {
				if (st.isSelected()) {
					sa.select();
				} else if (!st.isSelected()) {
					sa.unselect();
				}
			}
		} else if (o instanceof Tangible) {
//			catch all method for any leftover tangibles 
			TangibleView tv = TangibleViewManager.getInstance().getTangibleViewFor((Tangible)o);
			if (tv != null) {
				tv.update();
			}
		}
	}

	/**
	 * prevents instantiation
	 */
	private SceneObserver() {
		// prevent creation
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public SceneObserver getInstance() {
		if (instance == null) {
			instance = new SceneObserver();
		}
		return instance;
	}

	public void setView(View view) {
		_view = view;
	}

}
