package edu.ucsd.ccdb.ontomorph2.core.tangible.slide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;

/**
 * A slide that refers to some URI.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class URISlide extends Slide {


	URI _imageURI = null;
	URL _imageURL = null;
	
	public URISlide() {
		
	}
	
	public URISlide(URI imageURI) {
		_imageURI = imageURI;
	}
	
	public URISlide(URI filePath, float ratio) {
		this(filePath);
		setRatio(ratio);
	}
	

	public URI getImageURI() {
		return _imageURI;
	}
	

	private URL getImageURL() {
		if (_imageURL != null) {
			return _imageURL;
		}
		try {
			return this._imageURI.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage getBufferedImage() {
//		load image
		BufferedImage bufImg = null;
		try {
			if (this.getImageURI() != null) {
				
				bufImg = ImageIO.read(new File(this.getImageURI()));
				
			} else {
				bufImg = ImageIO.read(this.getImageURL());
			}
		} catch (IOException e) {
			System.out.println(this.getImageURL().getFile());
			e.printStackTrace();
		}
		return bufImg;
	}
}
