package edu.ucsd.ccdb.ontomorph2.core.data;

import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyNode;

public interface SemanticRepository {

	public OWLModel getOWLModel();

	public SemanticClass getSemanticClass(String uri);

	public MyNode getInstanceTree();

	/**
	 * Get the Instances in the database for all children of the root rootClass
	 * 
	 * @param rootClass - the class at the top of the hierarchy from which you want to retrieve instances
	 * @param requireLabel - if set to true, requires that the instance have a label in order to be included in the return list
	 * @return
	 */
	public List<SemanticInstance> getInstancesFromRoot(SemanticClass rootClass,
			boolean requireLabel);

	/**
	 * Get all instances in the database under the root of Cell
	 * @return a list of SemanticInstanceImpls
	 */
	public List<SemanticInstance> getCellInstances();

	/**
	 * Retrieve the rdfs:Label for an OWL Class from the database
	 */
	public String getClassLabel(Cls OWLClass, String URI);

	/**
	 * Gets the CCDB Microscopy Product IDs that correspond to data sets taken from Mouse
	 * @return
	 */
	public int[] getMPIDsForMouse();

	public List<SemanticInstance> getMicroscopyProductInstances();

	/**
	 * Creates a new OWL instance of the class specified in the parameter.
	 * @param string
	 * @return
	 */
	public SemanticInstance createNewInstanceOfClass(String uri);

	public SemanticInstance getSemanticInstance(String uri);

}