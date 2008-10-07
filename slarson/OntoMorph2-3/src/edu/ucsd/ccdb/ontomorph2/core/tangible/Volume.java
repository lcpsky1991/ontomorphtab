/**
 * 
 */
package edu.ucsd.ccdb.ontomorph2.core.tangible;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;

/**
 * Defines a volume of space, identified by the boundaries of a box or a sphere.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Volume extends ContainerTangible{

	public static final int BOX_SHAPE = 0;
	public static final int SPHERE_SHAPE = 1;
	
	int _shape = 0;
	Geometry _expShape = null;
		
	public Volume(String name, Geometry g) {
		super(name);
		_expShape = g;
		_expShape.setModelBound(new BoundingBox());
		_expShape.updateModelBound();
		//setPosition(new PositionImpl(_expShape.getLocalTranslation()));
		//setRotation(new RotationImpl(_expShape.getLocalRotation()));
	}
	
	/**
	 * 
	 * @return Any ISemanticsAware objects that are geometrically inside this IVolume
	 */
	/*
	public Set<ISemanticsAware> getContainedSemanticalObjects() {
		Set<ISemanticsAware> l = new HashSet<ISemanticsAware>();
		
		for (NeuronMorphologyView n : View.getInstance().getView3D().getCells()) {
			
			if (_expShape.getWorldBound().intersects(((NeuronMorphologyView)n).getWorldBound())) {
				l.add((ISemanticsAware)n.getMorphology());
			}
		}
		return l;
	}*/

	public boolean containsObject(Spatial s) {
		return (_expShape.getWorldBound().intersects(s.getWorldBound()));
	}

	/**
	 * Get the shape of this IVolume
	 * @return A shape token defined in IVolume
	 */
	public int getShape() {
		return _shape;
	}
	
	
	/**
	 * Set the shape of this IVolume
	 * @param shape - defined in IVolume
	 */
	public void setShape(int shape) {
		_shape = shape;
	}

	/**
	 * Explicitly set the shape of this volume
	 * @param g - a geometrical shape for this volume
	 */
	public void setExplicitShape(Geometry g) {
		_expShape = g;
	}

	/**
	 * Get the explicit shape of this volume
	 * @return - the explicit shape
	 */
	public Geometry getExplicitShape() {
		return _expShape;
	}

	/**
	 * Determine if this shape is represented explicitly or not
	 * @return true if explicit, false if not
	 */
	public boolean isExplicit() {
		return (_expShape != null);
	}

}
