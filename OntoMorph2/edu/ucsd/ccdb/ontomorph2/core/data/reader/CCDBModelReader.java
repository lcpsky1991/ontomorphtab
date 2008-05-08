package edu.ucsd.ccdb.ontomorph2.core.data.reader;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.data.CCDBMicroscopyData;

public abstract class CCDBModelReader {

	public static final int RAW_DATA = 0;
	public static final int RECONSTRUCTION_DATA = 1;
	public static final int SEGMENTATION_DATA = 2;
	
	
	public CCDBModelReader(CCDBMicroscopyData data) {}
	
	/**
	 * @param data_type - defined by ints in CCDBModelReader
	 * @return a list of CCDBFiles that provide InputStreams to read the data from the CCDB
	 */
	public abstract List<CCDBFile> getFiles(int data_type); 
	
}
