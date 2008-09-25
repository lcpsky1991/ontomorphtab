package edu.ucsd.ccdb.ontomorph2.util;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Utility class to house some general functions used by many other parts of WholeBrainCatalog and OntoMorphTab
 * which are not specific to View or Model
 * 
 * as a rule,
 * if a function references the View.getInstance() or Tangible.getModel() the function should probably NOT belong here
 * 
 * @author caprea
 *
 */
public abstract class OMTUtility {
	
	
	/**
	 * Rotates a vector by some angle around some axis and returns the new vector
	 * @param v the vector to be rotated
	 * @param angle how many degrees to rotate the vector
	 * @param aboutAxis the UNIT vector representing which axis to rotate this vector on
	 * @return a new vector of same length as v, which has been rotated around Axis
	 */
	 public static Vector3f rotateVector(Vector3f v, float angle, Vector3f aboutAxis)
	 {
		Vector3f r = new Vector3f(v);
		Quaternion quat = new Quaternion();
        quat.fromAngleAxis(FastMath.PI * (angle/180), aboutAxis);
        quat.mult(r, r);	
		return r;
	 }
	 
	 /**
	  * Rotates a vector around a specified quaternion
	  * @param v {@link Vector3f} to rotate
	  * @param q {@link Quaternion} about which to rotate v
	  * @return the {@link Vector3f} (v) as rotated around q
	  */
	 public static Vector3f rotateVector(Vector3f v, Quaternion q)
	 {
		 Vector3f r = new Vector3f(v);
		 r = q.mult(r,r);
		 return r;
	 }
	 
	 
	 /**
	 * for debugging, remove this function
	 * ca: still working on it as of 08/25/08
	 *
	 */
	public static OMTVector findCenter(GeomBatch queryMesh)
	{
		//This function is just a guesstimate, for a more accurate approach rewrite this to follow 
		//Calculating Properties of Polyhedra from Mathemetics for Comptuer Graphics (Hearn baker), pg817
		ArrayList<OMTVector> ovs = findVertices(queryMesh);
		
		//variables for the sums
		float sx = 0;
		float sy = 0;
		float sz = 0;
		long count = 0;
		
		DemoCoordinateSystem d = new DemoCoordinateSystem();
		
		
		//print normals buffer
		Geometry mother = queryMesh.getParentGeom();
		FloatBuffer fbNorms = mother.getBinormalBuffer(0);

		Tangible src = TangibleManager.getInstance().getSelectedRecent();
		
		OMTVector poscent = new OMTVector(queryMesh.getModelBound().getCenter());
		
		OMTVector left =  new OMTVector(View.getInstance().getCameraView().getCamera().getLeft());
		OMTVector up =  new OMTVector(View.getInstance().getCameraView().getCamera().getUp());	//must also offset a point by second dimension such that it makes a plane
		OMTVector dir = new OMTVector(View.getInstance().getCameraView().getCamera().getDirection());
		
		OMTVector posa = new OMTVector(left.add(up).mult(-6f));
		OMTVector posb = new OMTVector(left.mult(-5f));
		
		
		OMTVector average = null;
		count = 0;
		for (int i=0; i < ovs.size(); i++)
		{
			OMTVector v = ovs.get(i);
			sx += v.getX();
			sy += v.getY();
			sz += v.getZ();
			
			Sphere s = View.getInstance().createSphere(v);
			if ( i == 1 ) 
			{
				//you must apply the coords then translate
				d.applyToSpatial(s);
				s.setLocalTranslation(v);
				s.setSolidColor(ColorRGBA.red);
			}
			View.getInstance().createDebugRay(posa, posb);
			
			System.out.println(v + " A " + s.getLocalTranslation() + " B " + s.getWorldTranslation() + " C " + s.getCenter());
			count++;		//incriment number of vertexs
		}
		
		//only create the vector if something useful will come of it (if center exists)
		//avoids 1/0 as an added bonus :)
		if (count > 0)
		{
			average = new OMTVector(sx/count,sy/count,sz/count);
		}
		
		return average;
	}
	
	
	/**
	 * Generates a list of OMTVectors that represent the points of the GeometryBatch's vertices
	 * @param jmeMesh the {@link GeomBatch} which has 3Dvertices
	 * @return an ArrayLIst of {@link OMTVector}s, or null if there was an error calculating them
	 * @author caprea
	 */
	public static ArrayList<OMTVector> findVertices(GeomBatch jmeMesh)
	{
//			note: GeomMesh.gettris() is useless (ca)
		FloatBuffer fbVerts = jmeMesh.getVertexBuffer();
		ArrayList<OMTVector> omtPoints = new ArrayList<OMTVector>();
		DemoCoordinateSystem dcoords = new DemoCoordinateSystem();
		
		//Early exit for erroneous calls to this function
		if (jmeMesh == null) return null;
		
		try
		{
			//Loop through the FloatBuffer vertices, taking 3 elements at a time for each new OMT Vector
			for (int i=0; i < ((fbVerts.limit() + 1) / 3); i++)	//to get accurate size incriment by one then divide by three to get: # of vects
			{
				//take three elements from the buffer to make a 3D vector
				OMTVector n = new OMTVector(fbVerts.get(3*i), fbVerts.get(3*i+1), fbVerts.get(3*i+2));
				n.addLocal(dcoords.getOriginVector());
				omtPoints.add(n);
			}
		}
		catch(Exception e)
		{
			System.err.println("Error: Exception calculating vertices for JME Mesh (invalid number of vertices?)");
			omtPoints = null;	//clear the list if there was a problem
		}
		
		return omtPoints;
	}
	
	/**
	 * Tells whetehr or not the camera (or object) looking at a plane is 'above' the origin
	 * By calculating the angle between the vectors is such that the object is determined to be in positive Z-space
	 * if the angle > 90 and angle < 270; and in negative Z-space otherwise
	 * Z-space refers to the space of the plane 
	 * @param pointingAt The 'lookingAt' direction  of an object, preferably the Camera.getDirection()
	 * @param normalToPlane the vector that defines the plane the object is looking at
	 * @return True if in positive Z, False is in negative Z space (looking at from under plane)
	 */
	public static boolean isLookingFromAbove(OMTVector pointingAt, OMTVector normalToPlane)
	{
		//rotate the normal 90 degrees along the X axis = yaxis
		//rotate the normal 90 degrees along the Y axis = xaxis
		
		float angle = pointingAt.angleBetween(normalToPlane);
		
		if ( angle < (FastMath.PI / 2) || angle > (FastMath.PI * (3/2))) // 90 || 270
			return false;
		else
			return true;
	}

	/**
	 * Provides a random number of guassian distribution that is centered on some other number
	 * @param center Values will be centered around this number, they can be more or less
	 * @param bound The 'upperbound' of the distribution for 75% of the data
	 * @return
	 */
	public static double randomNumberGuassian(double center, double bound)
	{
		double r = 0;
		Random generator = new Random();
		r = (generator.nextGaussian()  * bound) + center;
		return r;
	}
	
	/**
	 * Returns a number of uniform distribution between lowerBound and upperBound inclusive
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public static double randomNumber(double lowerBound, double upperBound)
	{
		double r = 0;
		Random generator = new Random();
		r = (generator.nextDouble()  * upperBound) + lowerBound;
		return r;
	}
	
}


