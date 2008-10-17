package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.morphml.metadata.schema.Curve;
import org.morphml.networkml.schema.CellInstance;
import org.morphml.networkml.schema.CurveAssociation;
import org.morphml.networkml.schema.Instances;
import org.morphml.networkml.schema.Population;
import org.morphml.networkml.schema.Populations;
import org.morphml.networkml.schema.impl.CellInstanceImpl;
import org.morphml.networkml.schema.impl.InstancesImpl;
import org.morphml.networkml.schema.impl.PopulationImpl;
import org.morphml.networkml.schema.impl.PopulationsImpl;
import org.morphml.neuroml.schema.CurveSet;
import org.morphml.neuroml.schema.Level3Cell;
import org.morphml.neuroml.schema.Level3Cells;
import org.morphml.neuroml.schema.NeuroMLLevel3;
import org.morphml.neuroml.schema.Neuroml;
import org.morphml.neuroml.schema.SlideSet;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.impl.CurveSetImpl;
import org.morphml.neuroml.schema.impl.Level3CellsImpl;
import org.morphml.neuroml.schema.impl.NeuromlImpl;
import org.morphml.neuroml.schema.impl.SlideSetImpl;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.MemoryCacheRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
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
	public static final String allenLowDetailObjMeshDir = objDir + "allen_meshes_low_detail" + File.separatorChar;
	public static final String allenHighDetailObjMeshDir = objDir + "allen_meshes_high_detail" + File.separatorChar;

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
		//System.out.println("addSceneObject" + s);
		s.setVisible(true);
	}
	
		
	/**
	 * Loads data from the cellular knowledge base, which is a
	 * metadata warehouse.  Once the metadata is sorted through,
	 * the raw data is retrieved from the CCDB and other sources.
	 * @deprecated
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
	 * files on the client.
	 * @see #loadFromCKB()
	 *
	 */
	public abstract void load(String filename);
	

	/**
	 * Writes a neuroML document that describes the current scene.  Still needs to impliment
	 * saving slides, saving exact positions of cells with rotation and scale, coordinate systems,
	 * etc
	 *
	 */
	@SuppressWarnings("unchecked")
	public void save(String strFileOut) 
	{
		System.out.println("Attempting to save file to " + strFileOut);
		Neuroml scene = new NeuromlImpl();
		scene.setLengthUnits("micron");
		
		//=======================================
		// set cells
		//=======================================
		Level3Cells cells = new Level3CellsImpl();
		Set alreadySaved = new HashSet();
		for (NeuronMorphology mmnm : getCells()) 
		{
			if (!alreadySaved.contains(mmnm.getName())) 
			{
				cells.getCell().add(mmnm.getMorphMLCell());
				alreadySaved.add(mmnm.getName());
			}
		}
		scene.setCells(cells);
		//------------------------------------------
		
        //=======================================
		// set cell instances
		//=======================================
		Populations populations = new PopulationsImpl();
		HashMap<String, Population> m = new HashMap<String, Population>();
		for (NeuronMorphology mmnm : getCells()) 
		{
			Population cellType = m.get(mmnm.getName());
			if (cellType == null) {
				cellType = new PopulationImpl();
				cellType.setName(mmnm.getName());
				cellType.setCellType(mmnm.getName());
				Instances i = new InstancesImpl();
				cellType.setInstances(i);
				m.put(mmnm.getName(), cellType);
				populations.getPopulation().add(cellType);
			}
			List list = cellType.getInstances().getInstance();
			
			list.add(mmnm.getMorphMLCellInstance());
			cellType.getInstances().setSize(BigInteger.valueOf(list.size()));
		}
		scene.setPopulations(populations);
		
		
		//=======================================
		// set curves
		//=======================================
		CurveSet curves = new CurveSetImpl();
		for (Curve3D c : getCurves()) 
		{
			curves.getCurve().add(c.getMorphMLCurve());
		}
		scene.setCurves(curves);
		
		//=======================================
		// set slides
		//=======================================
		
		SlideSet slides = new SlideSetImpl();
		for (Slide s: getSlides())
		{
			slides.getSlide().add(s.getMorphMLSlide());
		}
		scene.setSlides(slides);
		
		
		try 
		{
			JAXBContext context = JAXBContext.newInstance("org.morphml.neuroml.schema");
			
			//Create the marshaller
			final Marshaller marshaller = context.createMarshaller();
			FileOutputStream file = new FileOutputStream(strFileOut);
			
			
			marshaller.marshal(scene, file);
			//marshall the XML
			file.flush();
			file.close();
		} 
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.warn("Finished saving " + strFileOut);
		
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

	public Set<SphereParticles> getParticles(){
		return manager.getParticles();
	}
	
}
