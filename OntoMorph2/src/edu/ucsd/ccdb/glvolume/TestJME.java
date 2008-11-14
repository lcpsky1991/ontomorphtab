package edu.ucsd.ccdb.glvolume;

import com.jme.bounding.*;
import com.jme.math.*;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
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
public class TestJME {
    public static void main(String[] args) throws Exception
    {
        StandardGame game = new StandardGame("A Simple Test");
        GameSettingsPanel.prompt(game.getSettings());
        game.start();

        DebugGameState state = new DebugGameState();
        Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        box.updateRenderState();
        state.getRootNode().attachChild(box);
        GameStateManager.getInstance().attachChild(state);
        state.setActive(true);

    
        
        
        
        DebugGameState state1 = new DebugGameState();
        Cylinder cyl = new Cylinder("my cyl", 5, 5, 5.0f, 1.0f);
        cyl.setModelBound(new BoundingSphere());
        cyl.updateModelBound();
        cyl.updateRenderState();
        state1.getRootNode().attachChild(cyl);
        GameStateManager.getInstance().attachChild(state1);
        state1.setActive(true);
        
        while(true) {
            Thread.sleep(200);
            state.setActive(false);
            Thread.sleep(200);
            state.setActive(true);
        }
    }
}
