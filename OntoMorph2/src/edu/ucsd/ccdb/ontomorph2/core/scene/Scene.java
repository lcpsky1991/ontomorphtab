package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;

/**
 * Defines the totality of the objects that can be viewed in the 3D world
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class Scene extends Observable{
	
	TangibleManager manager = null;
	
	protected int cameraPosition = CAMERA_SLIDE_POSITION;
	
	public static final String baseDir = "." + File.separatorChar + "etc" + File.separatorChar;
	public static final String imgDir = baseDir + "img" + File.separatorChar;
	public static final String morphMLDir = baseDir + "morphml" 
											+ File.separatorChar + "hippocampus" + File.separatorChar;
	public static final String mitoDir = baseDir + File.separatorChar + "mito" + File.separatorChar;
	public static final String objDir = baseDir + File.separatorChar + "obj" + File.separatorChar;
	public static final String allenDir = baseDir + File.separatorChar + "allen" 
									+ File.separatorChar;
	public static final String allenMeshDir = allenDir + "Mesh25" + File.separatorChar;

	public static final int CHANGED_PART = 1;
	public static final int CHANGED_LOAD = 0;
	public static final int CHANGED_VOLUME = 2;
	public static final int CHANGED_SLIDE = 3;
	public static final int CHANGED_CELL = 4;
	public static final int CHANGED_CURVE = 5;
	public static final int CHANGED_SURFACE = 6;
	public static final int CHANGED_UNKNOWN = -1;
	public static final int CHANGED_TEST = -2;
	
	public static final int CAMERA_SLIDE_POSITION = 99;
	public static final int CAMERA_LATERAL_POSITION = 98;
	public static final int CAMERA_MEDIAL_POSITION = 97;
	public static final int CAMERA_CELLS_POSITION = 96;
	public static final int CAMERA_SUBCELLULAR_POSITION = 95; 
	public static final int CAMERA_CEREBELLUM_POSITION = 94;
	
	public Scene() {
		manager = TangibleManager.getInstance();
	}
	
	protected void setCameraPosition(int c) {
		cameraPosition = c;
	}
	
	public int getCameraPosition() {
		return cameraPosition;
	}
	
	protected void addSceneObject(Tangible s) {
		s.setVisible(true);
	}
	
		
	/**
	 * Loads data from the cellular knowledge base, which is a
	 * metadata warehouse.  Once the metadata is sorted through,
	 * the raw data is retrieved from the CCDB and other sources.
	 */
	public void loadFromCKB() {
		//load up SemanticRepository
		List<SemanticInstance> instances = GlobalSemanticRepository.getInstance().getMicroscopyProductInstances();
		for (SemanticInstance i : instances) {
			//if species == mouse
		      //if image 
		          //make image instances into slides
				  //store the SemanticInstance object in the slide
		          //add slides to scene
		      //if neuron reconstruction
		          //make instance into neuron morphology
		          //store the SemanticInstance object in the neuronMorphology
			      //add neuron morphology to scene
		    //}
		}
	}
	
	/**
	 * Loads objects into the scene.  Currently this is done manually mostly from
	 * files on the client.  This is being transitioned to the loadFromCKB method
	 * where we are loading data from the cellular knowledge base
	 * @see #loadFromCKB()
	 *
	 */
	public abstract void load();
	

	/*
	public void save() {
	}*/
	
	
	public Set<Slide> getSlides() {
		return manager.getSlides();
	}
	
	public Set<NeuronMorphology> getCells() {
		return manager.getCells();
	}

	 public void changed (int arg) {       
		  setChanged();                 
		  notifyObservers(arg);            
	 }

	public Set<Curve3D> getCurves() {
		return manager.getCurves();
	}

	public Set<Surface> getSurfaces() {
		return manager.getSurfaces();
	}

	public Set<DataMesh> getMeshes() {
		return manager.getMeshes();
	}

	public Set<Volume> getVolumes() {
		return manager.getVolumes();
	}

	
}
