package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.morphml.networkml.schema.NetworkmlType;
import org.morphml.networkml.schema.Population;

import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Defines a whole network of cells, with populations and connectivity
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class NeuroMLNetwork extends Tangible {
	
	URL loc = null;
	NetworkmlType network = null;
	
	public NeuroMLNetwork(URL morphLoc) {
		Log.warn("Loading NeuroMLNetwork");
		long tick = Log.tick();
		loc = morphLoc;
		
		JAXBContext context;
		try {
			
			//check to see if this particular file has already been loaded and cached
			
			context = JAXBContext.newInstance("neuroml.generated");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement o = (JAXBElement)unmarshaller.unmarshal(new File(loc.getFile()));
			network = (NetworkmlType)o.getValue();
			
			
			for (Object ob : network.getPopulations().getPopulation()) {
				Population p = (Population)ob;
				String cellType = p.getCellType();
				
				//retrieve the NeuronMorphology that has this cell name
				/*
				for (CellInstance ci : p.getInstances().getInstance()) {
					//set position of each instance of the neuron morphology
					//ci.getLocation()
				}*/
			}

		} catch (JAXBException e) {
			throw new OMTException("Problem loading " + loc.getFile(), e);
		}
		Log.tock("Loading NeuroMLNetwork " + loc.getFile() + " took ", tick);
	}
}
