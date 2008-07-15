package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.stanford.smi.protege.model.Cls;

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

	public SemanticClass(Cls owlClass, String uri) {
		super(owlClass, uri);
		// TODO Auto-generated constructor stub
	}

}
