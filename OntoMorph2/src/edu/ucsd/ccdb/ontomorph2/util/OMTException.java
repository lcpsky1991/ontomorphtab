package edu.ucsd.ccdb.ontomorph2.util;

/**
 * Base class for all application-level exceptions
 * 
 * @author stephen
 *
 */
public class OMTException extends RuntimeException {

		public OMTException(String s, Exception e) {
			super(s, e);
		}
}
