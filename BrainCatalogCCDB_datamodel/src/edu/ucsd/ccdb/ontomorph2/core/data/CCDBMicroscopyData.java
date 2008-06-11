/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.ontomorph2.core.data;

/**
 *
 * @author Administrator
 */
public class CCDBMicroscopyData implements java.io.Serializable
{
    
    private String MICROSCOPYPRODUCT_ID = null; //CCDB ID
    private String region = null;  //Example: cerebellum
    private String SUBREGION = null;
    private String STRUCTURE = null;
    private String species = null;     //Example: mouse/rat
    private String IMAGE2D_512_URL = null; //512x512 image URL 
    private String RECON_512_URL = null; //512x512 image URL
    private String SEG_512_URL = null;//512x512 image URL
    private String IMAGE2D_DATA_URL = null;  //Data file URL
    private String IMAGE2D_ANIMATION_URL = null; //Animation URL
    private String RECON_DATA_URL = null; //Data file URL
    private String RECON_ANIMATION_URL = null;//Animation URL
    private String SEG_DATA_URL = null;//Data file URL
    private String PROJECT = null;
    private String EXPERIMENT = null;
    private String SUBJECT = null;
    private String SPECIMENPREPARATION = null;
    private String MICROSCOPY = null;
    private String ANATOMICALDETAIL = null;
    private String IMAGE2D = null; 
    private String IMAGING_PARAMETER = null;
    private String IMAGING_PRODUCT_TYPES= null;
    private String RECONSTRUCTION = null;
    private String SEGMENTATION = null;

    public String getMICROSCOPYPRODUCT_ID() {
        return MICROSCOPYPRODUCT_ID;
    }

    public void setMICROSCOPYPRODUCT_ID(String MICROSCOPYPRODUCT_ID) {
        this.MICROSCOPYPRODUCT_ID = MICROSCOPYPRODUCT_ID;
    }

    public String getIMAGE2D_512_URL() {
        return IMAGE2D_512_URL;
    }

    public void setIMAGE2D_512_URL(String IMAGE2D_512_URL) {
        this.IMAGE2D_512_URL = IMAGE2D_512_URL;
    }

    public String getRECON_512_URL() {
        return RECON_512_URL;
    }

    public void setRECON_512_URL(String RECON_512_URL) {
        this.RECON_512_URL = RECON_512_URL;
    }

    public String getSEG_512_URL() {
        return SEG_512_URL;
    }

    public void setSEG_512_URL(String SEG_512_URL) {
        this.SEG_512_URL = SEG_512_URL;
    }

    public String getIMAGE2D_DATA_URL() {
        return IMAGE2D_DATA_URL;
    }

    public void setIMAGE2D_DATA_URL(String IMAGE2D_DATA_URL) {
        this.IMAGE2D_DATA_URL = IMAGE2D_DATA_URL;
    }

    public String getIMAGE2D_ANIMATION_URL() {
        return IMAGE2D_ANIMATION_URL;
    }

    public void setIMAGE2D_ANIMATION_URL(String IMAGE2D_ANIMATION_URL) {
        this.IMAGE2D_ANIMATION_URL = IMAGE2D_ANIMATION_URL;
    }

    public String getRECON_DATA_URL() {
        return RECON_DATA_URL;
    }

    public void setRECON_DATA_URL(String RECON_DATA_URL) {
        this.RECON_DATA_URL = RECON_DATA_URL;
    }

    public String getRECON_ANIMATION_URL() {
        return RECON_ANIMATION_URL;
    }

    public void setRECON_ANIMATION_URL(String RECON_ANIMATION_URL) {
        this.RECON_ANIMATION_URL = RECON_ANIMATION_URL;
    }

    public String getSEG_DATA_URL() {
        return SEG_DATA_URL;
    }

    public void setSEG_DATA_URL(String SEG_DATA_URL) {
        this.SEG_DATA_URL = SEG_DATA_URL;
    }

    public String getPROJECT() {
        return PROJECT;
    }

    public void setPROJECT(String PROJECT) {
        this.PROJECT = PROJECT;
    }

    public String getEXPERIMENT() {
        return EXPERIMENT;
    }

    public void setEXPERIMENT(String EXPERIMENT) {
        this.EXPERIMENT = EXPERIMENT;
    }

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getSPECIMENPREPARATION() {
        return SPECIMENPREPARATION;
    }

    public void setSPECIMENPREPARATION(String SPECIMENPREPARATION) {
        this.SPECIMENPREPARATION = SPECIMENPREPARATION;
    }

    public String getMICROSCOPY() {
        return MICROSCOPY;
    }

    public void setMICROSCOPY(String MICROSCOPY) {
        this.MICROSCOPY = MICROSCOPY;
    }

    public String getANATOMICALDETAIL() {
        return ANATOMICALDETAIL;
    }

    public void setANATOMICALDETAIL(String ANATOMICALDETAIL) {
        this.ANATOMICALDETAIL = ANATOMICALDETAIL;
    }

    public String getIMAGE2D() {
        return IMAGE2D;
    }

    public void setIMAGE2D(String IMAGE2D) {
        this.IMAGE2D = IMAGE2D;
    }

    public String getIMAGING_PARAMETER() {
        return IMAGING_PARAMETER;
    }

    public void setIMAGING_PARAMETER(String IMAGING_PARAMETER) {
        this.IMAGING_PARAMETER = IMAGING_PARAMETER;
    }

    public String getIMAGING_PRODUCT_TYPES() {
        return IMAGING_PRODUCT_TYPES;
    }

    public void setIMAGING_PRODUCT_TYPES(String IMAGING_PRODUCT_TYPES) {
        this.IMAGING_PRODUCT_TYPES = IMAGING_PRODUCT_TYPES;
    }

    public String getRECONSTRUCTION() {
        return RECONSTRUCTION;
    }

    public void setRECONSTRUCTION(String RECONSTRUCTION) {
        this.RECONSTRUCTION = RECONSTRUCTION;
    }

    public String getSEGMENTATION() {
        return SEGMENTATION;
    }

    public void setSEGMENTATION(String SEGMENTATION) {
        this.SEGMENTATION = SEGMENTATION;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSUBREGION() {
        return SUBREGION;
    }

    public void setSUBREGION(String SUBREGION) {
        this.SUBREGION = SUBREGION;
    }

    public String getSTRUCTURE() {
        return STRUCTURE;
    }

    public void setSTRUCTURE(String STRUCTURE) {
        this.STRUCTURE = STRUCTURE;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
