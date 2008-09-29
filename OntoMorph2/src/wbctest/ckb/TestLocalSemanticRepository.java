package wbctest.ckb;

import java.util.List;

import junit.framework.TestCase;
import edu.ucsd.ccdb.ontomorph2.core.semantic.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.LocalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticProperty;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.TreeNode;

public class TestLocalSemanticRepository extends TestCase {
	
	LocalSemanticRepository repo = null;
	
	public void setUp() {
		repo = LocalSemanticRepository.getInstance();
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository.getSemanticClass(String)'
	 */
	public void testGetSemanticClass() {
		//get semantic thing for a pyramidal cell

		assertNotNull(repo.getSemanticClass("sao:sao830368389"));
		
		//assertNotNull(repo.getSemanticClass("ccdb:MICROSCOPYPRODUCT_OBJTAB"));
	}

	public void testGetSemanticInstance() {
		SemanticClass s = repo.getSemanticClass(SemanticClass.IMAGE_CLASS);
		assertNotNull(s);
		
		SemanticInstance i = s.createInstance();
		assertNotNull(i);
		
		List<SemanticInstance> instances = repo.getInstancesFromRoot(s, false);
		assertTrue(instances.contains(i));
		
		i.removeFromRepository();
		List<SemanticInstance> instances2 = repo.getInstancesFromRoot(s, false);
		assertFalse(instances2.contains(i));
		
	}
	
	public void testGetCellInstances() {
		SemanticClass s = repo.getSemanticClass("sao:sao830368389");
		SemanticInstance i = s.createInstance();
		assertNotNull(i);
		
		List<SemanticInstance> cellInstances = repo.getCellInstances();
		assertTrue(cellInstances.contains(i));
		
		for (SemanticInstance si : repo.getCellInstances()) {
			for (SemanticProperty p : si.getProperties()) {
				//assertNotNull(si.getPropertyValue(p));
			}
		}
	}
	
	public void testGetSemanticProperty() {
		SemanticProperty p = repo.getSemanticProperty(SemanticProperty.CONTAINS);
		assertNotNull(p);
	}
	
	public void testAssignPropertyBetweenInstances() {
		SemanticProperty p = repo.getSemanticProperty(SemanticProperty.CONTAINS);
		SemanticClass s = repo.getSemanticClass("sao:sao830368389");
		SemanticInstance i = s.createInstance();
		SemanticInstance i2 = s.createInstance();
		
		i.setPropertyValue(p, i2);
		boolean existsInPropsList = false;
		for (SemanticProperty p2 : i.getProperties()){
			if (p2.equals(p)) {
				existsInPropsList = true;
			}
		}
		assertTrue(existsInPropsList);
		
		assertEquals(i.getPropertyValue(p), i2);
		
		i.removePropertyValue(p, i2);
		assertNull(i.getPropertyValue(p));
	}
	
	public void testGetMPIDsForMouse() {
		repo.getMPIDsForMouse();
	}

}
