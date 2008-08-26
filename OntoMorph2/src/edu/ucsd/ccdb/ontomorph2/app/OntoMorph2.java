package edu.ucsd.ccdb.ontomorph2.app;

import java.util.logging.Level;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Main class for the application.  Here environment properties and application 
 * configuration files are read and processed.  Logging is configured.  The main 
 * classes for the MVC architecture are initialized.  View, SceneObserver, and 
 * Scene are initialized and related to each other.  The scene is loaded with 
 * Tangibles from the central repository.  High-level error handling happens here as well.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OntoMorph2 {
	
	static Scene _scene;
	
	public static void main(String[] args) {
        //		set global log level to warning
		//useful link on this:
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=5864.0
		Log.getLogger("").setLevel(Level.WARNING);
		Log.getLogger("").getHandlers()[0].setLevel(Level.WARNING);
		
		System.setProperty("java.library.path", "lib");
		
		View view = View.getInstance();
		
		SceneObserver obs = SceneObserver.getInstance();
		obs.setView(view);
		_scene = new Scene();
	
		_scene.addObserver(obs);
		
		//since the view takes over the thread after it is started
		//need to have the view do the initial loading of the scene.
		view.setScene(_scene);	
		try {
			view.start();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("SEVERE ERROR");
		}
	}
	
	//gets called by View during initialization
	public static void initialization() {
		//scene can't be loaded before View has been initializaed
		try {
			_scene.load();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("SEVERE ERROR");
		}
		
		View.getInstance().getView2D().addInfoText("This is an example of \nloading neuronal morphologies...");
	}
	
	
	


}
