package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.ComboBox;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.layout.StaticLayout;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.Log;


/**
 * The root for the 2D "heads up display" menus, popups, and so forth for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class View2D extends Display  {
	
		
	FengJMEInputHandler input;
	/**
	 * Holds singleton instance
	 */
	private static View2D instance;
	private TextEditor infoText = null;
	
	private AtlasBrowser aBrowser = null;
	private BasicSearchWidget basicSearch = null;
	
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
 
		this.addWidget(new MenuBar());
        
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

	
	
	
	
	protected void loadCellChooser() {
		MyNode root = TangibleManager.getInstance().getCellTree();
		
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
				Log.warn("Feature Not Implemented Yet");
				//selectionChangedEvent..setVisible(false);
			}
			
		});
	}
	
	
//	get a tree pane display showing cells and their semantic contents
	void loadInstanceBrowser()
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

	public void loadBasicSearchBox() {
		if (basicSearch == null) {
			basicSearch = new BasicSearchWidget(this);
		}
	}
	

}
