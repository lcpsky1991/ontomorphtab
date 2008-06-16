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
		if (Color.RED.equals(c)) {
			return ColorRGBA.red;
		} else if (Color.YELLOW.equals(c)) {
			return ColorRGBA.yellow;
		} else if (Color.BLUE.equals(c)) {
			return ColorRGBA.blue;
		} else if (Color.MAGENTA.equals(c)) {
			return ColorRGBA.magenta;
		} else if (Color.GREEN.equals(c)) {
			return ColorRGBA.green;
		} else if (Color.DARK_GRAY.equals(c)) {
			return ColorRGBA.gray; //gray shows up better on black background for now
		} else if (Color.WHITE.equals(c)) {
			return ColorRGBA.white;
		} else if (Color.GRAY.equals(c)) {
			return ColorRGBA.gray;
		}
		return ColorRGBA.pink;
	}
}
