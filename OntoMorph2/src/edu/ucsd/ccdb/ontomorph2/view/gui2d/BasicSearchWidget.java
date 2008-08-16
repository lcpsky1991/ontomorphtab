package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.composites.TextArea;
import org.fenggui.composites.Window;
import org.fenggui.event.ActivationEvent;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IActivationListener;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IKeyPressedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.KeyPressedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.FormLayout;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import com.jme.input.Input;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.MouseInput;
import com.jme.input.action.KeyInputAction;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.View3DMouseHandler;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget extends InputHandler{
    
	FengJMEInputHandler guiInput = null;
	//MouseManager mouse;
	KeyBindingManager keyboard; 
	
	private float displayX, displayY;
	private KeyInputAction keyAction;
	public BasicSearchWidget(Display d) {
		
		displayX = d.getDisplayX();
		displayY = d.getDisplayY();
		guiInput = new FengJMEInputHandler(d);
		keyboard = KeyBindingManager.getKeyBindingManager();
		View3DMouseHandler view3DMouseHandler = new View3DMouseHandler();
        guiInput.addAction(view3DMouseHandler , InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_ALL, true );
		MyNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		Window window = new Window(true, false, false, true);
		//d.addWidget(window);
		window.setSize(200, 300);
        window.getContentContainer().setLayoutManager(new BorderLayout());
		window.setTitle("Search");       
        TextEditor textArea = FengGUI.createTextField(window.getContentContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);
        //System.out.println("writing state " + textArea.isInWritingState());
        
        Button button = new Button( "Start Search" );
        button.setSize(80, 30);
        button.setPosition(new Point(45, 180));
        button.addButtonPressedListener( new IButtonPressedListener() {

        	
        public void buttonPressed( ButtonPressedEvent arg0 ) {
            		//search();
        			System.out.println("Button Pressed");
            }
        } );

        button.addMouseEnteredListener(FocusManager.focusManager);
        button.addMouseExitedListener(FocusManager.focusManager);
        window.getContentContainer().addWidget( button );
		window.setPosition(new Point(0,100));
        textArea.setPosition(new Point(30,220));
        d.layout();
        MouseInput.get().setCursorVisible(true);
        /*if(d.fireMousePressed() == true){
        	System.out.println("it comes from the gui");
        }*/
        d.addWidget(window);
        
       //d.mouseMoved(, );
	}

	public void textChanged(TextChangedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


