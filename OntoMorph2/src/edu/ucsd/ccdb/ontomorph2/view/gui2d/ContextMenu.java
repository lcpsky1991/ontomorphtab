package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.HashMap;
import java.util.Iterator;
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
	
	static final String NEW = "New...";
	static final String N_CURVE = "Curve";
	static final String ANNOTATE = "Annotate";
	static final String ANIMATE = "Animate";
	static final String PROPERTIES = "Display Properties";
	static final String N_ANCHOR = "Anchor Point";
	static final String N_CELL = "Cell";
	static ContextMenu instance = null;
	TitledBorder border = null;
	
	
	private static final DemoCoordinateSystem dcoords =  new DemoCoordinateSystem();	//coordinates for test-case new objects
	
	public static ContextMenu getInstance() {
		if (instance == null) {
			instance = new ContextMenu();
		}
		return instance;
	}
	
	private ContextMenu() {
		
        Menu mnuContext = this;
    
        
        this.setShrinkable(false);
        this.setSize(200,200);
        this.getAppearance().removeAll();
		this.getAppearance().add(new PlainBackground(new Color(0.5f, 0.5f, 0.5f, 5.0f)));
		border = new TitledBorder("context menu");
		border.setTextColor(Color.WHITE);
		this.getAppearance().add(border);
		this.getAppearance().setTextColor(Color.WHITE);
		this.getAppearance().setTextSelectionColor(Color.YELLOW);
        
		  
        Menu newMenu = new Menu();
        mnuContext.registerSubMenu(newMenu, NEW);
		makeMenuItem(N_CURVE, newMenu);
		makeMenuItem(N_ANCHOR, newMenu);
		makeMenuItem(N_CELL, newMenu);
        makeMenuItem(ANNOTATE, mnuContext);
        makeMenuItem(ANIMATE, mnuContext);
        makeMenuItem(PROPERTIES, mnuContext);
      
        this.setSizeToMinSize();
        this.layout();
	}
	
	public void displayMenuFor(int xCoord, int yCoord, List<Tangible> t) 
	{
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
	
		Tangible orig = TangibleManager.getInstance().getSelectedRecent();
		
		if (ANNOTATE.equals(opt)) {
			System.out.println("do annotation");
		} else if (ANIMATE.equals(opt)) {
			System.out.println("do animation");
		} else if (PROPERTIES.equals(opt)) {
			System.out.println("show properties");
		} else if (N_CURVE.equals(opt)) 
		{
			//make a new bezier curve right here
			if (orig instanceof Tangible)
			{
				testCreateCurve(orig);
			}
		}
		else if (N_ANCHOR.equals(opt))
		{
			if (orig instanceof CurveAnchorPoint)
			{
				testCreatePoint(orig);
			}
		}
		else if (N_CELL.equals(opt))
		{
			testCreateCell(orig);
		}
		View2D.getInstance().removePopup();
	}
	
	public void testCreatePoint(Tangible src)
	{
		CurveAnchorPoint cp = (CurveAnchorPoint) src;
		int time = cp.getIndex() + 1;
		
		OMTVector place = new OMTVector(src.getRelativePosition().add(1f,1f,0f));
		
		cp.getParentCurve().addControlPoint(time, place);
		
	}
	
	//TODO: MOVE this function and replace its signature with something appropriate
	public void testCreateCurve(Tangible src)
	{
		OMTVector a = new OMTVector(16,13,20);
		OMTVector b = new OMTVector(-5,35,20);
		OMTVector c = new OMTVector(0,0,20);
		
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
		/*NeuronMorphology cell11 = new MorphMLNeuronMorphology(cell11URL, curve1, ((float)i)/20f-0.01f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		cell11.setRelativeScale(0.01f);
		cell11.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
		addSceneObject(cell11);
		*/
		NeuronMorphology nc = null;
		Curve3D ocurve = null;
		float t = 0.5f;	//default is middle
		if ( src instanceof Curve3D)
		{
			//if going fromt he curve take the previous two cells and get their difference in time
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
		
		List<NeuronMorphology> cells = ocurve.getChildrenCells();
		
		float high=0;
		//get information about the distribution of the cells along the curve
		for (int c=0; c < cells.size(); c++)
		{
			float num=cells.get(c).getTime();
			if (num > high) high = num;
		}
		
		
		t = (high / cells.size()) + high;
		
		TangibleManager.getInstance().unselectAll();
		//do the rest of the actions
		nc = new MorphMLNeuronMorphology(View.getInstance().getScene().cell11URL, ocurve, t, NeuronMorphology.RENDER_AS_LOD, dcoords);
		nc.setRelativeScale(0.01f);
		nc.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
		nc.setVisible(true);
		nc.select();
		View.getInstance().getScene().changed(Scene.CHANGED_PART);
		
	}
}


