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
	 * 
	 * @param mpid - Microscopy Product ID - uniquely references any data in the CCDB
	 * @return
	 */
	public CcdbMicroscopyData getCCDBData(int mpid) {
		CcdbMicroscopyData data = null;
		try {
			
			GetCCDBMicroscopyDataService ws = new GetCCDBMicroscopyDataService();
			
			data =  ws.getGetCCDBMicroscopyDataPort().getCCDBMicroscopy(mpid);
			
			/*			
			CCDBModelReader reader = new CCDBModelReader(data);
			List file = reader.getFiles();

			System.out.println(file.size());
			
			for(int i=0;i<file.size();i++)
			{
				CCDBFile f = (CCDBFile)file.get(i);
				System.out.println(f.getPath()+"  "+f.getCCDBCatagory()+"  "+f.getCCDBFileType());
				
				InputStream in = f.getInputStream();
				writeFile(in, new File("C:\\test"+i+"."+f.getFileExtension()));
			}
			*/
		} catch (Exception e) {
			throw new OMTException("Problem loading CCDB Data", e);
		}
		return data;
	}

	/*
	private  void writeFile(InputStream inStream, File file) throws IOException {
		final int bufferSize = 1000;
		FileOutputStream fout = new FileOutputStream(file);
		byte[] buffer = new byte[bufferSize];
		int readCount = 0;
		while ((readCount = inStream.read(buffer)) != -1) { 
			if (readCount < bufferSize) {
				fout.write(buffer, 0, readCount);
			} else {
				fout.write(buffer);
			}
		}
	}*/
}
