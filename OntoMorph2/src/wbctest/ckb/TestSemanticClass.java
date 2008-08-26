package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;

public class TestSemanticClass extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThingImpl.getCls()'
	 */
	public void testGetCls() {
		SemanticClass s = GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao830368389");
		assertNotNull(s);
		
		SemanticClass s2 = GlobalSemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB");
		assertNotNull(s2);

	}

}
