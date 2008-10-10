package edu.ucsd.ccdb.tiff;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class jviewer {

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
				
			
				//write the array to a raw file
				//this file is little-endian, 32-bit and either RGBA or ABGR
				ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream ("java.raw"));
				oos.writeObject (vals);
				oos.close ();
					 
				
				
				System.out.println("\n");
			}
			catch (Exception e)
			{
				System.out.println("Could not make jtiffLoader object (check that the library is available?)");
				System.err.println("Exception: " + e.getMessage());
			}
	}
}
