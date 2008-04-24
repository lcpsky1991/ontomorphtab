package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.IScene;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.scene.NeuronMorphologyImpl;
import edu.ucsd.ccdb.ontomorph2.core.scene.SegmentGroupImpl;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.view.scene.INeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.IView;
import edu.ucsd.ccdb.ontomorph2.view.scene.ViewImpl;


/**
 * Represents a singleton.
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
			_view.getView3D().setSlides(scene.getSlides());
			_view.getView3D().setCells(scene.getCells());
			for (INeuronMorphology c: scene.getCells()) {
				NeuronMorphologyImpl mi = (NeuronMorphologyImpl)c;
				mi.addObserver(this);
			}
			_view.getView3D().setCurves(scene.getCurves());
			_view.getView3D().setSurfaces(scene.getSurfaces());
		} else if (o instanceof INeuronMorphology) { //if an INeuronMorphology is changed
			for (INeuronMorphologyView struct3d : _view.getView3D().getCells()) { //for all IStructure3Ds that are known
				NeuronMorphologyImpl morph = (NeuronMorphologyImpl)struct3d.getMorphology();
				if (morph == o) { // find the one that matches this INeuronMorphology and update it
					struct3d.updateSelected(morph.isSelected());
					struct3d.updateSelectedSegments(morph.getSelectedSegments());
					struct3d.updateSelectedSegmentGroups(morph.getSelectedSegmentGroups());
				}
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
