package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.TreeNode;

/**
 * Parent class for a semantic repository.  Subclasses may have different locations for where
 * the database resides.  This wraps around an ontology class and instance store.  
 * Operations here retrieve objects from the Protege libraries that interact with the ontology store.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public abstract class SemanticRepository {

	OWLDatabaseModel owlModel = null;
	Map<String, OWLNamedClass> clsFlyweightStore = new HashMap<String,OWLNamedClass>();
	Map<String, OWLIndividual> insFlyweightStore = new HashMap<String,OWLIndividual>();
	Map<String, AbstractOWLProperty> propFlyweightStore = new HashMap<String,AbstractOWLProperty>();
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository#getOWLModel()
	 */
	public OWLModel getOWLModel() {
		return owlModel;
	}
	
	/**
	 * Returns the LocalSemanticRepository for now.
	 * @return
	 */
	public static SemanticRepository getAvailableInstance() {
		return LocalSemanticRepository.getInstance();
		/*
		try {
			return GlobalSemanticRepository.getInstance();
		} catch (OMTOfflineException e) {
			return LocalSemanticRepository.getInstance();
		}*/
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository#getSemanticClass(java.lang.String)
	 */
	public SemanticClass getSemanticClass(String uri) {
		OWLNamedClass cls = null;
		if (owlModel != null) {
			try {
				cls = clsFlyweightStore.get(uri);
				if (cls == null) {
					cls = owlModel.getOWLNamedClass(uri);
					clsFlyweightStore.put(uri, cls);
				}
			} catch (Exception e ) {
				throw new OMTException("Problem finding URI in semantic repository" + uri, e);
			}
		}
		SemanticClass s = new SemanticClass(cls, uri);
		return s;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository#getInstanceTree()
	 */
	public TreeNode getInstanceTree() {
		TreeNode root = new TreeNode("Instances", null);
		for (SemanticInstance si : this.getObjectInstances()) {
			TreeNode node = new TreeNode(si.getId(), si);
			
			for (SemanticProperty p : si.getProperties()) {
				SemanticInstance subject = si.getPropertyValue(p);
				if (subject != null) {
					node.children.add(new TreeNode(p.getLabel() + ": " + subject.getId(), null));
				}
			}
			
			root.children.add(node);
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
	public List<SemanticInstance> getInstancesFromRoot(SemanticClass rootClass, boolean requireLabel) {
		List<SemanticInstance> runningList = new ArrayList<SemanticInstance>();
		
		for (Iterator it = rootClass.getAllInstances(owlModel).iterator(); it.hasNext(); ) {
			SemanticInstance i = (SemanticInstance)it.next();
			if (requireLabel) {
				if (i.getLabel() != null) {
					runningList.add(i);
				}
			} else {
				runningList.add(i);
			}
			/*
			OWLIndividual i = (OWLIndividual)it.next();
			if (i instanceof SimpleInstance) {
				SemanticInstance si = new SemanticInstance(i);
				if (requireLabel) {
					if (si.getLabel() != null) {
						runningList.add(si);
					}
				} else {
					runningList.add(si);
				}
			}*/
		}
		return runningList;
	}

	/**
	 * Get all instances in the database under the root of Cell
	 * @return a list of SemanticInstanceImpls
	 */
	public List<SemanticInstance> getCellInstances() {
		return getInstancesFromRoot(getSemanticClass(SemanticClass.CELL_CLASS), false);
	}
	
	/**
	 * Get all instances in the database under the root of BFO Object
	 * @return a list of SemanticInstances
	 */
	public List<SemanticInstance> getObjectInstances() {
		return getInstancesFromRoot(getSemanticClass(SemanticClass.INDEPENDENT_CONTINUANT_CLASS), false);	
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

	/**
	 * Gets the CCDB Microscopy Product IDs that correspond to data sets taken from Mouse
	 * @return
	 */
	public int[] getMPIDsForMouse() {
		/*
		QueryResults qr = null;
		try {
		String q = "SELECT distinct ?mpid" + 
"\nWHERE" + 
"\n{" +
"\n?subject rdf:type ccdb:MICROSCOPYPRODUCT_OBJTAB ." +
"\n?subject ccdb:mpid ?mpid ." +
"\n?subject ccdb:SECURITY_LEVEL ?level ." +
"\n?subject obo_base:has_part ?someting . "+
"\n?something rdf:type birn_org_tax:birnlex_167 . " +
"\nfilter regex(str(?level),\"1\")" +
"\n}" +
"\norder by ?mpid";
		qr = ((OWLModel)getOWLModel()).executeSPARQLQuery(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (qr != null) {
			if (qr.hasNext()) {
				List vars = qr.getVariables();
				Map varsToVals = qr.next();
                
				for (Object o : vars) {
					RDFObject rdf = (RDFObject)varsToVals.get((String)o);
					if (rdf instanceof RDFSLiteral) {
						RDFSLiteral rdfs = (RDFSLiteral)rdf;
						System.out.println(rdfs.getInt());
					}
				}
            }
		}
		*/
		//can't get above code to work, so for now hard coding the values
		int[] hackOutput = {1, 14, 16, 17, 29, 3, 30, 31, 32, 33, 3339, 3379, 
				  3380, 3382, 3383, 3384, 35, 3561, 3563, 3587, 3592, 
				  3593, 36, 3652, 3659, 3687, 3693, 39, 3993, 3996, 
				  4, 40, 4003, 	4043, 4044, 4045, 4046, 4047, 4048,
				  4049, 4050, 4051, 4052, 4053, 4054, 4055, 4056, 
				  4057, 4058, 4059, 4060, 4061, 4062, 4063, 4064, 
				  4065, 4066, 4067, 4068, 4069, 4070, 4071, 4072,
				  4073, 4074, 4075, 41, 48, 49, 50, 51, 53, 54, 
				  6, 7};
		return hackOutput;
	}

	public List<SemanticInstance> getMicroscopyProductInstances() {
		return getInstancesFromRoot(getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"), false);
	}
	
	public SemanticProperty getSemanticProperty(String uri) {
		AbstractOWLProperty prop = null;
		if (owlModel != null) {
			try {
				prop = propFlyweightStore.get(uri);
				if (prop == null) {
					prop = (AbstractOWLProperty)owlModel.getOWLProperty(uri);
					propFlyweightStore.put(uri, prop);
				}
			} catch (Exception e ) {
				throw new OMTException("Problem finding URI in semantic repository" + uri, e);
			}
		}
		SemanticProperty p = new SemanticProperty(prop);
		//s.addObserver(SceneObserver.getInstance());
		return p;
	}
	
	public abstract SemanticInstance getSemanticInstance(String uri);
}