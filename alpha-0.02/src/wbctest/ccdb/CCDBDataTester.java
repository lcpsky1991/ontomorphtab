package wbctest.ccdb;

import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.data.CCDBRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFileType;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBModelReader;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;

public class CCDBDataTester {

	public static void main(String[] args ){
		CcdbMicroscopyData hippoImage = null;
		try {
			hippoImage = CCDBRepository.getInstance().getCCDBData(53);
		} catch (OMTOfflineException e) {
			e.printStackTrace();
		}
		URL imageURL = null;
		CCDBModelReader reader;
		try {
			reader = new CCDBModelReader(hippoImage);
			List file = reader.getFiles();
			
			for(int i=0;i<file.size();i++)
			{
				CCDBFile f = (CCDBFile)file.get(i);
				System.out.println(f.getCCDBCatagory());
				if (f.getCCDBFileType() == CCDBFileType.IMAGE_JPEG) {
						imageURL = f.getURL();
				} 
			}
			
			//if we don't find something within the CCDBFile, throw an exception since this Slide will 
			//be in a bad state.
			if (imageURL == null) {
				throw new OMTException("CcdbMicroscopyData did not contain an image type that was supported!", null);
			}
		} catch (Exception e) {
			throw new OMTException("Unable to load CCDB data!", e);
		}
	}
}
