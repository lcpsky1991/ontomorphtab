package edu.ucsd.ccdb.ontomorph2.view.misc;


/**
 * @$comment Provides a user-friendly graphical interface to allow neuroscientists to build up
 * descriptions of biological entities.  Allows descriptions of explicit types of things
 * (Neurons, Ribosomes, Spines) as well as implicit types of things by a property or
 * set of properties (all neurons with dopamine receptors, all neurons in the hippocampus,
 * all neurons in the hippocampus with dopamine receptors).  Allows descriptions of
 * specific things within a type (one unique neuron in the hippocampus that has
 * dopamine receptors).  Is associated with the StatementWizard, which allows
 * those descriptions to be included in SemanticStatements, which create additional implicit
 * properties for those entites in the statements.
 * @see edu.ucsd.ccdb.ontomorph2.core.misc.SemanticStatement
 * @see edu.ucsd.ccdb.ontomorph2.view.misc.EntityViewer
 * @see edu.ucsd.ccdb.ontomorph2.view.misc.ExplicitImplicitEntityTreePanel
 * @see edu.ucsd.ccdb.ontomorph2.view.misc.StatementWizard
 */

public class EntityDetailView {

	edu.ucsd.ccdb.ontomorph2.view.misc.StatementWizard lnkStatementWizard = null;

	/**
	 * @link aggregationByValue
	 */
	EntityViewer lnkMorphologyViewer = null;

	/**
	 * @link aggregationByValue
	 */
	edu.ucsd.ccdb.ontomorph2.view.misc.ExplicitImplicitEntityTreePanel lnkNamedPlusCustomEntityTreePanel = null;
}
