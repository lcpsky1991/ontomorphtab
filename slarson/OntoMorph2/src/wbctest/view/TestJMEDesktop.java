package wbctest.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme.app.SimpleGame;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.state.LightState;
import com.jmex.awt.swingui.JMEAction;
import com.jmex.awt.swingui.JMEDesktop;


public class TestJMEDesktop extends SimpleGame{
	
	
	/**
	 * Entry point for the test,
	 * @param args
	 */
	public static void main(String[] args) {
		TestJMEDesktop app = new TestJMEDesktop();
		app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
		app.start();
	}
	
	/**
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("Test JMEDesktop");
		
		test1();
	}
	
	protected void test1() {
		final Node guiNode = new Node("guiNode");
		guiNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		final JMEDesktop desktop = new JMEDesktop("desktop", display.getWidth(), display.getHeight(), input);
		guiNode.attachChild(desktop);
			
		// center desktop on screen
		desktop.getLocalTranslation().set(display.getWidth()/2, display.getHeight()/2, 0);
			
		// perform all the swing stuff in the swing thread
	      	// (Only access the Swing UI from the Swing event dispatch thread!
		// See SwingUtilities.invokeLater()		
		// and http://java.sun.com/docs/books/tutorial/uiswing/concurrency/index.html for details.)
	        SwingUtilities.invokeLater( new Runnable() {
	            public void run() {
	                // make it transparent blue
	                desktop.getJDesktop().setBackground( new Color( 0, 0, 1, 0.0f ) );

	                // create a swing button
	                final JButton button = new JButton( "Click Me" );
	                // and put it directly on the desktop
	                desktop.getJDesktop().add( button );
	                // desktop has no layout - we layout ourselves (could assign a layout to desktop here instead)
	                button.setLocation( 200, 200 );
	                button.setSize( button.getPreferredSize() );
	                // add some actions
	                // standard swing action:
	                button.addActionListener( new ActionListener() {
	                    public void actionPerformed( ActionEvent e ) {
	                        // this gets executed in swing thread
	                        // alter swing components ony in swing thread!
	                        button.setLocation( FastMath.rand.nextInt( display.getWidth() ), FastMath.rand.nextInt( display.getHeight()) );                       
	                    }
	                } );
	                // action that gets executed in the update thread:
	                button.addActionListener( new JMEAction( "my action", input ) {
	                    public void performAction( InputActionEvent evt ) {
	                        // this gets executed in jme thread
	                        // do 3d system calls in jme thread only!
	                        guiNode.updateRenderState(); // this call has no effect but should be done in jme thread :)
	                    }
	                });
	            }
	        } );

	        // don't cull the gui away
	        guiNode.setCullMode( SceneElement.CULL_NEVER );
	        // gui needs no lighting
	        guiNode.setLightCombineMode( LightState.OFF );
	        // update the render states (especially the texture state of the deskop!)
	        guiNode.updateRenderState();
	        // update the world vectors (needed as we have altered local translation of the desktop and it's
	        //  not called in the update loop)
	        guiNode.updateGeometricState( 0, true );      
		
		rootNode.attachChild(guiNode);
	}
	
	
}

	
