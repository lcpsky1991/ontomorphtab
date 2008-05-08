package edu.ucsd.ccdb.ontomorph2.core.data;

import java.net.URL;

public interface ICCDBRepository {

	/**
	 * 
	 * @param mpid - Microscopy Product ID - uniquely references any data in the CCDB
	 * @return
	 */
	public CCDBMicroscopyData getCCDBData(int mpid);
}
