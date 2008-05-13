package edu.ucsd.ccdb.ontomorph2.core.data.reader;

import java.io.InputStream;

public abstract class CCDBFile {

	/**
	 * 
	 * @return an input stream that is opened only upon calling this method - needs to be closed after use
	 */
	public abstract InputStream getInputStream();
	/**
	 * 
	 * @return an int defined in CCDBFileType
	 */
	public abstract int getCCDBFileType();
}
