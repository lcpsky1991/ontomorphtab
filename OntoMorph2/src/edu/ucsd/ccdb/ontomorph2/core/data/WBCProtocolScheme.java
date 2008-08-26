package edu.ucsd.ccdb.ontomorph2.core.data;

import java.net.URI;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

public class WBCProtocolScheme {

	public URI getOntologyClassURI(SemanticClass s) {
		return URI.create("ontology/nif/class#" + s.getId());
	}
	
	public URI getOntologyInstanceURI(SemanticInstance i) {
		return URI.create("ontology/nif/instance#" + i.getId());
	}
	
	public URI getDataStructureURI(NeuronMorphology n) {
		return URI.create("data/structure/" + n.getName());
	}
	
	public URI getLocationURI(PositionVector p) {
		return URI.create("location/" + p.x + "/" + p.y + "/" + p.z);
	}
	
	/*
	public URI getImageURI(Slide s) {
		return URI.create("data/image/" );
	}
	
	
	public URI getBrainRegionURI() {
		
	}
	
	public URI getUserURI() {
		
	}
	
	public URI getReferenceURI() {
		
	}
	*/
	
}
