package edu.ucsd.ccdb.ontomorph2.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

public class TangibleViewManager {

	static TangibleViewManager manager = null;
	public static final ColorRGBA highlightSelectedColor = ColorRGBA.yellow;

	HashMap<Geometry, TangibleView> geometryToTangibleView = null;
	HashMap<Tangible, TangibleView> tangibleToTangibleView = null;
	
	
	
	public static TangibleViewManager getInstance() {
		if (manager == null) {
			manager = new TangibleViewManager();
		}
		return manager;
	} 
	
	private TangibleViewManager() {
		geometryToTangibleView = new HashMap<Geometry,TangibleView>();
		tangibleToTangibleView = new HashMap<Tangible, TangibleView>();
		
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
}
