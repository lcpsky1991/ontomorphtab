package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.morphml.morphml.schema.Cable;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines a group of segments in a neuron morphology.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISegment
 * @see INeuronMorphology
 *
 */
public interface ICable extends INeuronMorphologyPart{

	public List<String> getTags();

	public void setMorphMLCable(Cable cable);


}
