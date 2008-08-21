package edu.ucsd.ccdb.ontomorph2.util;

import org.fenggui.Widget;
import org.fenggui.event.mouse.IMouseExitedListener;
import org.fenggui.event.mouse.IMouseEnteredListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;

public class FocusManager extends Widget implements IMouseEnteredListener, IMouseExitedListener {

	public static FocusManager focusManager;
	public static MouseEnteredEvent mouseEntered;
	public static MouseExitedEvent mouseExited;
	
	public boolean guiInFocus = false;
	public FocusManager() {
		focusManager = this;
	}	
	
	public void mouseEntered(MouseEnteredEvent mouseEnteredEvent) 
	{
		this.guiInFocus = true;
	}
	
	public void mouseExited(MouseExitedEvent mouseExitedEvent) 
	{
		this.guiInFocus = false;
	}
}