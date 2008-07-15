package edu.ucsd.ccdb.ontomorph2.util;

import java.util.logging.Level;
import java.util.logging.Logger;

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
		getInstance().getLogger("").warning(msg);
	}
	
	
	public static long tick() {
		return System.currentTimeMillis();
	}
	
	public static void tock(String msg, long tick) {
		long total = System.currentTimeMillis() - tick;
		Log.warn(msg + " " + total + " ms.");
	}

}
