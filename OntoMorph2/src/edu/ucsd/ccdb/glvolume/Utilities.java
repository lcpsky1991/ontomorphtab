//****************************************************************************
// Filename:          	Utilities.java
// Functionality:      	Java class independent utility methods
// Author:            	Juergen Schulze-Doebold (schulze@hlrs.de)
// Institution:       	University of Stuttgart, Supercomputing Center (HLRS)
// Operating System:  	SGI IRIX 6.2
// Requirements:      	Java 1.1.6, Swing 1.1.1
// History:           	99-10-12  Creation date
//****************************************************************************

package edu.ucsd.ccdb.glvolume;

import java.awt.*;

public class Utilities
{
  public static void setGBConstraints(GridBagLayout gbl, Component c, 
    int x, int y, double wx, double wy, int w, int h,
    int top, int left, int bottom, int right, int anc, int fill)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = x;
	  gbc.gridy = y;
	  gbc.weightx = wx;
	  gbc.weighty = wy;
    gbc.gridwidth = w;
    gbc.gridheight = h;
    gbc.insets = new Insets(top, left, bottom, right);
    gbc.anchor = anc;
    gbc.fill = fill;
    gbl.setConstraints(c, gbc);
  }
}