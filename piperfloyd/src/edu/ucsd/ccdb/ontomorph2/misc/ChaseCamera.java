package edu.ucsd.ccdb.ontomorph2.misc;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;

/**
 * the chasecamera stays always behind its target.
 * Dummy ChaseCamera Module
 */
@SuppressWarnings("serial")
public class ChaseCamera extends InputHandler {

	/**
     * the target.
     */
    private Spatial target = null;

    /**
     * distance in the Y-Axis to the target.
     */
    private float yDistance = 0;

    /**
     * distance in the Z-Axis to the target.
     */
    private float zDistance = 0;

    /**
     * Zoom factor.
     */
    private float zoomFactor = 0.3f;

    /**
     * last mousewheel position.
     */
    private float lastPos = 0;

    /**
     * the camera.
     */
    private CameraNode camNode = null;

    /**
     * Attaches the ChaseCam with an Y- and Z-Axis offset to the target.
     * @param target target to follow
     * @param y distance to the target on the Y-Axis
     * @param z distance to the target on the Z-Axis
     */
    public ChaseCamera(final Spatial target, final float y, final float z) {
        this.yDistance = y;
        this.zDistance = z;

        this.target = target;

        camNode = new CameraNode("chaseCamNode", DisplaySystem
                .getDisplaySystem().getRenderer().getCamera());

        InputAction keyZoom = new InputAction() {
            public void performAction(final InputActionEvent evt) {
                if (evt.getTriggerName().equals("cam up") ) {
                        yDistance += zoomFactor;
                        return;
                }
                if (evt.getTriggerName().equals("cam down") ) {
                        yDistance -= zoomFactor;
                        return;
                }
                
                if (evt.getTriggerDevice().equals("mouse")) {
                    zDistance += (zoomFactor * (lastPos < evt
                            .getTriggerPosition() ? -1 : 1));
                    lastPos = evt.getTriggerPosition();
                } else if (evt.getTriggerName().equals("zoom in")) {
                    zDistance += zoomFactor;
                } else {
                    zDistance -= zoomFactor;
                }
            }
        };

        addAction(keyZoom, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE,
                2, false);
        addAction(keyZoom, "zoom in", KeyInput.KEY_PGUP, true);
        addAction(keyZoom, "zoom out", KeyInput.KEY_PGDN, true);
        
        addAction(keyZoom, "cam up", KeyInput.KEY_HOME, true);
        addAction(keyZoom, "cam down", KeyInput.KEY_END, true);
    }

    /**
     * Update location and rotation of the ChaseCam to stay behind the target.
     * @param time time since last frame.
     */
    public final void update(final float time) {
        super.update(time);
        target.updateWorldVectors();
        Vector3f targetVec = target.localToWorld(new Vector3f(0, yDistance,
                zDistance), null);
        camNode.getLocalTranslation().set(targetVec);
        camNode.getLocalRotation().slerp(target.getLocalRotation(), 7*time);
    }

    /**
     * returns the camera node.
     * @return reference to camera node.
     */
    public final CameraNode getCamNode() {
        return camNode;
    }
}

