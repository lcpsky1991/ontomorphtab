package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;

import org.fenggui.ComboBox;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.Border;
import org.fenggui.border.PlainBorder;
import org.fenggui.composites.TextArea;
import org.fenggui.composites.Window;
import org.fenggui.event.IMenuItemPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.MenuItemPressedEvent;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.StaticLayout;
import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuBar;
import org.fenggui.menu.MenuItem;
import org.fenggui.render.Pixmap;
import org.fenggui.tree.ITreeModel;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.manager.MyNode;

import edu.ucsd.ccdb.ontomorph2.core.manager.SceneObjectManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;


/**
 * Represents a singleton.
 */

public class View2DImpl extends Display implements IView2D, IMenuItemPressedListener {
	
	public static final String LOAD_SCENE = "Load Scene...";
	public static final String SAVE_SCENE = "Save Scene...";
	public static final String CELLS = "Cells...";
	public static final String VOLUMES = "Volumes...";
	public static final String SEMANTICS = "Semantics...";
	public static final String LIST_INSTANCES = "List Instances...";

	FengJMEInputHandler input;
	/**
	 * Holds singleton instance
	 */
	private static View2DImpl instance;
	private TextEditor infoText = null;
	
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
        mB.setSize(ViewImpl.getInstance().getDisplaySystem().getWidth(), 20);
        mB.setPosition(new Point(0,ViewImpl.getInstance().getDisplaySystem().getHeight()-20));
        mB.setShrinkable(false);

        this.addWidget(mB);
        
        Menu fileMenu = new Menu();
        mB.registerSubMenu(fileMenu, "File");
        MenuItem loadScene = new MenuItem(LOAD_SCENE);
        MenuItem saveScene = new MenuItem(SAVE_SCENE);
        loadScene.addMenuItemPressedListener(this);
        saveScene.addMenuItemPressedListener(this);
        fileMenu.addItem(loadScene);
        fileMenu.addItem(saveScene);
	
        Menu objMenu = new Menu();
        mB.registerSubMenu(objMenu, "Objects");
        MenuItem cells = new MenuItem(CELLS);
        MenuItem volumes = new MenuItem(VOLUMES);
        MenuItem semantics = new MenuItem(SEMANTICS);
        cells.addMenuItemPressedListener(this);
        volumes.addMenuItemPressedListener(this);
        semantics.addMenuItemPressedListener(this);
        objMenu.addItem(cells);
        //objMenu.addItem(volumes);
        //objMenu.addItem(semantics);
		
        Menu ckbMenu = new Menu();
        mB.registerSubMenu(ckbMenu, "Cellular KB");
        MenuItem listInstances = new MenuItem(LIST_INSTANCES);
        listInstances.addMenuItemPressedListener(this);
        ckbMenu.addItem(listInstances);
        
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
	private View2DImpl() {
		super(new org.fenggui.render.lwjgl.LWJGLBinding());
		this.initGUI();
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public View2DImpl getInstance() {
		if (instance == null) {
			instance = new View2DImpl();
		}
		return instance;
	}

	public void menuItemPressed(MenuItemPressedEvent arg0) {
		if (LOAD_SCENE.equals(arg0.getItem().getText())) {
			loadFileChooser();
		} else if (SAVE_SCENE.equals(arg0.getItem().getText())) {
			System.out.println("Feature Not Implemented Yet");
		} else if (CELLS.equals(arg0.getItem().getText())) {
			loadCellChooser();
		} else if (VOLUMES.equals(arg0.getItem().getText())) {
			
		} else if (SEMANTICS.equals(arg0.getItem().getText())) {
			
		} else if (LIST_INSTANCES.equals(arg0.getItem().getText())) {
			loadInstanceBrowser();
		}
	}
	

	
	protected void loadCellChooser() {
		getCellTree(this);
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
	
	//get a tree pane display showing cells and their semantic contents
	private void getCellTree(Display display)
	{
		MyNode root = SceneObjectManager.getInstance().getCellTree();
		
		Window window = FengGUI.createWindow(display, true, false, false, true);
		window.getAppearance().removeAll();
		
		window.setTitle("Cells...");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		
		Tree<MyNode> tree = this.<MyNode>createTree(sc);
		
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
		
		Tree<MyNode> tree = this.<MyNode>createTree(sc);
		
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
	
	/**
	 * Create a Tree widget.
	 * @param <T> type parameter
	 * @param parent the parent container
	 * @return new tree widget.
	 */
	private <T> Tree<T> createTree(IContainer parent)
	{
		Tree<T> result = new Tree<T>();
		FengGUI.setUpAppearance(result);
		result.getAppearance().removeAll();
		result.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		result.getAppearance().setTextColor(Color.WHITE);
		result.getAppearance().add(new PlainBorder(Color.WHITE_HALF_OPAQUE));
		
		parent.addWidget(result);
		return result;
	}

	class MyTreeModel implements ITreeModel<MyNode>
	{
		MyNode root = null;
		
		public MyTreeModel(MyNode root) {
			this.root = root;
		}
		public int getNumberOfChildren(MyNode node)
		{
			return node.children.size();
		}

		public Pixmap getPixmap(MyNode node)
		{
			return null;
		}

		public String getText(MyNode node)
		{
			return node.text;
		}

		public MyNode getRoot()
		{
			return root;
		}

		public MyNode getNode(MyNode parent, int index)
		{
			return parent.children.get(index);
		}
	}

}
