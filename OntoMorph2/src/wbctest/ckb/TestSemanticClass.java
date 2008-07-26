package wbctest.ckb;

import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import junit.framework.TestCase;

public class TestSemanticClass extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThingImpl.getCls()'
	 */
	public void testGetCls() {
		SemanticClass s = SemanticRepository.getInstance().getSemanticClass("sao:sao830368389");
		assertNotNull(s);
		
		SemanticClass s2 = SemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB");
		assertNotNull(s2);

	}

}
