package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.scene.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.SegmentGroupImpl;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;

/**
 * This main observer is triggered when any scene object changes and updates
 * the view accordingly.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
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
			for (NeuronMorphologyView struct3d : _view.getView3D().getCells()) { //for all IStructure3Ds that are known
				NeuronMorphology morph = (NeuronMorphology)struct3d.getMorphology();
				if (morph == o) { // find the one that matches this NeuronMorphology and update it
					
					//TODO: remove this debug code
					//Moves the segment to see if this is how to update model
					PositionVector p = (PositionVector)struct3d.getMorphology().getAbsolutePosition();
					struct3d.getNode().setLocalTranslation(p);
					//System.out.println("Observer Pos: " + p.asVector3f());
					//--end debug
					
					struct3d.updateSelected(morph.isSelected());
					struct3d.updateSelectedSegments(morph.getSelectedSegments());
					struct3d.updateSelectedSegmentGroups(morph.getSelectedSegmentGroups());
					
					
				
					
				}
				//UPDATE INFO STRING ON VIEW 2D
				
				if (((NeuronMorphology)o).hasSelectedSegmentGroups()) {
					for (ISegmentGroup isg : ((NeuronMorphology)o).getSelectedSegmentGroups()) {
						SegmentGroupImpl sg = (SegmentGroupImpl)isg;
						//this is getting called more times than it should
						String infoString = sg.getTags().toString() + "\n";
						for (ISemanticThing s: sg.getSemanticThings()) {
							infoString += (s.toString() + "\n"); 
						}
						infoString += ((NeuronMorphology)sg.getParentCell()).getSemanticThings();
						View.getInstance().getView2D().setInfoText(infoString);
					}
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
		} else if (o instanceof BrainRegion) {
			BrainRegion br = (BrainRegion)o;
			
			View.getInstance().getView3D().updateBrainRegion(br);
			
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
