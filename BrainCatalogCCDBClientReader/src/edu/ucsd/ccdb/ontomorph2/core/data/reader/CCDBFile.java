/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.ontomorph2.core.data.reader;
import java.net.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author Administrator
 */
public class CCDBFile 
{   
        private String path= null;
        private int catagory = -1;
        private int fileType = -1;
        
        public CCDBFile(String path, int catagory, int fileType)
        {
            this.path = path;
            this.catagory = catagory;
            this.fileType = fileType;
        }
	/**
	 * 
	 * @return an input stream that is opened only upon calling this method - needs to be closed after use
	 */
	public java.io.InputStream getInputStream() throws Exception
        {
            URL url = this.getURL();
            
            return url.openStream();
        }
        
        
        public URL getURL() throws Exception
        {
            URL url = new URL(this.path);
            return url;
        }
	/**
	 * 
	 * @return an int defined in CCDBFileType
	 */
	public  int getCCDBFileType()
        {
            return this.fileType;
        }
        public int getCCDBCatagory()
        {
            return this.catagory;
        }
        
        public String getPath()
        {
            return this.path;
        }
        
        public String getFileExtension()
        {
            if(this.fileType == CCDBFileType.IMAGE_JPEG)
                return "jpg";
            else if(this.fileType == CCDBFileType.VIDEO_AVI)
                return "avi";
            
            else if(this.fileType == CCDBFileType.VIDEO_MPEG)
                return "mpg";
            else if(this.fileType == CCDBFileType.VIDEO_QT)
                return "qt";     
            
            else 
                return "unknown";
        }
}