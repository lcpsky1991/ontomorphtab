package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.IScene;
import edu.ucsd.ccdb.ontomorph2.core.SceneImpl;
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
