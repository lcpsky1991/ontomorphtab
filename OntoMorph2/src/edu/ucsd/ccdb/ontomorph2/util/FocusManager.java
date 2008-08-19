package edu.ucsd.ccdb.ontomorph2.util;

import org.fenggui.event.mouse.IMouseExitedListener;
import org.fenggui.event.mouse.IMouseEnteredListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;

public class FocusManager implements IMouseEnteredListener, IMouseExitedListener  {

	public static FocusManager focusManager;
	
	public FocusManager() {
		focusManager = this;
	}
	
	public boolean guiInFocus = false;
	
	public void mouseEntered(MouseEnteredEvent mouseEnteredEvent) 
	{
		System.out.println("Inside Gui Entered");
		guiInFocus = true;
	}
	
	public void mouseExited(MouseExitedEvent mouseExitedEvent) 
	{
		System.out.println("Gui Exited");
		guiInFocus = false;
	}
}