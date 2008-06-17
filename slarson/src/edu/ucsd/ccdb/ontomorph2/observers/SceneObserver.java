package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.IScene;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.scene.NeuronMorphologyImpl;
import edu.ucsd.ccdb.ontomorph2.core.scene.SegmentGroupImpl;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.view.IView;
import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;
import edu.ucsd.ccdb.ontomorph2.view.scene.INeuronMorphologyView;

//TODO: remove vector3f from here and INeuroMorph changed (bookmark: p)
import com.jme.math.Vector3f;

/**
 * This main observer is triggered when any scene object changes and updates
 * the view accordingly.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SceneObserver implements Observer{

	IView _view;
	/**
	 * Holds singleton instance
	 */
	private static SceneObserver instance;

	public void update(Observable o, Object arg) {
		if (o instanceof IScene) {
			IScene scene = (IScene)o;
			_view.getView3D().setVolumes(scene.getVolumes());
			_view.getView3D().setSlides(scene.getSlides());
			_view.getView3D().setCells(scene.getCells());
			for (INeuronMorphology c: scene.getCells()) {
				NeuronMorphologyImpl mi = (NeuronMorphologyImpl)c;
				mi.addObserver(this);
			}
			_view.getView3D().setCurves(scene.getCurves());
			_view.getView3D().setSurfaces(scene.getSurfaces());
			_view.getView3D().setMeshes(scene.getMeshes());
			
		} else if (o instanceof INeuronMorphology) { //if an INeuronMorphology is changed
			for (INeuronMorphologyView struct3d : _view.getView3D().getCells()) { //for all IStructure3Ds that are known
				NeuronMorphologyImpl morph = (NeuronMorphologyImpl)struct3d.getMorphology();
				if (morph == o) { // find the one that matches this INeuronMorphology and update it
					
					//TODO: remove this debug code
					//Moves the segment to see if this is how to update model
					PositionVector p = (PositionVector)struct3d.getMorphology().getAbsolutePosition();
					struct3d.getNode().setLocalTranslation(p);
					System.out.println("Observer Pos: " + p.asVector3f());
					//--end debug
					
					struct3d.updateSelected(morph.isSelected());
					struct3d.updateSelectedSegments(morph.getSelectedSegments());
					struct3d.updateSelectedSegmentGroups(morph.getSelectedSegmentGroups());
					
					
				
					
				}
				//UPDATE INFO STRING ON VIEW 2D
				
				if (((INeuronMorphology)o).hasSelectedSegmentGroups()) {
					for (ISegmentGroup isg : ((NeuronMorphologyImpl)o).getSelectedSegmentGroups()) {
						SegmentGroupImpl sg = (SegmentGroupImpl)isg;
						//this is getting called more times than it should
						String infoString = sg.getTags().toString() + "\n";
						for (ISemanticThing s: sg.getSemanticThings()) {
							infoString += (s.toString() + "\n"); 
						}
						infoString += ((NeuronMorphologyImpl)sg.getParentCell()).getSemanticThings();
						ViewImpl.getInstance().getView2D().setInfoText(infoString);
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
			if (br.isSelected()) {
				ViewImpl.getInstance().getView3D().displayBrainRegion(br);
			} else {
				ViewImpl.getInstance().getView3D().unDisplayBrainRegion(br);
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

	public void setView(IView view) {
		_view = view;
	}

}
