package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import neuroml.generated.CellInstance;
import neuroml.generated.Level3Cells;
import neuroml.generated.NetworkML;
import neuroml.generated.NeuroMLLevel3;
import neuroml.generated.Population;
import neuroml.generated.Populations;
import edu.ucsd.ccdb.ontomorph2.core.data.DataCacheManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Defines a whole network of cells, with populations and connectivity
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class NeuroMLNetwork extends Tangible {
	
	URL loc = null;
	NetworkML network = null;
	
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
			network = (NetworkML)o.getValue();
			
			
			for (Population p : network.getPopulations().getPopulation()) {
				String cellType = p.getCellType();
				
				//retrieve the NeuronMorphology that has this cell name
				for (CellInstance ci : p.getInstances().getInstance()) {
					//set position of each instance of the neuron morphology
					//ci.getLocation()
				}
			}

		} catch (JAXBException e) {
			throw new OMTException("Problem loading " + loc.getFile(), e);
		}
		Log.tock("Loading NeuroMLNetwork " + loc.getFile() + " took ", tick);
	}
}
