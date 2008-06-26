package wbctest.atlas;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.util.BitMath;
import junit.framework.TestCase;

public class TestReferenceAtlas2 extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas.getBrainRegion(int, int, int)'
	 */
	public void testGetBrainRegionIntIntInt() {
		ReferenceAtlas atlas = ReferenceAtlas.getInstance();
		
		BrainRegion b1 = atlas.getBrainRegion(0,0,0);
		if (b1 != null) { 
			System.out.println(b1.getName());
		}
		BrainRegion b5 = atlas.getBrainRegion(22,44,66);
		if (b5 != null) {
			System.out.println(b5.getName());
		}
		BrainRegion b4 = atlas.getBrainRegion(50,100,200);
		if (b4 != null) {
			System.out.println(b4.getName());
		}
		BrainRegion b3 = atlas.getBrainRegion(100,100,100);
		if (b3 != null) {
			System.out.println(b3.getName());
		}
		BrainRegion b2 = atlas.getBrainRegion(200,200,200);
		if (b2 != null) {
			System.out.println(b2.getName());
		}
	}
	
	/**
	 * This test is intended to determine what portions of the Voxel Atlas actually
	 * has brain region data.  Apparently the first several hundred thousand
	 * bytes only has 0.
	 *
	 */
	public void testGetVoxelAtlasStream() {
		ReferenceAtlas atlas = ReferenceAtlas.getInstance();
		FileInputStream s = atlas.getVoxelAtlasStream();
		try {
			byte[] brainRegionIdByteArray = new byte[40000000];
			
			System.out.println(s.available());
			s.read(brainRegionIdByteArray);
			System.out.println(s.available());
			brainRegionIdByteArray = new byte[1];
			System.out.println(brainRegionIdByteArray);
			byte[] finalBytes = new byte[5];
			int j = 0;
			while(s.available() > 0) {
				s.read(brainRegionIdByteArray);
				if (brainRegionIdByteArray[0] > 0) {
					if (Arrays.binarySearch(finalBytes, brainRegionIdByteArray[0]) == -1) {
						if (j < finalBytes.length) {
							finalBytes[j++] = brainRegionIdByteArray[0];
						} else {
							break;
						}
					}
				}
			}
			for (int i = 0; i < finalBytes.length; i++){
				System.out.print(finalBytes[i] + " ");
			}
				//convert byte array to java int
				//int brainRegionId = BitMath.convertByteArrayToInt(brainRegionIdByteArray);
				//System.out.println(brainRegionId);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
