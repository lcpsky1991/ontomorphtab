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

import edu.ucsd.ccdb.ontomorph2.view.View3DMouseListener;
 
public class FengJMEInputHandler extends InputHandler
{
	
	private Display disp;
	private KeyInputAction keyAction;
	
	private boolean keyHandled;
	private boolean mouseHandled;
	
	public FengJMEInputHandler(Display disp)
	{
		this.disp = disp;
		
		keyAction = new KeyAction();
		addAction(keyAction, DEVICE_KEYBOARD, BUTTON_ALL, AXIS_NONE, false);
		
		MouseInput.get().addListener(new MouseListener());
	}
	
	public void update(float time)
	{
		mouseHandled = false;
		super.update(time);
	}
	
	public boolean wasKeyHandled()
	{
		return keyHandled;
	}
	
	public void setKey(boolean mouseHandled){
		keyHandled = mouseHandled;
	}
	public boolean wasMouseHandled()
	{
		return mouseHandled;
	}
	
	public void set(boolean mouseValue){
		mouseHandled = mouseValue;
	}
	private class KeyAction extends KeyInputAction
	{
		/*
		public void performAction(InputActionEvent evt)
		{
			char character = evt.getTriggerCharacter();
			Key key = mapKeyEvent();
			if(evt.getTriggerPressed())
				keyHandled = disp.fireKeyPressedEvent(character, key);
			else
				keyHandled = disp.fireKeyReleasedEvent(character, key);
		}
		*/
		public void performAction(InputActionEvent evt)
		{
			
			//System.out.println("perform action");
			char character = evt.getTriggerCharacter();
			
			//System.out.println("character " + character + " " + evt.getTriggerPressed());
			Key key = mapKeyEvent();
			if(evt.getTriggerPressed()){
				disp.fireKeyPressedEvent(character, key);
				if (key == Key.LETTER || key == Key.DIGIT) {
					disp.fireKeyTypedEvent(character);
				}
			}
			else {
				disp.fireKeyReleasedEvent(character, key);
			}
		}
		
		/**
		 * Helper method that maps LWJGL key events to FengGUI.
		 * @return The Key enumeration of the last key pressed.
		 */
		private Key mapKeyEvent()
		{
			Key keyClass;
			
			//System.out.println("map Key Event");
	        switch(Keyboard.getEventKey()) 
	        {
		        case Keyboard.KEY_BACK:
		        	keyClass = Key.BACKSPACE;
		            break;
		        case Keyboard.KEY_RETURN:
		        	keyClass = Key.ENTER;
		            break;
		        case Keyboard.KEY_DELETE: 
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
		        case Keyboard.KEY_NUMPAD1:
		        	keyClass = Key.DIGIT;
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
	        
	        return keyClass;
		}
		
	}
	
	private class MouseListener implements MouseInputListener
	{
		
		private boolean down;
		private int lastButton;
		
		public void onButton(int button, boolean pressed, int x, int y)
		{
			down = pressed;
			lastButton = button;
			if(pressed){
				mouseHandled = disp.fireMousePressedEvent(x, y, getMouseButton(button), 1);
				set(mouseHandled);
			}
			
			else{
				mouseHandled = disp.fireMouseReleasedEvent(x, y, getMouseButton(button), 1);
				set(mouseHandled);
			}
		}
 
		public void onMove(int xDelta, int yDelta, int newX, int newY)
		{
			// If the button is down, the mouse is being dragged
			if(down){
				mouseHandled = disp.fireMouseDraggedEvent(newX, newY, getMouseButton(lastButton));
				setKey(mouseHandled);
			}	
			else{
				mouseHandled = disp.fireMouseMovedEvent(newX, newY);
				setKey(mouseHandled);
			}	
		}
 
		public void onWheel(int wheelDelta, int x, int y)
		{
			// wheelDelta is positive if the mouse wheel rolls up
			if(wheelDelta > 0)
				mouseHandled = disp.fireMouseWheel(x, y, true,0);
			else
				mouseHandled = disp.fireMouseWheel(x, y, false,0);
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