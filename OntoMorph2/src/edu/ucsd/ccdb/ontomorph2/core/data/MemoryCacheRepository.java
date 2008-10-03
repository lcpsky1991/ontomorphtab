package edu.ucsd.ccdb.ontomorph2.core.data;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;



/**
 * Wraps a cache that keeps local copies of objects
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MemoryCacheRepository {

	static MemoryCacheRepository repo = null;
	SessionFactory sFact = null;
	Map<String, Object> cache = new HashMap<String, Object>();
	
	public static MemoryCacheRepository getInstance() {
		if (repo == null) {
			repo = new MemoryCacheRepository();
		}
		return repo;
	}
	
	protected MemoryCacheRepository() {
	}
	
	public boolean isFileCached(String url) {
		return getCachedFile(url) != null;
	}
	
	public Object getCachedFile(String url) {
		return cache.get(url);
	}
	
	public void cacheFile(String url, Object o) {
		cache.put(url, o);
	}
}
