package edu.ucsd.ccdb.ontomorph2.app;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import org.fenggui.render.lwjgl.LWJGLOpenGL;
import org.lwjgl.opengl.Display;

import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jmex.awt.lwjgl.LWJGLCanvas;

import edu.ucsd.ccdb.ontomorph2.core.scene.DefaultScene;
import edu.ucsd.ccdb.ontomorph2.core.scene.DemoScene;
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
 * System properties are loaded from wbc.properties.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OntoMorph2 
{
	
	static Scene _scene;
	static Properties wbcProps = null;
	public static final String OFFLINE_MODE = "offlineMode";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String LAST_LOADED_DIRECTORY = "last.load.directory";
	public static final String PROPERTIES_FILENAME = "wbc.properties";
	public static final String DEBUG_MODE = "debugMode";
	public static final String SCENE = "scene";
	
	
	public static void main(String[] args) {
        //		set global log level to warning
		//useful link on this:
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=5864.0
		Log.getLogger("").setLevel(Level.WARNING);
		Log.getLogger("").getHandlers()[0].setLevel(Level.WARNING);
		
	
		View view = View.getInstance();
		
		SceneObserver obs = SceneObserver.getInstance();
		obs.setView(view);

		
		_scene = new DefaultScene();
		_scene.addObserver(obs);
		
		//since the view takes over the thread after it is started
		//need to have the view do the initial loading of the scene.
		view.setScene(_scene);	
		try 
		{
			view.start();
		} 
		catch (Exception e) {
			e.printStackTrace();
			Log.error("SEVERE ERROR");
		}
	}
	
	//gets called by View during initialization
	public static void initialization() {
		//scene can't be loaded before View has been initializaed
		try 
		{
			_scene.load(OntoMorph2.getWBCProperties().getProperty(OntoMorph2.SCENE));
		}
		catch (Exception e) {
			Log.error("SEVERE ERROR ATTEMPTING TO LOAD SCENE");
			e.printStackTrace();
		}
		
		//View.getInstance().getView2D().addInfoText("This is an example of \nloading neuronal morphologies...");
	}
	
	
	
	
	public static Scene getCurrentScene() {
		return _scene;
	}
	
	public static Properties getWBCProperties() {
		if (wbcProps == null) {
			try {
				wbcProps = new Properties();
				URL url = new File("wbc.properties").toURI().toURL();
				wbcProps.load(url.openStream());
			} catch (Exception e) {
				Log.error("Problem loading configuration file!");
				e.printStackTrace();
				System.exit(1);
			}
		}
		return wbcProps;
	}
	
	/**
	 * Saves the wbc.properties file to the root directory where the app is running
	 *
	 */
	public static void saveWBCProperties() {
		try {
        	getWBCProperties().store(new FileOutputStream("wbc.properties"), "");
        } catch (Exception e) {
			Log.warn("Unable to write properties file");
		}
	}

	/**
	 * Convenience method to see if the system is in offline mode
	 * @return
	 */
	public static boolean isOfflineMode() {
		return TRUE.equals(getWBCProperties().getProperty(OFFLINE_MODE));
	}
	
	public static boolean isDemo() {
		return "demo".equals(getWBCProperties().getProperty(SCENE));
	}
	
	public static void setDebugMode(boolean debug) {
		if (debug) {
			getWBCProperties().setProperty(DEBUG_MODE, TRUE);
		}	else {
			getWBCProperties().setProperty(DEBUG_MODE, FALSE);
		}
		saveWBCProperties();
	}
	
	/**
	 * Convenience method to see if the application is in debug mode
	 * @return
	 */
	public static boolean isDebugMode() {
		return TRUE.equals(getWBCProperties().getProperty(DEBUG_MODE));
	}
}
