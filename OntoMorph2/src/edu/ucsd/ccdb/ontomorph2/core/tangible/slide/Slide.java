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
import org.morphml.neuroml.schema.XWBCQuat;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.impl.XWBCQuatImpl;
import org.morphml.neuroml.schema.impl.XWBCSlideImpl;

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
        if (lookup == null) {
        	super.initializeTangible(name, new XWBCSlideImpl());
        	setRatio(1f);
        } else {
        	this.theSpatial = lookup;
        }
		
        this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(
				SemanticClass.IMAGE_CLASS));
		this.getSemanticInstance();
	}
	

	public Slide(XWBCSlide slide) {
		super(slide.getName());
		this.theSpatial = slide;
		
		this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(
				SemanticClass.IMAGE_CLASS));
		this.getSemanticInstance();
	}

	
	public Slide(String name, URI imageURI) {
		this(name);
		try {
			this.setURL(imageURI.toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	}
	
	
	public boolean loadFromDB(String name)
	{
		theSpatial = (XWBCSlide) DataRepository.getInstance().loadTangible(XWBCSlide.class, name);
		return (theSpatial != null);
	}
	
	public XWBCSlideImpl getMorphMLSlide()
	{
		return (XWBCSlideImpl)theSpatial;
	}
	
	public void setURL(URL imageURL) {
		getMorphMLSlide().setImageURL(imageURL.toExternalForm());
	}
	
	public URL getURL() {
		try {
			return new URL(getMorphMLSlide().getImageURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private URL getImageURL() {
		return this.getURL();
	}
	
	public BufferedImage getBufferedImage() {
//		load image
		BufferedImage bufImg = null;
		try 
		{
			if (this.getImageURL() != null) 
			{
				bufImg = ImageIO.read(this.getImageURL());
			}
			else
			{
				bufImg =  ImageIO.read(new File("failed"));
			}
		} 
		catch (IOException e) 
		{
			Log.warn("Error getting buffered image: " + this.getName());
			e.printStackTrace();
		}
		return bufImg;
	}
	
	
	@Override
	public void save()
	{
	   	//get the appropriate content
		
	    try
	    {
		    super.save();
	    	System.out.println("Saved " + this.getName());
		}
		catch(Exception e)
		{
			Log.warn(e.getMessage());
		}
	}
}
