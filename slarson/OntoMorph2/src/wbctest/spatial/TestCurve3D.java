package wbctest.spatial;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.jme.curve.Curve;

import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

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
		testCurve2 = new Curve3D("test curve", initialControlPoints);
	}

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D.getBezierCurve()'
	 */
	public void testGetBezierCurve() {
		Curve bCurve1 = testCurve1.getCurve();
		assertNotNull(bCurve1);
		
		Curve bCurve2 = testCurve2.getCurve();
		assertNotNull(bCurve2);
	}

	
	
	public void testMovePoints()
	{
		List<CurveAnchorPoint> ptsNoCoords = testCurve1.getAnchorPoints();
		List<CurveAnchorPoint> ptsDemo = testCurve2.getAnchorPoints();
		
		
		PositionVector orig=null;
		PositionVector dest=null;
		PositionVector destL=null;
		
		CurveAnchorPoint capOrig=null;
		CurveAnchorPoint capDest=null;

		assertEquals(ptsDemo.get(1).getPosition(),ptsNoCoords.get(1).getPosition());
		assertTrue(!ptsDemo.get(1).getPosition().equals(ptsNoCoords.get(1).getPosition()));
		
		//move the anchr points for the demo curve
		//for ( int i =0; i < ptsDemo.size(); i++)
		{
			//try DEMO
			capOrig = ptsDemo.get(1);
			orig = capOrig.getPosition();
			//orig = capOrig.getRelativePosition();
			//capOrig.move(5f, 10f, new OMTVector(1,1,0));
			dest = capOrig.getPosition();
			//dest = capOrig.getRelativePosition();
			System.out.println("orig: " + orig + "   -->  " + dest);
			
			//try NO COORDs
			capOrig = ptsNoCoords.get(1);
			orig = capOrig.getPosition();
			//orig = capOrig.getRelativePosition();
			//capOrig.move(10f, 10f, new OMTVector(1,1,0));
			dest = capOrig.getPosition();
			//dest = capOrig.getRelativePosition();
			System.out.println("no orig: " + orig + "   -->  " + dest);			
		}
		
		
		/**	RESULTS
		 * Moving an object by .move does not change it's relative position
		 */
		
	}
	
	
	
	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D.getControlPoints()'
	 * 
	 * This test we try getting anchor points for both curves, then using the "move" method on them,
	 * then testing to see if they end up where we thing they should.
	 */
	public void testGetCurveAnchorPoints() {
		//anchor points from the curve with no coordinate system
		List<CurveAnchorPoint> cPoints1 = testCurve1.getAnchorPoints();
		//anchor points from the curve with the Demo coordinate system
		List<CurveAnchorPoint> cPoints2 = testCurve2.getAnchorPoints();
		
		for (int i = 0; i < cPoints1.size(); i++) {
			//should have the same relative positions
			assertTrue(cPoints1.get(i).getPosition().equals(cPoints2.get(i).getPosition()));
			//should have different absolute positions because of coordinate system differences
			assertTrue(!cPoints1.get(i).getPosition().equals(cPoints2.get(i).getPosition()));
		}
		
		List<List<CurveAnchorPoint>> temp = new ArrayList<List<CurveAnchorPoint>>();
		temp.add(cPoints1);
		temp.add(cPoints2);
		
		//do the same thing for both CurveAnchorPoints lists
		int i = 1;
		for (List<CurveAnchorPoint> temp2 : temp) {
		
			CurveAnchorPoint anchorPoint1 = temp2.get(1);
			CurveAnchorPoint anchorPoint2 = temp2.get(2);
			
			//record original position
			float oldX1 = anchorPoint1.getPosition().x;
			float oldY1 = anchorPoint1.getPosition().y;
			float oldZ1 = anchorPoint1.getPosition().z;
			
			//do move
			//anchorPoint1.move(0.5f, 0.5f, new OMTVector(1,1,0));
			
			//test
			assertEquals("Testing predicted movement on point 1 of curve " + i, 
					new PositionVector(oldX1 + 0.5f, oldY1 + 0.5f, oldZ1), temp2.get(1).getPosition());
			
			//do move
			//anchorPoint1.move(0.5f, 0.5f, new OMTVector(1,1,0));
			//test
			assertEquals("Testing second predicted movement on point 1 of curve " + i, 
					new PositionVector(oldX1 + 1.0f, oldY1 + 1.0f, oldZ1), temp2.get(1).getPosition());
			
			//record original position
			float oldX2 = anchorPoint2.getPosition().x;
			float oldY2 = anchorPoint2.getPosition().y;
			float oldZ2 = anchorPoint2.getPosition().z;
			
			//do move
			//anchorPoint2.move(0.5f, -0.5f, new OMTVector(1,1,0));
			
			//test
			assertEquals("Testing predicted movement on point 2 of curve " + i, 
					new PositionVector(oldX2 + 0.5f, oldY2 - 0.5f, oldZ2), temp2.get(2).getPosition());
			//test
			assertEquals("make sure point one is still where we expect it for curve " +i, 
					new PositionVector(oldX1 + 1.0f, oldY1 + 1.0f, oldZ1), temp2.get(1).getPosition());
			
			//do move
			//anchorPoint2.move(0.5f, -0.5f, new OMTVector(1,1,0));
			
			//test
			assertEquals("Testing predicted movement on point 2 of curve " + i, 
					new PositionVector(oldX2 + 1.0f, oldY2 - 1.0f, oldZ2), temp2.get(2).getPosition());
			//test
			assertEquals("make sure point one is still where we expect it for curve " + i, 
					new PositionVector(oldX1 + 1.0f, oldY1 + 1.0f, oldZ1), temp2.get(1).getPosition());
			i++;
		}
	}

	
	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible.getRelativePosition()'
	 */
	public void testGetRelativePosition() {
		PositionVector v1 = testCurve1.getPosition();
		PositionVector v2 = testCurve2.getPosition();
		
		PositionVector test = new PositionVector(0f, 0f, 0f);
		assertEquals(v1, test);
		assertEquals(v2, test);
	}
	
	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible.getAbsolutePosition()'
	 */
	public void testGetAbsolutePosition() {
		PositionVector v1 = testCurve1.getPosition();
		PositionVector v2 = testCurve2.getPosition();
		
		PositionVector test1 = new PositionVector(0f, 0f, 0f);
		//second curve should have absolute coordinates corresponding
		//to the way the DemoCoordinateSystem was initialized
		PositionVector test2 = new PositionVector(300f, -113f, -180f);
		
		assertEquals(v1, test1);
		assertEquals(v2, test2);
	}

}
