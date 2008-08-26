package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;

public class TestLocalSemanticRepository extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getSemanticClass(String)'
	 */
	public void testGetSemanticClass() {
		//get semantic thing for a pyramidal cell

		assertNotNull(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao830368389"));
		
		assertNotNull(GlobalSemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"));
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getMicroscopyProducts()'
	 */
	public void testGetMicroscopyProducts() {
		//there are 184 microscopy product instances in the current version of the CKB.
		System.err.println(GlobalSemanticRepository.getInstance().getMicroscopyProductInstances().size());
		assertTrue(GlobalSemanticRepository.getInstance().getMicroscopyProductInstances().size() == 577);
	}
	
	public void testGetMPIDsForMouse() {
		GlobalSemanticRepository.getInstance().getMPIDsForMouse();
	}

}
