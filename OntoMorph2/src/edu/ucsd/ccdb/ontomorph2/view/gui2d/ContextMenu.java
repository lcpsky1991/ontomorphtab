package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

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
import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;
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
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
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
	public static final int CTX_ACTION_NEW_CELL = 104;
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
	
	//IDs for easily creating cells
	public static final String TYPE_CELL_DG_A = "5199202a";
	public static final String TYPE_CELL_DG_B = " ";
	public static final String TYPE_CELL_DG_C = "";
	public static final String TYPE_CELL_PYR_CA1_A = "pc1c";
	public static final String TYPE_CELL_PYR_CA1_B = "pc2a";
	public static final String TYPE_CELL_PYR_CA1_C = "";
	public static final String TYPE_CELL_PYR_CA3_A = "cell1zr";
	public static final String TYPE_CELL_PYR_CA3_B = "cell2zr";
	public static final String TYPE_CELL_PYR_CA3_C = "cell6zr";
	
	//ms stands for Menu String
	static final String msNEW = "New ...";
	//static final String msN_CURVE = "Curve";
	//static final String msANNOTATE = "Annotate";
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
			System.out.println("WTF");
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
	        menuItemFactory(this, "Annotate", CTX_ACTION_ANNOTATE);
	        menuItemFactory(this, "Properties", CTX_ACTION_DISPROP);
	        menuItemFactory(this, "Debug", CTX_ACTION_DEBUG);
	        menuItemFactory(this, "Delete", CTX_ACTION_DELETE);
	        menuItemFactory(this, "Rename", CTX_ACTION_RENAME);
			
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
				menuItemFactory(mnuNew_Cell, msN_CELL_DG, CTX_ACTION_NEW_CELL);
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
		
		OMTVector camDir = new OMTVector(View.getInstance().getCameraNode().getCamera().getDirection());
		OMTVector plane = src.getWorldNormal();
		
		info += src.getWorldNormal() + "\n";
		info += " above: " + OMTUtility.isLookingFromAbove(camDir, plane);
		
		
		System.out.println(info);
	}
	
	public void doActionOnSelected(int action, Tangible target, double value)
	{
		List<Tangible> selected = TangibleManager.getInstance().getSelected();
		//Tangible recent = TangibleManager.getInstance().getSelectedRecent();
		
		String strReply = null;
		int ival = 0;
		
		for (int t = 0; t < selected.size(); t++)
		{
			Tangible single = selected.get(t);
			switch (action)
			{
				case CTX_ACTION_DISPROP:
					System.out.println("show properties for: " + single);
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
					
					if ( ec != null)
					{
						ec.setAnchorPointsVisibility(!ec.getAnchorPointsVisibility()); 
						ec.reapply();
					}
					break;
				}
				case CTX_ACTION_PROPOGATE:
				{
					strReply = JOptionPane.showInputDialog(null, "Propogate how many cells?", "How many?", JOptionPane.QUESTION_MESSAGE);
					if ( strReply != null) ival = Integer.parseInt(strReply);
					propogate(single, ival);
				}
					
					break;
				case CTX_ACTION_NEW_CELL:
					createCellOn(single);
					break;
				case CTX_ACTION_DELETE:
					{
						ival = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete " + single.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
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
					strReply = JOptionPane.showInputDialog("Rename '" + single.getName() + "' to:", single.getName());
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
					System.out.println("menu pressed but not handled " + single);
					break;	
				}
			}
		}
	}
	
	/**
	 * Propogates a cell with normal distribution
	 * @param original
	 */
	public void propogate(Tangible original, int howMany)
	{
			if (original instanceof NeuronMorphology)
			{
				NeuronMorphology cell = (NeuronMorphology) original;
				for (int i = 0; i < howMany; i++)
				{
					NeuronMorphology copy = cellFactory(cell.getName(),cell.getCurve());	//create a copy of the cells
					
					float rx = 0;
					float ry=0;
					
					//if cell is attached to curve have to scale the movement by alot more
					if (!cell.isFreeFloating())
					{
						copy.positionAlongCurve(cell.getCurve(), cell.getTime()); //start in same place
						rx = (float)OMTUtility.randomNumberGuassian(0, 100);
						copy.move(rx, ry, 0, 0);
					}
					else
					{	//put it at the original place
						copy.setRelativePosition(cell.getRelativePosition());	//start in same place
						rx = (float)OMTUtility.randomNumberGuassian(0, 10) + copy.getRelativePosition().getX();
						ry = (float)OMTUtility.randomNumberGuassian(0, 10) + copy.getRelativePosition().getY();
						copy.setRelativePosition(rx, ry, copy.getRelativePosition().getZ()); //keep the same Z
					}
					copy.rotate(rx, 0, new OMTVector(0,1,0)); //for aesthetics rotate them about Y to make them seem more random
				}
			}
	}
	
	public void createPoint(Tangible src)
	{
		//if source is not an  then dont do anything
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
		
		cp.getParentCurve().reapply(); //TODO: remove this line
	}
	
	/**
	 * This function intends to create a curve in the view based on some originating Tangible.
	 * If the originating Tangible has a CoordinateSystem then the curve is supposed to be created in parallel to that Coords
	 * If the originating Tangible has no CoordinateSystem then the curve will be parallel to the camera's plane and will also adop that plane to be its coordinatesystem
	 * Regarding movement for movement of the newly created curve it will inherit movement based on global, or on the CoordinateSystem
	 * 
	 * @param src
	 */
	private Curve3D createCurve(Tangible src)
	{
		//TODO: rewrite this!
		OMTVector cent = new OMTVector(src.getRelativePosition());
		OMTVector up = null;
		OMTVector left = null;
		OMTVector posa = null;
		OMTVector posb = null;
		OMTVector towardcam = null;
		float offset = 5f;	//the ammount of offset the new points from the originator
		
		//System.out.println("src " + src.getAbsolutePosition() + src.getRelativePosition());
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
			towardcam = new OMTVector(View.getInstance().getCameraNode().getCamera().getDirection().normalize().negate().mult(5f)); 
			left = new OMTVector(View.getInstance().getCameraNode().getCamera().getLeft()); //too keep units consistent multiply by -1 so positive is 'right' (a droite)
			up = new OMTVector(View.getInstance().getCameraNode().getCamera().getUp());
			
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
		
		OMTVector[] pts = {posb, cent, posa};
		Curve3D cap = new Curve3D("user-created curve", pts, system);	//FIXME: need to set demo coordinates on new curves
		cap.setColor(java.awt.Color.orange);
		cap.setVisible(true);
		cap.setModelBinormalWithUpVector(towardcam, 0.01f);	
		cap.addObserver(SceneObserver.getInstance());
		cap.changed();
		
		//redraw the scene, but not the whole scene, let observer know the curves have changed
		View.getInstance().getScene().changed(Scene.CHANGED_CURVE);
		
		return cap;
	}
	
	
	/**
	 * 
	 * 		mousePos.set( mouse.getLocalTranslation().x, mouse.getLocalTranslation().y );
            DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePos, 0, pickRay.origin );
            DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePos, 0.3f, pickRay.direction ).subtractLocal( pickRay.origin ).normalizeLocal()
	 */
	
	private void createCellOn(Tangible src)
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
			t = ocp.aproxTime();	//make the cell appear on the anchorpoints time
		}
		else if ( src != null)
		{
			ocurve = createCurve(src);
			//ocurve = null;
		}
		else
		{
			//exit early without updating the scene
			System.out.println("Cell not created, error with source");
			return;
		}
		
		//WHERE
		//Find out where to put it
		
		//CREATE
		nc = cellFactory(TYPE_CELL_DG_A, ocurve);	//create the cell

		nc.select();
	}
	
	
	
	public void createFreeCell(String type)
	{
		
		
		//FIND WHERE to put it
		Vector3f camPos = View.getInstance().getCameraNode().getCamera().getLocation();
		Vector3f camDir = View.getInstance().getCameraNode().getCamera().getDirection().normalize().mult(30f); //get 4 unit-direction 
		Vector3f dest = camPos.add(camDir);
		
		//camPos = OMTUtility.rotateVector(camPos, src.getCoordinateSystem().getRotationFromAbsolute());
		System.out.println("Pos: " + camPos + "  -   Dir: " + camDir);
		
			
		NeuronMorphology nc = cellFactory(type, null);	//create the cell
		//place the thing in front of the camera
		nc.setCoordinateSystem(null);
		nc.setRelativePosition(new PositionVector(dest));
		
	}
	

	/**
	 * Makes a generic cell based on type
	 * @see OMTUtility TYPE_s 
	 * @param type
	 * @return
	 */
	public NeuronMorphology cellFactory(String type, Curve3D parent)
	{
		NeuronMorphology nc = null;
		
		String f=type;	//filename
		String s=null;	//semantic class
		

			if (type.equals(TYPE_CELL_DG_A))	s = SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS;
			if (type.equals(TYPE_CELL_DG_B))	s = SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS; 
			if (type.equals(TYPE_CELL_DG_C))	s = SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA1_A))	s = SemanticClass.CA1_PYRAMIDAL_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA1_B))	s = SemanticClass.CA1_PYRAMIDAL_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA1_C))	s = SemanticClass.CA1_PYRAMIDAL_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA3_A))	s = SemanticClass.CA3_PYRAMIDAL_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA3_B))   s = SemanticClass.CA3_PYRAMIDAL_CELL_CLASS;
			if (type.equals(TYPE_CELL_PYR_CA3_C))	s = SemanticClass.CA3_PYRAMIDAL_CELL_CLASS;
		
		return cellFactory(s, f, parent, true);
	}
	
	/**
	 * For conveiniance, cells can be created without updating the scene. This may be useful for making many cells at once.
	 * If the parameters are erroneous, the factory will attempt to make a free-floating cell
	 * @param cellType Semantic string
	 * @param modelURL	String to the filename of the 3D-model file
	 * @param crvParent The parent curve to attach the cell to. If null, the cell will be free-floating.
	 * @param updateView True - forces Viewto update the scene. If creating many cells at once, it is nice to only redraw at the end 
	 * @return cell created
	 */
	public NeuronMorphology cellFactory(String cellType, String modelURL, Curve3D crvParent, boolean updateView)
	{
		NeuronMorphology ncell = null;
		
		if (null == modelURL)
		{
			System.out.println("Warning: created erroneous cell");
			crvParent = null; //continue to create cell with the assumption its a free floating one
		}
		
		float t = 0.5f;
		
		//create the cell two different ways, depending on whether it's a free-floating or attached cell
		if (null == crvParent)
		{	//free float
			ncell = new MorphMLNeuronMorphology(modelURL, null, t, NeuronMorphology.RENDER_AS_LOD, null);
		}
		else
		{	//attached
			ncell = new MorphMLNeuronMorphology(modelURL, crvParent, t, NeuronMorphology.RENDER_AS_LOD, crvParent.getCoordinateSystem());
		}
		
		ncell.setRelativeScale(0.01f);

		ncell.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(cellType));
		
		ncell.setVisible(true);
		ncell.addObserver(SceneObserver.getInstance()); //add an observer to the new cell
		
		
		if ( updateView) View.getInstance().getScene().changed(Scene.CHANGED_CELL); //
		
		return ncell;
	}
	
	
}


