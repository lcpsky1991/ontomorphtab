package edu.ucsd.ccdb.ontomorph2.core.data;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import neuroml.generated.NeuroMLLevel2;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;

/**
 * Manages the disk cache for items downloaded from the internet
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataCacheManager {

	protected static DataCacheManager manager = null;
	Map cache = new HashMap();
	
	
	protected DataCacheManager() {}
	
	public static DataCacheManager getInstance() {
		if (manager == null){
			manager = new DataCacheManager();
		}
		return manager;
	}
	
	public void cacheMorphML(String url, NeuroMLLevel2 morphml){
		cache.put(url, morphml);
	}
	
	public Object getCachedMorphML(String url) {
		return cache.get(url);
	}
	
	public URL cacheFile(CCDBFile f) {
		
		return null; 
	}

	public boolean isMorphMLCached(String url) {
		return getCachedMorphML(url) != null;
	}
	
}
