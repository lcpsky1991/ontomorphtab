/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.brainCatalog.ccdb.ws;

import javax.jws.WebService;
import java.util.Vector;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import javax.xml.ws.WebServiceContext;

import javax.xml.ws.WebServiceException;

import javax.xml.ws.handler.MessageContext;

import edu.ucsd.brainCatalog.ccdb.database.*;
import edu.ucsd.ccdb.ontomorph2.core.data.*;      
/**
 *
 * @author Administrator
 */

@WebService()
public class getCCDBMicroscopyData 
{
@Resource
private WebServiceContext wsContext;
/**
     * Web service operation
     */
    @WebMethod(operationName = "getCCDBMicroscopy")
    public CCDBMicroscopyData getCCDBMicroscopy(@WebParam(name = "mid")
    int mid) throws Exception {
        CCDBUtil util = new CCDBUtil(this.getDBConnection());
       
        return util.getMicroscopyDetails(mid);
    }
    
    
    private String getDBConnection()
    {
MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest req =
(((HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST)));
        HttpSession session =req.getSession();
           String dbpath =  
         session.getServletContext().getInitParameter("DBConnection");  
           
           return dbpath;
    }
    

}
