package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Implements the main menu bar at the top of the application.  Also processes the events when
 * the menu items are selected and makes the corresponding calls.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MenuBar extends org.fenggui.menu.MenuBar implements IMenuItemPressedListener{

	
	public static final String LOAD_SCENE = "Load Scene...";
	public static final String SAVE_SCENE = "Save Scene...";
	public static final String CELLS = "Cells...";
	public static final String VOLUMES = "Volumes...";
	public static final String SEMANTICS = "Semantics...";
	public static final String LIST_INSTANCES = "List Instances...";
	public static final String SHOW_ATLAS = "Show Atlas...";
	public static final String DISPLAY_BASIC_ATLAS = "Display Basic Atlas";
	public static final String SLIDE_VIEW = "View Example Slide";
	public static final String ATLAS_LATERAL_VIEW = "View Atlas Lateral Side";
	public static final String ATLAS_MEDIAL_VIEW = "View Atlas Medial Side";
	public static final String ATLAS_MEDIAL_VIEW_SMOOTH = "Smoothly Zoom To Atlas Medial Side";
	public static final String strMNU_MANIPULATE = "Manipulate Object";
	public static final String strMNU_MANI_ROTATEA = "Rotate (X - Axis)";
	public static final String strMNU_MANI_ROTATEB = "Rotate (Y - Axis)";
	public static final String strMNU_MANI_ROTATEC = "Rotate (Z - Axis)";
	public static final String strMNU_MANI_MOVE = "Move";
	public static final String strMNU_MANI_LOOK = "Focus Camera";
	public static final String strMNU_MANI_SCALE = "Re-Scale";
	
	
	public MenuBar() {
//		generate the menu
        MenuBar mB = this;
        mB.setSize(View.getInstance().getDisplaySystem().getWidth(), 20);
        mB.setPosition(new Point(0,View.getInstance().getDisplaySystem().getHeight()-20));
        mB.setShrinkable(false);
        mB.getAppearance().removeAll();
		mB.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		mB.getAppearance().setTextColor(Color.WHITE);
		mB.getAppearance().setSelectionTextColor(Color.YELLOW);
		//mB.getAppearance().add(new PlainBorder(Color.WHITE_HALF_OPAQUE));
        mB.setVisible(true);
       
        
        //=[  FILE  ]=
        Menu mnuFile = new Menu();
        mB.registerSubMenu(mnuFile, "File");
        makeMenuItem(LOAD_SCENE, mnuFile);
        makeMenuItem(SAVE_SCENE, mnuFile);
	
        //=[  VIEW  ]=
        Menu mnuView = new Menu();
        mB.registerSubMenu(mnuView, "View");
        makeMenuItem(SLIDE_VIEW, mnuView);
        makeMenuItem(ATLAS_LATERAL_VIEW, mnuView);
        makeMenuItem(ATLAS_MEDIAL_VIEW, mnuView);
        makeMenuItem(ATLAS_MEDIAL_VIEW_SMOOTH, mnuView);
        
        //=[  OBJ  ]=
        Menu mnuObjects = new Menu();
        mB.registerSubMenu(mnuObjects, "Objects");
        
        makeMenuItem(CELLS, mnuObjects);
        makeMenuItem(VOLUMES, mnuObjects);
        makeMenuItem(SEMANTICS, mnuObjects);
        
        //=[  CKB  ]=
        Menu mnuCKB = new Menu();
        mB.registerSubMenu(mnuCKB, "Cellular KB");
        makeMenuItem(LIST_INSTANCES, mnuCKB);
        
        //=[  ATLAS  ]=
        Menu mnuAtlas = new Menu();
        mB.registerSubMenu(mnuAtlas, "Reference Atlas");
        makeMenuItem(SHOW_ATLAS, mnuAtlas);
        makeMenuItem(DISPLAY_BASIC_ATLAS, mnuAtlas);
        
        //=[ MANIPULATE  ]= 
        Menu mnuManip = new Menu();
        mB.registerSubMenu(mnuManip, strMNU_MANIPULATE);
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
	
	public void menuItemPressed(MenuItemPressedEvent arg0)
	{
		//FIXME: replace this string business with references to objects
		String act = arg0.getItem().getText(); //action to perform
		
		if ( LOAD_SCENE.equals(act) )
		{
			View2D.getInstance().loadFileChooser();
		}
		else if ( SAVE_SCENE.equals(act) )
		{
			System.out.println("Feature Not Implemented Yet");
		}
		else if ( CELLS.equals(act) )
		{
			View2D.getInstance().loadCellChooser();
		}
		else if ( VOLUMES.equals(act) )
		{

		}
		else if ( SEMANTICS.equals(act) )
		{

		}
		else if ( LIST_INSTANCES.equals(act) )
		{
			View2D.getInstance().loadInstanceBrowser();
		}
		else if ( SHOW_ATLAS.equals(act) )
		{
			View2D.getInstance().loadAtlasBrowser();
		}
		else if ( DISPLAY_BASIC_ATLAS.equals(act) )
		{
			ReferenceAtlas.getInstance().displayBasicAtlas();
		}
		else if ( SLIDE_VIEW.equals(act) )
		{
			View.getInstance().getCamera().setToSlideView();
		} 
		else if (ATLAS_LATERAL_VIEW.equals(arg0.getItem().getText())) 
		{
			View.getInstance().getCamera().setToAtlasLateralView();
		} 
		else if (ATLAS_MEDIAL_VIEW.equals(arg0.getItem().getText())) 
		{
			View.getInstance().getCamera().setToAtlasMedialView();
		}
		else if (ATLAS_MEDIAL_VIEW_SMOOTH.equals(arg0.getItem().getText())) 
		{
			View.getInstance().getCamera().smoothlyZoomToAtlasMedialView();
		}
		else if ( strMNU_MANI_MOVE.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_MOVE);
		}
		else if ( strMNU_MANI_ROTATEA.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_ROTATEX);
		}
		else if ( strMNU_MANI_ROTATEB.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_ROTATEY);
		}
		else if ( strMNU_MANI_ROTATEC.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_ROTATEZ);
		}
		else if ( strMNU_MANI_LOOK.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_LOOKAT);
		}
		else if ( strMNU_MANI_SCALE.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_SCALE);
		}
	}
}