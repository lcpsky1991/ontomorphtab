package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory2;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.TreeNode;


/**
 * Represents a database of semantic things.  This database is stored on the local disk via 
 * as Hypersonic SQL database
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class LocalSemanticRepository extends SemanticRepository{
	
	/**
	 * Holds singleton instance
	 */
	private static LocalSemanticRepository instance;
	
	private LocalSemanticRepository() {
		this.loadOntology();
		//start with a fresh workspace without instances each time local semantic repository loads up
		removeWorkspaceInstances();
	}
	
	
	public void removeWorkspaceInstances() {
		for (SemanticInstance obj : this.getObjectInstances()) {
			obj.removeFromRepository();
		}
	}
	
	//initialize the semantic repository by connecting to the 
	//database and retriving a knowledge base object
	private void loadOntology() {
    	try {

    		/*
    		OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
    		ArrayList errors = new ArrayList();
    		owlModel = (OWLDatabaseModel)factory.createKnowledgeBase(errors);
    		
    		
			String driver = "org.hsqldb.jdbcDriver";
			String url = "jdbc:hsqldb:db/db";
			String tableName = "nifontology";			
			String userName = "sa";
    		
			
			try {
				DatabaseFrameDb db = new DatabaseFrameDb();
				db.initialize(owlModel.getFrameFactory(), driver, url, userName, "", tableName, false);

			} catch (Exception e) {
				throw new OMTException("Problem loading knowledgebase into db! ", e);
			}
			
			*/
			/*
            NarrowFrameStore nfs = ((KnowledgeBaseFactory2)factory).createNarrowFrameStore(driver);
            MergingNarrowFrameStore mergingFrameStore = MergingNarrowFrameStore.get(owlModel);
            
            ArrayList uris = new ArrayList();
            mergingFrameStore.addActiveFrameStore(nfs, uris);
            */
			
    		//factory.loadKnowledgeBase(owlModel, driver, tableName, url, userName, "", errors);
    		
    		Project p = Project.loadProjectFromFile("etc/NIF/localSemanticRepository.pprj", new ArrayList());
    		
			//projectManager.loadProject(uri);
			owlModel = (OWLDatabaseModel)p.getKnowledgeBase();
    	} catch (Exception e) {
    		throw new OMTException("Cannot connect to Local OWL Database!", e);
    	}		
    }

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public LocalSemanticRepository getInstance() {
		if (instance == null) {
			instance = new LocalSemanticRepository();
		}
		return instance;
	}

	public SemanticInstance getSemanticInstance(String uri) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
