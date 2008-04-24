package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.ICell;
import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IScene;
import edu.ucsd.ccdb.ontomorph2.core.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.MorphologyImpl;
import edu.ucsd.ccdb.ontomorph2.view.IStructure3D;
import edu.ucsd.ccdb.ontomorph2.view.IView;
import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;


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
			for (ICell c: scene.getCells()) {
				MorphologyImpl mi = (MorphologyImpl)c.getMorphology();
				mi.addObserver(this);
			}
			_view.getView3D().setCurves(scene.getCurves());
			_view.getView3D().setSurfaces(scene.getSurfaces());
		} else if (o instanceof IMorphology) { //if an IMorphology is changed
			for (IStructure3D struct3d : _view.getView3D().getCells()) { //for all IStructure3Ds that are known
				if (struct3d.getMorphology() == o) { // find the one that matches this IMorphology and update it
					struct3d.updateSelected(struct3d.getMorphology().isSelected());
					struct3d.updateSelectedSegments(struct3d.getMorphology().getSelectedSegments());
					struct3d.updateSelectedSegmentGroups(struct3d.getMorphology().getSelectedSegmentGroups());
				}
				if (((IMorphology)o).hasSelectedSegmentGroups()) {
					for (ISegmentGroup sg : ((IMorphology)o).getSelectedSegmentGroups()) {
						//this is getting called more times than it should
						String infoString = sg.getTags().toString() + "\n";
						for (ISemanticThing s: sg.getSemanticThings()) {
							infoString += (s.toString() + "\n"); 
						}
						infoString += sg.getParentCell().getSemanticThings();
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
