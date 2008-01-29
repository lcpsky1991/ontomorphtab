package edu.ucsd.ccdb.ontomorph2.view;


/**
 * @$comment Tree panel that allows viewing both named entites defined explicitly by an
 * ontology and entities defined by implicit definitions such as by property or
 * location.  The first branch splits explicit and implicit classes.  Explicit classes
 * have branches with their given names and have children which are any
 * individuals that are defined as members of that class.  Implicit classes have
 * branches labelled as defined and have children that are either other explicit classes
 * that fall under the category or individuals.  Panel allows the scope of the
 * entire tree to be changed to different categories.  Available categories include
 * "cells", "cellular components & regional parts", "molecules".  When an explicit class
 * is selected, its properties can be displayed in an associated EntityPropertiesPanel.
 * Panel includes a search box, a button for an advanced search which brings up 
 * an EntitySearchPanel.  Includes a button for adding implicit and explicit entities.
 */

public class ExplicitImplicitEntityTreePanel {

			
			/**
			 * @link aggregationByValue
			 */
			
			edu.ucsd.ccdb.ontomorph2.view.EntityPropertiesPanel lnkEntityPropertiesPanel = null;
		edu.ucsd.ccdb.ontomorph2.view.EntitySearchPanel lnkEntitySearchPanel = null;

}
