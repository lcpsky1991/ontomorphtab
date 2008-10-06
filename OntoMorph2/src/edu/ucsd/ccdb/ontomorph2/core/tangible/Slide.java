package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.morphml.metadata.schema.Point3D;
import org.morphml.neuroml.schema.XWBCQuat;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.impl.XWBCQuatImpl;
import org.morphml.neuroml.schema.impl.XWBCSlideImpl;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.URISlide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;



/**
 * A Panel in 3D space that displays an image of a brain slice.
 *  .
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public abstract class Slide extends Tangible {
	
	float ratio = 1f;

	protected URI _imageURI = null;
	protected URL _imageURL = null;
	protected XWBCSlide morphmlSlide = null; //must be gotten from DB
	
	public Slide() 
	{
		this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(
				SemanticClass.IMAGE_CLASS));
		this.getSemanticInstance();
	}

	public float getRatio() {
		return ratio;
	}
	
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
	
	public boolean loadFromDB(String name)
	{
		setName(name);
		
		XWBCSlide lookup = null; //look up myself in the DB
		lookup = (XWBCSlide) DataRepository.getInstance().loadTangible(XWBCSlide.class, name);
		
		//get all the properties of the XWBCSlide and copy them over to the the model (Tangible)
		if (lookup != null)
		{
			URI place = null;
			
			try
            {
                place = new URI(lookup.getImageURL());
            }
            catch (URISyntaxException e1)
            {
                // TODO Auto-generated catch block
                Log.warn("Error converting URI in Slide.java (" + this.getName() + ": " + e1.getMessage());
            }
			
            this.setName(lookup.getName());	//copy the ID (name)
			
            this.setRatio((float) lookup.getRatio());	//copy the ratio
            
            //copy the URL info
            try
            {
                this._imageURL = place.toURL();
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
			//copy over the positions and quaternions
			Point3D pos = lookup.getPosition();
			XWBCQuat rot = lookup.getRotation();
			
			try
			{
				this.setRelativeRotation(new RotationVector((float)rot.getX(),(float)rot.getY(),(float)rot.getZ(),(float)rot.getW()));
				this.setRelativePosition((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
			}
			catch(Exception e)
			{
				Log.warn("Error: could not convert location/rotation in Slide.java: " + e.getMessage());
			}
			
			return true;
		}
		else
		{	//did not find the Slide in the DB, use defaults? or don't do anything (for defaultScene created object)
			/*
			this.setRelativePosition(0,0,0);
			this.setRelativeRotation(new RotationVector(0,0,0,1));
			this.setRatio(1f);
			this.setName(name);
			*/
			
		}
		
		//if all went well return the newly created slide, otehrwise return null
		return false;
		
	}
	
	
	@Override
	public void save()
	{
	    super.save();
	    
	   	//get the appropriate content
		try
		{
	        //convert the position to Point3D
	        OMTVector pt = new OMTVector(this.getRelativePosition());
	        
	        //first instantiate the instance by getting it form the DB
	        morphmlSlide = (XWBCSlide) DataRepository.getInstance().loadTangible(XWBCSlide.class, this.getName());
	        if ( morphmlSlide== null) morphmlSlide = new XWBCSlideImpl();
	        
	        //update the model that is being saved
	        String r = this._imageURI.toURL().toString();
	        if (r == null) r = this._imageURL.toString();
		    morphmlSlide.setImageURL(r);
		    
		    
		    morphmlSlide.setName(this.getName());
		    morphmlSlide.setPosition(pt.asPoint3D());
		    morphmlSlide.setRatio(this.getRatio());
		    
		    //= === save rotation
		    XWBCQuat qs = new XWBCQuatImpl();
		    qs.setW(this.getRelativeRotation().w);
		    qs.setZ(this.getRelativeRotation().z);
		    qs.setY(this.getRelativeRotation().y);
		    qs.setX(this.getRelativeRotation().x);
		    morphmlSlide.setRotation(qs);
		    //====
		    DataRepository.getInstance().saveFileToDB(morphmlSlide);
		    System.out.println("Saved: " + this.getName() + " with " + r);
		}
		catch(Exception e)
		{
			Log.warn(e.getMessage());
		}
	}

	
}
