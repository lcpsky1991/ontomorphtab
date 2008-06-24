package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.ComboBox;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.composites.Window;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuBar;
import org.fenggui.menu.MenuItem;
import org.fenggui.render.Pixmap;
import org.fenggui.tree.ITreeModel;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.SceneObjectManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.misc.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.view.View;


/**
 * Defines all 2D "heads up display" menus, popups, and so forth for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see IView2D
 *
 */
public class View2D extends Display implements IMenuItemPressedListener {
	
	public static final String LOAD_SCENE = "Load Scene...";
	public static final String SAVE_SCENE = "Save Scene...";
	public static final String CELLS = "Cells...";
	public static final String VOLUMES = "Volumes...";
	public static final String SEMANTICS = "Semantics...";
	public static final String LIST_INSTANCES = "List Instances...";
	public static final String SHOW_ATLAS = "Show Atlas...";
	public static final String DISPLAY_BASIC_ATLAS = "Display Basic Atlas";
	public static final String SLIDE_VIEW = "View Example Slide";
	public static final String ATLAS_SIDE_VIEW = "View Atlas Side";
	public static final String strMNU_MANIPULATE = "Manipulate Object";
	public static final String strMNU_MANI_ROTATEA = "Rotate (XY - Axis)";
	public static final String strMNU_MANI_ROTATEB = "Rotate (Z - Axis)";
	public static final String strMNU_MANI_MOVE = "Move";

	
	
	FengJMEInputHandler input;
	/**
	 * Holds singleton instance
	 */
	private static View2D instance;
	private TextEditor infoText = null;
	
	private AtlasBrowser aBrowser = null;
	
	protected TextEditor getInfoText() {
		if (infoText == null ) {
			/*
			Window window = FengGUI.createWindow(this, true, false, false, true);
			window.getAppearance().removeAll();
			
			window.setTitle("info");
			*/
			/*ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
			sc.getAppearance().removeAll();
			sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
			*/
			
			//infoText = FengGUI.createTextArea(window.getContentContainer());
			infoText = FengGUI.createTextArea(this);
			FengGUI.setUpAppearance(infoText);
			infoText.setSize(300,80);
			infoText.setExpandable(false);
			infoText.setShrinkable(false);
			infoText.setMultiline(true);
			infoText.setWordWarp(true);
			infoText.setSelectOnFocus(false);
			infoText.setPosition(new Point(0,20));
			infoText.getAppearance().removeAll();
			infoText.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
			infoText.getAppearance().setTextColor(Color.WHITE);
			infoText.getAppearance().add(new PlainBorder(Color.WHITE_HALF_OPAQUE));

			//sc.addWidget(infoText);
			//window.setSize(300, 100);
			//StaticLayout.center(window, this);
			//window.layout();
			
		}
		return infoText;
	}
	
	public void addInfoText(String s) {
		getInfoText().addTextLine(s);
	}
	

	public void setInfoText(String s) {
		getInfoText().setText(s);
	}

	
	/**
	 * Create our GUI.  FengGUI init code goes in here
	 *
	 */
	protected void initGUI()
	{
 
		input = new FengJMEInputHandler(this);
 
//		generate the menu
        MenuBar mB = new MenuBar();
        mB.setSize(View.getInstance().getDisplaySystem().getWidth(), 20);
        mB.setPosition(new Point(0,View.getInstance().getDisplaySystem().getHeight()-20));
        mB.setShrinkable(false);

        this.addWidget(mB);
        
//      =[  FILE  ]=
        Menu mnuFile = new Menu();
        mB.registerSubMenu(mnuFile, "File");
        MenuItem mnuFile_loadScene = new MenuItem(LOAD_SCENE);
        MenuItem mnuFile_saveScene = new MenuItem(SAVE_SCENE);
        mnuFile_loadScene.addMenuItemPressedListener(this);
        mnuFile_saveScene.addMenuItemPressedListener(this);
        //fileMenu.addItem(loadScene);
        mnuFile.addItem(mnuFile_saveScene);
	
        //=[  VIEW  ]=
        Menu mnuView = new Menu();
        mB.registerSubMenu(mnuView, "View");
        MenuItem mnuView_slide = new MenuItem(SLIDE_VIEW);
        MenuItem mnuView_atlasSide = new MenuItem(ATLAS_SIDE_VIEW);
        mnuView_slide.addMenuItemPressedListener(this);
        mnuView_atlasSide.addMenuItemPressedListener(this);
        mnuView.addItem(mnuView_slide);
        mnuView.addItem(mnuView_atlasSide);
        
//      =[  OBJ  ]=
        Menu mnuObjects = new Menu();
        mB.registerSubMenu(mnuObjects, "Objects");
        MenuItem mnuObjects_cells = new MenuItem(CELLS);
        MenuItem mnuObjects_volumes = new MenuItem(VOLUMES);
        MenuItem mnuObjects_semantics = new MenuItem(SEMANTICS);
        mnuObjects_cells.addMenuItemPressedListener(this);
        mnuObjects_volumes.addMenuItemPressedListener(this);
        mnuObjects_semantics.addMenuItemPressedListener(this);
        mnuObjects.addItem(mnuObjects_cells);
        //objMenu.addItem(volumes);
        //objMenu.addItem(semantics);
		
        
//      =[  CKB  ]=
        Menu mnuCKB = new Menu();
        mB.registerSubMenu(mnuCKB, "Cellular KB");
        MenuItem listInstances = new MenuItem(LIST_INSTANCES);
        listInstances.addMenuItemPressedListener(this);
        mnuCKB.addItem(listInstances);
        
        //=[  ATLAS  ]=
        Menu mnuAtlas = new Menu();
        mB.registerSubMenu(mnuAtlas, "Reference Atlas");
        MenuItem mnuAtlas_show = new MenuItem(SHOW_ATLAS);
        MenuItem mnuAtlas_displayBasic = new MenuItem(DISPLAY_BASIC_ATLAS);
        mnuAtlas_show.addMenuItemPressedListener(this);
        mnuAtlas_displayBasic.addMenuItemPressedListener(this);
        mnuAtlas.addItem(mnuAtlas_show);
        mnuAtlas.addItem(mnuAtlas_displayBasic);
        
        
        
        //=[ MANIPULATE  ]= 
        Menu mnuManip = new Menu();
        mB.registerSubMenu(mnuManip, strMNU_MANIPULATE);
        MenuItem mnuManip_move = new MenuItem(strMNU_MANI_MOVE);
        MenuItem mnuManip_rotate = new MenuItem(strMNU_MANI_ROTATEA);
        mnuManip_move.addMenuItemPressedListener(this);
        mnuManip_rotate.addMenuItemPressedListener(this);
        mnuManip.addItem(mnuManip_move);
        mnuManip.addItem(mnuManip_rotate);
        
        /*
		//	 Create a dialog and set it to some location on the screen
		Window frame = new Window();
		this.addWidget(frame);
		frame.setX(20);
		frame.setY(350);
		frame.setSize(200, 100);
		frame.setShrinkable(false);
		//frame.setExpandable(true);
		frame.setTitle("Pick a color");
		frame.getContentContainer().setLayoutManager(new StaticLayout());
 
		// Create a combobox with some random values in it
		//   we'll change these values to something more useful later on.
		ComboBox<String> list = new ComboBox<String>();
		frame.addWidget(list);
		list.setSize(150, list.getMinHeight());
		list.setShrinkable(false);
		list.setX(25);
		list.setY(25);
		list.addItem("White");
		list.addItem("Green");
		list.addItem("Blue");
		list.addItem("Red");
 
		list.addSelectionChangedListener(new CBListener());
 
		//try to add TextArea here but get OpenGLException
		TextEditor ta = new TextEditor(false);
		this.addWidget(ta);
		ta.setText("Hallo Text");
		ta.setX(40);
		ta.setY(50);
		//ta.setSize(100, ta.getAppearance().getFont().get)
		ta.setSizeToMinSize();
 
 		*/
        this.getInfoText();
		// Update the display with the newly added components
		this.layout();
	}
 

	/**
	 * prevents instantiation
	 */
	private View2D() {
		super(new org.fenggui.render.lwjgl.LWJGLBinding());
		this.initGUI();
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public View2D getInstance() {
		if (instance == null) {
			instance = new View2D();
		}
		return instance;
	}

	public void menuItemPressed(MenuItemPressedEvent arg0)
	{
		//FIXME: replace this string business with references to objects
		String act = arg0.getItem().getText(); //action to perform
		
		if ( LOAD_SCENE.equals(act) )
		{
			loadFileChooser();
		}
		else if ( SAVE_SCENE.equals(act) )
		{
			System.out.println("Feature Not Implemented Yet");
		}
		else if ( CELLS.equals(act) )
		{
			loadCellChooser();
		}
		else if ( VOLUMES.equals(act) )
		{

		}
		else if ( SEMANTICS.equals(act) )
		{

		}
		else if ( LIST_INSTANCES.equals(act) )
		{
			loadInstanceBrowser();
		}
		else if ( SHOW_ATLAS.equals(act) )
		{
			loadAtlasBrowser();
		}
		else if ( DISPLAY_BASIC_ATLAS.equals(act) )
		{
			ReferenceAtlas.getInstance().displayBasicAtlas();
		}
		else if ( SLIDE_VIEW.equals(act) )
		{
			View.getInstance().setCameraToSlideView();
		}
		else if ( ATLAS_SIDE_VIEW.equals(act) )
		{
			View.getInstance().setCameraToAtlasSideView();
		}
		else if ( strMNU_MANI_MOVE.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_MOVE);
		}
		else if ( strMNU_MANI_ROTATEA.equals(act) )
		{
			View.getInstance().setManipulation(View.METHOD_ROTATEA);
		}
	}
	
	
	
	protected void loadCellChooser() {
		MyNode root = SceneObjectManager.getInstance().getCellTree();
		
		Window window = FengGUI.createWindow(this, true, false, false, true);
		window.getAppearance().removeAll();
		
		window.setTitle("Cells...");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		
		Tree<MyNode> tree = MyTreeModel.<MyNode>createTree(sc);
		
		window.setSize(200, 300);
		window.setPosition(new Point(0,100));
		window.layout();
		tree.setModel(new MyTreeModel(root));

		tree.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				if (!selectionChangedEvent.isSelected()) {
					MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
					n.value.unselect();
					return;
				}
				MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
				n.value.select();
				
			}
			
		});
	}
	
	protected void loadAtlasBrowser() {
		if (aBrowser == null) {
			aBrowser = new AtlasBrowser(this);
		}
	}
	
	protected void loadFileChooser() {
//		 Create a dialog and set it to some location on the screen
		Window frame = new Window();
		this.addWidget(frame);
		frame.setX(20);
		frame.setY(350);
		frame.setSize(200, 100);
		frame.setShrinkable(false);
		//frame.setExpandable(true);
		frame.setTitle("Pick a file");
		frame.getContentContainer().setLayoutManager(new StaticLayout());
		
		ComboBox<String> list = new ComboBox<String>();
		frame.addWidget(list);
		list.setSize(150, list.getMinHeight());
		list.setShrinkable(false);
		list.setX(25);
		list.setY(25);
		list.addItem("File 1");
		list.addItem("File 2");
		list.addItem("File 3");
		list.addItem("File 4");
 
		list.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				if (!selectionChangedEvent.isSelected()) return;
				String value = selectionChangedEvent.getToggableWidget().getText();
				/*
				if ("White".equals(value)) light.setDiffuse(ColorRGBA.white);
				if ("Red".equals(value)) light.setDiffuse(ColorRGBA.red);
				if ("Blue".equals(value)) light.setDiffuse(ColorRGBA.blue);
				if ("Green".equals(value)) light.setDiffuse(ColorRGBA.green);
				*/
				System.out.println("Feature Not Implemented Yet");
				//selectionChangedEvent..setVisible(false);
			}
			
		});
	}
	
	
//	get a tree pane display showing cells and their semantic contents
	private void loadInstanceBrowser()
	{
		Display display = this;
		MyNode root = SemanticRepository.getInstance().getInstanceTree();
		
		Window window = FengGUI.createWindow(display, true, false, false, true);
		window.getAppearance().removeAll();
		
		window.setTitle("Instances..");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		
		Tree<MyNode> tree = MyTreeModel.<MyNode>createTree(sc);
		
		window.setSize(200, 300);
		//StaticLayout.center(window, display);
		window.setPosition(new Point(0,100));
		window.layout();
		tree.setModel(new MyTreeModel(root));

		tree.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				if (!selectionChangedEvent.isSelected()) {
					MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
					if (n.value != null) {
						n.value.unselect();
					}
					return;
				}
				MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
				if (n.value != null) {
					n.value.select();
				}
				
			}
			
		});
	}
	
	

}
