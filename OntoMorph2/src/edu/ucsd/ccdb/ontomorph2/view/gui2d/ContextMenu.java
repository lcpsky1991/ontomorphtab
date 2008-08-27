package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fenggui.background.PlainBackground;
import org.fenggui.border.TitledBorder;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;

import com.jme.input.KeyInput;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
import edu.ucsd.ccdb.ontomorph2.view.View3DMouseHandler;

/**
 * A dynamic context menu that pops up when you right click in the display.
 * 
 * <a href="http://openccdb.org/wiki/index.php/Brain_Catalog_Interface#Context_Menu" More information</a>
 * about the design of this class is available on the website.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class ContextMenu extends Menu implements IMenuItemPressedListener{
	
	
	//METHODS that Menu buttons take
	//CTX is short for ConTeXt
	public static final int CTX_ACTION_ANNOTATE = 100;
	public static final int CTX_ACTION_DISPROP = 101;
	public static final int CTX_ACTION_DESELECT = 103;
	public static final int CTX_ACTION_NEW_CELL = 104;
	public static final int CTX_ACTION_NEW_CURVE = 105;
	public static final int CTX_ACTION_NEW_ANCHOR = 106;
	public static final int CTX_ACTION_SELECT = 107;
	public static final int CTX_ACTION_MODE = 108;
	public static final int CTX_ACTION_ANIMATE = 109;
	public static final int CTX_ACTION_NONE = 0;
	public static final int CTX_ACTION_DEBUG = 110;
	
	
	
	//ms stands for Menu String
	static final String msNEW = "New ...";
	static final String msN_CURVE = "Curve";
	static final String msANNOTATE = "Annotate";
	static final String msANIMATE = "Animate";
	static final String msPROPERTIES = "Display Properties";
	static final String msN_ANCHOR = "Anchor Point";
	static final String msN_CELL = "Cell ...";
	static final String msN_CELL_DG = "DG Granule";
	static final String msN_CELL_CA3Pyr = "CA3 Pyramidal";
	static final String msN_CELL_CA1Pyr = "CA1 Pyramidal";
	static final String msSELECT = "Select ...";
	static final String msDESELECT = "De-Select ...";
	static final String msSELECTPART = "Select Part ...";
	static final String msEDIT = "Toggle Edit";
	static final String msManipulate = "Manipulate ...";
	static final String msNone = "(Nothing Selected)";
	static final String msDebug = "Debug";
	static ContextMenu instance = null;
	
	//These are the titles of the user-defined fields of the menu items
	static final String mFIELD_ACTION = "action";
	static final String mFIELD_REFERENCE = "reference tangible";
	
	TitledBorder border = null;
	
	//want to have 'persistent' objects that can be added and removed from the context menu
	private static final DemoCoordinateSystem dcoords =  new DemoCoordinateSystem();	//coordinates for test-case new objects
	
	public static ContextMenu getInstance()	{
		if (instance == null) 
		{
			instance = new ContextMenu();
		}
		return instance;
	}
	
	/**
	 * Root
	 * 		New		(dynamic)
	 * 			Cell	(dynamic)
	 * 				DG
	 * 				CA1
	 * 				CA3
	 * 			Curve
	 * 			Anchor Point
	 * 		Select (dynamic)
	 * 			A
	 * 			B
	 * 			C 
	 * 			......
	 * 		Manipulate
	 * 			Rotate X
	 * 			Rotate Y
	 * 			Move
	 * 			Move
	 * 
	 * 		------
	 * 		Annotate
	 * 		Display Properties
	 * @author caprea
	 *
	 */
	
	
	private ContextMenu() 
	{        
		//in anticipation of not-recreating the contextmenu, much of initialization is now in create()
		//create menuItem objects
		
	
	    this.addMouseEnteredListener(FocusManager.get());
	    this.addMouseExitedListener(FocusManager.get());
	}
	
	private void decorate()
	{
		this.setShrinkable(false);
        this.setSize(200,200);
        this.getAppearance().removeAll();
		this.getAppearance().add(new PlainBackground(new Color(0.5f, 0.5f, 0.5f, 5.0f)));
		border = new TitledBorder("context menu");
		border.setTextColor(Color.WHITE);
		this.getAppearance().add(border);
		this.getAppearance().setTextColor(Color.WHITE);
		this.getAppearance().setTextSelectionColor(Color.YELLOW);
	}
	
	
	private void removeAllItems()
	{
		Iterator i = this.getMenuItems().iterator();
		while ( i.hasNext() )
		{
			MenuItem m = (MenuItem) i.next();
			m.removeMenuItemPressedListener(this);
			m = null;
			i.remove();
		}
	
	}
	
	/**
	 * loops through all elements in the array to tell if theya re the same class or not
	 * @param consider
	 * @return True if all elements have the same Class type
	 */
	private boolean isSameClass(ArrayList<Tangible> consider)
	{
		Tangible firstElement = consider.get(0);
		
		for (int i = 0; i < consider.size(); i++)
		{
			Tangible single = consider.get(i);
			
			if (!single.getClass().equals(firstElement.getClass()))
			{
				return false;
			}
		}
		return true;
	}
	
	private void create(List<Tangible> tans)
	{
		//"If you build it, they will come"
		removeAllItems();
		
		//submenus
		Menu mnuNew = new Menu();
		Menu mnuNew_Cell = new Menu();
		Menu mnuSelect = new Menu();
		Menu mnuDeselect = new Menu();
		Menu mnuManipulate = new Menu();
		Menu mnuPart = new Menu();
		
		
		//apply formatting
		decorate();
		
		//
		if ( tans.size() > 0 )
		{
			//==================================
			//	SECONDARY MENU ITEMS
			// secondary must be built before primary in order to attach them as non-empty
			
			
			//===================================
			menuItemFactory(mnuNew, msN_CURVE, CTX_ACTION_NEW_CURVE, null);
	        menuItemFactory(mnuNew, msN_ANCHOR, CTX_ACTION_NEW_ANCHOR, null);
	        menuItemFactory(mnuNew_Cell, msN_CELL_DG, CTX_ACTION_NEW_CELL, null);
	        menuItemFactory(this, msANNOTATE, CTX_ACTION_ANNOTATE, null);
	        menuItemFactory(this, msEDIT, CTX_ACTION_MODE, null);
	        menuItemFactory(this, msPROPERTIES, CTX_ACTION_DISPROP, null);
	        menuItemFactory(this, msDebug, CTX_ACTION_DEBUG, null);
	        
	        //DYNAMIC SELECT
	        {	//find all thing a user MIGHT want to select and puts them in the select submenu
	        	ArrayList<Tangible> others = View.getInstance().getView3DMouseHandler().psuedoPick(KeyInput.get().isControlDown(), false);
		        for (int i=0; i < others.size(); i++)
		        {
		        	Tangible single = others.get(i);
		        	String name = single.getName();
			        menuItemFactory(mnuSelect, name, CTX_ACTION_SELECT, single);	
		        }
	        }
	        
	        //DYNAMIC RE-SELECT
	        {
		        for (int i=0; i < tans.size(); i++)
		        {
		        	Tangible single = tans.get(i);
		        	String name = single.getName();
		        	menuItemFactory(mnuPart, name, CTX_ACTION_SELECT, single);
		        }
	        }
	
	        //========================================
			// PRIMARY MENU ITEMS 
			//========================================
	        
			this.registerSubMenu(mnuNew, msNEW);
			this.registerSubMenu(mnuManipulate, msManipulate);
			mnuNew.registerSubMenu(mnuNew_Cell, msN_CELL);
			this.registerSubMenu(mnuPart,msSELECTPART);
			this.registerSubMenu(mnuSelect,msSELECT);
		}
		else
		{
			//If nothing was selected make the 'default' menu
			menuItemFactory(this, msNone, View3DMouseHandler.METHOD_NONE, null);
		}
			
        this.setSizeToMinSize();
        this.layout();			
	}
	
	
	public void displayMenuFor(int xCoord, int yCoord, List<Tangible> t) 
	{
		
		int x = xCoord;
		int y = yCoord;
		this.setXY(x, y);
		
		create(t);
		
		border.setTitle(dynamicTitle(t));
		
		if(this.equals(View2D.getInstance().getDisplay().getPopupWidget())) // popupmenu is already visible!
		{
			View2D.getInstance().removePopup();
		}
		
		View2D.getInstance().displayPopUp(this);
	}
	
	
	private String dynamicTitle(List<Tangible> t)
	{
		String ntitle = "";
		
//		handles the case of a SINGLE selection
		if (t != null & t.size() == 1) {
			ntitle = (t.iterator().next().getName());
		}	//handles the case of multiselection
		else if ( t != null & t.size() > 1)
		{
			ntitle = " Many ";
		}	
		else 
		{
			ntitle = " (none) ";
		}
		return ntitle;
	}
	
	/**
	 * Conveiniance method that wraps the creation of menus
	 * Each menu item will potentially have an 'action' tied to it as well as an originating {@link Tangible} to perform that action upon
	 * @author caprea
	 * @param title
	 * @param action the action to perform on the Tangible, which is paramaterized by tangChange 
	 * @param effectedTangibles the {@link Tangible} within the world to perform action upon
	 * @param parent If null, will create a new parent menu
	 * @see View3DMouseHandler 
	 */
	private void menuItemFactory(Menu mparent, String title, int action, Tangible effectedTangibles)
	{
		try
		{
			if (title == null || "".equals(title))
			{
				title = "(null)";
			}
			if (null == mparent )
			{
				return; //exit gracefully if parent is empty
			}
			else if ( mparent instanceof Menu)
			{
				MenuItem mitem;
				mitem = new MenuItem(title);
				mitem.addMenuItemPressedListener(this);
				mparent.addItem(mitem); 
				mitem.addUserData(mFIELD_REFERENCE, effectedTangibles); 	//tie a Tangible to the menu button
				mitem.addUserData(mFIELD_ACTION, action);			//tie an associated action to the menu button
			}
		}
		catch (Exception e)
		{
			Log.warn("Exception; Error creating submenu; " + e.getMessage());
		}
        
	}
	public void menuItemPressed(MenuItemPressedEvent arg0) {
		
		String opt = arg0.getItem().getText();
		MenuItem trigger = arg0.getItem();
		
		
		//====== SETUP ==============
		//get all of the fields form the MenuItem
		int fieldAct = (Integer)trigger.getUserData(mFIELD_ACTION); //find out what action we should do on the tangible
		Tangible fieldOrig = (Tangible)trigger.getUserData(mFIELD_REFERENCE);
		
		//===========================
		//SINGLE - ACTION CODE (this code does not iterate over all, only concerned with menu-referenced object)
		if (CTX_ACTION_SELECT == fieldAct)
		{
				if (fieldOrig != null) fieldOrig.select();
		}
		else if (CTX_ACTION_DESELECT == fieldAct) 
		{
				if (fieldOrig != null) fieldOrig.unselect();
		}
		else
		{//MULTIPLE - ACTION CODE
			doActionOnSelected(fieldAct); //most things wont care about the 'reference' object so we concern ourselves with whats currently selected
		}

		this.closeBackward();
		
	}
	
	
		//===========================
		//OLD ACTION CODE
		/*
		for (int t=0; t < TangibleManager.getInstance().getSelected().size(); t++)
		{
			Tangible orig = TangibleManager.getInstance().getSelected().get(t);	//find the originating tangible
			if (orig instanceof Tangible)
			{
				if (msANNOTATE.equals(opt)) {
					System.out.println("do annotation");
				} else if (msANIMATE.equals(opt)) {
					System.out.println("do animation");
				} else if (msPROPERTIES.equals(opt)) {
					System.out.println("show properties");
				}
				else if (msN_CURVE.equals(opt)) createCurve(orig);
				
				else if (msN_ANCHOR.equals(opt)) createPoint(orig);
				
				else if (msN_CELL_DG.equals(opt))	testCreateCell(orig);
				
				else if (msEDIT.equals(opt))
				{
					Curve3D ec = null;
					//get the curve that corresponds to the originating curve
					if ( orig instanceof Curve3D) ec = (Curve3D) orig;
					//else if ( orig instanceof CurveAnchorPoint)	ec = ((CurveAnchorPoint)orig).getParentCurve();
					
					if ( ec != null) ec.setAnchorPointsVisibility(!ec.getAnchorPointsVisibility());
				}
			}
			else if ( opt != null)
			{
				System.out.println("Warning: There was no tangible do to this action on (" + opt + ")");
			}	
		}*/
	
	
	private void debug(Tangible src)
	{
		String info = "";
		
		OMTVector camDir = new OMTVector(View.getInstance().getCamera().getCamera().getDirection());
		OMTVector plane = src.getWorldNormal();
		
		info += src.getWorldNormal() + "\n";
		info += " above: " + OMTUtility.isLookingFromAbove(camDir, plane);
		
		
		System.out.println(info);
	}
	
	public void doActionOnSelected(int action)
	{
		List<Tangible> selected = TangibleManager.getInstance().getSelected();
		for (int t = 0; t < selected.size(); t++)
		{
			Tangible single = selected.get(t);
			switch (action)
			{
				case CTX_ACTION_DISPROP:
					System.out.println("show properties for: " + single);
					{
						debug(single);
					}
					break;
				case CTX_ACTION_ANNOTATE:
					System.out.println("annotate: " + single);
					break;
				case CTX_ACTION_ANIMATE:
					System.out.println("animate: " + single);
					break;
				case CTX_ACTION_NEW_CURVE:
					createCurve(single);
					break;
				case CTX_ACTION_NEW_ANCHOR:
					createPoint(single);
					break;
				case CTX_ACTION_MODE:
				{
					Curve3D ec = null;
					//get the curve that corresponds to the originating curve
					if ( single instanceof Curve3D) ec = (Curve3D) single;
					
					if ( ec != null) ec.setAnchorPointsVisibility(!ec.getAnchorPointsVisibility());
					break;
				}
				case CTX_ACTION_DEBUG:
					if (single instanceof Slide)
					{
						System.out.println(((Slide)single).getWorldNormal());
					}
					break;
				default:
				{
					System.out.println("menu pressed but not handled " + single);
					break;	
				}
			}
		}
	}
	
	public void createPoint(Tangible src)
	{
		//if source is not an anchorpoint then dont do anything
		if (!(src instanceof CurveAnchorPoint)) return;	
		CurveAnchorPoint cp = (CurveAnchorPoint) src;
		int i = cp.getIndex() + 1;
		float t = cp.aproxTime();
		float delta = 0.05f;
		OMTVector place = null;
		
		
		//place = new OMTVector(src.getRelativePosition()); //original
		//find out what 'time' the current point is at, incriment that ammount a small ammount
		//find out the tangent of the current point, then add the tangent unit to the position
		//DOes not work well with the end points
		place = new OMTVector(cp.getParentCurve().getPoint(t+delta));

		cp.getParentCurve().addControlPoint(i, place);
	}
	
	/**
	 * This function intends to create a curve in the view based on some originating Tangible.
	 * If the originating Tangible has a CoordinateSystem then the curve is supposed to be created in parallel to that Coords
	 * If the originating Tangible has no CoordinateSystem then the curve will be parallel to the camera's plane and will also adop that plane to be its coordinatesystem
	 * Regarding movement for movement of the newly created curve it will inherit movement based on global, or on the CoordinateSystem
	 * 
	 * @param src
	 */
	public void createCurve(Tangible src)
	{
		//TODO: rewrite this!
		OMTVector cent = new OMTVector(src.getRelativePosition());
		OMTVector up = null;
		OMTVector left = null;
		OMTVector posa = null;
		OMTVector posb = null;
		OMTVector towardcam = null;
		float offset = 5f;	//the ammount of offset the new points from the originator
		
		System.out.println("src " + src.getAbsolutePosition() + src.getRelativePosition());
		//cent is the originating point, in some coordinate system
		//create two side points not in that coordinate system
		
		
		//if there is a coordinate system, apply this curve to that coordinate system
		//if no coordinate system make it aligned with the camera
		CoordinateSystem system = src.getCoordinateSystem();
		if (system != null)
		{
			//cent.subtractLocal(system.getOriginVector());
			towardcam = new OMTVector(OMTVector.UNIT_Z);
			up = new OMTVector(OMTVector.UNIT_Y);
			left = new OMTVector(OMTVector.UNIT_X);
		}
		else
		{
			//find the coordinate system of the camera on which to draw the curve parallel to, to do this we need three vectors
			towardcam = new OMTVector(View.getInstance().getCamera().getCamera().getDirection().normalize().negate().mult(5f)); 
			left = new OMTVector(View.getInstance().getCamera().getCamera().getLeft()); //too keep units consistent multiply by -1 so positive is 'right' (a droite)
			up = new OMTVector(View.getInstance().getCamera().getCamera().getUp());
			
			Vector3f combined = towardcam.normalize().add(left.normalize().add(up).normalize());
			//adopt the plane of the camera to be the coordinate system
			//TODO: apply coordinate system
			
		}
		
		//make two side points that are +/-X and +/-Y
		posa = new OMTVector(left.add(up).mult(offset));
		posb = new OMTVector(left.mult(-offset).add(up.mult(offset)));
		
		//align the side points to be near the center point
		posa.addLocal(cent);	
		posb.addLocal(cent);
		
		//adjust the curve so it appears ever-slightly in front of the background objects
		cent.addLocal(towardcam); 
		posa.addLocal(towardcam);
		posb.addLocal(towardcam);

		//System.out.println("sys " + system);
		//System.out.println("new curve @ " + posa + cent + posb);
		
		OMTVector[] pts = {posa, cent, posb};
		Curve3D cap = new Curve3D("capreas new deal", pts, system);	//FIXME: need to set demo coordinates on new curves
		cap.setColor(java.awt.Color.orange);
		cap.setVisible(true);
		cap.setModelBinormalWithUpVector(towardcam, 0.01f);		
		
		//redraw the scene, but not the whole scene, let observer know the curves have changed
		View.getInstance().getScene().changed(Scene.CHANGED_CURVE);
	}
	
	
	/**
	 * 
	 * 		mousePos.set( mouse.getLocalTranslation().x, mouse.getLocalTranslation().y );
            DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePos, 0, pickRay.origin );
            DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePos, 0.3f, pickRay.direction ).subtractLocal( pickRay.origin ).normalizeLocal()
	 */
	
	public void testCreateCell(Tangible src)
	{
		NeuronMorphology nc = null;
		Curve3D ocurve = null;
		
		//INITIAL
		//first, set things up and get the Reference Curve
		float t = 0.5f;	//default is middle
		if ( src instanceof Curve3D)
		{
			ocurve = (Curve3D) src;
		}
		else if ( src instanceof CurveAnchorPoint)
		{
			CurveAnchorPoint ocp = (CurveAnchorPoint) src;
			ocurve = ocp.getParentCurve();
		}
		else
		{
			//exit early without updating the scene
			return;
		}
		
		//SET
		//Find out where to put it
		List<NeuronMorphology> cells = ocurve.getChildrenCells();
		float high=0;
		//get information about the distribution of the cells along the curve
		for (int c=0; c < cells.size(); c++)
		{
			float num=cells.get(c).getTime();
			if (num > high) high = num;
		}
		t = (high / cells.size()) + high;
		
		
		//CREATE
		TangibleManager.getInstance().unselectAll();
		//do the rest of the actions
		nc = new MorphMLNeuronMorphology("5199202a", ocurve, t, NeuronMorphology.RENDER_AS_LOD, dcoords);
		nc.setRelativeScale(0.01f);
		nc.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
		nc.setVisible(true);
		nc.select();
		
		View.getInstance().getScene().changed(Scene.CHANGED_PART);
		
		/*
		View.getInstance().getView3D().addCell(nc);
		TangibleView cv = TangibleViewManager.getInstance().getTangibleViewFor(nc);
		if ( cv != null)
		{
			cv.update();
		}
		*/
	}
}


