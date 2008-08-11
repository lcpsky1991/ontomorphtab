package edu.ucsd.ccdb.ontomorph2.util;

import java.util.ArrayList;
import java.util.List;

import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.lod.DiscreteLodNode;

/**
 * Provides a node that has convenience methods for setting the distance switch model at the
 * same time that a child is being added.  Also allows you to get at the currently active
 * geometries easily.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OMTDiscreteLodNode extends DiscreteLodNode {


	//necessary for implementing discrete level of detail nodes
	private List<List<Geometry>> currentGeometries = new ArrayList<List<Geometry>>();
	private List<Node> nodeList = new ArrayList<Node>();
	private DistanceSwitchModel _dsm = null;
	private int activeChildren = 0;
	
	public OMTDiscreteLodNode(DistanceSwitchModel dsm) {
		super("Discrete Lod Node", dsm);
		this._dsm = dsm;
	}
	
	public DistanceSwitchModel getSwitchModel() {
		return _dsm;
	}
	
	/**
	 * This method requires that you use successive values of node index (0, 1, 2)
	 * @param nodeIndex
	 * @param g
	 * @param minDistance
	 * @param maxDistance
	 */
	public void addDiscreteLodNodeChild(int nodeIndex, List<Geometry> g, int minDistance, int maxDistance) {
//		set the min and max distance for this switch model
    	this.getSwitchModel().setModelDistance(nodeIndex, minDistance, maxDistance);
    	//add the geoemetries to the currentGeometry list of lists
    	if (currentGeometries.size() <= nodeIndex) {
    		currentGeometries.add(new ArrayList<Geometry>());
    	}
    	currentGeometries.get(nodeIndex).addAll(g);
    	
    	//attach the children at the 'activeChildren' index
    	if (nodeList.size() <= nodeIndex) {
    		nodeList.add(new Node("child from " + minDistance + "camera distance to " + maxDistance +" camera distance"));
    	}
    	
    	for (Geometry ge: g) {
    		nodeList.get(nodeIndex).attachChild(ge);
    	}
    	this.attachChildAt(nodeList.get(nodeIndex), nodeIndex);
	}
	

}
