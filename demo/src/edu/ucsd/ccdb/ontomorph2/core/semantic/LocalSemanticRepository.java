package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;


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
		List<SemanticInstance> si = new ArrayList<SemanticInstance>();
		Collection c = owlModel.getInstances(getSemanticClass(SemanticClass.CONTINUANT_CLASS).getOWLClass());
		for (Iterator it = c.iterator(); it.hasNext();) {
			OWLIndividual ind = (OWLIndividual)it.next();
			ind.delete();
		}
	}
	
	//initialize the semantic repository by connecting to the 
	//database and retriving a knowledge base object
	private void loadOntology() {
    	try {

    		Project p = Project.loadProjectFromFile("etc/NIF/localSemanticRepository.pprj", new ArrayList());
    		
			//projectManager.loadProject(uri);
			owlModel = (OWLDatabaseModel)p.getKnowledgeBase();
    	} catch (Exception e) {
    		throw new OMTException("Cannot connect to Local OWL Database!  Make sure there isn't " +
    				"another instance of the database running and that you have loaded the semantic " +
    				"database schema!", e);
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
}
