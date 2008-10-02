package edu.ucsd.ccdb.ontomorph2.util;

import java.awt.Color;

import com.jme.renderer.ColorRGBA;

/**
 * Utility class to convert java Color classes to jME ColorRGBA.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class ColorUtil {

	public static ColorRGBA convertColorToColorRGBA(Color c) {
		if (c == null) {return null;}
		
		int red = c.getRed();
		int blue = c.getBlue();
		int green = c.getGreen();
		int alpha = c.getAlpha();
		
		ColorRGBA out = new ColorRGBA();
		out.set((float)red/255, (float)blue/255, (float)green/255, (float)alpha/255);
		return out;
	}
}
