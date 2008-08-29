package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

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

import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Implements an ICable. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICable
 *
 */
public class MorphMLCableImpl extends Tangible implements ICable{
	
	/**************************************************
	 * Be careful with instances of this class.  The constructor has been made protected
	 * in order to avoid careless instantiation.  This class is basically
	 * just a proxy for the underling Cable representation that ought to be
	 * persisted in the database.  We want to throw away instances of this class 
	 * as quickly as possible, and simply use the setMorphMLCable method to
	 * get the data about the underlying Cable representation.
	 ***************************************************/

	Cable c = null;
	MorphMLNeuronMorphology parentCell = null;
	MorphMLSegmentImpl tempSegment = null;
	ArrayList<Tangible> containedItems = null;
	
	protected MorphMLCableImpl(MorphMLNeuronMorphology parentCell, Cable c) {
		this.parentCell = parentCell;
		this.c = c;
		this.containedItems = new ArrayList();
	}
	
	/**
	 * Get the underlying model of this cable.
	 * @return
	 */
	public Cable getMorphMLCable() {
		return c;
	}
	

	/**
	 * Sets the underlying model of this MorphMLCableImpl
	 * to be cable.
	 */
	public void setMorphMLCable(Cable cable) {
		this.c = cable;
	}
	
	public List<ISemanticThing> getSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		for (Object ob : getMorphMLCable().getGroup()) {
			String s = (String)ob;
			if ("dendrite_group".equals(s)) {
				l.add(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1211023249"));
			}
			if ("soma_group".equals(s)) {
				l.add(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1044911821"));
			} 
			if ("axon_group".equals(s)) {
				l.add(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1770195789"));
				//should be adding all these segGroups to the Axon class and treating them as a separate unit.
			}
			if ("apical_dendrite".equals(s)) {
				l.add(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao273773228"));
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
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(this.getSemanticThings());
		for (ISegment sg : this.getSegments()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}*/


	public MorphMLNeuronMorphology getParent() {
		return this.parentCell;
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
		MorphMLCableImpl copy = new MorphMLCableImpl(this.getParent(), this.c);
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
			if (t instanceof MorphMLCableImpl) {
				MorphMLCableImpl m = (MorphMLCableImpl)t;
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
			infoString = this.getTags().toString() + "\n";
			for (ISemanticThing s : this.getSemanticThings())
			{
				infoString += (s.toString() + "\n"); 
			}
			infoString += ((NeuronMorphology)this.getParent()).getSemanticThings();
			
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
	 * Says how many MorphMLSegmentImpls are associated with this MorphMLCableImpl
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
				tempSegment = new MorphMLSegmentImpl(this.getParent(), s);
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

	public void addTangible(Tangible t) {
		//should create an instance of this cable in the semantic repository
		//and assign a relation to the tangible being added
		//this should add that instance to this cable
		//that instance should have a contains relationship to 
		//the instance describing the tangible.
		
		t.setCoordinateSystem(this.getCoordinateSystem());
		t.setRelativePosition(this.getRelativePosition());
		this.containedItems.add(t);
	}

	public List<Tangible> getTangibles() {
		return this.containedItems;
	}
}
