package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindResult;
import edu.stanford.smi.protegex.owl.ui.search.finder.ResultsViewModelFind;


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
	
	public Set<SemanticInstance> createSimpleQuery(String queryString) {
		Set<SemanticInstance> results = new HashSet<SemanticInstance>();
		SemanticRepository repo = SemanticRepository.getAvailableInstance();
		OWLModel owlModel = repo.getOWLModel();
		/*
		RDFProperty property = (RDFProperty)owlModel.getSlot("rdfs:label");
        
        Collection matches = owlModel.getMatchingResources(property, queryString, -1);
        List result = new ArrayList();
        for (Iterator it = matches.iterator(); it.hasNext();) {
        	Object r = it.next();
        	if (r instanceof OWLNamedClass){
				results.addAll(repo.getInstancesFromRoot(new SemanticClass((OWLNamedClass)r), false));
			}
        }*/
        
		//use Protege's Basic Find class to define a search where the query is contained in the term
		ResultsViewModelFind f = new ResultsViewModelFind(owlModel, Find.CONTAINS);
		f.addResultListener(null);
		f.startSearch(queryString);
		//The results will be a set of classes, not instances
		//Use those classes to look up any instances that exist in the 
		//knowledge base right now.
		Map<RDFResource, FindResult> resultMap = f.getResults();
		
		for (RDFResource r : resultMap.keySet()) {
			if (r instanceof DefaultOWLIndividual){
				//results.addAll(repo.getInstancesFromRoot(new SemanticClass((OWLNamedClass)r), false));
				results.add(SemanticRepository.getAvailableInstance().getSemanticInstance((OWLIndividual)r));
			}
		}
		//return these instances as a list of results.
		return results;
	}

}
