package edu.ucsd.ccdb.ontomorph2.core.tangible.slide;

import java.nio.ByteBuffer;

import com.jme.image.Image;

import edu.ucsd.ccdb.tiff.jviewerBufferedImage;

/**
 * A slide that supports accessing pyramidal tiffs.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TiledSlide extends Slide{


	
	public TiledSlide(String name, Image i, float ratio) {
		super(name);
	}

	public Image getImage() {

		jviewerBufferedImage test = new jviewerBufferedImage();
		int[] vals = test.getTestImageArray();
		byte[] byteArray = new byte[vals.length];
		for (int i = 0; i < vals.length; i++) {
			byteArray[i] = (byte)vals[i];
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		Image i = new Image(Image.GUESS_FORMAT, 1405,1080, byteBuffer);
		return i;
		
		
	}

	
	
}
