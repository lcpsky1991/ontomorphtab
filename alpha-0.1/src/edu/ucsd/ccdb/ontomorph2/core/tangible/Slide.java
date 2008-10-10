package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.net.URI;
import java.net.URL;

import org.morphml.neuroml.schema.XWBCQuat;
import org.morphml.neuroml.schema.XWBCSlide;
import org.morphml.neuroml.schema.impl.XWBCQuatImpl;
import org.morphml.neuroml.schema.impl.XWBCSlideImpl;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;



/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public abstract class Slide extends Tangible {
	
	float ratio = 1f;

	protected URI _imageURI = null;
	protected URL _imageURL = null;
	private XWBCSlide morphmlSlide = null; //must be gotten from DB
	
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
	        morphmlSlide = (XWBCSlide) DataRepository.getInstance().findSlideByName(this.getName());
	        if ( morphmlSlide== null) morphmlSlide = new XWBCSlideImpl();
	        
	        //update the model that is being saved
	        String r = this._imageURI.toURL().toString();
	        if (r == null) r = this._imageURL.toString();
		    morphmlSlide.setImageURL(r);
		    
		    
		    morphmlSlide.setName(this.getName());
		    morphmlSlide.setPosition(pt.asPoint3D());
		     
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
