package edu.ucsd.ccdb.ontomorph2.util;

import java.util.ArrayList;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;

public class LocalSemanticRepositorySetup {

	public static void main(String args[]) {
		LocalSemanticRepositorySetup s1 = new LocalSemanticRepositorySetup();
		s1.loadOntologyOne();
		//s1.loadOntologyTwo();
	}
	
	private void loadOntologyOne() {
		JenaOWLModel owlModel  = null;
		try {
			owlModel = ProtegeOWL.createJenaOWLModelFromURI("http://purl.org/nif/ontology/nif.owl");					    			
		} catch (Exception e) {
			throw new OMTException("Cannot load NIF ontology!", e);
		}	
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:db/db";
		String tableName = "nifontology";			
		String userName = "sa";
		
		try {
			DatabaseFrameDb db = new DatabaseFrameDb();
			db.initialize(owlModel.getFrameFactory(), driver, url, userName, "", tableName, false);
			db.overwriteKB(owlModel, true);
			
		} catch (Exception e) {
			throw new OMTException("Problem loading knowledgebase into db! ", e);
		}
	}
	
	private void loadOntologyTwo() {
		JenaOWLModel owlModel  = null;
		try {
			//NOTE: This pprj points to a .repository file which has a hard coded path in it.
			//need to figure out how to work around this before deploying.
			Project p = Project.loadProjectFromFile("etc/NIF/allen_mapping/allen-bridge.pprj", new ArrayList());
						
			owlModel = (JenaOWLModel) p.getKnowledgeBase();
		} catch (Exception e) {
			throw new OMTException("Cannot load allen-bridge ontology!", e);
		}	
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:db/db";
		String tableName = "allenbridge";			
		String userName = "sa";
		
		try {
			DatabaseFrameDb db = new DatabaseFrameDb();
			db.initialize(owlModel.getFrameFactory(), driver, url, userName, "", tableName, false);
			db.overwriteKB(owlModel, true);
			
		} catch (Exception e) {
			throw new OMTException("Problem loading knowledgebase into db! ", e);
		}
	}
}
