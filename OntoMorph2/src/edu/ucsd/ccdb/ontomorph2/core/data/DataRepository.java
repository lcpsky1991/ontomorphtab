package edu.ucsd.ccdb.ontomorph2.core.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Wraps a database that stores all the information about current position/rotation/scale of objects.
 * Also keeps track of users, changes, layers, and user preferences.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataRepository {

	static DataRepository repo = null;
	SessionFactory sFact = null;
	Map<String, Serializable> cache = new HashMap<String, Serializable>();
	
	public static DataRepository getInstance() {
		if (repo == null) {
			repo = new DataRepository();
		}
		return repo;
	}
	
	protected DataRepository() {
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
	
	public boolean isFileCached(String url, Class c) {
		return getCachedFile(url, c) != null;
	}
	
	public void cacheFile(String url, Object o){
//		 Open the session
		final Session saveSession = sFact.openSession();
//		 Save the unmarshalled object into the database
		saveSession.saveOrUpdate(o);
//		 Get the id
		final Serializable id = saveSession.getIdentifier(o);
//      Cache the id
		cache.put(url, id);
		
//		 Flush and close the session
		saveSession.flush();
		saveSession.close();	
	}
	
	public Object getCachedFile(String url, Class c) {
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
	}
	
	
}
