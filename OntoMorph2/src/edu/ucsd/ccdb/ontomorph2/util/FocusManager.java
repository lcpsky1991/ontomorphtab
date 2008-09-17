package edu.ucsd.ccdb.ontomorph2.util;

import org.fenggui.Widget;
import org.fenggui.event.mouse.IMouseEnteredListener;
import org.fenggui.event.mouse.IMouseExitedListener;
import org.fenggui.event.mouse.MouseEnteredEvent;
import org.fenggui.event.mouse.MouseExitedEvent;

import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;

public class FocusManager extends Widget implements IMouseEnteredListener, IMouseExitedListener {

	private static FocusManager instance;
	private static MouseEnteredEvent mouseEntered;
	private static MouseExitedEvent mouseExited;
	
	private boolean guiInFocus = false;
	public FocusManager() 
	{
		instance = this;	
	}	
	
	public static FocusManager get()
	{
		//Daily moment of Zen:
		//A manager's job is to focus its subordinates,
		//if the subordinates are focused, 
		//then what focus has the manager?
		//
	
		
		if (instance == null)
		{
			instance = new FocusManager();
		}
		return instance;
	}
	
	public void mouseEntered(MouseEnteredEvent mouseEnteredEvent) 
	{
		//System.out.println("mouse Entered");
		this.guiInFocus = true;
	}
	
	public void mouseExited(MouseExitedEvent mouseExitedEvent) 
	{
		//System.out.println("mouse exited");
		this.guiInFocus = false;
	}
	
	public boolean isWidgetFocused()
	{
		return this.guiInFocus;
	}
}