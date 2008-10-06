package wbctest.ckb;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;

public class TestSemanticClass extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThing.getCls()'
	 */
	public void testGetCls() {

		SemanticClass s = SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.IMAGE_CLASS);
		assertNotNull(s);
		
		SemanticInstance i = s.createInstance();
		assertNotNull(i);
		
		SemanticClass s2 = SemanticRepository.getAvailableInstance().getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB");
		assertNotNull(s2);
	}

}
