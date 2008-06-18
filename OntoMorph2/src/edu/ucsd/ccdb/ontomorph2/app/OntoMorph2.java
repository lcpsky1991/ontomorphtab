package edu.ucsd.ccdb.ontomorph2.app;

import java.io.File;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;

/**
 * Main class for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OntoMorph2 {
	
	static Scene _scene;
	
	public static void main(String[] args) {
		String prop = System.getProperty("java.endorsed.dirs");
		System.setProperty("java.endorsed.dirs", prop + File.pathSeparatorChar + "C:\\Documents and Settings\\stephen\\workspace2\\OntoMorph2-2\\lib\\endorsed");
		prop = System.getProperty("java.endorsed.dirs");
		System.out.println("java.endorsed.dirs=" + prop);
		ViewImpl view = ViewImpl.getInstance();
		
		SceneObserver obs = SceneObserver.getInstance();
		obs.setView(view);
		_scene = new Scene();
	
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
