package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.semantic.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;

public class TestSemanticClass extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThingImpl.getCls()'
	 */
	public void testGetCls() {
		try {
		SemanticClass s = GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao830368389");
		assertNotNull(s);
		
		SemanticClass s2 = GlobalSemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB");
		assertNotNull(s2);
		} catch (OMTOfflineException e) {
			e.printStackTrace();
		}

	}

}
