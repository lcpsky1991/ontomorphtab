package edu.ucsd.ccdb.ontomorph2.core.tangible.slide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Log;

/**
 * A slide that refers to some URI.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class URISlide extends Slide {
	
	public URISlide(String name){
		super(name);
	}
	
	public URISlide(String name, URI imageURI) {
		this(name);
		_imageURI = imageURI;
	}
	
	public URISlide(String name, URI filePath, float ratio) 
	{
		this(name, filePath);
		setRatio(ratio);
	}
	

	public URI getImageURI() {
		return _imageURI;
	}
	

	private URL getImageURL() {
		if (_imageURL != null) 
		{
			return _imageURL;
		}
		if ( this._imageURI != null)
		{
			try 
			{
				return this._imageURI.toURL();
			} 
			catch (Exception e) 
			{
				
			}
		}
		return null;
	}
	
	public BufferedImage getBufferedImage() {
//		load image
		BufferedImage bufImg = null;
		try 
		{
			if (this.getImageURI() != null) 
			{
				
				bufImg = ImageIO.read(new File(this.getImageURI()));
				
			} 
			else if (this.getImageURL() != null) 
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
	

	
}
