package edu.ucsd.ccdb.ontomorph2.core;

/**
 * @$comment A semantic statement is a particular kind of OWL statement that combines explicit
 * and implicit semantic entities with properties.  Statements are also associated with
 * References.  A statement should appear in the properties of all explicit and implicit
 * entities named in the statement.
 * For example:  All Apical Dendrites of Pyramidal Cells have Glutamate Receptors.
 * Represent as an OWL class that has Necessary & Sufficient conditions matching the following:
 * Apical_Dendrite and (is_Regional_Part_Of some Pyramidal_Cell)
 * and necessary conditions matching:
 * has_Molecular_Constituent some GLU-R2_Receptor
 * When looking at the properties of the explicit class "Apical_Dendrite" we should see:
 * has_Molecular_Constituent (GLU-R2_Receptor when is_Regional_Part_Of Pyramidal_Cell)
 * (this coresponds to a process of looking up children of the class Apical Dendrite,
 * separating out what makes each child distinct from the parent (either having a different
 * name, or having an additional property), and then listing its necessary properties.)
 * When looking at the properties of the explicit class "GLU-R2_Receptor" we should see:
 * is_Molecular_Constituent_Of (Apical_Dendrite when is_Regional_Part_Of Pyramidal_Cell)
 * (this corresponds to a process of looking for all restrictions that include GLU-R2_Receptors 
 * and listing their inverse properties as inverse properties.  I think Protege does this already)
 * When looking at the properties of the explicit class "Pyramidal_Cell" we should see:
 * has_Regional_Part (Apical_Dendrite has_Molecular_Constituent GLU-R2_Receptor)
 * (I'm not sure how to implement this).
 */

public class SemanticStatement {

			private edu.ucsd.ccdb.ontomorph2.core.ISemanticThing lnkISemanticThing;
		/**
	 * @associates edu.ucsd.ccdb.ontomorph2.core.Reference
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkReference = null;
}
