package edu.ucsd.ccdb.ontomorph2.util;

/**
 * Exception that indicates that the OMT is in offline mode or otherwise cannot access the internet
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OMTOfflineException extends Exception {

	public static final String MSG = "Client is offline!";
	
	public OMTOfflineException(String s, Exception e) {
		super(s, e);
		// TODO Auto-generated constructor stub
	}

	public OMTOfflineException(String string) {
		super(MSG + " " + string);
	}

}
