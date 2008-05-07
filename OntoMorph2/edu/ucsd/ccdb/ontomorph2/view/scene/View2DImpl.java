package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;

import org.fenggui.ComboBox;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
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
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.manager.SceneObjectManager.MyNode;

import edu.ucsd.ccdb.ontomorph2.core.manager.SceneObjectManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;
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

	FengJMEInputHandler input;
	/**
	 * Holds singleton instance
	 */
	private static View2DImpl instance;
	private TextArea infoText = null;
	
	protected TextArea getInfoText() {
		if (infoText == null ) {
			infoText = new TextArea();
			infoText.setSize(300,100);
			infoText.setExpandable(false);
			infoText.setShrinkable(false);
			infoText.setPosition(new Point(0,20));
			//this.addWidget(infoText);
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
        objMenu.addItem(volumes);
        objMenu.addItem(semantics);
		
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
		window.setTitle("Cells...");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		
		Tree<MyNode> tree = this.<MyNode>createTree(sc);
		
		window.setSize(200, 300);
		StaticLayout.center(window, display);
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
