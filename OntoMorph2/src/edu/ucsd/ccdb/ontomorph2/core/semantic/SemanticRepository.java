package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyNode;


/**
 * Represents a database of semantic things.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
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
	

	
	public KnowledgeBase getOWLModel() {
		return owlModel;
	}

	public SemanticClass getSemanticClass(String uri) {
		Cls cls = null;
		if (owlModel != null) {
			try {
				cls = clsFlyweightStore.get(uri);
				if (cls == null) {
					cls = owlModel.getCls(uri);
					clsFlyweightStore.put(uri, cls);
				}
			} catch (Exception e ) {
				throw new OMTException("Problem finding URI in semantic repository" + uri, e);
			}
		}
		SemanticClass s = new SemanticClass(cls, uri);
		s.addObserver(SceneObserver.getInstance());
		return s;
	}
	
	public MyNode getInstanceTree() {
		MyNode root = new MyNode("Instances", null);
		for (SemanticInstance si : this.getCellInstances()) {
			root.children.add(new MyNode(si.getLabel(), si));
		}
		return root;
	}
	
	/**
	 * Get the Instances in the database for all children of the root rootClass
	 * 
	 * @param rootClass - the class at the top of the hierarchy from which you want to retrieve instances
	 * @param requireLabel - if set to true, requires that the instance have a label in order to be included in the return list
	 * @return
	 */
	public List<SemanticInstance> getInstancesFromRoot(Cls rootClass, boolean requireLabel) {
		List<SemanticInstance> runningList = new ArrayList<SemanticInstance>();
		for (Iterator it = rootClass.getInstances().iterator(); it.hasNext(); ) {
			Instance i = (Instance)it.next();
			if (i instanceof SimpleInstance) {
				SemanticInstance si = new SemanticInstance(i);
				if (requireLabel) {
					if (si.getLabel() != null) {
						runningList.add(si);
					}
				} else {
					runningList.add(si);
				}
			}
			
		}
		return runningList;
	}

	/**
	 * Get all instances in the database under the root of Cell
	 * @return a list of SemanticInstanceImpls
	 */
	public List<SemanticInstance> getCellInstances() {
		return getInstancesFromRoot(getSemanticClass("sao:sao1813327414").getCls(), true);
	}
	
	//initialize the semantic repository by connecting to the 
	//database and retriving a knowledge base object
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
    		throw new OMTException("Cannot connect to OWL Database!", e);
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

	/**
	 * Retrieve the rdfs:Label for an OWL Class from the database
	 */
	public String getClassLabel(Cls OWLClass, String URI) {
//		must be done before getLabel() is run!!!

		String label = null;
		
		if (owlModel != null) {
			Slot rdfsLabel = owlModel.getSlot("rdfs:label");
			
			//Cls root = owlModel.getRootCls();
			//Cls entity = owlModel.getCls("bfo:Entity");
			//System.out.println("The root class is: " + entity.getName());
			//Node rootNode = getTree().addRoot();
			rdfsLabel = owlModel.getSlot("rdfs:label");
			label = (String)OWLClass.getDirectOwnSlotValue(rdfsLabel);
			String prefix = null;//owlModel.getPrefixForResourceName(entity.getName());
			if (prefix != null) {
				label =  prefix + ":" + label;
			}
			if (URI != null) {
				label = label + "(" + URI + ")";
			}
		}			
		return label;

	}


	public List<SemanticInstance> getMicroscopyProductInstances() {
		return getInstancesFromRoot(getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB").getCls(), false);
	}
	
	/**
	 * Creates a new OWL instance of the class specified in the parameter.
	 * @param string
	 * @return
	 */
	public ISemanticThing createNewInstanceOfClass(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}
