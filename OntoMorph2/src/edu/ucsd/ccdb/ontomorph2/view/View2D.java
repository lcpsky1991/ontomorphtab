package edu.ucsd.ccdb.ontomorph2.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.AtlasBrowser;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.BasicSearchWidget;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.InstanceBrowser;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MenuBar;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyTreeModel;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.TreeNode;


/**
 * The root for the 2D "heads up display" menus, popups, and so forth for the application.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class View2D extends Display{
	
	/**
	 * Holds singleton instance
	 */
	private static View2D instance;
	private TextEditor infoText = null;
	
	private AtlasBrowser aBrowser = null;
	private InstanceBrowser iBrowser = null;
	private BasicSearchWidget basicSearch = null;
	//private MiniMap miniMap = null;
	
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
	 * @override
	 */
	protected void cleanup()
	{
		MouseInput.destroyIfInitalized();
		KeyInput.destroyIfInitalized();
		//TODO: Clear Keyboard and Mouse Listeners if mouse is within a popup, textfield or FengGui app
	}

	/**
	 * @override 	 
	 * @param tpf
	 */
	public void render(float tpf) {
		//TODO: render FengGui after JME to ensure FengGui apps are on top of JME 
    }

	/**
	 * Create our GUI.  FengGUI init code goes in here
	 *
	 */
	protected void initGUI()
	{	
		this.addWidget(new MenuBar());        
        this.getInfoText();
		// Update the display with the newly added components
		this.layout();
	}
 
	/*protected void render(float interpolation)
	{
		// Clear previous
		this.getRenderer().clearBuffers();

		// Draw jME stuff
		this.getRenderer().draw(rootNode);

		// Set back to first texture unit so GUI displays properly
		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		// Draw GUI
		this.display();
	}*/

	
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
	
	
	
	public File showFileChooser()
	{
		//Create a file chooser
		final JFileChooser fc = new JFileChooser(OntoMorph2.getWBCProperties().getProperty("last.load.directory"));
		
//		In response to a button click:
		JFrame f = new JFrame();
		f.setSize(0,0);
		f.setLocation(100,100);
		f.setVisible(false);
		int returnVal = fc.showOpenDialog(f);
		
		File rfile = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{
			rfile = fc.getSelectedFile();
			OntoMorph2.getWBCProperties().setProperty("last.load.directory", rfile.getAbsolutePath());
			OntoMorph2.saveWBCProperties();
		} else {
			Log.warn("Open command cancelled by user.");
		}
		
		f.dispose();
		
		return rfile;
	}
	
	
	
	public void loadCellChooser() {
		TreeNode root = TangibleManager.getInstance().getCellTree();
		
		Window window = FengGUI.createWindow(this, true, false, false, true);
		window.getAppearance().removeAll();
		
		window.setTitle("Cells...");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		Tree<TreeNode> tree = MyTreeModel.<TreeNode>createTree(sc);
	
		window.setSize(200, 300);
		window.setPosition(new Point(0,100));
		window.layout();
		
		tree.setModel(new MyTreeModel(root));

		tree.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				if (!selectionChangedEvent.isSelected()) {
					TreeNode n = (TreeNode)selectionChangedEvent.getToggableWidget().getValue();
					n.value.unselect();
					return;
				}
				TreeNode n = (TreeNode)selectionChangedEvent.getToggableWidget().getValue();
				n.value.select();
		}
			
		});
	}
	
	public void loadAtlasBrowser() {
		aBrowser = new AtlasBrowser(this);
	}
	
//	get a tree pane display showing cells and their semantic contents
	public void loadInstanceBrowser()
	{
		iBrowser = new InstanceBrowser(this);
	}
	
	/*
	public void loadFileChooser() {
//		Create a file chooser
		final JFileChooser fc = new JFileChooser(OntoMorph2.getWBCProperties().getProperty("last.load.directory"));
		
		
//		In response to a button click:
		JFrame f = new JFrame();
		f.setSize(0,0);
		f.setLocation(100,100);
		f.setVisible(true);
		int returnVal = fc.showOpenDialog(f);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			OntoMorph2.getWBCProperties().setProperty("last.load.directory", file.getAbsolutePath());
			OntoMorph2.saveWBCProperties();
			
			
		} else {
			Log.warn("Open command cancelled by user.");
		}
		
		f.dispose();
	}
	*/
	
	


	public void loadBasicSearchBox() {
			basicSearch = new BasicSearchWidget(this);

	}
}
