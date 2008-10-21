package edu.ucsd.ccdb.ontomorph2.view;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;

import com.jme.bounding.BoundingVolume;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;

import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

/**
 * Keeps track of TangibleViews.  Provides a mapping between the underlying Geometry of 3D objects,
 * the Tangible model that tracks its position and properties, and the TangibleView object.
 * 
 * Useful for identifying the TangibleView or Tangible for any geometry in the 3D world.
 * 
 * Is a singleton.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
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
	

	public void removeTangibleView(TangibleView tv)
	{
		tangibleToTangibleView.remove(tv.getModel());
		ArrayList<Geometry> plist = (ArrayList) tangibleViewToGeometry.remove(tv);
		for ( Geometry g : plist)
		{
			geometryToTangibleView.remove(g);	
		}
		
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
	 * Return a list of ContainerTangibles that contain the TangibleView tv
	 * @param tv
	 * @return
	 */
	public Set<ContainerTangible> getContainerTangibles(TangibleView tv) {
		//get the bounding volume of the TangibleView
		BoundingVolume bv = tv.getWorldBound();
		
		//get the geometries associated with the TangibleView
		Set<Geometry> allGeometriesForThisTangibleView = this.getGeometriesForTangibleView(tv);
		
		Set<ContainerTangible> containers = new HashSet<ContainerTangible>();
		//loop over all the geometries 
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
				
				//make sure this is a ContainerTangible
				if ((model instanceof ContainerTangible) == false ) { continue; }
				
				//for NeuronMorphologyViews, make the model to be the specific Cable that encloses tv
				if (tv2 instanceof NeuronMorphologyView) {
					NeuronMorphologyView nmv = (NeuronMorphologyView)tv2;
					BigInteger idOfSpecificCable = nmv.getCableIdFromGeometry(g);
					NeuronMorphology nm = (NeuronMorphology)model;
					containers.add((ContainerTangible)nm.getCable(idOfSpecificCable));
				}
				
				containers.add((ContainerTangible)model);
			}
		}
		return containers;
	}
}
