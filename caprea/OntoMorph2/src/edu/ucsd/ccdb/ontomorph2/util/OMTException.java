package edu.ucsd.ccdb.ontomorph2.util;

/**
 * Base class for all application-level exceptions
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OMTException extends RuntimeException {

		public OMTException(String s, Exception e) {
			super(s, e);
		}
}
