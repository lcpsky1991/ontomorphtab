package edu.ucsd.ccdb.ontomorph2.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
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
	
	public Collection getGeometriesForTangibleView(TangibleView tv) {
		return (Collection)this.tangibleViewToGeometry.get(tv);
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
			if (g.getWorldBound().contains(gArg.getWorldTranslation())) {
				containers.add(getTangibleView(g));
			}
		}
		return containers;
	}
}
