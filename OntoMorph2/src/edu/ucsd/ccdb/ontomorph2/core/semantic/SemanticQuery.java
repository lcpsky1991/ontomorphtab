package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;

/**
 * Represents a query that can be presented to the semantic repository to retrieve a set of semantic things.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see SemanticRepository
 * @see ISemanticThing
 *
 */
public class SemanticQuery {
	
	String queryString = null;
	public void createSimpleQuery(String queryString) {
		this.queryString= queryString;
	}

}
