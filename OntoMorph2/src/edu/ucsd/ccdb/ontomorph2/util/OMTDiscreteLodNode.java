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
	private DistanceSwitchModel _dsm = null;
	private int activeChildren = 0;
	
	public OMTDiscreteLodNode(DistanceSwitchModel dsm) {
		super("Discrete Lod Node", dsm);
		this._dsm = dsm;
	}
	
	public DistanceSwitchModel getSwitchModel() {
		return _dsm;
	}

	//order of adding DiscreteLod child matters
	public void addDiscreteLodNodeChild(List<Geometry> g, int minDistance, int maxDistance) {
		//set the min and max distance for this switch model
    	this.getSwitchModel().setModelDistance(activeChildren, minDistance, maxDistance);
    	//add the geoemetries to the currentGeometry list of lists
    	currentGeometries.add(activeChildren, g);
    	//attach the children at the 'activeChildren' index
    	Node n = new Node("child from " + minDistance + "camera distance to " + maxDistance +"camera distance");
    	for (Geometry ge: g) {
    		n.attachChild(ge);
    	}
    	this.attachChildAt(n, activeChildren);
    	//increment the 'activeChildren' index
    	activeChildren++;
	}
	
	public void addDiscreteLodNodeChild(Geometry g, int minDistance, int maxDistance) {
		List<Geometry> l = new ArrayList<Geometry>();
		l.add(g);
		this.addDiscreteLodNodeChild(l, minDistance, maxDistance);
	}
	
	public void addDiscreteLodNodeChild(Spatial s, List<Geometry> g, int minDistance, int maxDistance) {
//		set the min and max distance for this switch model
    	this.getSwitchModel().setModelDistance(activeChildren, minDistance, maxDistance);
    	//if geometries are present, add them to list
		if (g != null) {
			currentGeometries.add(activeChildren, g);
		}
		this.attachChildAt(s, activeChildren);
		activeChildren++;
	}
	
	public int attachChild(Spatial s) {
		if (s instanceof Geometry) {
			Geometry g = (Geometry)s;
			List<Geometry> l = new ArrayList<Geometry>();
			l.add(g);
			currentGeometries.add(l);
		}
		return super.attachChild(s);
	}
	
	public List<Geometry> getActiveGeometries() {
		if (this.getActiveChild() > -1) { 
			return currentGeometries.get(this.getActiveChild());
		} else if (currentGeometries.size() > 0) {
			return currentGeometries.get(0);
		} else {
			return new ArrayList<Geometry>();
		}
	}
	
	public List<Geometry> getAllGeometries() {
		List<Geometry> out = new ArrayList<Geometry>();
		for (List<Geometry> l : currentGeometries) {
			out.addAll(l);
		}
		return out;
	}
}
