package edu.ucsd.ccdb.ontomorph2.core.data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.morphml.metadata.schema.Curve;
import org.morphml.neuroml.schema.Level3Cell;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.XWBCTangible;
import org.morphml.neuroml.schema.impl.XWBCSlideImpl;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;



/**
 * Wraps a database that stores all the information about current position/rotation/scale of objects.
 * Also keeps track of users, changes, layers, and user preferences.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DataRepository {

	static DataRepository repo = null;
	SessionFactory sFact = null;
	
	public static DataRepository getInstance() {
		if (repo == null) {
			repo = new DataRepository();
		}
		return repo;
	}
	
	protected DataRepository() 
	{
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
	
	
	
	
	public Object findCurve(String name)
	{
//		TODO: change Tangivle to some neuroML class
		Object objFound = null;
		final Session sesLoad = sFact.openSession();	//open connection to DB (SQL)
		
		Criteria search = sesLoad.createCriteria(Curve.class);
		List results = search.list();
		
		/**
		 * pull out all rows formt he db that correspond to the class in question
		 * and then look through them all and find matching name
		 */
		
		for (int i=0; i < results.size(); i++)
		{
			Curve consider =  (Curve) results.get(i);
			
		
			if (consider.getName() != null && consider.getName().equals(name))
			{
				objFound = consider;
				break;	//stop iterating through the rest
			}
		}
		
		return objFound;
	}
	
	
	@Deprecated
	public Object findSlideByName(String name)
	{
		Object objFound = null;
		final Session sesLoad = sFact.openSession();	//open connection to DB (SQL)
		
		Criteria search = sesLoad.createCriteria(XWBCSlide.class);
		List results = search.list();
		
		/**
		 * pull out all rows formt he db that correspond to the class in question
		 * and then look through them all and find matching name
		 */
		
		for (int i=0; i < results.size(); i++)
		{
			XWBCSlide consider =  (XWBCSlide) results.get(i);
			if (consider.getName() != null && consider.getName().equals(name))
			{
				objFound = consider;
				break;	//stop iterating through the rest
			}
		}
		
		return objFound;
	}
	
	@Deprecated
	public XWBCTangible findTangible(Class type, String name)
	{
		XWBCTangible objFound = null;
		final Session sesLoad = sFact.openSession();	//open connection to DB (SQL)
		
		Criteria search = sesLoad.createCriteria(type);
		List results = search.list();
		
		/**
		 * pull out all rows formt he db that correspond to the class in question
		 * and then look through them all and find matching name
		 */
		
		for (int i=0; i < results.size(); i++)
		{
			XWBCTangible consider =  (XWBCTangible) results.get(i);
			if (consider.getName() != null && consider.getName().equals(name))
			{
				objFound = consider;
				break;	//stop iterating through the rest
			}
		}
		
		return objFound;
		
		
		
	}
	public Object findMorphMLByName(String name) {
//		 Open the session
		final Session loadSession = sFact.openSession();

		Object objFound = null;
		/*
		final Object loadedObject = 
			((org.hibernate.classic.Session) loadSession).find("from Cell as cell where cell.name = ?", 
					name, Hibernate.STRING);
					*/
		/*
		Query q = loadSession.createQuery("from Cell as cell where cell.name = " + name);
		Object o = q.list().get(0);
		*/
		
		
		/**
		 * pull out all rows formt he db that correspond to the class in question
		 * and then look through them all and find matching name
		 */
		Criteria crit = loadSession.createCriteria(Level3Cell.class);//.add(Expression.eq("name", name));
		List l = crit.list();
		for (int i = 0; i < l.size(); i++) {
			Level3Cell c = (Level3Cell)l.get(i);
			if (c.getName() != null && c.getName().equals(name)) {
				objFound = c;
			}
		}
		
//		 Close the session
		loadSession.close();
		
		return objFound;
		//return loadedObject;

	}
}