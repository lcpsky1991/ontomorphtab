package wbctest.wbcdb;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import junit.framework.TestCase;

public class TestMorphMLNeuronMorphology extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology.getCableCount()'
	 */
	public void testGetCableCount() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology.getCable(int)'
	 */
	public void testGetCableInt() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology.getCable(BigInteger)'
	 */
	public void testGetCableBigInteger() {

	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology.MorphMLNeuronMorphology(String)'
	 */
	public void testMorphMLNeuronMorphologyString() {
		MorphMLNeuronMorphology neuron = new MorphMLNeuronMorphology("cell1zr");
		neuron.getMorphMLCell();
	}

}
