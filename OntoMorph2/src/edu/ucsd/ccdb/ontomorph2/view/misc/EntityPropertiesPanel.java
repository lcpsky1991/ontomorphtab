package edu.ucsd.ccdb.ontomorph2.view.misc;

/**
 * @$comment Displays properties associated with an entity.  If an individual is selected, it will
 * display both the explicit and implicit properties associated with the entity.
 * An explicit property is one within the definition of the individual.  An implicit
 * property is one that is defined within a statement where that individual, or
 * any explicit or implicit classes it belongs to is included.  Implicit properties 
 * indicated next to them what the statement is that causes the property
 * to be displayed and provides a link to that Statement.
 * Next to each property
 * slot is a box that if clicked brings up any references to that property, and if
 * a reference is not included, visually indicates that a reference can be added.
 * Includes a button that if pressed allows a statement to be composed about the
 * currently associated entity (which will show up in the implicit properties when
 * complete).
 * If an explicit class is selected, then the explicit and implicit properties
 * of the class will be displayed.  Additionally, it is possible to view explicit and implicit
 * properties of any individuals of this class.
 * If an implicit class is selected, then it is possible to view explicit and implicit
 * properties of any explicit classes that fall into this class, as well as any
 * individuals of those explicit classes.
 * @see src.edu.ucsd.ccdb.ontomorph2.core.misc.SemanticStatement
 */

public class EntityPropertiesPanel {
}
