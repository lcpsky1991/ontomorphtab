package edu.ucsd.ccdb.ontomorph2.util;

import java.sql.Connection;
import java.sql.DriverManager;

import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;

public class LocalSemanticRepositorySetup {

	public static void main(String args[]) {
		try {
			
			JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI("http://purl.org/nif/ontology/nif.owl");

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
			
		} catch (Exception e) {
			throw new OMTException("Cannot load NIF ontology!", e);
		}	
	}
}
