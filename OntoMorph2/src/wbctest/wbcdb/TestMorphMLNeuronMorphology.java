package wbctest.wbcdb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;

public class TestMorphMLNeuronMorphology extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.MorphMLNeuronMorphology.getCableCount()'
	 */
	public void testGetCableCount() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.MorphMLNeuronMorphology.getCable(int)'
	 */
	public void testGetCableInt() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.MorphMLNeuronMorphology.getCable(BigInteger)'
	 */
	public void testGetCableBigInteger() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.MorphMLNeuronMorphology.MorphMLNeuronMorphology(String)'
	 */
	public void testMorphMLNeuronMorphologyString() {
		NeuronMorphology neuron = new NeuronMorphology("cell1zr");
		neuron.getMorphMLCell();
	}

}
