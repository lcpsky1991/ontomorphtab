package edu.ucsd.ccdb.ontomorph2.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supports logging for the system.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Log extends Logger {

	static Log log = null;
	
	protected Log(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}
	
	public static Log getInstance() {
		if (log == null) {
			log = new Log("WBC Logger", null);
		}
		return log;
	}
	
	public static void warn(String msg) {
		Log.getLogger("").warning(msg);
	}
	
	public static void error(String msg) {
		Log.getLogger("").severe(msg);	
	}
	
	
	public static long tick() {
		return System.currentTimeMillis();
	}
	
	public static void tock(String msg, long tick) {
		long total = System.currentTimeMillis() - tick;
		Log.warn(msg + " " + total + " ms.");
	}

}
