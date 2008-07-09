package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neuroml.generated.Level2Cell;
import neuroml.generated.Point;
import neuroml.generated.Segment;
import neuroml.generated.Cell.Cables;
import neuroml.generated.Cell.Segments;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

/**
 * Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public abstract class NeuronMorphology extends Tangible{

	public static final String RENDER_AS_LINES = "lines";

	public static final String RENDER_AS_CYLINDERS = "cylinders";

	public static final String RENDER_AS_LOD = "LOD";

	public static final String RENDER_AS_LOD_2 = "LOD2";

	
	
	String _renderOption = RENDER_AS_LINES; //default render option
	ArrayList<ISegment> segmentList = null;
	Set<ISegment> selectedSegmentList = new HashSet<ISegment>();
	Set<ISegmentGroup> selectedSegmentGroupList = new HashSet<ISegmentGroup>();
	Level2Cell theCell;
	Set<ISegmentGroup> segmentGroupList = null;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	PositionVector lookAtPosition = null;
	
	Curve3D _curve = null;
	float _time = 0.0f;
	private Vector3f _upVector;
	
	/**
	 * Get the Curve that this NeuronMorphology has been associated with
	 * @return
	 */
	public Curve3D getCurve() {
		return _curve;
	}
	
	/**
	 * Retrieves the "time" along the curve that this INeuronMorphology is positioned at
	 * @return
	 */
	public float getTime() {
		return _time;
	}

	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getRenderOption()
	 */
	public String getRenderOption() {
		return _renderOption;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#setRenderOption(java.lang.String)
	 */
	public void setRenderOption(String renderOption) {
		if (RENDER_AS_LINES.equals(renderOption) || 
				RENDER_AS_CYLINDERS.equals(renderOption) ||
				RENDER_AS_LOD.equals(renderOption) ||
				RENDER_AS_LOD_2.equals(renderOption)) {
			_renderOption = renderOption;
		}
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegments()
	 */
	public abstract List<ISegment> getSegments();
	

	/**
	 * 
	 * @return the ISegmentGroups that are associated with this INeuronMorphology
	 */
	public abstract Set<ISegmentGroup> getSegmentGroups();
	

	/** 
	 * Convenience method to select a segment within this INeuronMorphology
	 * @param s - the segment to select
	 */
	public void selectSegment(ISegment s) {
		selectedSegmentList.add(s);
		changed();
	}
	
	/** 
	 * Convenience method to unselect a segment within this INeuronMorphology
	 * @param s - the segmen to unselect
	 */
	public void unselectSegment(ISegment s) {
		selectedSegmentList.remove(s);
		changed();
	}
	

	/**
	 * 
	 * @return all ISegment's that are currently selected
	 */
	public Set<ISegment> getSelectedSegments() {
		return selectedSegmentList;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#selectSegmentGroup(edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISegmentGroup)
	 */
	public void selectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.add(g);
		changed();
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#unselectSegmentGroup(edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISegmentGroup)
	 */
	public void unselectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.remove(g);
		changed();
	}

	/**
	 * 
	 * @return all ISegmentGroups that are currently selected
	 */
	public Set<ISegmentGroup> getSelectedSegmentGroups() {
		return selectedSegmentGroupList;
	}


	/**
	 * 
	 * @return true if this INeuronMorphology has ISegmentGroups that are selected, false otherwise
	 */
	public boolean hasSelectedSegmentGroups() {
		return getSelectedSegmentGroups().size() > 0;
	}
	
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(getSemanticThings());
		for (ISegmentGroup sg : this.getSegmentGroups()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}
	
	/*
	public void addSemanticClass(String classURI) {
		semanticThings.add(SemanticRepository.getInstance().getSemanticClass(classURI));
	}*/

	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c
	 *
	 */
	public void positionAlongCurve(Curve3D c, float time) {
		setRelativePosition(new PositionVector(((Curve3D)c).getPoint(time)));
	}

	/**
	 * Rotates the NeuronMorphology to aim its 'up' direction towards p
	 * @param p
	 */
	public void lookAt(PositionVector p) {
		lookAtPosition = p;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getLookAtPosition()
	 */
	public PositionVector getLookAtPosition() {
		return lookAtPosition;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getName()
	 */
	public abstract String getName();

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#setUpVector(com.jme.math.Vector3f)
	 */
	public void setUpVector(Vector3f vector3f) {
		_upVector = vector3f;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getUpVector()
	 */
	public Vector3f getUpVector() {
		if (_upVector != null) {
			return _upVector;
		}
		return Vector3f.UNIT_Y;
	}

}