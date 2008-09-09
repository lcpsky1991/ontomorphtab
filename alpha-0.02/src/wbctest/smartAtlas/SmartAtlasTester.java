package wbctest.smartAtlas;

import java.util.ArrayList;
import java.util.Iterator;

import net.birn.components.spatialatlas.spatialatlasclient.SpatialAtlasClientDataModel;
import net.birn.components.spatialatlas.spatialatlasclient.SpatialAtlasWebserviceClient;


public class SmartAtlasTester {

	public static void main(String[] args) {
		test1();
		test2();
		test3();
	}
	 
	public static void test1() {
		
		SpatialAtlasWebserviceClient client = new SpatialAtlasWebserviceClient();//Input is in String, and output is in String too.
		
		SpatialAtlasClientDataModel dataModel;
		try {
			dataModel = client.getAtlasEnvelope("Smart Atlas");

			System.out.println("Map Units - " + dataModel.getMapUnits() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void test2() {
		SpatialAtlasWebserviceClient client = new SpatialAtlasWebserviceClient();
		
		
		
		ArrayList list = null;
		try {
			list = client.getBrainRegionNames("-0.04", "-2.175", "-2.077");
			
			Iterator iterator = list.iterator();
			
			String brainRegionName = "";
			
			
			
			while(iterator.hasNext()) {
				
				brainRegionName = (String)iterator.next();
				
				System.out.println("Brain Region name - " + brainRegionName );
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Input is in String and output is the arraylist of the string object
		
	}
	
	public static void test3() {
		SpatialAtlasWebserviceClient client = new SpatialAtlasWebserviceClient();

		SpatialAtlasClientDataModel dataModel;
		try {
			dataModel = client.getAtlasEnvelope("Smart Atlas");
//			input is in string and the name of the atlas that you need to use is “Smart Atlas”
			
			System.out.println("Atlas Name - " + dataModel.getAtlasName() ); //output is in string
			
			System.out.println("Atlas Type - " + dataModel.getAtlasType() ); //output is in string
			
			System.out.println("Minimum X Coordinate - " + dataModel.getMinimumXCoordinates() ); //output is in string
			
			System.out.println("Maximum X Coordinate - " + dataModel.getMaximumXCoordinates() ); //output is in string
			
			System.out.println("Minimum Y Coordinate - " + dataModel.getMinimumYCoordinates() ); //output is in string
			
			System.out.println("Maximum Y Coordinate - " + dataModel.getMaximumYCoordinates() ); //output is in string
			
			System.out.println("Minimum Z Coordinate - " + dataModel.getMinimumZCoordinates() ); //output is in string
			
			System.out.println("Maximum Z Coordinate - " + dataModel.getMaximumZCoordinates() ); //output is in string
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
