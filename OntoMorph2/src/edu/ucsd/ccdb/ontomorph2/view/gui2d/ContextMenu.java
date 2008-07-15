package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.background.PlainBackground;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * A dynamic context menu that pops up when you right click in the display.
 * 
 * <a href="http://openccdb.org/wiki/index.php/Brain_Catalog_Interface#Context_Menu" More information</a>
 * about the design of this class is available on the website.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class ContextMenu extends Menu implements IMenuItemPressedListener{
	
	static final String ANNOTATE = "Annotate";
	static final String ANIMATE = "Animate";
	static final String PROPERTIES = "Display Properties";
	
	public ContextMenu() {
		View2D.getInstance().addWidget(this);
		
        Menu mnuContext = this;
    
        
        this.setShrinkable(false);
        //this.getAppearance().removeAll();
		//this.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		//this.getAppearance().setTextColor(Color.WHITE);
		
        
        makeMenuItem(ANNOTATE, mnuContext);
        makeMenuItem(ANIMATE, mnuContext);
        makeMenuItem(PROPERTIES, mnuContext);
        
        this.setSizeToMinSize();
        this.layout();
	}
	
	public void displayMenuFor(int x, int y, TangibleView t) {
		
		this.setPosition(new Point(x,y));
        
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
			Log.warn("Exception; Error creating submenu; " + e.getMessage());
		}
        
	}
	public void menuItemPressed(MenuItemPressedEvent arg0) {
		String opt = arg0.getItem().getText();
		if (ANNOTATE.equals(opt)) {
			System.out.println("do annotation");
		} else if (ANIMATE.equals(opt)) {
			System.out.println("do animation");
		} else if (PROPERTIES.equals(opt)) {
			System.out.println("show properties");
		}
	}
}
