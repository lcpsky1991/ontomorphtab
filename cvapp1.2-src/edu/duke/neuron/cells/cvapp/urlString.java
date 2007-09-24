package edu.duke.neuron.cells.cvapp;

/*
 cvapp - neuronal morphology viewer, editor and file converter
 Copyright (C) 1998  Robert Cannon

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 please send comments, bugs, and feature requests to rcc1@soton.ac.uk
 or see http://www.neuro.soton.ac.uk/cells/

 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.awt.*;

import javax.swing.JFrame;	//potentially for use with download dialog/progress bar

public abstract class urlString {

	public static String[] readStringArrayFromURL(URL u)
	{
		Vector vs = new Vector();

		System.out.println("opening " + u);
		String[] sdat = null;
		if (u != null) {
			try {

				HttpURLConnection http = (HttpURLConnection)u.openConnection();
				InputStream in = http.getInputStream();
				System.out.println("got input stream");
				BufferedReader bis = new BufferedReader(new InputStreamReader(in));
				System.out.println("got data input stream");


				String buffer;
				long fileSize=0;	//size of file to be downloaded
				long got=0;			//ammount of file that has been got already
				final long factor = 1024;	//The filesize is probably counting characters, divide by 1024 for KB
				//Will read file one line at a time into the buffer
				//buffer contents are stored in sdat matrix, which is returned
				//Keep readining one line until EOF is reached (null)

				//Check filesize before downloading
				fileSize = http.getContentLength();
				System.out.println("Estimated filesize is: " + fileSize / factor + "KB (" + fileSize + ")");



				//Create an info dialog box to display
				Frame info;

				info = new Frame("Downloading");			//create new invisible frame with appropriate title
				info.setLayout(new FlowLayout());


				TextField txtInfo = new TextField("Downloading...");
				info.add(txtInfo, BorderLayout.CENTER);

				//p is approximately center of screen
				int p = (int) (( Toolkit.getDefaultToolkit().getScreenSize().getHeight() ) * (3/8) );

				info.setSize(700, 100);
				info.setLocation(p ,p);
				info.validate();
				info.setVisible(true);
				info.repaint();

				do
				{
					buffer = bis.readLine();
					if ( buffer != null ) // dont add null elements, dont incriment g on null
					{
						got += buffer.length();
						vs.addElement(buffer);
						txtInfo.setText(got/factor + "KB  of " + fileSize/factor + "KB)");
						info.repaint();
					}
				} while ( buffer != null );

				//destroy info dialog box to free resources
				info.setVisible(false);
				info.dispose();

				http.disconnect();	//close the connection to free resources

			}
			catch (IOException ex) {
				System.out.println("URL read error ");
			}



			System.out.println("read " + vs.size() + " lines");



			if (vs.size() > 0) {
				sdat = new String[vs.size()];
				for (int i = 0; i < vs.size(); i++) {
					sdat[i] = (String) (vs.elementAt(i));
					//System.out.println(sdat[i]);
				}
			}

		}
		return sdat;
	}

}
