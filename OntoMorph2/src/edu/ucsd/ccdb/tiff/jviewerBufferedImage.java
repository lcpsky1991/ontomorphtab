package edu.ucsd.ccdb.tiff;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;


public class jviewerBufferedImage {

	public static void main(String[] args)
	{

			
			try
			{
				System.setProperty("java.library.path", "lib");
				jtiffLoader m = new jtiffLoader();

				int[] vals;
				
				//m.init("/home/caprea/Documents/data/images/alz_11x11_2xbin_6_24_pyr.tif");	//the DPI and screensize dont matter
				m.init("./etc/img/ECE_191_hires_poster_pyr.tiff");	//the DPI and screensize dont matter
				
				
				System.out.println("The image width through java is: " + m.getImageW() );
				System.out.println("The image height through java is: " + m.getImageH() );
				System.out.println("The tile through java is: " + m.getTileW() );
				System.out.println("The tile through java is: " + m.getTileH() );
				vals = m.getRGBA(0.5f,0.5f,0.9f,0.9f,1200,1200);
				System.out.println("image len: " + vals.length );
				System.out.println("size: " + m.getW() + " by " + m.getH() );
				
				//this file is little-endian, 32-bit and either RGBA or ABGR
				final BufferedImage image = new BufferedImage(1405,1080, BufferedImage.TYPE_4BYTE_ABGR);
				
				image.setRGB(0,0, 1405,1080, vals, 0, 0);
				
				final Image img2 = Toolkit.getDefaultToolkit().getImage(new File(Scene.imgDir + "hippo_slice1.jpg").toURI().toURL()); 

				JFrame frame = new JFrame();
				JPanel imagePanel = new JPanel() {
					public void paintComponent(Graphics g) {
						//g.drawImage(img2, 0,0, this);
					
						g.drawImage(image, 0,0, this);
					}
				};
				imagePanel.setPreferredSize(new Dimension(1405,1080));
				frame.add(imagePanel);
				frame.setSize(1405,1080);
				frame.setVisible(true);
				
				System.out.println("\n");
			}
			catch (Exception e)
			{
				System.out.println("Could not make jtiffLoader object (check that the library is available?)");
				System.err.println("Exception: " + e.getMessage());
			}
	}
}
