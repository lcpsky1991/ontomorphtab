package edu.ucsd.ccdb.ontomorph2.util;

import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.NeuroMLLevel2.Cells;

public class MorphMLLoader {
	
	public void loadscene(URL filename) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("neuroml.generated");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement o = (JAXBElement)unmarshaller.unmarshal(new File(filename.getFile()));
			NeuroMLLevel2 neuroml = (NeuroMLLevel2)o.getValue();
			Cells c = neuroml.getCells();
			
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
