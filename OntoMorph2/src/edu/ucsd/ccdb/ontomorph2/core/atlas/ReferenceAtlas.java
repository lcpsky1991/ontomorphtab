package edu.ucsd.ccdb.ontomorph2.core.atlas;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;
import edu.ucsd.ccdb.ontomorph2.util.MyNode;


/**
 * Represents a singleton.
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
			FileReader fr = new FileReader(new File(SceneImpl.allenDir + "ontology.csv"));
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				String[] line = br.readLine().split(",");
				brainRegions.add(new BrainRegion(line[0], line[1], line[2], 
						new Color(Integer.parseInt(line[3]), Integer.parseInt(line[4]), Integer.parseInt(line[5]))));
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
}
