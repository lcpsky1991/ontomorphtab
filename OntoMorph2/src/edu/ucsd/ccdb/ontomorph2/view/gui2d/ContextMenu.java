package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fenggui.background.PlainBackground;
import org.fenggui.border.TitledBorder;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color;

import com.jme.input.KeyInput;

import edu.ucsd.ccdb.ontomorph2.core.scene.CellFactory;
import edu.ucsd.ccdb.ontomorph2.core.scene.CurveFactory;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
import edu.ucsd.ccdb.ontomorph2.view.View3DMouseListener;

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
	
	public static final int CTX_ACTION_NEW_CURVE = 105;
	public static final int CTX_ACTION_NEW_ANCHOR = 106;
	public static final int CTX_ACTION_SELECT = 107;
	public static final int CTX_ACTION_MODE = 108;
	public static final int CTX_ACTION_ANIMATE = 109;
	public static final int CTX_ACTION_NONE = 0;
	public static final int CTX_ACTION_DEBUG = 110;
	public static final int CTX_ACTION_MANIP = 99;
	public static final int CTX_ACTION_ATTACH = 114;
	public static final int CTX_ACTION_PROPOGATE = 115;
	public static final int CTX_ACTION_DELETE = 116;
	public static final int CTX_ACTION_RENAME = 117;
	public static final int CTX_ACTION_NEW_CELLE = 120;
	public static final int CTX_ACTION_NEW_CELL = 121;
	public static final int CTX_ACTION_VISIBLE = 125;
		
	//ms stands for Menu String
	static final String msNEW = "New ...";
	//static final String msN_CURVE = "Curve";
	//static final String msANNOTATE = "Annotate";
	static final String msANIMATE = "Animate";
	static final String msPROPERTIES = "Display Properties";
	static final String msN_ANCHOR = "Anchor Point";
	static final String msN_CELL = "Cell ...";
	//static final String msN_CELL_DG = "DG Granule";
	//static final String msN_CELL_CA3Pyr = "CA3 Pyramidal";
	//static final String msN_CELL_CA1Pyr = "CA1 Pyramidal";
	static final String msSELECT = "Select ...";
	static final String msDESELECT = "De-Select ...";
	static final String msSELECTPART = "Select Part ...";
	static final String msEDIT = "Toggle Edit";
	static final String msManipulate = "Manipulate ...";
	static final String msRotateY = "Rotate-Y";
	static final String msRotateX = "Rotate-X";
	static final String msRotateZ = "Rotate-Z";
	static final String msScale = "Scale";
	static final String msMove = "Move";
	static final String msNone = "(Nothing Selected)";
	//static final String msDebug = "Debug";
	static final String msATTACH = "Attach to...";
	//static final String msDELETE = "Delete";
	static final String msPropogate = "Propogate";
	static ContextMenu instance = null;
	
	//These are the titles of the user-defined fields of the menu items
	static final String mFIELD_ACTION = "action";
	static final String mFIELD_REFERENCE = "reference tangible";
	static final String mFIELD_VALUE = "value";
	
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
	private boolean isSameClass(List<Tangible> consider)
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
	
	private void buildMenu(List<Tangible> seltans)
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
		Menu mnuAttach = new Menu();
		Menu mnuModify = new Menu();
		
		//apply formatting
		decorate();

		// ============= FIND CONTEXT ? ==========================
		//decide on the 'TargetContext' for this menu
		//the target context can either be the originating tangible, or a 'master' object (as with anchorpoints)
		//if they are all the same, use first item as 'target context' from which to base the menus, and apply actions to all of them
		//if they are not the same, limit the possibilities to the basic menu
		boolean multiContext = false;	//true if context is more than one thing
		boolean homog = false; 			//short for homogeneous, true if all of them are same type
		Tangible baseContext = null;
		
		//set the flas on how to build the context menu
		if ( seltans.size() < 1)			//nothing selecting
		{
			homog = false;
			multiContext = false;
		}
		else if ( seltans.size() == 1) 		//only one thing
		{
			homog = true;
			multiContext = false;
			baseContext = seltans.get(0);
		}
		else if ( seltans.size() > 1)		//multi selected
		{
			homog = isSameClass(seltans);
			multiContext = true;
			
			//Now it is know characteristics about the context list, figure out what the target context should be
			if (homog)	//if the list is homogenous, then the choice is simple (best to take the most recent one, at the end of the list)
			{
				baseContext =  seltans.get(seltans.size() - 1); //get last one
			}
			else
			{
				//the selection is non-homogenous, so the programmer decides what the user meant
				if ( seltans.get(0) instanceof Curve3D)
				{
					baseContext = seltans.get(0);
				}
				else
				{
					baseContext = null;
				}
			}
		}
		else
		{
			//All conditions about list size were already considered, this should never happen!
			//"'How often have I said to you that when you have eliminated the impossible, whatever remains, however improbable, must be the truth?' - Sherlock";
			Log.warn("WTF");
		}
		//===========================================
		
		
		//TODO: build generic menu
		//Here is the shell for the basic context decision
		if (baseContext != null)
		{
			//TODO: build the usual for tangibles
			//==================================
			//	SECONDARY MENU ITEMS
			// secondary must be built before primary in order to attach them as non-empty
			//===================================
			
			menuItemFactory(mnuNew, "New Curve", CTX_ACTION_NEW_CURVE); //new curves are a generic action
	        menuItemFactory(mnuModify, "Annotate", CTX_ACTION_ANNOTATE);
	        menuItemFactory(mnuModify, "Properties", CTX_ACTION_DISPROP);
	        menuItemFactory(this, "Debug", CTX_ACTION_DEBUG);
	        menuItemFactory(mnuModify, "Delete", CTX_ACTION_DELETE);
	        menuItemFactory(mnuModify, "Rename", CTX_ACTION_RENAME);
	        menuItemFactory(mnuModify, "Set inVisibile:" , CTX_ACTION_VISIBLE);
			
	        //add new anchor points?
			if (baseContext instanceof Curve3D || baseContext instanceof CurveAnchorPoint)
			{
				//build curve special menu
				menuItemFactory(mnuNew, msN_ANCHOR, CTX_ACTION_NEW_ANCHOR);
			}
			
			//toggle edit mode
			if (baseContext instanceof NeuronMorphology || baseContext instanceof Curve3D)
			{
				menuItemFactory(this, msEDIT, CTX_ACTION_MODE, baseContext, 0);
			}
			
			if (baseContext instanceof NeuronMorphology)
			{
				//build morphology special menu
				menuItemFactory(this, msPropogate, CTX_ACTION_PROPOGATE);
			}
			
			
			//add new cells
			if (baseContext instanceof Slide || baseContext instanceof Curve3D)
			{
				//build slide special menu
				menuItemFactory(mnuNew_Cell, "DG Cell", CTX_ACTION_NEW_CELLE);
				menuItemFactory(mnuNew_Cell, "From Disk...", CTX_ACTION_NEW_CELL);
			}
			
			
			//ATTACH TO:
	        if (baseContext instanceof NeuronMorphology)
	        {
	        	Set<Curve3D> candidates = View.getInstance().getScene().getCurves();
		        for (Curve3D c : candidates) menuItemFactory(mnuAttach, c.getName(), CTX_ACTION_ATTACH, c, 0);
	        }
			
			
			
			//MANIPULATE
			{//they are later distinguished by name, action is the same
				menuItemFactory(mnuManipulate, msMove, CTX_ACTION_MANIP, null, View3DMouseListener.METHOD_MOVE);
				menuItemFactory(mnuManipulate, msScale, CTX_ACTION_MANIP, null, View3DMouseListener.METHOD_SCALE);
				menuItemFactory(mnuManipulate, msRotateX, CTX_ACTION_MANIP, null, View3DMouseListener.METHOD_ROTATEX);
				menuItemFactory(mnuManipulate, msRotateY, CTX_ACTION_MANIP, null, View3DMouseListener.METHOD_ROTATEY);
				menuItemFactory(mnuManipulate, msRotateZ, CTX_ACTION_MANIP, null, View3DMouseListener.METHOD_ROTATEZ);
			}
			
	        //DYNAMIC RE-SELECT
	        {
		        for (int i=0; i < seltans.size(); i++)
		        {
		        	Tangible single = seltans.get(i);
		        	String name = single.getName();
		        	menuItemFactory(mnuPart, name, CTX_ACTION_SELECT, single, 0);
		        }
	        }
	        
	        
	        if (mnuNew_Cell.getItemCount() > 0) mnuNew.registerSubMenu(mnuNew_Cell, msN_CELL); //'newcell' must preceed registering parent 'new'
	        if (mnuModify.getItemCount() > 0) this.registerSubMenu(mnuModify, "Modify ...");
	        if (mnuNew.getItemCount() > 0) this.registerSubMenu(mnuNew, msNEW);
	        if (mnuAttach.getItemCount() > 0) this.registerSubMenu(mnuAttach, msATTACH);
			if (mnuManipulate.getItemCount() > 0) this.registerSubMenu(mnuManipulate, msManipulate);
			if (mnuPart.getItemCount() > 1) this.registerSubMenu(mnuPart,msSELECTPART);	//no need for parts if theres only 1 part
		} //end baseContext

        //DYNAMIC SELECT
        {	//find all thing a user MIGHT want to select and puts them in the select submenu
        	ArrayList<Tangible> others = View.getInstance().getView3DMouseListener().psuedoPick(KeyInput.get().isControlDown(), false);
	        for (int i=0; i < others.size(); i++)
	        {
	        	Tangible single = others.get(i);
	        	String name = single.getName();
		        menuItemFactory(mnuSelect, name, CTX_ACTION_SELECT, single, 0);	
	        }
        }
		
		//TODO: build the rest
		  //========================================
		// PRIMARY MENU ITEMS 
		//========================================
		if (mnuSelect.getItemCount() > 0) this.registerSubMenu(mnuSelect,msSELECT);
        this.setSizeToMinSize();
        this.layout();			
	}
	
	
	public void displayMenuFor(int xCoord, int yCoord, List<Tangible> t) 
	{
		
		int x = xCoord;
		int y = yCoord;
		this.setXY(x, y);
		
		buildMenu(t);
		
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
			ntitle = " Many (" + t.size() + ")";
		}	
		else 
		{
			ntitle = " (None) ";
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
	 * @param value an extra parmeter for the menu item, commonly, this is how much of the specified action is to be performed or 
	 * @param parent If null, will create a new parent menu
	 * @see View3DMouseHandler 
	 */
	private void menuItemFactory(Menu mparent, String title, int action, Tangible effectedTangibles, double value)
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
				mitem.addUserData(mFIELD_VALUE, value);
			}
		}
		catch (Exception e)
		{
			Log.warn("Exception; Error creating submenu; " + e.getMessage());
		}
	}
	
	/**
	 * Overloads menuItemFactory with fewer arguments for simplicity and default values
	 * Many items will not need to reference a tangible directly
	 * @param mparent
	 * @param title
	 * @param action
	 */
	private void menuItemFactory(Menu mparent, String title, int action)
	{
		menuItemFactory(mparent, title, action, null, 0);
	}
	
	public void menuItemPressed(MenuItemPressedEvent arg0) {
		
		String opt = arg0.getItem().getText();
		
		MenuItem trigger = arg0.getItem();
		System.out.println(opt + "     " + trigger);

		
		//====== SETUP ==============
		//get all of the fields form the MenuItem
		int fieldAct = (Integer)trigger.getUserData(mFIELD_ACTION); //find out what action we should do on the tangible
		Tangible fieldOrig = (Tangible)trigger.getUserData(mFIELD_REFERENCE);
		double fieldV = (Double)trigger.getUserData(mFIELD_VALUE);
		
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
			doActionOnSelected(fieldAct, fieldOrig, fieldV); //most things wont care about the 'reference' object so we concern ourselves with whats currently selected
		}

		this.closeBackward();
		
	}
	
	
	
	private void debug(Tangible src)
	{
		String info = "";
		
		/* debug view and plane
		OMTVector camDir = new OMTVector(View.getInstance().getCameraView().getCamera().getDirection());
		OMTVector plane = src.getWorldNormal();
		info += src.getWorldNormal() + "\n";
		info += " above: " + OMTUtility.isLookingFromAbove(camDir, plane);
		*/
		CoordinateSystem t = src.getCoordinateSystem();
		
		
		info += " coordinate system " + t + "\n";
		info += " demo system" + (new DemoCoordinateSystem());
		System.out.println(info);
	}
	
	private void doActionOnSelected(int action, Tangible target, double value)
	{
		List<Tangible> selected = TangibleManager.getInstance().getSelected();
		
		//Create a dummy frame for the dialog boxes to exist inside of, this forces the dialog boxes to appear 'on top' of the application
		JFrame frmDialog = new JFrame();
		frmDialog.setSize(0,0);
		frmDialog.setLocation(100,100);
		frmDialog.setVisible(true);
		
		
		String strReply = null;
		int ival = 0;
		
		for (int t = 0; t < selected.size(); t++)
		{
			Tangible single = selected.get(t);
			switch (action)
			{
				case CTX_ACTION_DISPROP:
					Log.warn("show properties for: " + single);
					break;
				case CTX_ACTION_ANNOTATE:
					Log.warn("annotate: " + single);
					break;
				case CTX_ACTION_ANIMATE:
					Log.warn("animate: " + single);
					break;
				case CTX_ACTION_NEW_CURVE:
					CurveFactory.getInstance().createCurve(single);
					break;
				case CTX_ACTION_NEW_ANCHOR:
					if (single instanceof CurveAnchorPoint) {
						((CurveAnchorPoint)single).createPoint();
					}
					break;
				case CTX_ACTION_VISIBLE:
					//TODO: generalize to all tangibles
					single.setVisible(!single.isVisible());
					View.getInstance().getScene().changed(Scene.CHANGED_SLIDE); //hacked for Ted Waitt, must be generalized
					break;
				case CTX_ACTION_MODE:
				{
					Curve3D ec = null;
					//get the curve that corresponds to the originating curve
					if ( single instanceof Curve3D) ec = (Curve3D) single;
					
					if ( ec != null)
					{
						ec.setAnchorPointsVisibility(!ec.getAnchorPointsVisibility()); 
						ec.reapply();
					}
					break;
				}
				case CTX_ACTION_PROPOGATE:
				{
					try
					{
						strReply = JOptionPane.showInputDialog(frmDialog, "Propagate how many cells?", "How many?", JOptionPane.QUESTION_MESSAGE);
						if ( strReply != null) ival = Integer.parseInt(strReply);
						if (single instanceof NeuronMorphology) {
							CellFactory.getInstance().propagate(((NeuronMorphology)single),ival);	
						}
					}
					catch (NumberFormatException e)
					{
						//user did not enter a valid number, do nothing
					}
				}
				
				break;
				case CTX_ACTION_NEW_CELLE:
					CellFactory.getInstance().createCellOn(single, CellFactory.TYPE_CELL_DG_A);
					Log.warn("Attempting to create " + CellFactory.TYPE_CELL_DG_A + " on " + single.getName());
					break;
				case CTX_ACTION_NEW_CELL:
					try
					{
						Log.warn("Attempting to add cell on " + single.getName());
						String file = View2D.getInstance().showFileChooser().getName();						
						file = file.substring(0, file.indexOf(".morph.xml"));		//strip off the extensions because the filenames are used as a key
						
						if (file != null) CellFactory.getInstance().createCellOn(single, file);
					}
					catch(Exception e)
					{
						Log.warn("failed createCellOn: " + e.getMessage());
					}
					
					break;
				case CTX_ACTION_DELETE:
					{
						ival = JOptionPane.showConfirmDialog(frmDialog, "Are you sure you wish to delete " + single.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
						if (ival == JOptionPane.YES_OPTION) 
						{
							if (!single.delete())
							{
								JOptionPane.showMessageDialog(null, "Unable to delete " + single.getName());
							}
						}
					}
					break;
				case CTX_ACTION_RENAME:
					strReply = JOptionPane.showInputDialog(frmDialog, "Rename '" + single.getName() + "' to:", single.getName());
					if (strReply != null) single.setName(strReply);
					break;
				case CTX_ACTION_MANIP:
					View.getInstance().getView3DMouseListener().setManipulation((int)value);
					break;
				case CTX_ACTION_DEBUG:
					debug(single);
					break;
				case CTX_ACTION_ATTACH:
					((NeuronMorphology)single).attachTo((Curve3D)target);
					break;
				default:
				{
					Log.warn("menu pressed but not handled " + single);					
					break;	
				}
			}//end switch

			frmDialog.setVisible(false);
			frmDialog.dispose(); //destroy the frame that holds the dialogs
		}
	}


	
}


