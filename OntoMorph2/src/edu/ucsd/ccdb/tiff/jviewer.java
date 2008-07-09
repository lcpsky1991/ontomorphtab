package edu.ucsd.ccdb.tiff;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public class jviewer {

	public static void main(String[] args)
	{

			
			try
			{
				jtiffLoader m = new jtiffLoader();

				int[] vals;
				
				m.init("cer_pyr.tiff");	//the DPI and screensize dont matter
				
				System.out.println("The image width through java is: " + m.getImageW() );
				System.out.println("The image height through java is: " + m.getImageH() );
				System.out.println("The tile through java is: " + m.getTileW() );
				System.out.println("The tile through java is: " + m.getTileH() );
				vals = m.getRGBA(0.1f,0.1f,0.9f,0.9f,800,600);
				System.out.println("image len: " + vals.length );
				System.out.println("size: " + m.getW() + " by " + m.getH() );
				
			
				//write the array to a raw file
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
