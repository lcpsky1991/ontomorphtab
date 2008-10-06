package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.morphml.morphml.schema.Cable;
import org.morphml.neuroml.schema.Level3Cell;
import org.morphml.neuroml.schema.Level3Cells;
import org.morphml.neuroml.schema.NeuroMLLevel3;
import org.morphml.neuroml.schema.impl.NeuromlImpl;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.MemoryCacheRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/** .
 * Describes the morphology of the cell, loaded by a MorphML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class MorphMLNeuronMorphology extends NeuronMorphology{
	
	MorphMLCable tempCable = null;
	Level3Cell theCell = null;
	
	public MorphMLNeuronMorphology(String name) {
		super(name);
	}
	

	public MorphMLNeuronMorphology(String name, PositionVector position, 
			RotationQuat rotation) {
		this(name);
		setPosition(position);
		setRotation(rotation);
	}

	
	public MorphMLNeuronMorphology(String name, PositionVector position, 
			RotationQuat rotation, String renderOption) {
		this(name, position, rotation);
		setRenderOption(renderOption);
	}
	
	
	public MorphMLNeuronMorphology(String name, Curve3D curve, float time, String renderOption) {
		this(name);
		setCurve(curve);
		setTime(time);
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	

	public Level3Cell getMorphMLCell() 
	{
		if (theCell == null) 
		{
			//try to retrieve file from the cache
			if (MemoryCacheRepository.getInstance().isFileCached(getName())) {
				theCell = (Level3Cell) MemoryCacheRepository.getInstance().getCachedFile(getName());
			}
			if (theCell != null) {
				Log.warn("Successfully uncached cell " + getName() + "!");
				return theCell;
			}
			
			try 
			{
				//search for file in global database by name
				theCell = (Level3Cell) DataRepository.getInstance().findMorphMLByName(getName());
			}
			catch (Exception e) 
			{
				Log.warn("Did not find " + getName() + " neuron morphology in the database.  Trying to load from disk now...");
			}
			
			if (theCell != null) 
			{
//				store the file in the DataRepository once it is loaded for the next time.
				MemoryCacheRepository.getInstance().cacheFile(getName(), theCell);
				
				Log.warn("Successfully loaded cell " + getName() + " from the DB!");
				return theCell;
			}
			//if not found, search in expected directory for xml file
			try {
				URL cellURL = new File(Scene.morphMLDir + getName() + ".morph.xml").toURI().toURL();
				
				if (cellURL != null) {
					JAXBContext context = JAXBContext.newInstance("org.morphml.neuroml.schema");
					//Create the unmarshaller
					final Unmarshaller unmarshaller = context.createUnmarshaller();
					//Unmarshall the XML
					NeuroMLLevel3 neuroml = (NeuromlImpl)unmarshaller.unmarshal(new File(cellURL.getFile()));
					
					Level3Cells c = neuroml.getCells();
					
					assert c.getCell().size() == 1;
					theCell = (Level3Cell)c.getCell().get(0);
					
//					store the file in the DataRepository once it is loaded for the next time.
					MemoryCacheRepository.getInstance().cacheFile(getName(), theCell);
					DataRepository.getInstance().saveFileToDB(theCell);
					Log.warn("Storing cell " + getName() + " in the DB");
				}
			} catch (Exception e) {
				throw new OMTException("Cannot load " + getName() + " morphology! ", e);
			}
		}
		return theCell;
	}	

	/**
	 * Says how many cables are associated with this neuron morphology
	 */
	public int getCableCount() {
		return getMorphMLCell().getCables().getCable().size();
	}

	/**
	 * Retrieves the cable at position i.  IMPORTANT NOTE: This method does not return a new reference 
	 * each time it is called.  Instead it uses the same instance of a cable each time and simply
	 * calls a set method to make it into the appropriate cable.  Do not add these cables
	 * to any collections or they will not work correctly.
	 */
	public MorphMLCable getCable(int i) {
		if (this.tempCable == null) {
			this.tempCable = new MorphMLCable(this, (Cable)getMorphMLCell().getCables().getCable().get(i));
			return this.tempCable;
		} 
		tempCable.setMorphMLCable((Cable)getMorphMLCell().getCables().getCable().get(i));
		return tempCable;
	}

	/*
	 *  (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getCable(java.math.BigInteger)
	 */
	public MorphMLCable getCable(BigInteger id) {
		for (int i = 0; i < getCableCount(); i++) {
			MorphMLCable c = getCable(i);
			if (c.getId().equals(id)) {
				return c; 
			}
		}
		return null;
	}

}
