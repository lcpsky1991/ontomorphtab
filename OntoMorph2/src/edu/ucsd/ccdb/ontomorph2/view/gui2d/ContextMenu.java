package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;

/**
 * A dynamic context menu that pops up when you right click in the display.
 * If an object is right-clicked on that can be manipulated, the user can choose 
 * to translate, rotate, or scale the object, and then the cursor changes to 
 * indicate that left dragging will perform the chosen operation.  Other 
 * object-specific manipulations will be dynamically added to the context menu
 * based on object type. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class ContextMenu extends Menu implements IMenuItemPressedListener{
	
	public static final String strMNU_MANIPULATE = "Manipulate Object";
	public static final String strMNU_MANI_ROTATEA = "Rotate (X - Axis)";
	public static final String strMNU_MANI_ROTATEB = "Rotate (Y - Axis)";
	public static final String strMNU_MANI_ROTATEC = "Rotate (Z - Axis)";
	public static final String strMNU_MANI_MOVE = "Move";
	public static final String strMNU_MANI_LOOK = "Focus Camera";
	public static final String strMNU_MANI_SCALE = "Re-Scale";
	
	public ContextMenu() {
//		=[ MANIPULATE  ]= 
        Menu mnuManip = this;
    
        makeMenuItem(strMNU_MANI_MOVE, mnuManip);
        makeMenuItem(strMNU_MANI_SCALE, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEA, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEB, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEC, mnuManip);
        makeMenuItem(strMNU_MANI_LOOK, mnuManip);
	}
	/**
	 * Conveiniance method that wraps the creation of menus
	 * @author caprea
	 * @param name
	 * @param parent If null, will create a new parent menu
	 */
	private void makeMenuItem(String name, Menu mparent)
	{
		try
		{
			if (name.equals(""))
			{
				name = "____________";
			}
			if (null == mparent )
			{
				return; //exit gracefully if parent is empty
			}
			else if ( mparent instanceof Menu)
			{
				MenuItem mnuToAdd = new MenuItem(name);
		        mnuToAdd.addMenuItemPressedListener(this);
		        mparent.addItem(mnuToAdd);	
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception; Error creating submenu; " + e.getMessage());
		}
        
	}
	public void menuItemPressed(MenuItemPressedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
