package edu.ucsd.ccdb.ontomorph2.app;

import edu.ucsd.ccdb.ontomorph2.core.scene.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.scene.ViewImpl;

/**
 * Main class for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OntoMorph2 {
	
	static SceneImpl _scene;
	
	public static void main(String[] args) {
		ViewImpl view = ViewImpl.getInstance();
		
		SceneObserver obs = SceneObserver.getInstance();
		obs.setView(view);
		_scene = new SceneImpl();
	
		_scene.addObserver(obs);
		
		//since the view takes over the thread after it is started
		//need to have the view do the initial loading of the scene.
		view.setScene(_scene);	
		view.start();
	}
	
	//gets called by ViewImpl during initialization
	public static void initialization() {
		//scene can't be loaded before ViewImpl has been initializaed
		_scene.load();
		ViewImpl.getInstance().getView2D().addInfoText("This is an example of \nloading neuronal morphologies...");
	}
	
	
	


}
