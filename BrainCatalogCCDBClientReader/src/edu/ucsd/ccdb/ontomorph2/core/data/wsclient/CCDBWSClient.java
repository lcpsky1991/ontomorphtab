/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.ontomorph2.core.data.wsclient;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author Administrator
 */
public class CCDBWSClient 
{
    public static void main(String[] args)
    {
        try
        {
            
            CCDBWSClient client = new CCDBWSClient();
    edu.ucsd.ccdb.ontomorph2.core.data.wsclient.GetCCDBMicroscopyDataService
    ws = new edu.ucsd.ccdb.ontomorph2.core.data.wsclient.GetCCDBMicroscopyDataService();
        
          edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData data =   
                  ws.getGetCCDBMicroscopyDataPort().getCCDBMicroscopy(21);
            
       //   System.out.println(data.getMICROSCOPY());
          CCDBModelReader reader = new CCDBModelReader(data);
          List file = reader.getFiles();
          
          System.out.println(file.size());
          
          for(int i=0;i<file.size();i++)
          {
              CCDBFile f = (CCDBFile)file.get(i);
              System.out.println(f.getPath()+"  "+f.getCCDBCatagory()+"  "+f.getCCDBFileType());
              
              InputStream in = f.getInputStream();
              client.writeFile(in, new File("C:\\test"+i+"."+f.getFileExtension()));
          }
          
        }
        catch(java.lang.Exception e)
        {
           e.printStackTrace();
                    
        }
    
    }
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
  }
}
