//****************************************************************************
// Filename:          	SmartDialog.java
// Functionality:      	Helper class for intelligent dialog windows
// Author:            	Juergen Schulze-Doebold (schulze@hlrs.de)
// Institution:       	University of Stuttgart, Supercomputing Center (HLRS)
// Operating System:  	SGI IRIX 6.2
// Requirements:      	Java 1.1.6, Swing 1.1.1
// History:           	99-08-01  Creation date
//****************************************************************************

package edu.ucsd.ccdb.glvolume;

import java.awt.*;
import javax.swing.*;

/** This class provides additional functionality for frames.
*/
public class SmartFrame extends JFrame
{
  public SmartFrame() 
  {
    super();
  }
  public SmartFrame(GraphicsConfiguration gc) 
  {
    super(gc);
  }
  public SmartFrame(String title) 
  {
    super(title);
  }
  public SmartFrame(String title, GraphicsConfiguration gc) 
  {
    super(title, gc);
  }
  protected void childDialogClosed(JDialog dlg)
  {
  }
}
