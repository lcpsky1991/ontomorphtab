package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.morphml.neuroml.schema.Curves;
import org.morphml.neuroml.schema.Level3Cells;
import org.morphml.neuroml.schema.NeuroMLLevel3;
import org.morphml.neuroml.schema.impl.CurvesImpl;
import org.morphml.neuroml.schema.impl.Level3CellsImpl;
import org.morphml.neuroml.schema.impl.NeuroMLLevel3Impl;
import org.morphml.neuroml.schema.impl.NeuromlImpl;
import org.w3c.dom.Node;

import com.sun.xml.stream.PropertyManager;
import com.sun.xml.stream.writers.XMLStreamWriterImpl;

import edu.ucsd.ccdb.ontomorph2.core.semantic.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;

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
	public static final String allenObjMeshDir = objDir + "allen_meshes_low_detail" + File.separatorChar;

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
		try {
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
		} catch (OMTOfflineException e) {
			Log.warn("Scene.loadFromCKB, cannot load because client is offline!");
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
	

	@SuppressWarnings("unchecked")
	public void save() {
		NeuroMLLevel3 scene = new NeuroMLLevel3Impl();
		Level3Cells cells = new Level3CellsImpl();
		for (NeuronMorphology nm : getCells()) {
			if (nm instanceof MorphMLNeuronMorphology) {
				MorphMLNeuronMorphology mmnm = (MorphMLNeuronMorphology)nm;
				cells.getCell().add(mmnm.getMorphMLCell());
			}
		}
		scene.setCells(cells);
		Curves curves = new CurvesImpl();
		for (Curve3D c : getCurves()) {
			curves.getCurve().add(c.getMorphMLCurve());
		}
		scene.setCurves(curves);
		
		try {
			JAXBContext context = JAXBContext.newInstance("org.morphml.neuroml.schema");
			
			//Create the marshaller
			final Marshaller marshaller = context.createMarshaller();
			FileOutputStream file = null;
			try {
				file = new FileOutputStream("saved_scene.xml");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			marshaller.marshal(scene, file);
			//Unmarshall the XML
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
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
