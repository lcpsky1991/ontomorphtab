package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fenggui.FengGUI;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.Border;
import org.fenggui.border.PlainBorder;
import org.fenggui.border.TitledBorder;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;
import org.fenggui.util.Color; //conflict with other import
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.misc.FengJME;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveAnchorPointView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

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
	static final String msManipulate = "Manipulate ...";
	static ContextMenu instance = null;
	TitledBorder border = null;
	
	
	
	//========================================
	// MENU ITEMS 
	//========================================
	//want to have 'persistent' objects that can be added and removed from the context menu
	Menu mnuNew = null;
	Menu mnuSelect = null;
	Menu mnuNew_Cell = null;
	Menu mnuManipulate = null;
	
	ContextMenuItem mniNew_curve = null;
	ContextMenuItem mniNew_point = null;
	ContextMenuItem mniNew_Cell_CA3Pyr = null;
	ContextMenuItem mniNew_Cell_CA1Pyr = null;
	ContextMenuItem mniNew_Cell_DG = null;
	ContextMenuItem mniSelectTangible = null;
	ContextMenuItem mniAnnotate = null;
	ContextMenuItem mniSimulate = null;
	ContextMenuItem mniProperties = null;
	ContextMenuItem mniSeperator = null;
	
	
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
	
	private void create()
	{
		removeAllItems();
		
		mnuNew = new Menu();
		mnuNew_Cell = new Menu();
		mnuSelect = new Menu();
		mnuManipulate = new Menu();
		
		decorate();
		
		
		
        menuItemFactory(mniNew_curve, mnuNew, msN_CURVE);
        menuItemFactory(mniNew_point, mnuNew, msN_ANCHOR);
        menuItemFactory(mniNew_Cell_DG, mnuNew_Cell, msN_CELL_DG);
        menuItemFactory(mniNew_Cell_DG, mnuNew_Cell, msN_CELL_DG);
        menuItemFactory(mniAnnotate , this, msANNOTATE);
        menuItemFactory(mniProperties, this, msPROPERTIES);
        
        
        for (int i = 0; i < 5; i++)
        {
        	menuItemFactory(mniSelectTangible, mnuSelect, "Cell " + i);	
        }
        
		this.registerSubMenu(mnuNew, msNEW);
		this.registerSubMenu(mnuSelect,msSELECT);
		this.registerSubMenu(mnuManipulate, msManipulate);
		mnuNew.registerSubMenu(mnuNew_Cell, msN_CELL);
        

		
        
        this.setSizeToMinSize();
        this.layout();			
	}
	
	
	public void displayMenuFor(int xCoord, int yCoord, List<Tangible> t) 
	{
		create();
		int x = xCoord;
		int y = yCoord;
		this.setXY(x, y);
		
		//handles the case of a SINGLE selection
		if (t != null & t.size() == 1) {
			border.setTitle(t.iterator().next().getName());
		}	//handles the case of multiselection
		else if ( t != null & t.size() > 1)
		{
			String check="";
			HashMap types = new HashMap();
			
			//loop through and see if they are all the same types			
			for (int i =0; i < t.size(); i++) //dont exit quick because we need the info later anyways
			{
				check = t.get(i).getName();
				//if map contains the word incriment it
				if ( types.containsKey(check) )
				{
					int c = (Integer) types.get(check) + 1;
					types.put(check, c);
					
				} //otherwise create it and flag that its a multiset
			}
			
			//if they are the same then just call it that
			if ( types.size() > 1)
			{
				border.setTitle("Many [" + types.keySet().iterator().next() + "]");
			}
			//otherwise we may be smart about it and figure out what we've got
			else
			{
				String title = "Set";
				Set possible = types.keySet();
				
				while (possible.iterator().hasNext()  )
				{
					title += " [" + possible.iterator().next() + "]";
				}
				
				//but the title can't be too long
				if (title.length() > 20)
				{
					title = title.substring(0, 20) + "] ...";
				}
				
				border.setTitle(title);
			}
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
	 * @param title
	 * @param parent If null, will create a new parent menu
	 */
	private void menuItemFactory(MenuItem mitem, Menu mparent, String title)
	{
		try
		{
			if (title.equals(""))
			{
				title = "____________";
			}
			if (null == mparent )
			{
				return; //exit gracefully if parent is empty
			}
			else if ( mparent instanceof Menu)
			{
				mitem = new MenuItem(title);
				mitem.addMenuItemPressedListener(this);
				mparent.addItem(mitem);	
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
		Tangible orig = TangibleManager.getInstance().getSelectedRecent();
		
		if (orig instanceof Tangible)
		{
			if (msANNOTATE.equals(opt)) {
				System.out.println("do annotation");
			} else if (msANIMATE.equals(opt)) {
				System.out.println("do animation");
			} else if (msPROPERTIES.equals(opt)) {
				System.out.println("show properties");
			}
			else if (msN_CURVE.equals(opt)) testCreateCurve(orig);
			
			else if (trigger.equals(mniSelectTangible))
			{
				System.out.println("Select");
			}
			
			else if (msN_ANCHOR.equals(opt)) CreatePoint(orig);
			
			else if (msN_CELL.equals(opt))	testCreateCell(orig);
			
			
		}
		else if ( opt != null)
		{
			System.out.println("Warning: There was no tangible do to this action on (" + opt + ")");
		}
		
		this.closeBackward();
	}
	
	public void CreatePoint(Tangible src)
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
	
	//TODO: MOVE this function and replace its signature with something appropriate
	public void testCreateCurve(Tangible src)
	{
		OMTVector a = null;
		OMTVector b = null;
		OMTVector c = null;
		
		b = new OMTVector(src.getRelativePosition().add(3f,3f,0f));
		c = new OMTVector(src.getRelativePosition().add(10f,10f,0f));
		a = new OMTVector(src.getRelativePosition().add(-5f,-5f,0f));
		
		OMTVector[] pts = {a, b, c};
		Curve3D cap = new Curve3D("capreas new deal", pts, dcoords);
		cap.setColor(java.awt.Color.orange);
		cap.setVisible(true);
		cap.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.01f);		
		
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
		nc.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
		nc.setVisible(true);
		nc.select();
		
		View.getInstance().getScene().changed(Scene.CHANGED_PART);
		
	}
}


