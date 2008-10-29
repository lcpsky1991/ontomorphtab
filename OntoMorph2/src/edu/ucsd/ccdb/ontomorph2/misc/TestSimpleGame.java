package edu.ucsd.ccdb.ontomorph2.misc;


import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
 
/**
 * TestSimpleGame
 * @author Joshua Slack
 * @version $Id: TestSimpleGame.java,v 1.7 2006/01/13 19:37:43 renanse Exp $
 */
public class TestSimpleGame extends SimpleGame {
 
  public static void main(String[] args) {
    TestSimpleGame app = new TestSimpleGame();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }
 
  protected void simpleInitGame() {
    display.setTitle("A Simple Test");
    Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
    box.setModelBound(new BoundingSphere());
    box.updateModelBound();
    rootNode.attachChild(box);
  }
}
