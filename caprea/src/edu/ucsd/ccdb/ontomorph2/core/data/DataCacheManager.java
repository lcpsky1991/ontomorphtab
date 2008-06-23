package edu.ucsd.ccdb.ontomorph2.core.data;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;

/**
 * Manages the disk cache for items downloaded from the internet
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataCacheManager {

	protected static DataCacheManager manager = null;
	
	protected DataCacheManager() {}
	
	public static DataCacheManager getInstance() {
		if (manager == null){
			manager = new DataCacheManager();
		}
		return manager;
	}
	public URL cacheFile(CCDBFile f) {
		
		return null; 
	}
	
}
