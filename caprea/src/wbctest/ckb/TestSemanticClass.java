package wbctest.ckb;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import junit.framework.TestCase;

public class TestSemanticClass extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThingImpl.getCls()'
	 */
	public void testGetCls() {
		SemanticClass s = SemanticRepository.getInstance().getSemanticClass("sao:sao830368389");
		assertNotNull(s.getCls());
		
		SemanticClass s2 = SemanticRepository.getInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB");
		assertNotNull(s2.getCls());

	}

}
