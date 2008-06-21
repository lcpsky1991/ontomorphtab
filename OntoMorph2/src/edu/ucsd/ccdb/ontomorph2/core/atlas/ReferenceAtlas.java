package edu.ucsd.ccdb.ontomorph2.core.atlas;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.AllenCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.util.BitMath;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyNode;


/**
 * Contains a list of brain regions of the mouse brain and methods to manage and extract information about them.
 */

public class ReferenceAtlas {

	/**
	 * Holds singleton instance
	 */
	private static ReferenceAtlas instance;
	private List<BrainRegion> brainRegions;

	/**
	 * prevents instantiation
	 */
	private ReferenceAtlas() {
		/*
		 * You'll also find an ontology.csv file there that maps 
		 * structure names to abbreviations and colors.  The columns are:
		 * 1. Structure name
		 * 2. Abbreviation
		 * 3. Parent structure's abbreviation
		 * 4, 5, 6. Red, green, blue (0-255) color in the Allen Reference Atlas
		 * 7. Structure ID 1
		 * 8. Structure ID 2
		 * 9, 10, 11. Centroid
		 */
		brainRegions = new ArrayList<BrainRegion>();
		try {
			FileReader fr = new FileReader(new File(Scene.allenDir + "ontology.csv"));
			BufferedReader br = new BufferedReader(fr);
			AllenCoordinateSystem sys = new AllenCoordinateSystem();
			while (br.ready()) {
				String[] line = br.readLine().split(",");
				brainRegions.add(new BrainRegion(line[0], line[1], line[2], 
						new Color(Integer.parseInt(line[3]), Integer.parseInt(line[4]), 
								Integer.parseInt(line[5])),line[6], sys));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public ReferenceAtlas getInstance() {
		if (instance == null) {
			instance = new ReferenceAtlas();
		}
		return instance;
	}

	public BrainRegion getBrainRegion(String string) {
		for (BrainRegion r : getBrainRegions()) {
			if (string.equals(r.getAbbreviation())) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve a brain region by its x,y,z voxel coordinates in the 
	 * Allen atlas
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public BrainRegion getBrainRegion(int rostralCaudal, int dorsalVentral, int lateralMedial) {
		if (rostralCaudal > 528 || rostralCaudal < 0 || dorsalVentral > 320 || dorsalVentral < 0 
				|| lateralMedial > 456 || lateralMedial < 0) {
			throw new OMTException("Invalid value entered for getting a brain region!", null);
		}
		BrainRegion br = null;
		/*
		 The meshes were extracted from a sagittally-oriented volume 
		 with 25 micron voxel spacing and dimensions 528 x 320 x 456 
		 voxels.  The voxel ordering is 528 voxels rostral to caudal, 
		 320 voxels dorsal to ventral, and 456 voxels lateral left to 
		 right.  Bregma is at 213, 41, 223.
		 
		 There are 2 volumes with the above dimensions you can use if 
		 you need the volumetric atlas.  Data\Annotation25 contains 
		 8-bit unsigned int voxels whose values correspond to column 
		 7 (Structure ID 1) in the ontology.csv file.  The second 
		 volume contains the Nissl sections of the atlas 
		 reconstructed in 3d also as 8-bit unsigned ints.  On 
		 Windows, its location is at [User profile folder (e.g., 
		 C:\Document and Settings \ userid)]\Application Data\Allen 
		 Institute\Brain Explorer\Atlas\Atlas25.  On the Mac, its in 
		 ~/Library/Application Support/Brain Explorer/Atlas/Atlas25.
		 */
		File fi;
		try {
			fi = new File("etc/allen/Annotation25");
			
			
			if (fi == null || !fi.canRead()) {
				throw new OMTException("Can't open Annotation25! " + fi.toString(), null);
			}
			
			FileInputStream file = new FileInputStream(fi);
			
			//calculate offset
			/**
			 * Consider a 3X3X3 cube:
			 * 
			 * 0 1 2   9 10 11  18 19 20
			 * 3 4 5  12 13 14  21 22 23
			 * 6 7 8  15 16 17  24 25 26
			 * 
			 * Where the 0 1 2 layer is above 9 10 11 is above 18 19 20
			 * 
			 * the 0 1 2 direction is analogous to the rostralCaudal direction
			 * the 0 3 6 direction is analogous to the dorsalVentral direction
			 * the 0 9 18 direction is analgous to the lateralMedial direction
			 * 
			 * so, 6 is at the 0, 2, 0 position.  To calculate that: 0*1 + 2*3 + 0*9
			 * so, 12 is at the 0, 1, 1 position.  To calculate that: 0 + 1*3 + 1*9
			 * 
			 * Therefore the formula is 1*x_coord + Y_MAX*y_coord + Y_MAX*Z_MAX+z_coord; 
			 */
			int offset = rostralCaudal+320*dorsalVentral+456*320*lateralMedial;
			//read byte array for unsigned int at offset
			byte[] brainRegionIdByteArray = new byte[BitMath.sizeOf8BitUnsignedInt];
			FileChannel c = (FileChannel)file.getChannel();
			
			//c.read(byteBuffer, offset);
			file.read(brainRegionIdByteArray);
			//convert byte array to java int
			int brainRegionId = BitMath.convertByteArrayToInt(brainRegionIdByteArray);
			//return BrainRegion corresponding to int
			for (BrainRegion b : getBrainRegions()) {
				if (b.getRegionId() == brainRegionId) {
					return b;
				}
			}

			file.close();
		} catch (Exception e) {
			throw new OMTException("Error returning brain region!", e);
		}
		
		return br;
	}
	
	public List<BrainRegion> getBrainRegions() {
		return brainRegions;
	}

	public MyNode getBrainRegionTree() {
		
		MyNode root = new MyNode("Brain Regions", null);
		HashMap<String, MyNode> m = new HashMap<String, MyNode>();
		
		for (BrainRegion r : getBrainRegions()) {
			m.put(r.getAbbreviation(), new MyNode(r.getName(), r));
		}
		for (MyNode n : m.values()) {
			//assemble hierarchy
			if (((BrainRegion)n.value).getParent() != null) {
				MyNode parentNode = m.get(((BrainRegion)n.value).getParent().getAbbreviation());
				parentNode.children.add(n);
				
//				add top level nodes to root
				if (((BrainRegion)n.value).getParent().getAbbreviation().equals("Brain")) {
					root.children.add(n);
				}
			}
		}
		
		return root;
	}
	
	public void displayBasicAtlas() {
		this.getBrainRegion("OLF").select();
		this.getBrainRegion("HPF").select();
		this.getBrainRegion("STRd").select();
		this.getBrainRegion("STRv").select();
		this.getBrainRegion("LSX").select();
		this.getBrainRegion("sAMY").select();
		this.getBrainRegion("PAL").select();
		this.getBrainRegion("TH").select();
		this.getBrainRegion("HY").select();
		this.getBrainRegion("MBsen").select();
		this.getBrainRegion("MBmot").select();
		this.getBrainRegion("MBsta").select();
		this.getBrainRegion("P").select();
		this.getBrainRegion("MY").select();
		this.getBrainRegion("CB").select();
	}
	
	public void displayLeafAtlas() {
		List<BrainRegion> regions = getBrainRegionLeaves();
		for (BrainRegion reg : regions) {
			reg.select();
		}
	}
	
	public List<BrainRegion> getBrainRegionLeaves() {
		List<BrainRegion> leaves = new ArrayList<BrainRegion>();
		MyNode tree = getBrainRegionTree();
		Stack<MyNode> s = new Stack<MyNode>();
		s.add(tree);
		while (!s.isEmpty()) {
			MyNode i = s.pop();
			for (MyNode n : i.children) {
				if (!n.children.isEmpty()) {
					s.addAll(n.children);
				} else {
					leaves.add((BrainRegion)n.value);
				}
			}
		}
		return leaves;
	}
}
