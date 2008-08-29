package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;

/** 
 * Represents an OWL class.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticClass extends SemanticThingImpl {

	public static final String DENTATE_GYRUS_GRANULE_CELL_CLASS = "nif_cell:nifext_153";
	public static final String CA3_PYRAMIDAL_CELL_CLASS = "nif_cell:nifext_158";
	public static final String CA1_PYRAMIDAL_CELL_CLASS = "nif_cell:nifext_157";
	
	OWLNamedClass OWLClass = null;

	public SemanticClass(OWLNamedClass owlClass, String uri) {
		this(owlClass);
		this.URI = uri;
	}
	
	public SemanticClass(OWLNamedClass owlClass) {
		OWLClass = owlClass;
	}

	/**
	 * Get the OWL label
	 */
	public String getLabel() {
		return GlobalSemanticRepository.getInstance().getClassLabel(OWLClass, URI);
	}
	
	public String toString() {
		return getLabel();
	}
	
	/**
	 * Get direct instances of this class
	 * @return
	 */
	public List<SemanticInstance> getInstances() {
		List<SemanticInstance> l = new ArrayList<SemanticInstance>();
		Collection instances = OWLClass.getInstances(false);
		for (Iterator it = instances.iterator(); it.hasNext();) {
			SemanticInstance si = new SemanticInstance((OWLIndividual)it.next());
			l.add(si);
		}
		return l;
	}

	public String getId() {
		return OWLClass.getName();
	}
	
	public String getURI() {
		return this.URI;
	}
	
}
