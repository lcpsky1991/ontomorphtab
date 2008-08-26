package edu.ucsd.ccdb.ontomorph2.core.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.morphml.neuroml.schema.Level3Cell;



/**
 * Wraps a database that stores all the information about current position/rotation/scale of objects.
 * Also keeps track of users, changes, layers, and user preferences.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class GlobalDataRepository {

	static GlobalDataRepository repo = null;
	SessionFactory sFact = null;
	
	public static GlobalDataRepository getInstance() {
		if (repo == null) {
			repo = new GlobalDataRepository();
		}
		return repo;
	}
	
	protected GlobalDataRepository() {
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
	
	public void saveFileToDB(Object o){
//		 Open the session
		final Session saveSession = sFact.openSession();
		final Transaction transaction = saveSession.beginTransaction();

		
//		 Save the unmarshalled object into the database
		saveSession.saveOrUpdate(o);
//		 Get the id
		final Serializable id = saveSession.getIdentifier(o);

		transaction.commit();
//		 Flush and close the session
		saveSession.flush();
		saveSession.close();	
	}
	
	
	public Object findMorphMLByName(String name) {
//		 Open the session
		final Session loadSession = sFact.openSession();

		Object o = null;
		/*
		final Object loadedObject = 
			((org.hibernate.classic.Session) loadSession).find("from Cell as cell where cell.name = ?", 
					name, Hibernate.STRING);
					*/
		/*
		Query q = loadSession.createQuery("from Cell as cell where cell.name = " + name);
		Object o = q.list().get(0);
		*/
		
		Criteria crit = loadSession.createCriteria(Level3Cell.class);//.add(Expression.eq("name", name));
		List l = crit.list();
		for (int i = 0; i < l.size(); i++) {
			Level3Cell c = (Level3Cell)l.get(i);
			if (c.getName() != null && c.getName().equals(name)) {
				o = c;
			}
		}
		
//		 Close the session
		loadSession.close();
		
		return o;
		//return loadedObject;

	}
}
