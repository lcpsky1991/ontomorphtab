package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.morphml.metadata.schema.Point;
import org.morphml.metadata.schema.Properties;
import org.morphml.metadata.schema.Property;
import org.morphml.metadata.schema.impl.PropertiesImpl;
import org.morphml.metadata.schema.impl.PropertyImpl;
import org.morphml.morphml.schema.Cable;
import org.morphml.morphml.schema.Segment;
import org.morphml.morphml.schema.Cell.SegmentsType;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticProperty;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines a group of segments in a neuron morphology.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see INeuronMorphology
 *
 */
public class NeuronCable extends ContainerTangible implements INeuronMorphologyPart{
	
	/**************************************************
	 * Be careful with instances of this class.  The constructor has been made protected
	 * in order to avoid careless instantiation.  This class is basically
	 * just a proxy for the underling Cable representation that ought to be
	 * persisted in the database.  We want to throw away instances of this class 
	 * as quickly as possible, and simply use the setMorphMLCable method to
	 * get the data about the underlying Cable representation.
	 ***************************************************/

	Cable c = null;
	NeuronMorphology parentCell = null;
	NeuronSegment tempSegment = null;
	
	protected NeuronCable(NeuronMorphology parentCell, Cable c) {
		super(parentCell.toString() + " " + c.toString());
		this.parentCell = parentCell;
		this.c = c;
	}
	
	/**
	 * Get the underlying model of this cable.
	 * @return
	 */
	public Cable getMorphMLCable() {
		return c;
	}
	

	/**
	 * Sets the underlying model of this NeuronCable
	 * to be cable.
	 */
	public void setMorphMLCable(Cable cable) {
		this.c = cable;
	}
	
	public List<SemanticClass> getSemanticClasses() {
		List<SemanticClass> l = new ArrayList<SemanticClass>();
		SemanticRepository repo = null;
		
		repo = SemanticRepository.getAvailableInstance();
		
		for (Object ob : getMorphMLCable().getGroup()) {
			String s = (String)ob;
			if ("dendrite_group".equals(s)) {
				l.add(repo.getSemanticClass(SemanticClass.DENDRITE_CLASS));
			}
			if ("soma_group".equals(s)) {
				l.add(repo.getSemanticClass(SemanticClass.SOMA_CLASS));
			} 
			if ("axon_group".equals(s)) {
				l.add(repo.getSemanticClass(SemanticClass.AXON_CLASS));
				//should be adding all these segGroups to the Axon class and treating them as a separate unit.
			}
			if ("apical_dendrite".equals(s)) {
				l.add(repo.getSemanticClass(SemanticClass.APICAL_DENDRITE_CLASS));
			}
			
		}
		return l;
	}
	
	
	public Color getColor() {
		for (Object ob : getMorphMLCable().getGroup()) {
			String s = (String)ob;
			if (s.startsWith("Colour_")) {
				if (s.endsWith("Magenta")) {
					 return Color.magenta;
				} else if (s.endsWith("Green")) {
					return Color.green;
				} else if (s.endsWith("White")) {
					return Color.WHITE;
				} else if (s.endsWith("DarkGrey")) {
					return Color.darkGray;
				}
			}
		}
		return Color.PINK;
	}
		
	public BigInteger getId() {
		return getMorphMLCable().getId();
	}
	
	public List<String> getTags() {
		return this.getMorphMLCable().getGroup();
	}
	
	
	/*
	public List<SemanticThing> getAllSemanticThings() {
		List<SemanticThing> l = new ArrayList<SemanticThing>();
		l.addAll(this.getSemanticThings());
		for (ISegment sg : this.getSegments()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}*/


	public NeuronMorphology getParent() {
		return this.parentCell;
	}
	
	/**
	 * Returns the main semantic instance and fills in the has_part relationship to the parent cell
	 */
	public SemanticInstance getSemanticInstance() {
		SemanticInstance i = super.getSemanticInstance();
		SemanticInstance parentCell = this.getParent().getSemanticInstance();
		
		SemanticProperty hasPart = SemanticRepository.getAvailableInstance().getSemanticProperty(SemanticProperty.HAS_PART);
		parentCell.setPropertyValue(hasPart, i);
		
		return i;
	}
	
	public void select() 
	{
		
		//set the selection in the MorphML model
		//does this persist behind the scenes automatically to the db?
		Properties p = this.c.getProperties();
		Property pr = new PropertyImpl();
		pr.setTag("selected");
		pr.setValue("true");
		if (p != null) {
			p.getProperty().add(pr);
		} else {
			Properties p2 = new PropertiesImpl();
			p2.getProperty().add(pr);
			this.c.setProperties(p2);
		}
		
		//call select on a copy of this instance, rather than this instance itself.
		NeuronCable copy = new NeuronCable(this.getParent(), this.c);
		TangibleManager.getInstance().select(copy);
		copy.changed(CHANGED_SELECT);
		
		setInfoText();
	}
	
	public boolean isSelected() 
	{
		//determine isSelected on the basis of the underlying
		// cable instance, rather than on the selected list.
		Properties p = this.c.getProperties();
		if (p != null) {
			for (int i = 0; i < p.getProperty().size(); i++) {
				Property pr = (Property)p.getProperty().get(i);
				if ("selected".equals(pr.getTag())) {
					return true;
				}
			}
		}
		return false;
	}

	public void unselect() 
	{
		//have to find the tangible that shares the same underlying cable instance
		//and call unselect on it
		for (Tangible t : TangibleManager.getInstance().getSelected()) {
			if (t instanceof NeuronCable) {
				NeuronCable m = (NeuronCable)t;
				if (m.getMorphMLCable().equals(this.c)) {
					TangibleManager.getInstance().unselect(t);
					t.changed(CHANGED_UNSELECT);
				}
			}
		}
		
		//remove the selected tag from the underlying cable instance
		Properties p = this.c.getProperties();
		for (int i = 0; i < p.getProperty().size(); i++) {
			Property pr = (Property)p.getProperty().get(i);
			if ("selected".equals(pr.getTag())) {
				p.getProperty().remove(pr);
			}
		}
		
		changed(CHANGED_UNSELECT);
	}
	
	private void setInfoText() {
//		TODO: move this to the cell select
		String infoString = "";
		try
		{
			
			//this is getting called more times than it should
			infoString = "";//this.getTags().toString() + "\n";
			for (SemanticThing s : this.getSemanticClasses())
			{
				infoString += (s.toString() + "\n"); 
			}
			infoString += ((NeuronMorphology)this.getParent()).getSemanticClasses();
			
		}
		catch(Exception e)
		{
			infoString += "\n Exception: " + e.getMessage();
		}
		
		View.getInstance().getView2D().setInfoText(infoString);
	}
	
	/**
	 * Gets a vector corresponding to the bottom of this segment or the bottom
	 * of the bottom most segment, if multiple segments are used.
	 * @return
	 */
	public OMTVector getBase() {
		OMTVector base = new OMTVector(this.getProximalPoint()[0], 
				this.getProximalPoint()[1], this.getProximalPoint()[2]);

		return base;
	}
	
	/**
	 * Gets a vector corresponding to the top of this segment, or the top
	 * of the top most segment, if multiple segments are used.
	 * @return
	 */
	public OMTVector getApex() {
		OMTVector apex = new OMTVector(this.getDistalPoint()[0], 
				this.getDistalPoint()[1], this.getDistalPoint()[2]);

		return apex;
	}
	
	/**
	 * Returns the radius of the base of this cable.  Equivalent to 
	 * getDistalRadius()
	 */
	public float getBaseRadius() {
		return this.getDistalRadius();
	}

	/**
	 * Returns the radius of the apex of this cable.  Equivalent to
	 * getProximalRadius().
	 */
	public float getApexRadius() {
		return this.getProximalRadius();
	}

	
	
	public float[] getProximalPoint() {
		Point p = this.getLastSegmentPoint();
		float[] f = {(float)p.getX(), (float)p.getY(), (float)p.getZ()};
		return f;
	}

	public float[] getDistalPoint() {
		Point p = this.getFirstSegmentPoint();
		float[] f = {(float)p.getX(), (float)p.getY(), (float)p.getZ()};
		return f;
	}

	public float getProximalRadius() {
		return (float)(this.getLastSegmentPoint().getDiameter() / 2);
	}

	public float getDistalRadius() {
		return (float)(this.getFirstSegmentPoint().getDiameter() / 2);
	}

	/**
	 * Says how many MorphMLSegmentImpls are associated with this NeuronCable
	 */
	public int getSubPartCount() {
		BigInteger cableId = this.getId();
		List segments = getSegmentsList();
		int j = 0;
		for (int i = 0; i < segments.size(); i++) {
			Segment s = (Segment)segments.get(i);
			//count the number of segments that have this cableId
			if (s.getCable().equals(cableId)) {
				j++;
			}
		}
		return j;
	}

	/**
	 * Retrieves the segment at number i.  IMPORTANT NOTE: This method does not return a new reference 
	 * each time it is called.  Instead it uses the same instance of a segment each time and simply
	 * calls a set method to make it into the appropriate segment.  Do not add these segments
	 * to any collections or they will not work correctly.
	 * 
	 * This is in an effort to save memory due to the large number of ISegments that the
	 * system needs to deal with
	 */
	public INeuronMorphologyPart getSubPart(int x) {
		Segment s = getSpecificSegment(x);
		if (s != null) {
			
			if (this.tempSegment == null) {
				tempSegment = new NeuronSegment(this.getParent(), s);
			} else {
				tempSegment.setMorphMLSegment(s);
			}
			
			return tempSegment;
		}
		return null;
	}
	
	private Segment getSpecificSegment(int x) {
		BigInteger cableId = this.getId();
		List segments = getSegmentsList();
		int j = 0;
		for (int i = 0; i < segments.size(); i++) {
			Segment s = (Segment)segments.get(i);
			//return the first cable that matches this id and is x into the list
			if (s.getCable().equals(cableId)) {
				if (j++ == x) {
					return s;
				}
			}
		}
		return null;
	}
	
	private Point getFirstSegmentPoint() {
		BigInteger cableId = this.getId();
		List segments = getSegmentsList();
		for (int i = 0; i < segments.size(); i++) {
			Segment s = (Segment)segments.get(i);
			//find first proximal point that has the cableId of this cable
			if (s.getCable().equals(cableId)) {
				return s.getProximal();
			}
		}
		return null;
	}
	
	private Point getLastSegmentPoint() {
		BigInteger cableId = this.getId();
		List segments = getSegmentsList();
		Point tempPoint = null;
		for (int i = 0; i < segments.size(); i++) {
			Segment s = (Segment)segments.get(i);
			//find last distal point that has the cableId of this cable
			if (s.getCable().equals(cableId)) {
				//tempPoint is updated, left with last segment after for loop ends
				tempPoint = s.getDistal();
			}
		}
		return tempPoint;
	}
	
	private List getSegmentsList() {
		List segs = new ArrayList();
		List segments = this.getParent().getMorphMLCell().getSegments();
		for (int i = 0; i < segments.size(); i++) {
			segs.addAll(((SegmentsType)segments.get(i)).getSegment());
		}
		return segs;
	}

}
