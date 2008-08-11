package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;

/**
 * An interface that refers to a part of a neuron morphology
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
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
	
	public OMTVector getBase();
	
	public OMTVector getApex();
	
	public Color getColor();
	
	public void setColor(Color c);
	
	public NeuronMorphology getParent();

	public float getBaseRadius();

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

	public BigInteger getId();
}
