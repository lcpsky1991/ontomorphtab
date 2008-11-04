//****************************************************************************
// Filename:          	SmartDialog.java
// Functionality:      	Helper class for intelligent dialog windows
// Author:            	Juergen P. Schulze (schulze@hlrs.de)
// Institution:       	University of Stuttgart, Supercomputing Center (HLRS)
//****************************************************************************

package edu.ucsd.ccdb.glvolume;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/** This class administers the saving of size and position of dialog windows.
    Dialog windows can extend this class to inherit all the new features.
*/
public class SmartDialog extends JDialog
{
  /** This class provides additional information for dialog windows, so that they can be tracked
      and be displayed at the same position and with the same size later on, after being closed.
      It is used by the SmartDialog class.
  */
  public class DialogInfoType
  {
    public int xpos, ypos;      // window position
    public int width, height;   // window size
    public boolean saveSize;                                 // true=save window size when disposing

    public DialogInfoType(int x, int y, int w, int h, boolean save) 
    {
      xpos = x;
      ypos = y;
      width = w;
      height = h;
      saveSize = save;
    }
  }

  private static Hashtable winInfoTable = new Hashtable();  ///< contains all dialog information of all previously opened windows
  private DialogInfoType winInfo;                           ///< dialog information of current window
  private String winID;                                     ///< current window ID string
  private SmartFrame parent;                                ///< this dialog window's parent
  private boolean isRestored = false;                       ///< true when window size was restored

  /** @param winID A unique string to identify this type of window
      @param saveSize if true size information is saved as well, otherwise 
                      only position information is saved when closed
  */
  public SmartDialog(String wID, SmartFrame p, boolean saveSize, int x, int y, int w, int h)
  {
    winID   = wID;
    parent = p;
    winInfo = (DialogInfoType)winInfoTable.get(winID);
    if (winInfo == null)   // if window did not exist previously
    {
      winInfo = new DialogInfoType(x, y, w, h, saveSize);
      winInfoTable.put(winID, winInfo);          // create new entry in hashtable
    }
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);  // DISPOSE_ON_CLOSE, DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE 
    addWindowListener(new WindowAdapter() 
    { public void windowClosing(WindowEvent we) 
      { closeDialog(); }});
  }

  /** This function override allows closing the dialog window with the ESC key.
  */
  protected JRootPane createRootPane() 
  {
    ActionListener actionListener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent actionEvent) 
      {
        closeDialog();
      }
    };
    JRootPane rootPane = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }

  /** This method sets the current window's position so that it is centered on 
      the parent window.
  */
  private void centerWindow()
  {
    Frame parent;
    Dimension dim;
    Point loc;
    Dimension screen;

    parent = (Frame)getParent();
    dim    = parent.getSize();
    loc    = parent.getLocationOnScreen();

    winInfo.xpos = loc.x + (dim.width - winInfo.width) / 2;
    winInfo.ypos = loc.y + (dim.height - winInfo.height) / 2;
    if (winInfo.xpos < 0) winInfo.xpos = 0;
    if (winInfo.ypos < 0) winInfo.ypos = 0;

    screen = getToolkit().getScreenSize();

    if (winInfo.width > screen.width)                        
      winInfo.width = screen.width;
    if (winInfo.height > screen.height)                      
      winInfo.height = screen.height;
    if (winInfo.xpos + winInfo.width > screen.width)    
      winInfo.xpos = screen.width - winInfo.width;
    if (winInfo.ypos + winInfo.height > screen.height)  
      winInfo.ypos = screen.height - winInfo.height;
  }

  private void memorizeSettings()
  {
    winInfo.xpos = getLocation().x;
    winInfo.ypos = getLocation().y;
    winInfo.width = getSize().width;
    winInfo.height = getSize().height;
    winInfoTable.put(winID, winInfo);
  }

  private void restoreSettings()
  {
    setLocation(winInfo.xpos, winInfo.ypos);
    if (winInfo.saveSize==true)
      setSize(winInfo.width, winInfo.height);
    isRestored = true;
  }

  /** Since it proved to be tough to implement a system independent text width 
      routine, several ways are attempted to do this here.
      @param longestText  represents the longest text line which has to fit in
                          the window
  */
  public void setOptimumWidth(String longestText)
  {
    final int INSET_X = 30;           // inset value for computation of width
    final int CHAR_WIDTH = 7;         // approximate average width of all characters
    FontMetrics fm;
    Font fnt;
    Graphics g;
    Toolkit tk;
    int textWidth;

    textWidth = INSET_X + CHAR_WIDTH * longestText.length();  // compute thumb value as default in case nothing else works to find out the text width

    fnt = getFont();

    if (fnt != null)
    {  
      fm = getFontMetrics(fnt);

      if (fm == null)
      {    
        g = getGraphics();
        if (g != null) fm = g.getFontMetrics(fnt);
      }

      if (fm!=null)            // if font metrics are finally found, use most sophisticated way to size window
         textWidth = INSET_X + fm.stringWidth(longestText);
    }

    // No matter how the text width was found out, resize the window now:
    setSize(Math.max(getSize().width, textWidth), getSize().height);
  }

  /** Display window 
  */
  public void setVisible(boolean visible)
  {
    if (visible==true && isRestored==false)
    {
      restoreSettings();
    }
    super.setVisible(visible);
  }

  protected void closeDialog()
  {
    parent.childDialogClosed(this);
    memorizeSettings();
    setVisible(false);
  }

  public void refresh()
  {
  }
}
