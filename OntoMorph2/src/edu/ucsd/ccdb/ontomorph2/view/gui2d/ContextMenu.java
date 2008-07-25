package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.List;
import java.util.Set;

import org.fenggui.background.PlainBackground;
import org.fenggui.border.Border;
import org.fenggui.border.PlainBorder;
import org.fenggui.border.TitledBorder;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
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
	
	static final String NEW = "New...";
	static final String CURVE = "Curve";
	static final String ANNOTATE = "Annotate";
	static final String ANIMATE = "Animate";
	static final String PROPERTIES = "Display Properties";
	static ContextMenu instance = null;
	TitledBorder border = null;
	
	public static ContextMenu getInstance() {
		if (instance == null) {
			instance = new ContextMenu();
		}
		return instance;
	}
	
	private ContextMenu() {
		
        Menu mnuContext = this;
    
        
        this.setShrinkable(false);
        this.setSize(100,200);
        this.getAppearance().removeAll();
		this.getAppearance().add(new PlainBackground(new Color(0.5f, 0.5f, 0.5f, 5.0f)));
		border = new TitledBorder("context menu");
		border.setTextColor(Color.WHITE);
		this.getAppearance().add(border);
		this.getAppearance().setTextColor(Color.WHITE);
		this.getAppearance().setTextSelectionColor(Color.YELLOW);
        
		  
        Menu newMenu = new Menu();
        mnuContext.registerSubMenu(newMenu, NEW);
		makeMenuItem(CURVE, newMenu);
        
        makeMenuItem(ANNOTATE, mnuContext);
        makeMenuItem(ANIMATE, mnuContext);
        makeMenuItem(PROPERTIES, mnuContext);
      
        
        this.setSizeToMinSize();
        this.layout();
	}
	
	public void displayMenuFor(int xCoord, int yCoord, List<Tangible> t) {
		int x = xCoord;
		int y = yCoord;
		this.setXY(x, y);
		
		if (t != null & t.size() == 1) {
			border.setTitle(t.iterator().next().getName());
		}
		
		if(this.equals(View2D.getInstance().getDisplay().getPopupWidget())) // popupmenu is already visible!
		{
			View2D.getInstance().removePopup();
		}
		
		View2D.getInstance().displayPopUp(this);
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
		} else if (CURVE.equals(opt)) {
			//make a new bezier curve right here
		}
		View2D.getInstance().removePopup();
	}
}
