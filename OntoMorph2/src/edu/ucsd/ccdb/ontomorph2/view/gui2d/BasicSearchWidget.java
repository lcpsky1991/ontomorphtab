package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.RadioButton;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.ToggableGroup;
import org.fenggui.background.PlainBackground;
import org.fenggui.composites.TextArea;
import org.fenggui.composites.Window;
import org.fenggui.event.ActivationEvent;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IActivationListener;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.IKeyPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.ITextChangedListener;
import org.fenggui.event.KeyPressedEvent;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.event.TextChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.FormAttachment;
import org.fenggui.layout.FormData;
import org.fenggui.layout.GridLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;
import org.fenggui.FengGUI;

import EDU.oswego.cs.dl.util.concurrent.Callable;

import com.jme.util.GameTaskQueueManager;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.util.Log;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget {

	
	
	public BasicSearchWidget(Display d) {
		MyNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		Window window = new Window();
		d.addWidget(window);
		window.setSize(200, 300);
		window.setTitle("Search");
		window.setPosition(new Point(0,100));
		//window.getContentContainer().setLayoutManager( new RowLayout(false) );
		d.layout();
        window.getContentContainer().getAppearance().setPadding( new Spacing( 10, 10 ) );
            
        TextEditor ta = new TextEditor();
       ta.addActivationListener(new IActivationListener(){

		@Override
		public void widgetActivationChanged(ActivationEvent activationEvent) {
			// TODO Auto-generated method stub
			System.out.println("activated");
		}
    	   
       });
        
        /*ta.addTextChangedListener(new ITextChangedListener() {
            public void textChanged(TextChangedEvent textChangedEvent) {
                System.out.println("Text has changed");
                System.out.println(textChangedEvent.getText());  // null
                //System.out.println(ta.getText());  
                if(textChangedEvent.getText()!= null){
                }
            }
        });
        /*ta.addKeyPressedListener(new IKeyPressedListener() {

			@Override
			public void keyPressed(KeyPressedEvent arg0) {
				System.out.println("Key is pressed");
			}

        });*/	
        ta.setSize(100, 20);
        ta.setText(" Enter keyword");
        //ta.setMultiline(true);
        //window.getContentContainer().addWidget(ta);
        ta.setPosition(new Point(30,220));
        window.getContentContainer().addWidget(ta);
        System.out.println("writing state " + ta.isInWritingState());

        /* commented out to avoid compiler error
        ta.addKeyPressedListener(new IKeyPressedListener(){
			@Override
			public void keyPressed(KeyPressedEvent keyPressedEvent) {
				// TODO Auto-generated method stub				
			}
        
	    }  );
	    */   

        Log.warn(" X: " + ta.getX() + " Y: " + ta.getY() );
              
        Button button = new Button( "Start Search" );
        button.setSize(80, 30);
        button.setPosition(new Point(45, 180));
        button.addButtonPressedListener( new IButtonPressedListener() {
 
        public void buttonPressed( ButtonPressedEvent arg0 ) {
            		//search();
        			System.out.println("Button Pressed");
            }
        } );

        window.getContentContainer().addWidget( button );
        //window.pack();
        
	}


}
