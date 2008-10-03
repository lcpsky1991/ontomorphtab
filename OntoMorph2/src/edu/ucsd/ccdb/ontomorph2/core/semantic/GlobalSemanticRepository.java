package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;


/**
 * Represents a database of semantic things.  This database sits on a central server that 
 * all clients connect to in the same way.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class GlobalSemanticRepository extends SemanticRepository {
	
	/**
	 * Holds singleton instance
	 */
	private static GlobalSemanticRepository instance;
	
	private GlobalSemanticRepository() throws OMTOfflineException{
		if (!OntoMorph2.isOfflineMode()) {
			this.loadOntology();
		} else {
			throw new OMTOfflineException("Cannot use Global Semantic Repository when in offline mode!");
		}
	}

		
	//initialize the semantic repository by connecting to the 
	//database and retriving a knowledge base object
	private void loadOntology() throws OMTOfflineException{
    	try {
    		    		
    		ApplicationProperties.setUrlConnectTimeout(100000);
			ApplicationProperties.setUrlConnectReadTimeout(100000);
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyPort", "8080");
			System.getProperties().put("proxyHost", "http://webproxy.ucsd.edu/proxy.pl");
//			JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI("http://ccdb.ucsd.edu/SAO/1.2.9/SAO.owl");
			//JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI("http://purl.org/nbirn/birnlex/ontology/BIRNLex-Anatomy.owl");
    		//JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI("http://purl.org/nif/ontology/nif.owl");
			
			//ProjectManager projectManager = ProjectManager.getProjectManager();
			//URI uri = new URI("file://C:\/Documents\and\ Settings\/stephen\/Desktop\/nifSaved\/nif.pprj");
						
			//Project p = Project.loadProjectFromFile("etc/NIF/CKB_db.pprj", new ArrayList());
			Project p = Project.loadProjectFromFile("etc/NIF/CKB_mega_db_v2.pprj", new ArrayList());
						
			//projectManager.loadProject(uri);
			owlModel = (OWLDatabaseModel)p.getKnowledgeBase();
					    			
			/*
			JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
			RepositoryManager rp = new RepositoryManager(owlModel);
			File dir = new File("C:/Documents and Settings/stephen/Desktop/nifSaved");
			rp.addGlobalRepository(new LocalFolderRepository(dir));
			FileReader reader = new FileReader("C:/Documents and Settings/stephen/Desktop/nifSaved/nif.owl");			
			owlModel.load(reader, FileUtils.langXMLAbbrev);
    		*/
    		
			/*
    		//must be done before getLabel() is run!!!
    		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
    		
    		if (owlModel != null) {

    			Cls root = owlModel.getRootCls();
    			Cls entity = owlModel.getCls("bfo:Entity");
    			System.out.println("The root class is: " + entity.getName());
    			//Node rootNode = getTree().addRoot();
    			rdfsLabel = owlModel.getSlot("rdfs:label");
    			String label = (String)entity.getDirectOwnSlotValue(rdfsLabel);
    			String prefix = owlModel.getPrefixForResourceName(entity.getName());
    			if (prefix != null) {
    				label =  prefix + ":" + label;
    			}
    			//rootNode.setString("label", label);
    			//hm.put(label, entity);
    			
    		}*/
    	} catch (Exception e) {
    		throw new OMTOfflineException("Cannot connect to OWL Database!", e);
    	}	
    }

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public GlobalSemanticRepository getInstance() throws OMTOfflineException{
		if (instance == null) {
			instance = new GlobalSemanticRepository();
		}
		return instance;
	}

	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository#createNewInstanceOfClass(java.lang.String)
	 */
	public SemanticInstance createNewInstanceOfClass(String uri) {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository#getSemanticInstance(java.lang.String)
	 */
	public SemanticInstance getSemanticInstance(String uri) {
		// TODO Auto-generated method stub
		return null;
	}
}
