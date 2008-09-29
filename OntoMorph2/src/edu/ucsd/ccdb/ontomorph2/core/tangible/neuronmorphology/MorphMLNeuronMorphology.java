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

import edu.ucsd.ccdb.ontomorph2.core.data.GlobalDataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.LocalDataRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Describes the morphology of the cell, loaded by a MorphML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class MorphMLNeuronMorphology extends NeuronMorphology{
	
	String filename;
	ICable tempCable = null;
	Level3Cell theCell = null;
	
	public MorphMLNeuronMorphology(String name) {
		this.filename = name;
		super.setName(name);
	}
	

	public MorphMLNeuronMorphology(String name, PositionVector position, 
			RotationVector rotation) {
		this(name);
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}

	
	public MorphMLNeuronMorphology(String name, PositionVector position, 
			RotationVector rotation, String renderOption) {
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
	
	public MorphMLNeuronMorphology(String name, Curve3D curve, float time, 
			String renderOption, CoordinateSystem c) {
		this(name, curve, time, renderOption);
		this.setCoordinateSystem(c);
	}
	
	public MorphMLNeuronMorphology(String url, Curve3D ocurve, float t, String render_as_lod, 
			DemoCoordinateSystem dcoords) {
		this(url, ocurve, t, render_as_lod);
		this.setCoordinateSystem(dcoords);
	}


	public Level3Cell getMorphMLCell() {
		if (theCell == null) {
			//try to retrieve file from the cache
			if (LocalDataRepository.getInstance().isFileCached(this.filename)) {
				theCell = (Level3Cell) LocalDataRepository.getInstance().getCachedFile(this.filename);
			}
			if (theCell != null) {
				Log.warn("Successfully uncached cell " + this.filename + "!");
				return theCell;
			}
			
			try {
				//search for file in global database by name
				theCell = (Level3Cell) GlobalDataRepository.getInstance().findMorphMLByName(this.filename);
				
			} catch (Exception e) {
				Log.warn("Did not find " + this.filename + " neuron morphology in the database.  Trying to load from disk now...");
			}
			
			if (theCell != null) {

//				store the file in the GlobalDataRepository once it is loaded for the next time.
				LocalDataRepository.getInstance().cacheFile(this.filename, theCell);
				
				Log.warn("Successfully loaded cell " + this.filename + " from the DB!");
				return theCell;
			}
			//if not found, search in expected directory for xml file
			try {
				URL cellURL = new File(Scene.morphMLDir + this.filename + ".morph.xml").toURI().toURL();
				
				if (cellURL != null) {
					JAXBContext context = JAXBContext.newInstance("org.morphml.neuroml.schema");
					//Create the unmarshaller
					final Unmarshaller unmarshaller = context.createUnmarshaller();
					//Unmarshall the XML
					NeuroMLLevel3 neuroml = (NeuromlImpl)unmarshaller.unmarshal(new File(cellURL.getFile()));
					
					Level3Cells c = neuroml.getCells();
					
					assert c.getCell().size() == 1;
					theCell = (Level3Cell)c.getCell().get(0);
					
//					store the file in the GlobalDataRepository once it is loaded for the next time.
					LocalDataRepository.getInstance().cacheFile(this.filename, theCell);
					GlobalDataRepository.getInstance().saveFileToDB(theCell);
					Log.warn("Storing cell " + this.filename + " in the DB");
				}
			} catch (Exception e) {
				throw new OMTException("Cannot load " + this.filename + " morphology! ", e);
			}
		}
		return theCell;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getName()
	 */
	public String getFilename() 
	{
		return this.filename;
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
	public ICable getCable(int i) {
		if (this.tempCable == null) {
			this.tempCable = new MorphMLCableImpl(this, (Cable)getMorphMLCell().getCables().getCable().get(i));
			return this.tempCable;
		} 
		tempCable.setMorphMLCable((Cable)getMorphMLCell().getCables().getCable().get(i));
		return tempCable;
	}

	/*
	 *  (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getCable(java.math.BigInteger)
	 */
	public ICable getCable(BigInteger id) {
		for (int i = 0; i < getCableCount(); i++) {
			ICable c = getCable(i);
			if (c.getId().equals(id)) {
				return c; 
			}
		}
		return null;
	}

}
