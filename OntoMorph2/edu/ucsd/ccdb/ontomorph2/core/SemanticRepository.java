package edu.ucsd.ccdb.ontomorph2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;


/**
 * Represents a singleton.
 */

public class SemanticRepository {
	
	//JenaOWLModel owlModel = null;
	KnowledgeBase owlModel = null;
	Map<String, Cls> clsFlyweightStore = new HashMap<String,Cls>();
	/**
	 * Holds singleton instance
	 */
	private static SemanticRepository instance;
	
	private SemanticRepository() {
		this.loadOntology();
	}
	
	public static void main(String[] args) {
		SemanticRepository.getInstance();
		//get semantic thing for a pyramidal cell
		ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		
	}
	
	public KnowledgeBase getOWLModel() {
		return owlModel;
	}

	public ISemanticThing getSemanticThing(String URI) {
		Cls cls = null;
		try {
			cls = clsFlyweightStore.get(URI);
			if (cls == null) {
				cls = owlModel.getCls(URI);
				clsFlyweightStore.put(URI, cls);
			}
		} catch (Exception e ) {
			throw new OMTException("Problem finding URI in semantic repository" + URI, e);
		}
		return new SemanticThingImpl(cls, URI);
	}
	
	private void loadOntology() {
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
						
			Project p = Project.loadProjectFromFile("etc/NIF/CKB_db.pprj", new ArrayList());
						
			//projectManager.loadProject(uri);
			owlModel = p.getKnowledgeBase();
					    			
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
    		e.printStackTrace();
    	}	
    }

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public SemanticRepository getInstance() {
		if (instance == null) {
			instance = new SemanticRepository();
		}
		return instance;
	}
}