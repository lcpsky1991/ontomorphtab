package edu.ucsd.ccdb.ontomorph2.core.data;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



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
		/*
		 try {
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/db", "sa", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 Configuration configuration = new Configuration().configure();
		 sFact = configuration.buildSessionFactory();
	}
	
	public boolean isFileCached(String url) {
		return getCachedFile(url) != null;
	}
	
	/*
	public Object loadFileFromDB(String url, Class c) {
//		 Open the session
		final Session loadSession = sFact.openSession();
//		 Load the object
		if (cache.get(url) == null) {
			return null;
		}
		final Object loadedObject = loadSession.get(c, cache.get(url));
//		 Close the session
		loadSession.close();
		return loadedObject;
	}*/
	
	public Object getCachedFile(String url) {
		return cache.get(url);
	}
	
	public void cacheFile(String url, Object o) {
//      Cache the file
		cache.put(url, o);
	}
}
