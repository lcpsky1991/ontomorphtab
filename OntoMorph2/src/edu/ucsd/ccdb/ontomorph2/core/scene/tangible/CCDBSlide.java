package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFileType;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBModelReader;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * A Slide that accesses images from the CCDB
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class CCDBSlide extends URISlide {


	
	public CCDBSlide(CcdbMicroscopyData image, float ratio) {
		URL imageURL = null;
		CCDBModelReader reader;
		try {
			reader = new CCDBModelReader(image);
			List file = reader.getFiles();
			
			for(int i=0;i<file.size();i++)
			{
				CCDBFile f = (CCDBFile)file.get(i);
				//System.out.println(f.getCCDBCatagory());
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
		
		_imageURL = imageURL;
		
		this.ratio = ratio;
	}
}
