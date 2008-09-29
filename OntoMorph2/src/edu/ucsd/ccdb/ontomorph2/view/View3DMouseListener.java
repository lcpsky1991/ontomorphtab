package edu.ucsd.ccdb.ontomorph2.view;

import java.math.BigInteger;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.fenggui.Display;
import org.fenggui.event.mouse.MouseButton;

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.ICable;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.BasicSearchWidget;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * Handles selection and manipulation of TangibleViews within the 3D world using the mouse.
 * 
 * @author caprea
 *
 */
public class View3DMouseListener implements MouseInputListener {
	
//	==================================
	// DECLARES
	// - used for manipulating the objects, setting the mode says what you're doing with dragging
	//==================================
	public static final int METHOD_NONE = 0;
	public static final int METHOD_PICK = 1;
	public static final int METHOD_MOVE = 2;
	public static final int METHOD_MOVETO = 3;
	public static final int METHOD_SCALE = 4;
	public static final int METHOD_ROTATEX = 8;
	public static final int METHOD_ROTATEY = 16;
	public static final int METHOD_ROTATEZ = 32;
	public static final int METHOD_LOOKAT = 64;
	public static final int METHOD_MOVE_FREE = 100;	//not constrained by the coordinate system
	
	
	Display disp;
	View v = new View();
	FengJMEInputHandler guiInput;
	BasicSearchWidget widget = new BasicSearchWidget();	
	
	private static int manipulation = METHOD_PICK; //set move to be default
	public static final int OMT_MBUTTON_LEFT = 0;
	public static final int OMT_MBUTTON_RIGHT = 1;
	public static final int OMT_MBUTTON_MIDDLE = 2;
	
	private boolean down;
	private int lastButton;
	long prevPressTime = 0;
	long dblClickDelay = 800;	//in milliseconds (1000 = 1 sec)
	
	PositionVector beginLoc = null;
	
	/**
	 * Created Constructors with two parameteres needed, called in View.java
	 * @param disp
	 * @param guiInput
	 */
	public View3DMouseListener(Display disp, FengJMEInputHandler guiInput){
		/*Two variables needed passed from View.java to get access to FengGUI listeners at FengJMEInputHandler*/
		this.disp = disp;
		this.guiInput = guiInput;
	}
	
	public void onButton(int button, boolean pressed, int x, int y)
	{	
		//if statement prohibiting mouse movements on scene to interfere with FengGUI windows
		if(!this.guiInput.wasMouseHandled()){
			down = pressed;
			lastButton = button;
			if(pressed) {
				onMousePress(button);
				//check double click
				if (System.currentTimeMillis() - this.prevPressTime < this.dblClickDelay) 
				{
					onMouseDouble(button);
				}
				prevPressTime = System.currentTimeMillis();
			} else {
				onMouseRelease(button);
			}
		}	
	}

	/**
	 * Higher method that determines onMouseMove versus onMouseDrag
	 * @see onMouseDrag
	 * @see onMouseMove
	 */
	public void onMove(int xDelta, int yDelta, int newX, int newY)
	{
		//if statement prohibiting mouse movements on scene to interfere with FengGUI windows
		if(!this.guiInput.wasMouseHandled()){
			// If the button is down, the mouse is being dragged
			if(down)
				onMouseDrag(lastButton, xDelta, yDelta, newX, newY);
			else
				onMouseMove();
		}	
	}	
		
	public void onWheel(int wheelDelta, int x, int y)
	{
//		====================================
    	//	WHEEL
    	//====================================
			//if statement prohibiting mouse movements on scene to interfere with FengGUI windows
			if(!this.guiInput.wasMouseHandled()){
				float dx = Math.abs(MouseInput.get().getWheelDelta());
				dx= (float) Math.log(1 + (3 * dx)); //scale it by some factor so it's less jumpy
			
			
				if ( wheelDelta < 0 ) dx = (-dx);	//exponents always produce positive results, allows for reverse zoom
			
				if (dx != 0)	
				{
					//zoom camera if Z press
					if ( KeyInput.get().isKeyDown(KeyInput.KEY_Z) )
					{
						View.getInstance().getCameraView().zoomIn(dx);	
					}
					//move camera if Z NOT pressed
					else
					{
						View.getInstance().getCameraView().moveForward(dx);
					}
				}
			}
	}

	/**
	 * Child method from handleMouseInput
	 */ 
	private void onMouseDrag(int button, int dX, int dY, int xPos, int yPos)
	{
		
		boolean mouseLook = false;
		//int numMouseBut = button;
		int numMouseBut = MouseInput.get().getButtonCount();
		
		//====================================
		//	MIDDLE MOUSE
		//====================================
		//mouse look trumps all other actions
    	
    	//middle click manipulates camera OR leftANDright mouse button
		//mouselook is true if there is a middle mouse button and it's pressed down
		if ( numMouseBut >= OMT_MBUTTON_MIDDLE ) if (MouseInput.get().isButtonDown(OMT_MBUTTON_MIDDLE)) mouseLook = true;
		 
		//mouselook is also true if left mouse and right mouse are pressed
		if ( MouseInput.get().isButtonDown(OMT_MBUTTON_RIGHT) && MouseInput.get().isButtonDown(OMT_MBUTTON_LEFT) ) mouseLook = true;
		
		if (mouseLook)
		{
			//find mouse change
			//View.getInstance().getCameraView().turnClockwise(dX / 100f);
			//View.getInstance().getCameraView().turnUp(dY / 100f);
			
			//rotate the camera around the selected tangibles
			Tangible recent = TangibleManager.getInstance().getSelectedRecent();
			{
				View.getInstance().getCameraView().rotateCameraAbout(recent, dX ,dY );
			}
			
		}
		else if (OMT_MBUTTON_LEFT == button) //left 
		{
			//in the case of no method, use panning instead of manipulating
			//this must be outside of manipulateCurrent because we apply it once, rather than over all selected
			if ( manipulation == METHOD_NONE)
			{
				//pan
				//View.getInstance().getCameraView().moveLeft(dX);
			}
			else
			{
				//dragging
				manipulateCurrentSelection();	
			}
		}
	}
	
	/**
	 * Child method from handleMouseInput
	 * still need to check which buttons are pressed
	 */ 
	private void onMousePress(int buttonIndex)
	{
		
		//	RIGHT CLICK
		if (1 == buttonIndex) //right
		{	
			doPick();
			//MouseInput.get().setCursorVisible(false); //hide mouse cursor
			ContextMenu.getInstance().displayMenuFor(MouseInput.get().getXAbsolute(),
					MouseInput.get().getYAbsolute(),TangibleManager.getInstance().getSelected());
		}
		
		
	}
	
	
	private void onMouseRelease(int buttonIndex)
	{		
		/** 
		 * This is for code to be executed when draggig is over
		 * for example, when yuo drag one object onto another
		 */
		if (OMT_MBUTTON_LEFT == buttonIndex) 
		{
			//after-manipulation code
			//find the originating tangible and the target that it is being dragge donto
			Tangible last = TangibleManager.getInstance().getSelectedRecent(); //could possibly be used for dragging ONTO in mouse release
			ArrayList<Tangible> targets = psuedoPick(KeyInput.get().isControlDown(), true); //dropped onto targets
			
			Tangible ontop = null;
			if (targets.size() > 0)	ontop = targets.get(0);
			
			//Execute code for post-dragging of anchorpionts
			if (last instanceof CurveAnchorPoint)
			{
				last.execPostManipulate(ontop);
			}
		}	
	}
	
	private void onMouseDouble(int buttonIndex)
	{
		if (OMT_MBUTTON_LEFT == buttonIndex) //left 
		{
			doPick();
		}
	}
	
	
	/**
	 * Exists only for possible future expansion
	 * @see onMove
	 */
	private void onMouseMove()
	{	
		/*
		 * ca: I can't think of anything that would be appropriate here, except a poem
		 * 
			Presently my soul grew stronger; hesitating then no longer,
 			`Sir,' said I, `or Madam, truly your forgiveness I implore;
 			But the fact is I was napping, and so gently you came rapping,
 			And so faintly you came tapping, tapping at my chamber door,
 			That I scarce was sure I heard you' - here I opened wide the door; -
 			Darkness there, and nothing more.
		 */
	}
	

	private void doPick() 
	{		
		//get the tangible picked
		ArrayList<Tangible> pickedlist = psuedoPick(KeyInput.get().isControlDown(), true);   
		
		boolean shift = KeyInput.get().isShiftDown();
		
		//enable multiselect if shift is down
		if ( shift ) TangibleManager.getInstance().setMultiSelect(true);
		
		//decide how to select it, is this multi select, deselect, etc?
		if (pickedlist.size() == 0 && !shift) //nothing was picked so do deselect
		{
			//if there are no results, unselect everything
			TangibleManager.getInstance().unselectAll();
		}
		else if (pickedlist.size() > 0)
		{			
			pickedlist.get(0).select();	//select the closest one
		}
		
		//turn off multiselection
		TangibleManager.getInstance().setMultiSelect(false);
	}
	

	/**
	 * Mouse picking (selection) workhorse
	 * Facilitates mouse picking, but does NOT actually select the object, that must be done elsewhere.
	 * Example: selecting the Tangible that is returned from this function
	 * @param modifyControl changes picking behavior (such as in the case of selecting subcomponents; false to select cables, true to select cells)
	 * @param useRanking If true, omits lower-ranking {@link TangibleView}s from the pick results, such that a cell behind a curve would be selected instead of curve. False for unintelligent pickResults 
	 * @return an ArrayList {@link Tangible}, of all camera-ray-intersected Tangibles where element (0) is the closest to the camera 
	 */
	public ArrayList<Tangible> psuedoPick(boolean modifyControl, boolean useRanking)
	{
		Tangible chosenOne= null;
		PickResults rawresults = getPickResults();
		PickData singleResult = null;
		ArrayList<Tangible> possible = new ArrayList<Tangible>();
		
		//omit lower-ranked items if using ranking
		if ( rawresults.getNumber() > 0 && useRanking) rawresults = reorderPickPriority(rawresults);
		
//		Find out the tangible for the geometry that was decided on
		
		//if ( rawresults.getNumber() > 0)
		for ( int r=0; r < rawresults.getNumber(); r++)
		{
			TangibleView tv = null;
			singleResult = rawresults.getPickData(r);	//find the geomtry
			tv = TangibleViewManager.getInstance().getTangibleView(singleResult.getTargetMesh().getParentGeom()); //get a tanview instance that is mapped to the selected geomtry
			
			//special case for NeuronMorphologies because they have subcomponents
			//TODO: should probably bring this piece of code inside NeuronMorphologyView via 
			//some kind of action handler because this is kind of a hack
			if (tv instanceof NeuronMorphologyView && modifyControl) //if control down proceed to the default case selection, otherwise return the part
			{
				NeuronMorphologyView nmv = (NeuronMorphologyView) tv;
				{
					//otherwise just select the part itself
					BigInteger id = nmv.getCableIdFromGeometry(singleResult.getTargetMesh().getParentGeom());
					ICable c = ((NeuronMorphology)nmv.getModel()).getCable(id);
					chosenOne = (Tangible) c;
				}
			}
			else if ( tv != null)
			{//CATCH ALL case for all other TangibleViews
				chosenOne = tv.getModel();
			}
			
			possible.add(chosenOne);	//add to the list of possible picks
		}
	
		possible = removeDuplicates(possible);
	
		return possible;
	}

	
	private ArrayList<Tangible> removeDuplicates(ArrayList<Tangible> list)
	{
		ArrayList<Tangible> edited = new ArrayList<Tangible>();
		Tangible single = null;
		
		//loop through and copy over the elements in list only if they do not already exist
		for (int i = 0; i < list.size(); i++)
		{
			single = list.get(i);
		
			//if the new list already contains the object don't add it
			if ( !edited.contains(single) ) edited.add(single);
		}
		
		return edited;
	}
	
	
	private PickResults reorderPickPriority(PickResults results)
	{
		PickResults reorder = new TrianglePickResults();
		
		//setup
		int cnt = results.getNumber();
		int highP = TangibleView.P_UNKNOWN;
		reorder.clear();
		reorder.setCheckDistance(true);
		
		//===== loop through all the item in results
		//===== find the highest priority item
		for (int i=0; i < cnt; i++)
		{
			GeomBatch obj = results.getPickData(i).getTargetMesh();
			TangibleView tv = TangibleViewManager.getInstance().getTangibleView(obj.getParentGeom());
			if ( tv != null)
			{
				int p = tv.pickPriority;
				if ( p > highP ) highP = p;	
			}
		}
		
		//===== get all instances of THAT item
			//copy all of those that belong to highP to reorder
		for (int i =0; i < cnt; i++)
		{
			GeomBatch obj = results.getPickData(i).getTargetMesh();
			TangibleView tv = TangibleViewManager.getInstance().getTangibleView(obj.getParentGeom());
			if ( tv != null)
			{
				int p = tv.pickPriority;
				if ( highP == p )
				{
					reorder.addPickData(results.getPickData(i));
				}	
			}
		}
		
		return reorder;
	} 
	
	/**
	 * Give the PickResults object for the object the mouse is trying to select on the screen
	 * Called during mouse handling
	 */
	private PickResults getPickResults() {
//		because dendrites can be densely packed need precision of triangles instead of bounding boxes
		PickResults pr = new TrianglePickResults(); 
		
		//Get the position that the mouse is pointing to
		Vector2f mPos = new Vector2f();
		mPos.set(MouseInput.get().getXAbsolute() ,MouseInput.get().getYAbsolute() );
		
		Vector3f closePoint = new Vector3f();
		Vector3f farPoint = new Vector3f();
		Vector3f dir = new Vector3f();
		
		// Get the world location of that X,Y value
		farPoint = View.getInstance().getDisplaySystem().getWorldCoordinates(mPos, 1.0f);
		closePoint = View.getInstance().getDisplaySystem().getWorldCoordinates(mPos, 0.0f);
		dir = farPoint.subtract(closePoint).normalize();
		
		// Create a ray starting from the camera, and going in the direction
		// of the mouse's location
		//Ray mouseRay = new Ray(closePoint, farPoint.subtractLocal(closePoint).normalizeLocal());
		Ray mouseRay = new Ray(closePoint, dir);
		
		View.getInstance().createDebugRay(closePoint, farPoint); //draws a picking ray and possibly a picking cone
		
		// Does the mouse's ray intersect the box's world bounds?
		pr.clear();
		pr.setCheckDistance(true);  //this function is undocumented, orders the items in pickresults
		View.getInstance().getView3D().findPick(mouseRay, pr);
		
		return pr;
	}
	
	
	private void complicatedMultiMove(Tangible master, float dx, float dy, int mx, int my)
	{
		/*
		 * How multi-movement works:
		 * First, while looping over ALL selected tangibles, ONLY move the most recently selected
		 * Second, after the most recently selected tangible is moved, find the displacement that the movement caused
		 * Third, apply that displacement to all OTHER selected tangibles
		 * 
		 * Finally, Tangibles like NeuronMorpologies do not move the same way, so they can be treated 'normally'
		 * where normal mans just apply the same to all
		 */
		
			PositionVector dis = master.move(dx, dy,mx, my);	//find the displacement caused by movement
			
			if ( dis != null) //displacement will be null for neurons on curves
			{
				for (Tangible mirror : TangibleManager.getInstance().getSelected())
				{
					if ( mirror != master) //aply the displacement to all OTHER tangibles (that are not the most recent)
					{
						PositionVector goes = new PositionVector(mirror.getRelativePosition().add(dis.asVector3f()));
						mirror.setRelativePosition(goes);
					}
				}
			}
			else
			{	//displacement was null, so move all the others as normal
				for (Tangible mirror : TangibleManager.getInstance().getSelected())
				{
					if ( mirror != master) //aply the displacement to all OTHER tangibles (that are not the most recent)
					{
						mirror.move(dx, dy, mx, my);
					}
				}
			}	
	}
	
	/**
	 * Apply manipulations to the tangible that is currently selected
	 * Called during mouse handling
	 */
	private void manipulateCurrentSelection() 
	{		
		int mx = MouseInput.get().getXAbsolute();
		int my = MouseInput.get().getYAbsolute();
		
		float dx = MouseInput.get().getXDelta();
		float dy = MouseInput.get().getYDelta();
		Tangible recent = TangibleManager.getInstance().getSelectedRecent();
		
		//do the manipulation to all selected objects
		//loop over the objects in reverse as to keep order of selected objects relevant
//		for (int t=TangibleManager.getInstance().getSelected().size() - 1; t >= 0 ; t--)
		for (Tangible manip : TangibleManager.getInstance().getSelected())
		{
		
			//widget.absolutePosition(TangibleManager.getInstance().getSelectedRecent().getAbsolutePosition());
			System.out.println("selection "  + recent + " manip:" + manip);
			System.out.println("position" + TangibleManager.getInstance().getSelectedRecent().getAbsolutePosition());

			widget.absolutePosition(TangibleManager.getInstance().getSelectedRecent().getAbsolutePosition());
			//check to see where the camera is positioned and compare it to the Tangible's plane
			//if it is under, reverse the X direction so movement is intuitive
			boolean reverse = !OMTUtility.isLookingFromAbove(new OMTVector(View.getInstance().getCameraView().getCamera().getDirection()), manip.getWorldNormal()); 
			if (reverse) dx = -dx;	//switch X movement if it is on the opposite side of the plane

			switch ( manipulation )
			{
			case METHOD_NONE:
				//do nothing
				break;
			case METHOD_PICK:
				//do nothing
				break;
			case METHOD_MOVE:
				
				if ( recent == manip ) //only move the most recent, do not apply movement to all selected
				{
					complicatedMultiMove(recent, dx, dy, mx, my);
				}
				break;
			case METHOD_ROTATEX:
				manip.rotate(dx, dy, new OMTVector(1,0,0));
				break;
			case METHOD_ROTATEY:
				manip.rotate(dx, dy, new OMTVector(0,1,0));
				break;
			case METHOD_ROTATEZ:
				manip.rotate(dx, dy, new OMTVector(0,0,1));
				break;
			case METHOD_LOOKAT:
				//FIXME: /* needs to be re-engineered to deal with multiple selections */
				Log.warn("LOOK AT is broken");
				try
				{
					View.getInstance().getCameraView().lookAt(TangibleManager.getInstance().getSelectedRecent().getAbsolutePosition() , new OMTVector(0,1,0)); //make the camera point a thte object in question	
				}
				catch(Exception e){
					Log.warn(e.getMessage());
				};
				break;
			case METHOD_SCALE:
				manip.scale(dx, dy, new OMTVector(1,1,1));
				break;
			}
		}
	}
	
		
	/**
	 * Sets the manipulation method that dragging the mouse should have on a selected object.
	 * 
	 * Use static fields defined in this class
	 * @param m - one of the constant ints defined in this class
	 * @see View3DMouseListener#METHOD_LOOKAT
	 * @see View3DMouseListener#METHOD_MOVE
	 * @see View3DMouseListener#METHOD_NONE
	 * @see View3DMouseListener#METHOD_PICK
	 * @see View3DMouseListener#METHOD_ROTATEX
	 * @see View3DMouseListener#METHOD_ROTATEY
	 * @see View3DMouseListener#METHOD_ROTATEZ
	 * @see View3DMouseListener#METHOD_SCALE
	 */
	public void setManipulation(int m)
	{
		manipulation = m;
		Log.warn("Manipulation method set to: " + m);
	}
}
