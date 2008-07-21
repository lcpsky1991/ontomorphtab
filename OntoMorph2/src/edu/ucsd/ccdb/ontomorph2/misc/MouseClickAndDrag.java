package edu.ucsd.ccdb.ontomorph2.misc;

import com.jme.input.action.*;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.input.RelativeMouse;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.input.*;

import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;

import org.fenggui.event.mouse.MouseButton;

/**
 * <code>MouseClickAndDrag</code> defines a mouse action that detects click and drag movement from
 * the mouse and turns it into camera rotation.
 * 
 * @author Mark Robinson 
 * @author caprea (editor for WBC)
 */
public class MouseClickAndDrag extends MouseInputAction {

    //actions to handle looking up down left and right.
    private KeyLookDownAction lookDown;

    private KeyLookUpAction lookUp;

    private KeyRotateLeftAction rotateLeft;

    private KeyRotateRightAction rotateRight;
    
    private MouseInput mouseInput;
    
    //the axis to lock.
    private Vector3f lockAxis;
    
    //current mouse position
    private Vector2f position;
    
    //the event to distribute to the looking actions.
    private InputActionEvent event;

    ViewCamera camNode; 
    
    /**
     * Constructor creates a new <code>MouseClickAndDrag</code> object. It takes the
     * mouse, camera and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param camera
     *            the camera to move.
     * @param speed
     *            the speed at which to alter the camera.
     */
    public MouseClickAndDrag(Mouse mouse, Camera camera, float speed) {
    	
    	//InputHandler input = new FirstPersonHandler(camera, 50, camNode.getRotationRate());
        this.mouse = (RelativeMouse) mouse;
        this.speed = speed;

        lookDown = new KeyLookDownAction(camera, speed);
        lookUp = new KeyLookUpAction(camera, speed);
        rotateLeft = new KeyRotateLeftAction(camera, speed);
        rotateRight = new KeyRotateRightAction(camera, speed);

        InputHandler input = new InputHandler();
        mouse.registerWithInputHandler(input);
        
        event = new InputActionEvent();
        //event.fireMouseClickEvent(1,2, MouseInput.get(),3);
        Log.warn("event " + event.getTriggerDevice() );
        mouseInput = MouseInput.get();
        if(mouseInput.isButtonDown(0))
        	Log.warn("mouse inputfsdfsddf "+ mouseInput);
       //input.addAction( dragg, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE, 2, true );

    	Log.warn("Inside constructor" + position + " camera: " + camera + " speed " + speed);
    }

    /**
     * 
     * <code>setLockAxis</code> sets the axis that should be locked down. This
     * prevents "rolling" about a particular axis. Typically, this is set to the
     * mouse's up vector. Note this is only a shallow copy.
     * 
     * @param lockAxis
     *            the axis that should be locked down to prevent rolling.
     */
    public void setLockAxis(Vector3f lockAxis) {
    	
    	Log.warn("set lock Axis");
        this.lockAxis = lockAxis;
        rotateLeft.setLockAxis(lockAxis);
        rotateRight.setLockAxis(lockAxis);
    }

    /**
     * Returns the axis that is currently locked.
     * 
     * @return The currently locked axis
     * @see #setLockAxis(com.jme.math.Vector3f)
     */
    public Vector3f getLockAxis() {
        return lockAxis;
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * 
     * @param speed
     *            the speed of the mouse look.
     */
    public void setSpeed(float speed) {
        super.setSpeed( speed );
        lookDown.setSpeed(speed);
        lookUp.setSpeed(speed);
        rotateRight.setSpeed(speed);
        rotateLeft.setSpeed(speed);
    }

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.MouseInputAction#performAction(InputActionEvent)
     */    	
	public void performAction(InputActionEvent evt) {
         //MouseInput i = MouseInput.get();
    	
		Log.warn("si viene para aca");
		float time;
		if(mouseInput.isButtonDown(0)){
       	  	
				Log.warn("Mouse is Button Down");
				time = evt.getTime() * speed;
		}        
		else
			time = 0;
         
		if (position.x > 0) {
			Log.warn("position x");
			event.setTime(time * mouse.getLocalTranslation().x);
			rotateRight.performAction(event);
		} else if (position.x < 0) {
			event.setTime(time * mouse.getLocalTranslation().x * -1);
			rotateLeft.performAction(event);
		}
		if (position.y > 0) {
			event.setTime(time * mouse.getLocalTranslation().y);
			lookUp.performAction(event);
		} else if (position.y < 0) {
			event.setTime(time * mouse.getLocalTranslation().y * -1);
			lookDown.performAction(event);
		}
	}
}
// IMPLIMENT WITH THIS CODE
/*
import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.input.*;


 * <code>MouseLookHandler</code> defines an InputHandler that allows to rotate the camera via the mouse while a mouse button is held down.
 * 
 * @author Mark Robinson

public class MouseClickAndDragHandler extends InputHandler {
        
    public MouseClickAndDragHandler( Camera cam, float speed ) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( this );

        MouseClickAndDrag mouseLook = new MouseClickAndDrag(mouse, cam, speed );
        mouseLook.setLockAxis(new Vector3f(cam.getUp().x, cam.getUp().y,
                cam.getUp().z));
        addAction(mouseLook);
    }
}

*/