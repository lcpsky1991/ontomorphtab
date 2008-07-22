package wbctest.spatial;

import java.util.List;

import com.jme.curve.BezierCurve;
import com.jme.curve.Curve;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import junit.framework.TestCase;

public class TestCurve3D extends TestCase {

	Curve3D testCurve1 = null;
	Curve3D testCurve2 = null;
	OMTVector[] initialControlPoints = null;
	
	public void setUp() {
		OMTVector p1 = new OMTVector(-5,2,20);
		OMTVector p2 = new OMTVector(-16,10,20);
		OMTVector p2a = new OMTVector(-40, -12,20);
		OMTVector p3 = new OMTVector(-50,-8,20);
		OMTVector p4 = new OMTVector(-30,-3,20);
		OMTVector p5 = new OMTVector(-10,-4,20);
				
		OMTVector[] array = {p1, p2, p2a, p3, p4, p5};
		initialControlPoints = array;
		//testCurve1 has no coordinate system associated with it
		testCurve1 = new Curve3D("test curve", initialControlPoints);
		DemoCoordinateSystem d = new DemoCoordinateSystem();
		testCurve2 = new Curve3D("test curve", initialControlPoints, d);
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D.getBezierCurve()'
	 */
	public void testGetBezierCurve() {
		Curve bCurve1 = testCurve1.getCurve();
		assertNotNull(bCurve1);
		
		Curve bCurve2 = testCurve2.getCurve();
		assertNotNull(bCurve2);
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D.getControlPoints()'
	 */
	public void testGetCurveAnchorPoints() {
		List<CurveAnchorPoint> cPoints1 = testCurve1.getAnchorPoints();
		List<CurveAnchorPoint> cPoints2 = testCurve2.getAnchorPoints();
		
		for (int i = 0; i < cPoints1.size(); i++) {
			//should have the same relative positions
			assertTrue(cPoints1.get(i).getRelativePosition().equals(cPoints2.get(i).getRelativePosition()));
			//should have different absolute positions because of coordinate system differences
			assertTrue(!cPoints1.get(i).getAbsolutePosition().equals(cPoints2.get(i).getAbsolutePosition()));
		}
		
		CurveAnchorPoint cPoints1a = cPoints1.get(1);
		CurveAnchorPoint cPoints2a = cPoints1.get(2);
		
		float oldX = cPoints1a.getRelativePosition().x;
		float oldY = cPoints1a.getRelativePosition().y;
		float oldZ = cPoints1a.getRelativePosition().z;
		cPoints1a.move(0.5f, 0.5f, new OMTVector(1,1,0));
		
		assertEquals(cPoints1.get(1).getRelativePosition(), new PositionVector(oldX + 0.5f, oldY + 0.5f, oldZ));
		
		cPoints1a.move(0.5f, 0.5f, new OMTVector(1,1,0));
		cPoints2a.move(0.5f, -0.5f, new OMTVector(1,1,0));
		cPoints2a.move(0.5f, -0.5f, new OMTVector(1,1,0));
	}

	
	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible.getRelativePosition()'
	 */
	public void testGetRelativePosition() {
		PositionVector v1 = testCurve1.getRelativePosition();
		PositionVector v2 = testCurve2.getRelativePosition();
		
		PositionVector test = new PositionVector(0f, 0f, 0f);
		assertEquals(v1, test);
		assertEquals(v2, test);
	}
	
	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible.getAbsolutePosition()'
	 */
	public void testGetAbsolutePosition() {
		PositionVector v1 = testCurve1.getAbsolutePosition();
		PositionVector v2 = testCurve2.getAbsolutePosition();
		
		PositionVector test1 = new PositionVector(0f, 0f, 0f);
		//second curve should have absolute coordinates corresponding
		//to the way the DemoCoordinateSystem was initialized
		PositionVector test2 = new PositionVector(300f, -113f, -180f);
		
		assertEquals(v1, test1);
		assertEquals(v2, test2);
	}

}
