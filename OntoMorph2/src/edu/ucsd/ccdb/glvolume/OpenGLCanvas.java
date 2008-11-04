//****************************************************************************
// Filename:            OpenGLCanvas.java
// Function:            Provides a native OpenGL Canvas to Java
// Author:              Juergen Schulze-Doebold (schulze@hlrs.de)
// Institution:         University of Stuttgart, HLRS (www.hlrs.de)
//****************************************************************************

package edu.ucsd.ccdb.glvolume;

import java.awt.*;
import java.awt.event.*;
import javax.accessibility.*;

public class OpenGLCanvas extends Canvas implements MouseListener, MouseMotionListener, ComponentListener
{
  public static String VIRVO_LIB = "multivolume"; //"virvo"
  static 
  { 
    try
    {
      System.out.println("Loading Virvo library");
      System.loadLibrary(VIRVO_LIB);   // this line only checks for the existence of the file
      	      	      	      	      	    // no checks on contained routines are done
    }
    catch (UnsatisfiedLinkError e)
    {
      System.out.println("Fatal Error: Error accessing Virvo library '" + VIRVO_LIB + "'.");
      System.exit(-1);
    }
    catch (SecurityException e)
    {
      System.out.println("Fatal Error: Cannot open Virvo library '" + VIRVO_LIB + "'.");
      System.exit(-1);
    }
  }
  
  // Native routines:
  public native void cGLCanvasResize(int width, int height);
  public native void cCleanupOpenGL();
  public native void cMouseDragged(int x, int y);
  public native void cMousePressed(int x, int y, int buttonState);
  public native void cMouseReleased(int x, int y, int buttonState);
  public native void paint(Graphics g);

  // Constructor
  public OpenGLCanvas()   
  {
    System.out.println("Java: OpenGLCanvas()");
    // Install listener routines:
    //addMouseListener(this);
    //addMouseMotionListener(this);
    addComponentListener(this);
  }

  public void removeNotify()
  {
      System.out.println("Java: removeNotify()");
      super.removeNotify();
      cCleanupOpenGL();
  }

  public void mouseEntered(MouseEvent e) 
  {
  	requestFocus();
	}

  public void mouseExited(MouseEvent e) 
  {
  }

  public void mouseClicked(MouseEvent e) 
  {
  }

  public void mousePressed(MouseEvent e)
  {
    cMousePressed(e.getX(), e.getY(), convertButtonState(e.getModifiers()));
  }

  public void mouseReleased(MouseEvent e)
  {
    cMouseReleased(e.getX(), e.getY(), convertButtonState(e.getModifiers()));
  }

  // Convert mouse button state from Java mode to native mode
  // Native Mode:  bit 0=1: Button 1 pressed, bit 1=1: Button 2 pressed, bit 2=1: Button 3 pressed
  private int convertButtonState(int javaState)
  {
    int buttonState = 0;   
    if ((javaState & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK || javaState==0) 
      buttonState |= 0x01;
    if ((javaState & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) buttonState |= 0x02;
    if ((javaState & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) buttonState |= 0x04;
    return buttonState;
  }

  public void mouseMoved(MouseEvent e) 
  {
  }

  public void mouseDragged(MouseEvent e)
  {
    cMouseDragged(e.getX(), e.getY());
  }

  public void componentHidden(ComponentEvent e)
  {
    System.out.println("Java: componentHidden()");
  }

  public void componentMoved(ComponentEvent e)
  {
    System.out.println("Java: componentMoved()");
    repaint();
  }

  public void componentResized(ComponentEvent e)
  {
    Dimension size;   // current canvas size
  
//    System.out.println("Java: componentResized()");
    size = getSize();
    cGLCanvasResize(size.width, size.height);
    repaint();
  }

  public void componentShown(ComponentEvent e)
  {
    System.out.println("Java: componentShown()");
    repaint();
  }
}
