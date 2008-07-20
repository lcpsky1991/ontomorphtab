package edu.ucsd.ccdb.ontomorph2.util;

import java.io.IOException;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;

public class CurveOnceController extends Controller {
    private static final long serialVersionUID = 1L;
	private Spatial mover;
    private Curve curve;
    private Vector3f up;
    private Quaternion rotation;
    private float orientationPrecision = 1.0f;
    private float currentTime = 0.0f;
    private float deltaTime = 0.0f;

    private boolean cycleForward = true;
    private boolean autoRotation = false;
    private Vector3f newPoint = new Vector3f();  // ***** added
    private Vector3f lastPoint = new Vector3f();   // ***** added 
    private boolean disableAfter;     // ***** added

    public CurveOnceController(Curve curve, CameraNode mover, RotationVector rotation) {
        this.curve = curve;
        this.mover = mover;
        this.rotation = rotation;
        setUpVector(new Vector3f(0,1,0));
        setMinTime(0);
        setMaxTime(Float.MAX_VALUE);
        setRepeatType(Controller.RT_CLAMP);
        setSpeed(1.0f);
    }

    public CurveOnceController(
        Curve curve,
        Spatial mover,
        float minTime,
        float maxTime) {
        this.curve = curve;
        this.mover = mover;
        setMinTime(minTime);
        setMaxTime(maxTime);
        setRepeatType(Controller.RT_CLAMP);
    }

    public void setUpVector(Vector3f up) {
        this.up = up;
    }

    public void setOrientationPrecision(float value) {
        orientationPrecision = value;
    }

    public void setAutoRotation(boolean value) {
        autoRotation = value;
    }

    public boolean isAutoRotating() {
        return autoRotation;
    }

    public void update(float time) {
    	
    	//Log.warn("comes into update curve");
        if(mover == null || curve == null || up == null) {
        	//Log.warn("everything is null");
            return;
        }
        float camRotationRate = FastMath.PI * 5 / 180;
    	Quaternion roll = new Quaternion();
        currentTime += time * getSpeed();
        Vector3f newUp;
        //System.out.println("what it the time " + time);
        if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {
            if (getRepeatType() == RT_CLAMP) {
            	//Log.warn("RT_CLAMP");
                deltaTime = currentTime - getMinTime();
                //Log.warn("delta time" + deltaTime);
                newPoint = curve.getPoint(deltaTime,mover.getLocalTranslation());   // ***** added
                //System.out.println(mover.getLocalTranslation());
                //Log.warn("newPoint" + newPoint);
                mover.setLocalTranslation(newPoint);   // ***** added
                if(deltaTime > .4 && deltaTime < .6){
                	mover.setLocalRotation(rotation);
                }
                if(autoRotation) {   // ***** added
                	//System.out.println("la gran locura");
                	/*angle = mover.getCamera().getFocus().getLocalRotation().toAngleAxis( new Vector3f(0,1,0) );

            		x = mover.getCenter().x - distance*FastMath.sin( angle );
            		z = mover.getCenter().z - distance*FastMath.cos( angle );
            		
            		mover.setLoc( x, y, z );
            		mover.lookAt( mover.getFocus().getLoc(), Vector3f.UNIT_Y );
            		float[] f = mover.getLocalRotation().toAngles(null);
            		f[0] = f[0] * FastMath.RAD_TO_DEG;
            		f[1] = f[1] * FastMath.RAD_TO_DEG;
            		f[2] = f[2] * FastMath.RAD_TO_DEG;
            	    */            	
            		//roll.fromAngleAxis(0*1*4, newPoint ); //rotates Rate degrees
            	//	roll = mover.getLocalRotation().multLocal(roll); // (q, save)
            		//mover.setLocalRotation(roll);
                	//mover.lookAt(up,new Vector3f(0,0,1));
            		
                	//System.out.println("float1" + f[0] + "float2" + f[1] + "float3" + f[2]);

                    //mover.setLocalRotation(rotation);
                	/*mover.setLocalRotation(
                            curve.getOrientation(
                                deltaTime,
                                orientationPrecision,
                                up));*/
                }
                if (isDisableAfterClamp() && lastPoint != null) {
                    if (lastPoint.equals(newPoint)) {
                        setActive(false);
                        lastPoint.set(0, 0, 0);
                        currentTime = 0.0f;
                    }
                    lastPoint.set(newPoint);
                }
            } else if (getRepeatType() == RT_WRAP) {
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (deltaTime > 1) {
                    currentTime = 0;
                    deltaTime = 0;
                }
                mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                if(autoRotation) {
                    mover.setLocalRotation(
                        curve.getOrientation(
                            deltaTime,
                            orientationPrecision,
                            up));
                }
            } else if (getRepeatType() == RT_CYCLE) {
                float prevTime = deltaTime;
                deltaTime = (currentTime - getMinTime()) % 1.0f;
                if (prevTime > deltaTime) {
                    cycleForward = !cycleForward;
                }
                if (cycleForward) {

                    mover.setLocalTranslation(curve.getPoint(deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                deltaTime,
                                orientationPrecision,
                                up));
                    }
                } else {
                    mover.setLocalTranslation(
                        curve.getPoint(1.0f - deltaTime,mover.getLocalTranslation()));
                    if(autoRotation) {
                        mover.setLocalRotation(
                            curve.getOrientation(
                                1.0f - deltaTime,
                                orientationPrecision,
                                up));
                    }
                }
            } else {
                return;
            }
        }
    }

    private boolean isDisableAfterClamp() {   // ***** added
        return disableAfter;
    }

    public void setDisableAfterClamp(boolean disableAfter) {   // ***** added
        this.disableAfter = disableAfter;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(mover, "mover", null);
        capsule.write(curve, "Curve", null);
        capsule.write(up, "up", null);
        capsule.write(orientationPrecision, "orientationPrecision", 0.1f);
        capsule.write(cycleForward, "cycleForward", true);
        capsule.write(autoRotation, "autoRotation", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);

        mover = (Spatial)capsule.readSavable("mover", null);
        curve = (Curve)capsule.readSavable("curve", null);
        up = (Vector3f)capsule.readSavable("up", null);
        orientationPrecision = capsule.readFloat("orientationPrecision", 0.1f);
        cycleForward = capsule.readBoolean("cycleForward", true);
        autoRotation = capsule.readBoolean("autoRotation", false);
    }
}    