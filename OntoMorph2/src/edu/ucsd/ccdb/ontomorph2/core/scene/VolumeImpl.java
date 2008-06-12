/**
 * 
 */
package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.view.scene.INeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyViewImpl;
import edu.ucsd.ccdb.ontomorph2.view.scene.ViewImpl;

/**
 * @author stephen
 *
 */
public class VolumeImpl extends SceneObjectImpl implements IVolume, ISemanticsAware {

	int _shape = 0;
	Geometry _expShape = null;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	private boolean _visible = true;
	
	
	public VolumeImpl(Geometry g) {
		_expShape = g;
		_expShape.setModelBound(new BoundingBox());
		_expShape.updateModelBound();
		//setPosition(new PositionImpl(_expShape.getLocalTranslation()));
		//setRotation(new RotationImpl(_expShape.getLocalRotation()));
	}
	
	public VolumeImpl() {
		
	}
	
	public VolumeImpl(Box box, CoordinateSystem d) {
		this(box);
		this.setCoordinateSystem(d);
	}

	/* (non-Javadoc)
	 * @see src.edu.ucsd.ccdb.ontomorph2.core.scene.IVolume#getContainedSemanticalObjects()
	 */
	public Set<ISemanticsAware> getContainedSemanticalObjects() {
		Set<ISemanticsAware> l = new HashSet<ISemanticsAware>();
		
		for (INeuronMorphologyView n : ViewImpl.getInstance().getView3D().getCells()) {
			
			if (_expShape.getWorldBound().intersects(((NeuronMorphologyViewImpl)n).getWorldBound())) {
				l.add((ISemanticsAware)n.getMorphology());
			}
		}
		return l;
	}

	public boolean containsObject(Spatial s) {
		return (_expShape.getWorldBound().intersects(s.getWorldBound()));
	}

	public int getShape() {
		return _shape;
	}

	public void setShape(int shape) {
		_shape = shape;
	}


	public void setExplicitShape(Geometry g) {
		_expShape = g;
	}


	public Geometry getExplicitShape() {
		return _expShape;
	}


	public boolean isExplicit() {
		return (_expShape != null);
	}
	
	public List<ISemanticThing> getSemanticThings() {
		return semanticThings;
	}
	
	public List<ISemanticThing> getAllSemanticThings() {
		return getSemanticThings();
	}
	
	public void addSemanticThing(ISemanticThing thing) {
		this.semanticThings.add(thing);
		thing.addSemanticsAwareAssociation(this);
	}
	
	public void removeSemanticThing(ISemanticThing thing) {
		this.semanticThings.remove(thing);
		thing.removeSemanticsAwareAssociation(this);
	}

}
