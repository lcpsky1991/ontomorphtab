package edu.ucsd.ccdb.ontomorph2.view;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;

import com.jme.bounding.BoundingVolume;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

public class TangibleViewManager {

	static TangibleViewManager manager = null;
	public static final ColorRGBA highlightSelectedColor = ColorRGBA.yellow;

	HashMap<Geometry, TangibleView> geometryToTangibleView = null;
	HashMap<Tangible, TangibleView> tangibleToTangibleView = null;
	MultiHashMap tangibleViewToGeometry = null;
	
	
	public static TangibleViewManager getInstance() {
		if (manager == null) {
			manager = new TangibleViewManager();
		}
		return manager;
	} 
	
	private TangibleViewManager() {
		geometryToTangibleView = new HashMap<Geometry,TangibleView>();
		tangibleToTangibleView = new HashMap<Tangible, TangibleView>();
		tangibleViewToGeometry = new MultiHashMap();
	}
	
	/**
	 * Add a tangibleView to this manager
	 * @param tv
	 */
	public void addTangibleView(TangibleView tv) {
		tangibleToTangibleView.put(tv.getModel(), tv);
	}
	

	/**
	 * Returns the TangibleView that has this Tangible as a model if one exists.
	 * @param t - the Tangible to search for.
	 * @return - null if no tangible matches this model, otherwise, the corresponding TangibleView
	 */
	public TangibleView getTangibleViewFor(Tangible t) {
		return tangibleToTangibleView.get(t);
	}
	

	/**
	 * Add a Geometry to a map that keeps track of which TangibleView it is associated with.
	 * Used for picking.
	 * 
	 * @param gb
	 * @param view
	 */
	public void addToGeometryTangibleViewMap(Geometry gb, TangibleView view) {
		this.geometryToTangibleView.put(gb,view);
		this.tangibleViewToGeometry.put(view,gb);
	}
	
	/**
	 * Return the TangibleView that has been previously associated with a Geometry
	 * @param gb
	 * @return
	 */
	public TangibleView getTangibleView(Geometry gb) {
		TangibleView tv = null;
		tv = this.geometryToTangibleView.get(gb);
		return tv;
	}
	
	/**
	 * Return all the geometries known to the TangibleViewManager
	 * @return
	 */
	public Set<Geometry> getAllGeometries() {
		return this.geometryToTangibleView.keySet();
	}
	
	public Set<Geometry> getGeometriesForTangibleView(TangibleView tv) {
		Set<Geometry> geometries = new HashSet<Geometry>();
		Collection c = (Collection)this.tangibleViewToGeometry.get(tv);
		if (c == null) {
			return geometries;
		}
		for (Iterator it = c.iterator(); it.hasNext();) {
			geometries.add((Geometry)it.next());
		}
		return geometries;
	}
	
	/**
	 * Returns the set of TangibleViews that contain a Geometry gArg
	 * Note this is not extremely efficient because it loops over all known geometries
	 * Could be improved by doing a lookup in the local region of gArg
	 * 
	 * @see com.jme.bounding.BoundingVolume#contains(com.jme.math.Vector3f)
	 */
	public Set<TangibleView> getContainers(Geometry gArg) {
		Set<TangibleView> containers = new HashSet<TangibleView>();
		for (Geometry g : this.getAllGeometries()) {
			//skip any geometries that are also part of the same TangibleView
			//since parts of the same TangibleView may technically contain
			//other parts
			if (getTangibleView(g).equals(getTangibleView(gArg)))
				continue;
			
			if (g.getWorldBound().contains(gArg.getWorldTranslation())) {
				containers.add(getTangibleView(g));
			}
		}
		return containers;
	}
	
	/**
	 * Return a list of TangibleViews that contain the bounding volume bv belonging to the
	 * TangibleView tv
	 * @param tv
	 * @param bv
	 * @return
	 */
	public Set<Tangible> getContainerTangibles(TangibleView tv) {
		BoundingVolume bv = tv.getWorldBound();
		Set<Geometry> allGeometriesForThisTangibleView = this.getGeometriesForTangibleView(tv);
		Set<Tangible> containers = new HashSet<Tangible>();
		for (Geometry g : this.getAllGeometries()) {
			//skip any geometries that are part of the same TangibleView
			if (allGeometriesForThisTangibleView.contains(g)) {
				continue;
			}
			//find out if the current geometry contains the bounding volume bv
			if (g != null && g.getWorldBound().contains(bv.getCenter()) && g.getWorldBound().intersects(bv)) {
				//for any tangible view containers, look up their corresponding
				//tangibles
				TangibleView tv2 = getTangibleView(g);
				Tangible model = tv2.getModel();
				
				
				//for NeuronMorphologyViews, make the model to be the specific Cable that encloses tv
				if (tv2 instanceof NeuronMorphologyView) {
					NeuronMorphologyView nmv = (NeuronMorphologyView)tv2;
					BigInteger idOfSpecificCable = nmv.getCableIdFromGeometry(g);
					NeuronMorphology nm = (NeuronMorphology)model;
					containers.add((Tangible)nm.getCable(idOfSpecificCable));
				}
				
				containers.add(model);
			}
		}
		return containers;
	}
}
