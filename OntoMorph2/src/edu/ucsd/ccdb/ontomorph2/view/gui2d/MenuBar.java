package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.io.File;

import org.fenggui.background.PlainBackground;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.CellFactory;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
import edu.ucsd.ccdb.ontomorph2.view.View3DMouseListener;

/**
 * Implements the main menu bar at the top of the application.  Also processes the events when
 * the menu items are selected and makes the corresponding calls.
 * 
 * <a href="http://openccdb.org/wiki/index.php/Brain_Catalog_Interface#Menu_Bar">More information</a>
 *  about the design of this class is available on the website.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MenuBar extends org.fenggui.menu.MenuBar implements IMenuItemPressedListener{
	
	public static final String LOAD_SCENE = "Import File...";
	public static final String SAVE_SCENE = "Save Scene...";
	public static final String strCELLS = "Cells...";
	public static final String VOLUMES = "Volumes...";
	public static final String SEMANTICS = "Semantics...";
	public static final String LIST_INSTANCES = "List Instances...";
	public static final String SHOW_ATLAS = "Show Atlas...";
	public static final String DISPLAY_BASIC_ATLAS = "Display Basic Atlas";
	public static final String HIDE_BASIC_ATLAS = "Hide Basic Atlas";
	public static final String DISPLAY_DEMO_ATLAS = "Display Demo Atlas";
	public static final String HIDE_DEMO_ATLAS = "Hide Demo Atlas";
	public static final String SLIDE_VIEW = "View Example Slide";
	public static final String ATLAS_LATERAL_VIEW = "View Atlas Lateral Side";
	public static final String ATLAS_MEDIAL_VIEW = "View Atlas Medial Side";
	public static final String SLIDE_VIEW_SMOOTH = "Smoothly Zoom to Hippocampus";
	public static final String SLIDE_VIEW_CEREB_SMOOTH = "Smoothly Zoom to Cerebellum";
	public static final String ATLAS_LATERAL_VIEW_SMOOTH = "Smoothly Zoom To Lateral Side";
	public static final String ATLAS_MEDIAL_VIEW_SMOOTH = "Smoothly Zoom To Medial Side";
	public static final String ATLAS_CELL_VIEW_SMOOTH = "Smoothly Zoom To View Hippocampal Cells";
	public static final String ATLAS_SUBCELL_VIEW_SMOOTH = "Smoothly Zoom To View Subcellular Components";
	public static final String strVIEW_SLIDES = "Toggle Slides";
	public static final String strMNU_MANIPULATE = "Manipulate Object";
	public static final String strMNU_MANI_ROTATEA = "Rotate (X - Axis)";
	public static final String strMNU_MANI_ROTATEB = "Rotate (Y - Axis)";
	public static final String strMNU_MANI_ROTATEC = "Rotate (Z - Axis)";
	public static final String strMNU_MANI_MOVE = "Move";
	public static final String strMNU_MANI_LOOK = "Focus Camera";
	public static final String strMNU_MANI_PICK = "Pick";
	public static final String strMNU_MANI_NONE = "Pan Camera";
	public static final String strMNU_MANI_SCALE = "Re-Scale";
	public static final String strNEW_CELL_A = CellFactory.TYPE_CELL_DG_A;
	public static final String strNEW_CELL_B = CellFactory.TYPE_CELL_PYR_CA3_A;
	public static final String strNEW_CELL_DISK = "From Disk...";
	
	public static final String strNEW = "New ...";
	public static final String BASIC_SEARCH = "Basic Search...";
	public static final String SHOW_SCENE_MONITOR = "Show Scene Monitor...";
		
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
        //mB.addMouseEnteredListener(FocusManager.get());
        //mB.addMouseExitedListener(FocusManager.get());
        
        //=[  FILE  ]=
        Menu mnuFile = new Menu();
        mB.registerSubMenu(mnuFile, "File");
        makeMenuItem(LOAD_SCENE, mnuFile);
        makeMenuItem(SAVE_SCENE, mnuFile);
        
        
        //=[ SEARCH ]=
        Menu mnuSearch = new Menu();
        mB.registerSubMenu(mnuSearch, "Search");
        makeMenuItem(BASIC_SEARCH, mnuSearch);
        

        //=[  VIEW  ]=
        Menu mnuView = new Menu();
        mB.registerSubMenu(mnuView, "View");
        makeMenuItem(strVIEW_SLIDES, mnuView);
        makeMenuItem(SLIDE_VIEW_CEREB_SMOOTH, mnuView);
        makeMenuItem(SLIDE_VIEW_SMOOTH, mnuView);
        makeMenuItem(ATLAS_CELL_VIEW_SMOOTH, mnuView);
        makeMenuItem(ATLAS_SUBCELL_VIEW_SMOOTH, mnuView);
        makeMenuItem(ATLAS_LATERAL_VIEW_SMOOTH, mnuView);
        makeMenuItem(ATLAS_MEDIAL_VIEW_SMOOTH, mnuView);
        
        
        //=[  OBJ  ]=
        Menu mnuObjects = new Menu();
        Menu mnuObjects_New = new Menu();
        
        mB.registerSubMenu(mnuObjects, "Objects");
        makeMenuItem(strCELLS, mnuObjects);
        
        
        mnuObjects.registerSubMenu(mnuObjects_New, strNEW);
        makeMenuItem(strNEW_CELL_A, mnuObjects_New);
        makeMenuItem(strNEW_CELL_B, mnuObjects_New);
        makeMenuItem(strNEW_CELL_DISK, mnuObjects_New);
        //makeMenuItem(VOLUMES, mnuObjects);
        //makeMenuItem(SEMANTICS, mnuObjects);
        
        
        //=[  CKB  ]=
        Menu mnuCKB = new Menu();
        mB.registerSubMenu(mnuCKB, "Cellular KB");
        makeMenuItem(LIST_INSTANCES, mnuCKB);
        
        //=[  ATLAS  ]=
        Menu mnuAtlas = new Menu();
        mB.registerSubMenu(mnuAtlas, "Reference Atlas");
        makeMenuItem(DISPLAY_BASIC_ATLAS, mnuAtlas);
        makeMenuItem(HIDE_BASIC_ATLAS, mnuAtlas);
        makeMenuItem(DISPLAY_DEMO_ATLAS, mnuAtlas);
        makeMenuItem(HIDE_DEMO_ATLAS, mnuAtlas);
        makeMenuItem(SHOW_ATLAS, mnuAtlas);
        
        //=[ MANIPULATE  ]= 
        Menu mnuManip = new Menu();
        mB.registerSubMenu(mnuManip, strMNU_MANIPULATE);
        makeMenuItem(strMNU_MANI_MOVE, mnuManip);
        makeMenuItem(strMNU_MANI_SCALE, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEA, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEB, mnuManip);
        makeMenuItem(strMNU_MANI_ROTATEC, mnuManip);
        makeMenuItem(strMNU_MANI_LOOK, mnuManip);
        makeMenuItem(strMNU_MANI_NONE, mnuManip);
        
        //=[ DEBUG ]=
        Menu mnuDebug = new Menu();
        mB.registerSubMenu(mnuDebug, "Debug");
        makeMenuItem(SHOW_SCENE_MONITOR, mnuDebug);

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
	
	public void menuItemPressed(MenuItemPressedEvent arg0)
	{
		//FIXME: replace this string business with references to objects
		String act = arg0.getItem().getText(); //action to perform
		
		if ( LOAD_SCENE.equals(act) )
		{
			//get file
			File nfile = View2D.getInstance().showFileChooser();
			//load that file
			TangibleManager.getInstance().loadFile(nfile);
			
		}
		else if ( SAVE_SCENE.equals(act) )
		{
			OntoMorph2.getCurrentScene().save();
		}
		else if ( strCELLS.equals(act) )
		{
			View2D.getInstance().loadCellChooser();
		}
		else if ( VOLUMES.equals(act) )
		{

		}
		else if ( SEMANTICS.equals(act) )
		{

		}
		else if ( strVIEW_SLIDES.equals(act))
		{
			//toggle slides on or off
			for (Slide s : TangibleManager.getInstance().getSlides())
			{
				s.setVisible(!s.isVisible());
				View.getInstance().getScene().changed(edu.ucsd.ccdb.ontomorph2.core.scene.Scene.CHANGED_SLIDE);
			}
		}
		else if ( strNEW_CELL_A.equals(act) )
		{
			CellFactory.getInstance().createFreeCell(CellFactory.TYPE_CELL_DG_A);
		}
		else if ( strNEW_CELL_B.equals(act) )
		{
			CellFactory.getInstance().createFreeCell(CellFactory.TYPE_CELL_PYR_CA3_A);
		}
		else if ( strNEW_CELL_DISK.equals(act) )
		{
			try
			{
				String strFile = View2D.getInstance().showFileChooser().getName();
				strFile = strFile.substring(0, strFile.indexOf(".morph.xml"));
				if (strFile != null);
				{
					CellFactory.getInstance().createFreeCell(strFile);	
				}	
			}
			catch(Exception e)
			{
				Log.warn("Failed to create free-floating cell because: " + e.getMessage());
			}
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
		else if ( HIDE_BASIC_ATLAS.equals(act) )
		{
			ReferenceAtlas.getInstance().hideBasicAtlas();
		}
		else if ( DISPLAY_DEMO_ATLAS.equals(act) )
		{
			ReferenceAtlas.getInstance().displayDemoAtlas();
		}
		else if ( HIDE_DEMO_ATLAS.equals(act) )
		{
			ReferenceAtlas.getInstance().hideDemoAtlas();
		}
		else if ( SLIDE_VIEW.equals(act) )
		{
			View.getInstance().getCameraView().setToSlideView();
		} 
		else if (ATLAS_LATERAL_VIEW.equals(act)) 
		{
			View.getInstance().getCameraView().setToAtlasLateralView();
		} 
		else if (ATLAS_MEDIAL_VIEW.equals(act)) 
		{
			View.getInstance().getCameraView().setToAtlasMedialView();
		}
		else if (SLIDE_VIEW_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToSlideView();
		} 
		else if (SLIDE_VIEW_CEREB_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToSlideCerebellumView();
		}
		else if (ATLAS_CELL_VIEW_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToCellView();
		}
		else if (ATLAS_SUBCELL_VIEW_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToSubcellularView();
		}
		else if (ATLAS_LATERAL_VIEW_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToAtlasLateralView();
		}
		else if (ATLAS_MEDIAL_VIEW_SMOOTH.equals(act)) 
		{
			View.getInstance().getCameraView().smoothlyZoomToAtlasMedialView();
		}
		else if ( strMNU_MANI_NONE.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_NONE);
		}
		else if ( strMNU_MANI_PICK.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_PICK);
		}
		else if ( strMNU_MANI_MOVE.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_MOVE);
		}
		else if ( strMNU_MANI_ROTATEA.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_ROTATEX);
		}
		else if ( strMNU_MANI_ROTATEB.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_ROTATEY);
		}
		else if ( strMNU_MANI_ROTATEC.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_ROTATEZ);
		}
		else if ( strMNU_MANI_LOOK.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_LOOKAT);
		}
		else if ( strMNU_MANI_SCALE.equals(act) )
		{
			View.getInstance().getView3DMouseListener().setManipulation(View3DMouseListener.METHOD_SCALE);
		}
		else if (BASIC_SEARCH.equals(act)) {
			View2D.getInstance().loadBasicSearchBox();
		}
		else if (SHOW_SCENE_MONITOR.equals(act)) {
			View.getInstance().getView3D().showSceneMonitor();
		}
	}
	
}
