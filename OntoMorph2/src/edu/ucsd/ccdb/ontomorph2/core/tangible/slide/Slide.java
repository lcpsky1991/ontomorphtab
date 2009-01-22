package edu.ucsd.ccdb.ontomorph2.core.tangible.slide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.morphml.metadata.schema.Point3D;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.impl.XWBCSlideImpl;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;



/**
 * A Panel in 3D space that displays an image of a brain slice.
 *  .
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class Slide extends Tangible {
	
	protected Slide(String name) 
	{
		super(name);
		DataRepository repo = DataRepository.getInstance();
		 //first instantiate the instance by getting it form the DB
		XWBCSlide lookup = (XWBCSlide) repo.loadTangible(XWBCSlide.class, name);
        if (lookup == null) 
        {
        	super.initializeTangible(name, new XWBCSlideImpl());
        	setRatio(1f);
        } 
        else
        {
        	this.theSpatial = lookup;
        }
		
        this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.IMAGE_CLASS));
		this.getSemanticInstance();
	}
	

	public Slide(XWBCSlide slide) {
		super(slide.getName());
		this.theSpatial = slide;
		
		this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(
				SemanticClass.HIPPOCAMPUS_CLASS));
		this.getSemanticInstance();
	}

	
	public Slide(String name, URI imageURI) 
	{
		this(name);
		this.setURL(imageURI.toString());
	}
	
	public Slide(String name, URI filePath, float ratio) 
	{
		this(name, filePath);
		setRatio(ratio);
	}
	


	public float getRatio() {
		return (float)getMorphMLSlide().getRatio();
	}
	
	public void setRatio(float ratio) {
		getMorphMLSlide().setRatio(ratio);
		save();
	}
	
	
	public boolean loadFromDB(String name)
	{
		theSpatial = (XWBCSlide) DataRepository.getInstance().loadTangible(XWBCSlide.class, name);
		return (theSpatial != null);
	}
	
	//should maybe be an Impl?
	public XWBCSlide getMorphMLSlide()
	{
		return (XWBCSlide)theSpatial;
	}
	
	public void setURL(String url) 
	{
		
		URL madeURL = null;
		String s = null;
		
		//first make a string-URL based on what was input
		try 
		{
			madeURL = new URL(url);
			
			//if that string-URL was a local or relative path, trim off the beginning
			if ( madeURL.getProtocol().equals("file"))
			{
				s = madeURL.getPath();
			}
			else
			{
				s = madeURL.toString();
			}
		} 
		catch (MalformedURLException e) 
		{
			s = url;
		}
		
		getMorphMLSlide().setImageURL(s);
		save();
		changed(Tangible.CHANGED_COLOR);
	}
	
	public String getURL() {
		return (getMorphMLSlide().getImageURL());
	}
	
	/**
	 * The job of this function is to return a path that is appoproaite given the string stored in the model.
	 * If the stored string is a relative path, this returns an absolute path (for this machine)
	 * if the stored string is on the WEB, it returns it untouched
	 * If the stored string is absolute -- thats dumb
	 * @return the absolute path to the image resource (which was previously specified as relative)
	 */
	private URL resolveURL()
	{
		URL place = null;
		String curDir = System.getProperty("user.dir"); //get the current working directory
		String where = this.getURL();
		
		
		try 
		{
			place = new URL(where);
			//do nothing with the protocol, it's already good to go as an internet thing
			//determine whether this is a local resource or an internet resource
		}
		catch (MalformedURLException e) 
		{
			//if there is nothing attatched to the front of it, it is a relative path, append CWD
			try 
			{
				place = new URL("file:" + curDir + where);
			}
			catch (MalformedURLException e1) 
			{
				//do nothing
			}
		}
		
		//Log.warn(where + " resolved to to " + place);
		return place;
	}
	
	public BufferedImage getBufferedImage() {
//		load image
		BufferedImage bufImg = null;
		
		try 
		{
			URL imgloc = this.resolveURL();
			if (imgloc != null) 
			{
				//append the current working directory to the front of the filename?
				//if its on the web load this way
				bufImg = ImageIO.read(imgloc);
			}
			else
			{
				bufImg =  ImageIO.read(new File("failed"));
			}
		} 
		catch (IOException e) 
		{
			Log.warn("Error getting buffered image: " + this.getName());
		}
		return bufImg;
	}
}
