package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.semantic.LocalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;

public class TestSemanticRepository extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getSemanticClass(String)'
	 */
	public void testGetSemanticClass() {
		//get semantic thing for a pyramidal cell

		SemanticClass s = LocalSemanticRepository.getInstance().getSemanticClass("sao:sao830368389");
		
		assertNotNull(s);
		
		assertNotNull(LocalSemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"));
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getMicroscopyProducts()'
	 */
	/*
	public void testGetMicroscopyProducts() {
		//there are 184 microscopy product instances in the current version of the CKB.
		System.err.println(LocalSemanticRepository.getInstance().getMicroscopyProductInstances().size());
		assertTrue(LocalSemanticRepository.getInstance().getMicroscopyProductInstances().size() == 577);
	}
	
	public void testGetMPIDsForMouse() {
		LocalSemanticRepository.getInstance().getMPIDsForMouse();
	}*/

}
