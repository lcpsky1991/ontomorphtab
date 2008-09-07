package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;

public class TestLocalSemanticRepository extends TestCase {
	
	GlobalSemanticRepository repo = null;
	
	public void setUp() {
		try {
			repo = GlobalSemanticRepository.getInstance();
		}catch (OMTOfflineException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getSemanticClass(String)'
	 */
	public void testGetSemanticClass() {
		//get semantic thing for a pyramidal cell

		assertNotNull(repo.getSemanticClass("sao:sao830368389"));
		
		assertNotNull(repo.getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"));
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getMicroscopyProducts()'
	 */
	public void testGetMicroscopyProducts() {
		//there are 577 microscopy product instances in the current version of the CKB.
		System.err.println(repo.getMicroscopyProductInstances().size());
		assertTrue(repo.getMicroscopyProductInstances().size() == 577);
	}
	
	public void testGetMPIDsForMouse() {
		repo.getMPIDsForMouse();
	}

}
