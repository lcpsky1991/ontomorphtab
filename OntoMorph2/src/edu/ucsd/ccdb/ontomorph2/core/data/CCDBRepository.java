package edu.ucsd.ccdb.ontomorph2.core.data;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.GetCCDBMicroscopyDataService;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;

/**
 * Manages all activity that interacts with the CCDB database. 
 * (http://ccdb.ucsd.edu)
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class CCDBRepository {

	protected static CCDBRepository repo = null;
	private CCDBRepository() {
		
	}
	
	public static CCDBRepository getInstance() {
		if (repo == null) {
			repo = new CCDBRepository();
		}
		return repo;
	}
	
	/**
	 * Returns a wrapper around any data from the CCDB.
	 * @param mpid - Microscopy Product ID - uniquely references any data in the CCDB
	 * @return
	 */
	public CcdbMicroscopyData getCCDBData(int mpid) throws OMTOfflineException{
		CcdbMicroscopyData data = null;
		if (OntoMorph2.isOfflineMode()) {
			throw new OMTOfflineException("Cannot access CCDB Data because you are in offline mode!");
		}
		try {
			
			GetCCDBMicroscopyDataService ws = new GetCCDBMicroscopyDataService();
			
			data =  ws.getGetCCDBMicroscopyDataPort().getCCDBMicroscopy(mpid);
			
		} catch (Exception e) {
			throw new OMTException("Problem loading CCDB Data", e);
		}
		return data;
	}
}
