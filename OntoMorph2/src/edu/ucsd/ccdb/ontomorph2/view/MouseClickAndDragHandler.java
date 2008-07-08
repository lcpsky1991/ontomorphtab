package edu.ucsd.ccdb.ontomorph2.view;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.input.*;

/**
 * <code>MouseLookHandler</code> defines an InputHandler that allows to rotate the camera via the mouse while a mouse button is held down.
 * 
 * @author Mark Robinson
 */
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