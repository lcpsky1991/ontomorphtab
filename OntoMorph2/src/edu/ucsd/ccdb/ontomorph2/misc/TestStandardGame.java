package edu.ucsd.ccdb.ontomorph2.misc;

import com.jme.bounding.*;
import com.jme.math.*;
import com.jme.scene.shape.Box;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jmex.awt.lwjgl.LWJGLCanvas;
import com.jmex.editors.swing.settings.*;
import com.jmex.game.*;
import com.jmex.game.state.*;
 
/**
 * TestStandardGame is meant to be an example replacement of
 * jmetest.base.TestSimpleGame using the StandardGame implementation
 * instead of SimpleGame.
 * 
 * @author Matthew D. Hicks
 */
public class TestStandardGame {
        public static void main(String[] args) throws Exception {
                // Instantiate StandardGame
                StandardGame game = new StandardGame("A Simple Test");
                // Show settings screen
                GameSettingsPanel.prompt(game.getSettings());
                // Start StandardGame, it will block until it has initialized successfully, then return
                game.start();
                
                // Create a DebugGameState - has all the built-in features that SimpleGame provides
                // NOTE: for a distributable game implementation you'll want to use something like
                // BasicGameState instead and provide control features yourself.
                DebugGameState state = new DebugGameState();
                // Put our box in it
                Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
                box.setModelBound(new BoundingSphere());
                box.updateModelBound();
                // We had to add the following line because the render thread is already running
                // Anytime we add content we need to updateRenderState or we get funky effects
                box.updateRenderState();
                state.getRootNode().attachChild(box);
                state.getRootNode().updateRenderState();
                // Add it to the manager
                GameStateManager.getInstance().attachChild(state);
                // Activate the game state
                
                LWJGLCanvas glCanvas = null;
                LWJGLDisplaySystem glDisplaySys = (LWJGLDisplaySystem)DisplaySystem.getDisplaySystem();
                
                glCanvas = (LWJGLCanvas) glDisplaySys.getCurrentCanvas();
                
                
                System.out.println(glCanvas);
                state.setActive(true);
                
        }
}