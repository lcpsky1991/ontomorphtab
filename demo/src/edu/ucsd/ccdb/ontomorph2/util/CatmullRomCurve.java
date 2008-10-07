/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.ucsd.ccdb.ontomorph2.util;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.geom.BufferUtils;

/**
 * <code>CatmullRomCurve</code>
 * 
 * @see <a href="http://www.mvps.org/directx/articles/catmull/">Catmull-Rom Splines</a>
 * @author Frederik Blthoff
 */
public class CatmullRomCurve extends Curve {
	
	private static final long serialVersionUID = 1L;
	
	private Vector3f tempVec0 = new Vector3f();
	private Vector3f tempVec1 = new Vector3f();
	private Vector3f tempVec2 = new Vector3f();
	private Vector3f tempVec3 = new Vector3f();
	private Vector3f tempVec4 = new Vector3f();
	private Vector3f firstPoint = new Vector3f();
	private Vector3f lastPoint = new Vector3f();
	private Vector3f up = new Vector3f(0,1,0);
	private float partPercentage;
	private Vector3f controlPoints;
	/**
	 * Constructor instantiates a new <code>CatmullRomCurve</code> object.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public CatmullRomCurve(String name) {
		super(name);
	}

	/**
	 * Constructor instantiates a new <code>CatmullRomCurve</code> object. The
	 * control points that define the curve are supplied.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param controlPoints
	 *            the points that define the curve.
	 */
	public CatmullRomCurve(String name, Vector3f[] controlPoints) {
		super(name, controlPoints);
		this.partPercentage = 1.0f / (getBatch(0).getVertexCount() - 1);
		Vector3f first = new Vector3f(), second = new Vector3f(), last = new Vector3f(), beforeLast = new Vector3f();
		BufferUtils.populateFromBuffer(first, getBatch(0).getVertexBuffer(), 0);
		BufferUtils.populateFromBuffer(second, getBatch(0).getVertexBuffer(), 1);
		BufferUtils.populateFromBuffer(beforeLast, getBatch(0).getVertexBuffer(), getBatch(0).getVertexCount() - 2);
		BufferUtils.populateFromBuffer(last, getBatch(0).getVertexBuffer(), getBatch(0).getVertexCount() - 1);
		Vector3f firstTangent = second.subtract(first);
		firstTangent.normalizeLocal();
		firstTangent.multLocal(0.01f);
		first.mult(firstTangent, this.firstPoint);
		Vector3f lastTangent = beforeLast.subtract(last);
		lastTangent.normalizeLocal();
		lastTangent.multLocal(0.01f);
		last.mult(lastTangent, this.lastPoint);
		this.controlPoints = controlPoints[1];
	}

	/**
	 * <code>getOrientation</code> calculates the rotation matrix for any
	 * given point along to the line to still be facing in the direction of the
	 * line.
	 * 
	 * @param time
	 *            the current time (between 0 and 1)
	 * @param precision
	 *            how accurate to (i.e. the next time) to check against.
	 * @return the rotation matrix.
	 * @see com.jme.curve.Curve#getOrientation(float, float)
	 */
	public Matrix3f getOrientation(float time, float precision) {
		Matrix3f rotation = new Matrix3f();

		//calculate tangent
		Vector3f point = getPoint(time);
//		if (point == vertex[getBatch(0).getVertQuantity() - 1] || point == vertex[0]) {
//			return rotation;
//		}
		Vector3f tangent = point.subtract(getPoint(time + precision));
		tangent = tangent.normalize();
		//calculate normal
		Vector3f tangent2 = getPoint(time - precision).subtract(point);
		Vector3f normal = tangent.cross(tangent2);
		normal = normal.normalize();
		
		//calculate binormal
		Vector3f binormal = tangent.cross(normal);
		binormal = binormal.normalize();

		rotation.setColumn(0, normal);
		rotation.setColumn(1, binormal);
		rotation.setColumn(2, tangent);
		
		//System.out.println("matrix rotation" + rotation);
		return rotation;
	}

	/**
	 * <code>getOrientation</code> calculates the rotation matrix for any
	 * given point along to the line to still be facing in the direction of the
	 * line. A up vector is supplied, this keep the rotation matrix following
	 * the line, but insures the object's up vector is not drastically changed.
	 * 
	 * @param time
	 *            the current time (between 0 and 1)
	 * @param precision
	 *            how accurate to (i.e. the next time) to check against.
	 * @return the rotation matrix.
	 * @see com.jme.curve.Curve#getOrientation(float, float)
	 */
	public Matrix3f getOrientation(float time, float precision, Vector3f direction) {
		if (direction == null) {
			return getOrientation(time, precision);
		}

		Matrix3f rotation = new Matrix3f();

		//calculate tangent
		Vector3f tangent = direction.subtract(getPoint(precision+time));
		tangent = tangent.normalize();
		
		//System.out.println("tangent" + tangent);

		//calculate binormal
		Vector3f binormal = tangent.cross(up);
		binormal = binormal.normalize();
		//System.out.println("binormal" + binormal);
		
		//calculate normal
		Vector3f normal = tangent.cross(binormal);
		normal = normal.normalize();
		//System.out.println("normal" + tangent);
		
		rotation.setColumn(0, binormal);
		rotation.setColumn(1, normal);
		rotation.setColumn(2, tangent);

		//System.out.println("rotation" + rotation);
		return rotation;
	
	}

	public Quaternion getRotation(Vector3f newPoint)
	{
		Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*1, Vector3f.UNIT_Y);
		return rotation;
		
	}
	/* (non-Javadoc)
	 * @see com.jme.curve.Curve#getPoint(float)
	 */
	@Override
	public Vector3f getPoint(float time) {
		return getPoint(time, new Vector3f());
	}

	/**
	 * <code>getPoint</code> calculates a point on a Catmull-Rom curve from a given
	 * time value within the interval [0, 1]. If the value is zero or less, the
	 * first control point is returned. If the value is one or more, the last
	 * control point is returned. Using the equation of a Catmull-Rom Curve, the
	 * point at the interval is calculated and returned.
	 * 
	 * @see com.jme.curve.Curve#getPoint(float)
	 */
	@Override
	public Vector3f getPoint(float time, Vector3f point) {
		// first point
		if (time <= 0) {
		    BufferUtils.populateFromBuffer(point, getBatch(0).getVertexBuffer(), 0);
			return point;
		}
		// last point.
		if (time >= 1) {
		    BufferUtils.populateFromBuffer(point, getBatch(0).getVertexBuffer(), getBatch(0).getVertexCount() - 1);
			return point;
		}
		
		float timeBetween = time / this.partPercentage;
		int firstPointIndex = ((int)FastMath.floor(timeBetween));
		
		time = timeBetween - firstPointIndex;
		float t  = time * 0.5f;
		float t2 = t * time;
		float t3 = t2 * time;
		
		firstPointIndex--;
		
		if (firstPointIndex == -1) tempVec1 = this.firstPoint;
		else BufferUtils.populateFromBuffer(tempVec1, getBatch(0).getVertexBuffer(), firstPointIndex);
		BufferUtils.populateFromBuffer(tempVec2, getBatch(0).getVertexBuffer(), ++firstPointIndex);
		BufferUtils.populateFromBuffer(tempVec3, getBatch(0).getVertexBuffer(), ++firstPointIndex);
		if (++firstPointIndex == getBatch(0).getVertexCount()) tempVec4 = this.lastPoint;
		else BufferUtils.populateFromBuffer(tempVec4, getBatch(0).getVertexBuffer(), firstPointIndex);
		
		point.zero();
		
		tempVec1.mult(-t3 + 2 * t2 - t, point).addLocal(tempVec2.mult(3 * t3 - 5 * t2 + 1, tempVec0)).addLocal(tempVec3.mult(-3 * t3 + 4 * t2 + t, tempVec0)).addLocal(tempVec4.mult(t3 - t2, tempVec0));
		return point;
	}
	
	/* (non-Javadoc)
	 * @see com.jme.scene.Spatial#findCollisions(com.jme.scene.Spatial, com.jme.intersection.CollisionResults)
	 */
	@Override
	public void findCollisions(Spatial scene, CollisionResults results) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial, boolean)
	 */
	@Override
	public boolean hasCollision(Spatial scene, boolean checkTriangles) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.jme.scene.Spatial#doPick(com.jme.math.Ray, com.jme.intersection.PickResults)
	 */
	@Override
	public void findPick(Ray toTest, PickResults results) {
		// TODO Auto-generated method stub
		
	}
}