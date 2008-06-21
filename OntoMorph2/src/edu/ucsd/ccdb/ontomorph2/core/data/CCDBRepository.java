package edu.ucsd.ccdb.ontomorph2.core.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBModelReader;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CCDBWSClient;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.GetCCDBMicroscopyDataService;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

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
	public CcdbMicroscopyData getCCDBData(int mpid) {
		CcdbMicroscopyData data = null;
		try {
			
			GetCCDBMicroscopyDataService ws = new GetCCDBMicroscopyDataService();
			
			data =  ws.getGetCCDBMicroscopyDataPort().getCCDBMicroscopy(mpid);
			
		} catch (Exception e) {
			throw new OMTException("Problem loading CCDB Data", e);
		}
		return data;
	}
}
