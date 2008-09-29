package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;

/**
 * Represents an axon of a NeuronMorphology.  This is treated as a separate object 
 * because axons are represented as objects that can be routed through multiple brain
 * structures.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Axon extends Tangible {
	CatmullRomCurve c = null;
	
	public Axon() {
		
	}
}
