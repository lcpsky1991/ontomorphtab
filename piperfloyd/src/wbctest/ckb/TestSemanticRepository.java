package wbctest.ckb;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import junit.framework.TestCase;

public class TestSemanticRepository extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository.getSemanticClass(String)'
	 */
	public void testGetSemanticClass() {
		//get semantic thing for a pyramidal cell

		assertNotNull(SemanticRepository.getInstance().getSemanticClass("sao:sao830368389"));
		
		assertNotNull(SemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"));
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository.getMicroscopyProducts()'
	 */
	public void testGetMicroscopyProducts() {
		//there are 184 microscopy product instances in the current version of the CKB.
		assertTrue(SemanticRepository.getInstance().getMicroscopyProductInstances().size() == 184);
	}

}
