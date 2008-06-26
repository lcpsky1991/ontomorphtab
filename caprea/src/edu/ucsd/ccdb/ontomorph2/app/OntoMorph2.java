package edu.ucsd.ccdb.ontomorph2.app;

import java.io.File;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Main class for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OntoMorph2 {
	
	static Scene _scene;
	
	public static void main(String[] args) {

		View view = View.getInstance();
		
		SceneObserver obs = SceneObserver.getInstance();
		obs.setView(view);
		_scene = new Scene();
	
		_scene.addObserver(obs);
		
		//since the view takes over the thread after it is started
		//need to have the view do the initial loading of the scene.
		//view.setScene(_scene);	//view should not be maintaining a model
		view.start();
	}
	
	//gets called by View during initialization
	public static void initialization() {
		//scene can't be loaded before View has been initializaed
		_scene.load();
		View.getInstance().getView2D().addInfoText("This is an example of \nloading neuronal morphologies...");
	}
	
	
	


}
