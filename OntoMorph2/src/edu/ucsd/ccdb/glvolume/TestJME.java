package edu.ucsd.ccdb.glvolume;

/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Frame;

import java.util.logging.Logger;
import javax.swing.JFrame;

import org.lwjgl.LWJGLException;


import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;

import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.awt.JMECanvas;
import com.jmex.awt.JMECanvasImplementor;
import com.jmex.awt.SimpleCanvasImpl;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLCanvas;


/**
 * <code>JMESwingTest</code> is a test demoing the JMEComponent and
 * HeadlessDelegate integration classes allowing jME generated graphics to be
 * displayed in a AWT/Swing interface.
 * 
 * Note the Repaint thread and how you grab a canvas and add an implementor to it.
 * 
 * @author Joshua Slack
 * @version $Id: JMESwingTest.java,v 1.18 2007/08/17 10:34:35 rherlitz Exp $
 */

public class TestJME {
    private static final Logger logger = Logger.getLogger(TestJME.class
            .getName());

    int width = 640, height = 480;

    // Swing frame
    private Frame frame;
    private static boolean started = false;
    
    static int count = 0;
	static Canvas comp = null;
	
	
    public TestJME() 
    {
        frame = new JFrame();
        // center the frame
        frame.setLocationRelativeTo(null);
        // show frame
        frame.setVisible(true);
    
        frame.setSize(width, height);
        
        //---------- init ---------------

        // make the canvas:
        
        
        //Initialize display
        comp = DisplaySystem.getDisplaySystem("lwjgl").createCanvas(width, height);

        
        //Important!  Here is where we add the guts to the panel:
        GImplementor impl = new GImplementor(width, height);
        JMECanvas jmeCanvas = ((JMECanvas) comp);
        jmeCanvas.setImplementor(impl);
        
        // -----------END OF GL STUFF-------------

        comp.setBounds(0, 0, width, height);
        frame.add(comp);
   
    }


    /**
     * Main Entry point...
     * 
     * @param args
     *            String[]
     * @throws LWJGLException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws LWJGLException
    {
        new TestJME();
        
        		while (true)
        		{
        			comp.repaint();
        		}
        //-------------------------------------
    }

    

    // **************** SWING FRAME ****************

    

    
    // IMPLEMENTING THE SCENE:
    
    class GImplementor extends SimpleCanvasImpl {

        private Quaternion rotQuat;
        private float angle = 0;
        private Vector3f axis;
        private Box box;
		long startTime = 0;
		long fps = 0;
		private int c = 0;
        private InputHandler input;

        JMultiResolutionVolume jmrv = new JMultiResolutionVolume();
        
        public GImplementor(int width, int height) 
        {
            super(width, height);
        }
        
        private void debug(JMultiResolutionVolume vol)
        {
          	if ( !started)
        	{
        		vol.initFor(comp);
            	started = true;
            	vol.load("/home/caprea/Documents/meshTester/meshData3/3Dmultichan5/config.txt");
            	vol.setCameraDistance(25);
            	vol.translate(0, -800, -800, 1000); 
            	vol.setBoundryVisiblity(0, true);
        	}
        	
          	if (started)
        	{
          		
          		//call display before translating it
          		vol.display(true);
          		//vol.rotate(0, 0.017, 0, -1, 0); //rotate 1 degree (0.017 rads)
          		vol.translate(0, 0, 0, -1);
        	}
          	
        		
        }
    
        //This is the basic rendering, notice the clearing and the swapping of the buffers
        //original code as follow for backup:
        //The scene is rendered to the back buffer and then brought to be active
        /*
         * renderer.clearBuffers();
	     * renderer.draw(rootNode);
	     * simpleRender();
	     * renderer.displayBackBuffer();
         */
        
    	public void doRender() 
    	{
    		
    		renderer.clearBuffers();
    		
    		renderer.draw(rootNode);
    		debug(jmrv);
	        simpleRender();
	        
	        renderer.displayBackBuffer();
    	}
    	
    	
        public void simpleSetup() 
        {
            // Normal Scene setup stuff...
            rotQuat = new Quaternion();
            axis = new Vector3f(1, 1, 0.5f);
            axis.normalizeLocal();

            Vector3f max = new Vector3f(5, 5, 5);
            Vector3f min = new Vector3f(-5, -5, -5);

            box = new Box("Box", min, max);
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            box.setLocalTranslation(new Vector3f(0, 0, -10));
            box.setRenderQueueMode(Renderer.QUEUE_SKIP);
            rootNode.attachChild(box);

            box.setRandomColors();

            TextureState ts = renderer.createTextureState();
            ts.setEnabled(true);
            //ts.setTexture(TextureManager.loadTexture(TestJME.class
                    //.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR, Texture.FM_LINEAR));
            		
            ts.setTexture(TextureManager.loadTexture("/home/caprea/workspace/wbc/etc/img/slice.jpg", Texture.MM_LINEAR, Texture.FM_LINEAR));

            rootNode.setRenderState(ts);
            startTime = System.currentTimeMillis() + 5000;
  
            
            
            //mouse handler
            /*input = new InputHandler();
            input.addAction(new InputAction() 
            {
                public void performAction( InputActionEvent evt ) 
                {
                    logger.info( evt.getTriggerName() );
                    
                }
            }, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, false );
            */
        }

        public synchronized void simpleUpdate()
        {

        	
            // Code for rotating the box... no surprises here.
            if (tpf < 1) {
                angle = angle + (tpf * 25);
                if (angle > 360) {
                    angle = 0;
                }
            }
            rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
            box.setLocalRotation(rotQuat);
            
			if (startTime > System.currentTimeMillis()) {
				fps++;
			} else {
				long timeUsed = 5000 + (startTime - System.currentTimeMillis());
				startTime = System.currentTimeMillis() + 5000;
				logger.info(fps + " frames in " + (timeUsed / 1000f) + " seconds = "
						+ (fps / (timeUsed / 1000f))+" FPS (average)");
				fps = 0;
			}				
        }
    }
}
