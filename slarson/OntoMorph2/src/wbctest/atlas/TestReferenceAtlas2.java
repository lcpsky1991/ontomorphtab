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
		
		//int rostralCaudal (132), int dorsalVentral (80), int lateralMedial (114)
		int[][] x = {{(int)(82.25*4),(int)(37.75*4), (int)(36.5*4)},
				{(int)(82.25*4),(int)(37.75*4), (int)(30.5*4)},
				{(int)(125.25*4),(int)(40.75*4), (int)(47.5*4)},
				{(int)(125.25*4),(int)(65.75*4), (int)(47.5*4)},
				{(int)(125.25*4),(int)(75.75*4), (int)(47.5*4)},
				{(int)(30.25*4),(int)(15.75*4), (int)(36.5*4)},
				{(int)(95.25*4),(int)(47.75*4), (int)(47.5*4)},
				{(int)(89.25*4),(int)(47.75*4), (int)(47.5*4)},
				{213,41,223},
				{(int)(105.25*4),(int)(47.75*4), (int)(47.5*4)}};
		
		for (int i = 0; i< x.length; i++) {
			BrainRegion b = atlas.getBrainRegionByVoxel(x[i][0], x[i][1], x[i][2]);
			if (b != null) { 
				System.out.println(b.getName());
			} else {
				System.out.println("[no region]");
			}
		}
	}
	
	/**
	 * Tests to make sure that the right data can be pulled out of the voxel atlas
	 * offset 29,962,170 should return a value of 28
	 */
	public void testGetVoxelAtlasStream() {
		ReferenceAtlas atlas = ReferenceAtlas.getInstance();
		FileInputStream s = atlas.getVoxelAtlasStream();
		try {
			
			int offset = 29962170;
			
			byte[] brainRegionIdByteArray = new byte[1];
			s.skip(offset);
			s.read(brainRegionIdByteArray);
			assertEquals(BitMath.convertByteArrayToInt(brainRegionIdByteArray), 28);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
