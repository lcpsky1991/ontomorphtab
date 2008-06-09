/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.brainCatalog.ccdb.database;

import edu.ucsd.ccdb.ontomorph2.core.data.*;
import java.sql.*;
/**
 *
 * @author Administrator
 */
public class CCDBUtil 
{
     private DBService db = null;
     public CCDBUtil(String path) throws Exception
     {
         db = new DBService(path);
     }
     
     
    public CCDBMicroscopyData getMicroscopyDetails(int mid) throws Exception
    {
        Connection c = db.getConnection();
        String sql = "select b.MICROSCOPYPRODUCT_ID, "+
    " b.region, b.SUBREGION, b.STRUCTURE, b.SPECIES, "+
    " b.IMAGE2D_512, b.RECON_512, "+
    " b.SEG_512, b.IMAGE2D_DATA, b.IMAGE2D_ANIMATION, "+
    " b.RECON_DATA, b.RECON_ANIMATION, b.SEG_DATA, "+
    " b.PROJECT, b.EXPERIMENT, b.SUBJECT, b.SPECIMENPREPARATION, b.MICROSCOPY, "+
    " b.ANATOMICALDETAIL, b.IMAGE2D, b.IMAGING_PARAMETER, b.IMAGING_PRODUCT_TYPES, "+
    " b.RECONSTRUCTION, b.SEGMENTATION "+
    " from brain_catalog_ccdb b where b.MICROSCOPYPRODUCT_ID =?";
        PreparedStatement ps = c.prepareCall(sql);
        ps.setInt(1, mid);
        
        ResultSet rs = ps.executeQuery();
        CCDBMicroscopyData data = new CCDBMicroscopyData();
        while(rs.next())
        {
            if(data.getMICROSCOPYPRODUCT_ID() == null)
                data.setMICROSCOPYPRODUCT_ID(rs.getString("MICROSCOPYPRODUCT_ID"));
            if(data.getRegion() == null)
                data.setRegion(rs.getString("region"));
            
            if(data.getSUBREGION() == null)
                data.setSUBREGION(rs.getString("SUBREGION"));
            
            if(data.getSTRUCTURE() == null)
                data.setSTRUCTURE(rs.getString("STRUCTURE"));
            
            if(data.getSpecies() == null)
                data.setSpecies(rs.getString("SPECIES"));
            
            if(data.getIMAGE2D_512_URL() == null)
                data.setIMAGE2D_512_URL(rs.getString("IMAGE2D_512"));
            
            if(data.getRECON_512_URL() == null)
                data.setRECON_512_URL(rs.getString("RECON_512"));
            
            if(data.getSEG_512_URL() == null)
                data.setSEG_512_URL(rs.getString("SEG_512"));
            
            if(data.getIMAGE2D_DATA_URL() == null)
                data.setIMAGE2D_DATA_URL(rs.getString("IMAGE2D_DATA"));
            
            if(data.getIMAGE2D_ANIMATION_URL() == null)
                data.setIMAGE2D_ANIMATION_URL(rs.getString("IMAGE2D_ANIMATION"));
            
            if(data.getRECON_DATA_URL() == null)
                data.setRECON_DATA_URL(rs.getString("RECON_DATA"));
            
            if(data.getRECON_ANIMATION_URL() == null)
                data.setRECON_ANIMATION_URL(rs.getString("RECON_ANIMATION"));
            
            if(data.getSEG_DATA_URL() == null)
                data.setSEG_DATA_URL(rs.getString("SEG_DATA"));
            
            if(data.getPROJECT() == null)
                data.setPROJECT(rs.getString("PROJECT"));
            
            if(data.getEXPERIMENT() == null)
                data.setEXPERIMENT(rs.getString("EXPERIMENT"));
            
            if(data.getSUBJECT() == null)
                data.setSUBJECT(rs.getString("SUBJECT"));
            
            if(data.getSPECIMENPREPARATION() == null)
                data.setSPECIMENPREPARATION(rs.getString("SPECIMENPREPARATION"));
            
            if(data.getMICROSCOPY() == null)
                data.setMICROSCOPY(rs.getString("MICROSCOPY"));
            
            if(data.getANATOMICALDETAIL() == null)
                data.setANATOMICALDETAIL(rs.getString("ANATOMICALDETAIL"));
            
            if(data.getIMAGE2D() == null)
                data.setIMAGE2D(rs.getString("IMAGE2D"));
            
            if(data.getIMAGING_PARAMETER() == null)
                data.setIMAGING_PARAMETER(rs.getString("IMAGING_PARAMETER"));
            
            if(data.getIMAGING_PRODUCT_TYPES() == null)
                data.setIMAGING_PRODUCT_TYPES(rs.getString("IMAGING_PRODUCT_TYPES"));
            
            if(data.getRECONSTRUCTION() == null)
                data.setRECONSTRUCTION(rs.getString("RECONSTRUCTION"));
            
            if(data.getSEGMENTATION() == null)
                data.setSEGMENTATION(rs.getString("SEGMENTATION"));
            else
            {
                String seg = data.getSEGMENTATION();
                StringBuffer buff = new StringBuffer();
                buff.append(seg);
                buff.append("\n\n");
                buff.append(rs.getString("SEGMENTATION"));
                data.setSEGMENTATION(buff.toString());
            }
        }
        db.closeConnection(c);
        return data;
    }

    public static void main(String[] args)
    {
        try
        {
            CCDBUtil util = new CCDBUtil("C:\\DBConnectionInfo.properties");
            CCDBMicroscopyData data = util.getMicroscopyDetails(21);
            System.out.println(data.getMICROSCOPY()+" \n "+data.getIMAGE2D_512_URL());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        
    }
    
}
