package edu.ucsd.ccdb.ontomorph2.util;

import org.fenggui.Display;
import org.fenggui.event.Key;
import org.fenggui.event.mouse.MouseButton;
import org.lwjgl.input.Keyboard;

import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
 
/**
 * FengJMEInputHandler
 * 
 * @author Joshua Keplinger
 *
 */
public class FengJMEInputHandler extends InputHandler
{
	
	private Display disp;
	private KeyInputAction keyAction;
	
	private boolean keyHandled;
	private boolean mouseHandled, mouseOverGui;
	
	public FengJMEInputHandler(Display disp)
	{
		this.disp = disp;
		
		keyAction = new KeyAction();
		addAction(keyAction, DEVICE_KEYBOARD, BUTTON_ALL, AXIS_NONE, false);
		
		MouseInput.get().addListener(new MouseListener());
	}
	
	public void update(float time)
	{
		keyHandled = false;
		mouseHandled = false;
		super.update(time);
	}
	
	public boolean wasKeyHandled()
	{
		return keyHandled;
	}
	
	public boolean wasMouseHandled()
	{
		return mouseHandled;
	}
	
	public boolean isMouseOverGui() {
        System.out.println("mouse over gui");
        return mouseOverGui;
    }
	
	public void setMouseOverGui( boolean mouseOver ) {
        mouseOverGui = mouseOver;
    }
	
	public void reset() {
        keyHandled = false;
        mouseHandled = false;
    }
	//@override
	public void mouseMoved(int displayX, int displayY)
	{
		System.out.println("mouse moved");
	}
	private class KeyAction extends KeyInputAction implements Runnable
	{
        private static final int MAX_DELAY = 8;
        private boolean keyPressed=false;
        private InputActionEvent lastEvent;
        private int delay=0;
        private Object LOCK=new Object();
        public KeyAction()
        {
            new Thread(this).start();
        }
        private void wakeUp()
        {
            synchronized (LOCK)
            {
                LOCK.notify();
            }
        }
        public void performAction(InputActionEvent evt)
        {
            char character = evt.getTriggerCharacter();
            Key key = mapKeyEvent();
            if (evt.getTriggerPressed())
            {
                delay=MAX_DELAY;
                lastEvent=evt;
                keyPressed=true;
                wakeUp();
                keyHandled = disp.fireKeyPressedEvent(character, key);
                // Bug workaround see note after code
                if (key == Key.LETTER || key == Key.DIGIT)
                {
                    keyHandled = disp.fireKeyTypedEvent(character);
                }
            }
            else
            {
                keyHandled = disp.fireKeyReleasedEvent(character, key);
                keyPressed=false;
            }
        }
        /**
         * Method to support Key Repetition in Widgets using Text Editor
         */
        public void keyRepeated(InputActionEvent evt)
        {
            char character = evt.getTriggerCharacter();
            Key key = mapKeyEvent();
            if (evt.getTriggerPressed())
            {
                keyHandled = disp.fireKeyPressedEvent(character, key);
                // Bug workaround see note after code
                if (key == Key.LETTER || key == Key.DIGIT)
                {
                    keyHandled = disp.fireKeyTypedEvent(character);
                }
            }
            else
            {
                keyHandled = disp.fireKeyReleasedEvent(character, key);
            }
        }
		
		/**
		 * Helper method that maps LWJGL key events to FengGUI.
		 * @return The Key enumeration of the last key pressed.
		 */
		private Key mapKeyEvent()
		{
			Key keyClass;
			
	        switch(Keyboard.getEventKey()) 
	        {
		        case Keyboard.KEY_BACK:
		        	keyClass = Key.BACKSPACE;
		            break;
		        case Keyboard.KEY_RETURN:
		        	keyClass = Key.ENTER;
		            break;
		        case Keyboard.KEY_DELETE: 
		        	System.out.println("delete");
		        	keyClass = Key.DELETE;
		            break;
		        case Keyboard.KEY_UP:
		        	keyClass = Key.UP;
		        	break;
		        case Keyboard.KEY_RIGHT:
		        	keyClass = Key.RIGHT;
		            break;
		        case Keyboard.KEY_LEFT:
		        	keyClass = Key.LEFT;
		            break;
		        case Keyboard.KEY_DOWN:
		        	keyClass = Key.DOWN;
		            break;
		        case Keyboard.KEY_SCROLL:
		        	System.out.println("shift");
		        	keyClass = Key.SHIFT;
		            break;
		        case Keyboard.KEY_LMENU:
		        	keyClass = Key.ALT;
		            break;
		        case Keyboard.KEY_RMENU:
		        	keyClass = Key.ALT;
		            break;
		        case Keyboard.KEY_LCONTROL:
		        	keyClass = Key.CTRL;
		            break;
		        case Keyboard.KEY_RSHIFT:
		        	keyClass = Key.SHIFT;
		            break;     
		        case Keyboard.KEY_LSHIFT:
		        	keyClass = Key.SHIFT;
		            break;              
		        case Keyboard.KEY_RCONTROL:
		        	keyClass = Key.CTRL;
		            break;
		        case Keyboard.KEY_INSERT:
		        	keyClass = Key.INSERT;
		            break;
		        case Keyboard.KEY_F12:
		        	keyClass = Key.F12;
		            break;
		        case Keyboard.KEY_F11:
		        	keyClass = Key.F11;
		            break;
		        case Keyboard.KEY_F10:
		        	keyClass = Key.F10;
		            break;
		        case Keyboard.KEY_F9:
		        	keyClass = Key.F9;
		            break;
		        case Keyboard.KEY_F8:
		        	keyClass = Key.F8;
		            break;
		        case Keyboard.KEY_F7:
		        	keyClass = Key.F7;
		            break;
		        case Keyboard.KEY_F6:
		        	keyClass = Key.F6;
		            break;
		        case Keyboard.KEY_F5:
		        	keyClass = Key.F5;
		            break;
		        case Keyboard.KEY_F4:
		        	keyClass = Key.F4;
		            break;
		        case Keyboard.KEY_F3:
		        	keyClass = Key.F3;
		            break;
		        case Keyboard.KEY_F2:
		        	keyClass = Key.F2;
		            break;
		        case Keyboard.KEY_F1:
		        	keyClass = Key.F1;
		            break;
		        default:
		        	if("1234567890".indexOf(Keyboard.getEventCharacter()) != -1) {
		        		keyClass = Key.DIGIT;
		        	} else { 
		        		// @todo must not necessarily be a letter!! #
		        		keyClass = Key.LETTER;
		        	}
		        	break;
	    	}
			System.out.println("keyclass" + keyClass);
	        return keyClass;
		}
		
		/**
		 * Run method
		 */
		public void run() {
            while(true)
            {
                if(keyPressed)
                {
                    try
                    {
                        if(delay>0)
                        {
                            --delay;
                        }
                        else if(lastEvent!=null)
                        {
                            keyRepeated(lastEvent);
                        }
                        Thread.sleep(100);
                    }
                    catch (Exception e)
                    {
                    }
                }
                else
                {
                    synchronized (LOCK)
                    {
                        {
                            try
                            {
                                LOCK.wait();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
		}
	}
	
	private class MouseListener implements MouseInputListener
	{
		
		private boolean down;
		private int lastButton;
				
		public void onButton(int button, boolean pressed, int x, int y)
		{
			//System.out.println("onButton");
			down = pressed;
			lastButton = button;
			if(pressed)
				mouseHandled = disp.fireMousePressedEvent(x, y, getMouseButton(button), 1);
			else
				mouseHandled = disp.fireMouseReleasedEvent(x, y, getMouseButton(button), 1);
		}
 
		public void onMove(int xDelta, int yDelta, int newX, int newY)
		{
			//System.out.println("onMove");
			// If the button is down, the mouse is being dragged
			if(down)
				mouseHandled = disp.fireMouseDraggedEvent(newX, newY, getMouseButton(lastButton));
			else
				mouseHandled = disp.fireMouseMovedEvent(newX, newY);
		}
 
		public void onWheel(int wheelDelta, int x, int y)
		{
			//System.out.println("onWheel");
			// wheelDelta is positive if the mouse wheel rolls up
                        if(wheelDelta > 0)
		                mouseHandled = disp.fireMouseWheel(x, y, true, wheelDelta);
                        else
                                mouseHandled = disp.fireMouseWheel(x, y, false, wheelDelta);
 
                        // note (johannes): wheeling code not tested on jME, please report problems on www.fenggui.org/forum/
		}
		
		/**
		 * Helper method that maps the mouse button to the equivalent
		 * FengGUI MouseButton enumeration.
		 * @param button The button pressed or released.
		 * @return The FengGUI MouseButton enumeration matching the
		 * button.
		 */
		private MouseButton getMouseButton(int button)
		{
			switch(button)
			{
				case 0:
					return MouseButton.LEFT;
				case 1:
					return MouseButton.RIGHT;
				case 2:
					return MouseButton.MIDDLE;
				default:
					return MouseButton.LEFT;
			}
		}
		
	}
	
}