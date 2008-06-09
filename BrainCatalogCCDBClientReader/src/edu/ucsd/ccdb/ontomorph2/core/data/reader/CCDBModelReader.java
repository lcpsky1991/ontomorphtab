/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.ontomorph2.core.data.reader;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import  edu.ucsd.ccdb.ontomorph2.core.data.wsclient.*;
//import edu.ucsd.ccdb.ontomorph2.core.data.CCDBMicroscopyData;

public  class CCDBModelReader 
{
       private Vector vFiles = new Vector();

	
	public CCDBModelReader(edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData data) throws java.lang.Exception
        {
            this.initCCDBModel(data);
            
        }
        
        private void initCCDBModel(CcdbMicroscopyData data) throws java.lang.Exception
        {
            if(data == null)
                return;
            
            if(data.getIMAGE2D512URL()!= null)
            {
                CCDBFile file = new CCDBFile(data.getIMAGE2D512URL(),
                        CCDBDataCatagory.IMAGE2D,CCDBFileType.IMAGE_JPEG);
                this.vFiles.addElement(file);
            }
            
            if(data.getRECON512URL()!= null)
            {
                CCDBFile file = new CCDBFile(data.getRECON512URL(),
                        CCDBDataCatagory.RECONSTRUCTION,CCDBFileType.IMAGE_JPEG);
                this.vFiles.addElement(file);
            }  
            
            if(data.getSEG512URL()!= null)
            {
                CCDBFile file = new CCDBFile(data.getSEG512URL(),
                        CCDBDataCatagory.SEGMENTATION,CCDBFileType.IMAGE_JPEG);
                this.vFiles.addElement(file);
            }  
            
            if(data.getIMAGE2DANIMATIONURL() != null)
            {
                String path = data.getIMAGE2DANIMATIONURL();
                CCDBFile file = new CCDBFile(path,
                        CCDBDataCatagory.IMAGE2D,this.getVideoType(path));
                this.vFiles.addElement(file);                
            }
            if(data.getRECONANIMATIONURL() != null)
            {
                String path = data.getRECONANIMATIONURL();
                CCDBFile file = new CCDBFile(path,
                        CCDBDataCatagory.RECONSTRUCTION,this.getVideoType(path));
                this.vFiles.addElement(file);                
            }
        
        
        }
        
        private int getVideoType(String path)
        {
            if(path.toLowerCase().endsWith("avi"))
                return CCDBFileType.VIDEO_AVI;
            else if(path.toLowerCase().endsWith("mpeg") ||path.toLowerCase().endsWith("mpg") )
                return CCDBFileType.VIDEO_MPEG;           
            else if(path.toLowerCase().endsWith("qt"))
                return CCDBFileType.VIDEO_QT;  
            
            else
                return CCDBFileType.VIDEO_UNKNOWN;
        }
	/**
	 * @param data_type - defined by ints in CCDBModelReader
	 * @return a list of CCDBFiles that provide InputStreams to read the data from the CCDB
	 */
	public  List<CCDBFile> getFiles()
        {
            return this.vFiles;
            
        }
	
}

