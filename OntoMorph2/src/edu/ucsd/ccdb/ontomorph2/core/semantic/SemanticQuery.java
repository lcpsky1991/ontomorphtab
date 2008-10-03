package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;


/**
 * Represents a query that can be presented to the semantic repository to retrieve a set of 
 * semantic things.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see GlobalSemanticRepository
 * @see ISemanticThing
 *
 */
public class SemanticQuery {
	
	String queryString = null;
	public List<SemanticInstance> createSimpleQuery(String queryString) {
		this.queryString= queryString;
		//use Protege's Basic Find class
		
		//The results will be a set of classes, not instances
		//Use those classes to look up any instances that exist in the 
		//knowledge base right now.
		
		//return these instances as a list of results.
		return null;
	}

}
