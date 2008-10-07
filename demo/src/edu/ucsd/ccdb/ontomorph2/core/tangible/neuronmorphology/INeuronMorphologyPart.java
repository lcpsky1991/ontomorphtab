package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import java.awt.Color;
import java.math.BigInteger;

import edu.ucsd.ccdb.ontomorph2.core.tangible.ISelectable;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * An interface that refers to a part of a NeuronMorphology.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public interface INeuronMorphologyPart extends ISelectable{
	
	/**
	 * 
	 * @return a 3-dimensional float array defining the proximal point.  x=0, y=1, z=2.
	 */
	public float[] getProximalPoint();
	
	/**
	 * 
	 * @return a 3-dimensional float array defining the distal point.  x=0, y=1, z=2.
	 */
	public float[] getDistalPoint();
	
	/**
	 * 
	 * @return a float value representing the proximal radius
	 */
	public float getProximalRadius();
	
	/**
	 * 
	 * @return a float value representing the distal radius
	 */
	public float getDistalRadius();
	
	/**
	 * @return the 3D point in space of the base of this NeuronMorphologyPart
	 */
	public OMTVector getBase();
	
	/**
	 * @return the 3D point in space of the apex of this NeuronMorphologyPart
	 */
	public OMTVector getApex();
	
	/**
	 * @return the color of this NeuronMorphologyPart
	 */
	public Color getColor();
	
	/**
	 * @param c - the desired color of this NeuronMorphologyPart
	 */
	public void setColor(Color c);
	
	/**
	 * @return The NeuronMorphology that this NeuronMorphologyPart is part of
	 */
	public NeuronMorphology getParent();

	/**
	 * @return The radius at the base of this NeuronMorphologyPart
	 */
	public float getBaseRadius();

	/**
	 * @return The radius at the apex of this NeuronMorphologyPart
	 */
	public float getApexRadius();
	
	/**
	 * Get a count of the sub parts within this part.  If this is a simple segment, there will be
	 * no sub parts.  If this is a cable, there will be segments as sub parts.
	 * 
	 * @return the number of sub parts
	 */
	public int getSubPartCount();

	/**
	 * Get a sub parts associated with this part at location i.  If this is a simple segment, there will be
	 * no sub parts.  If this is a cable, there will be segments as sub parts.
	 * 
	 * @param i
	 * @return the sub part.  null if it is not found.
	 */
	public INeuronMorphologyPart getSubPart(int i);

	/**
	 * @return A unique ID within the parent NeuronMorphology for this Part.
	 */
	public BigInteger getId();
	
}
