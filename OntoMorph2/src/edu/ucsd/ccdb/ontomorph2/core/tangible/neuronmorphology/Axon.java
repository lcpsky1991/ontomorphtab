package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;


import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;


/**
 * Represents an axon of a NeuronMorphology.  This is treated as a separate object 
 * because axons are represented as objects that can be routed through multiple brain
 * structures. Axons behave similarly to curves, with an anchored control point at the neuron
 * 
 * @author Christopher Aprea (caprea)
 *
 */
public class Axon extends Curve3D 
{
	
	/* 
	 * although the cellbody is stored, access should not be made through the axon class because
	 * the soma should be acting on the axon, not the other way around
	 * soma HAS AN axon
	 */
	private NeuronMorphology parentCellBody = null;	 //see comment above
	
	
	/**
	 * Creates a new Axon representation based on a curve depicting it characteristic model and a soma on which it originates
	 * @param soma the cellbody from which this axon Originates
	 * @param controlGraph This is the characteristic curve/graph/polygon for the shape of this spline
	 * @throws Exception 
	 */
	public Axon(NeuronMorphology soma,  Curve3D controlGraph)  
	{
		super(controlGraph.getMorphMLCurve());	//start as an empty curve
		super.setControlPoint(0, soma.getPosition());	//force one of the control points to be at the neuron's cellbody
		parentCellBody = soma;
		
	}
	
	
	@Override
	protected void setControlPoint(int i, OMTVector pos)
	{
		//The begining control point is always at the cellbody
		//so do not allow the origin to be changed
		if ( i == 0)
		{
			System.out.println("Axon setControlPoint");
			return;
		}
		
		super.setControlPoint(i, pos);
	}
	
	
	@Override
	public PositionVector move(float dx, float dy, int mx, int my) 
	{
		//PositionVector change = super.move(dx, dy, mx, my); 
		CurveAnchorPoint origin = getAnchorPoints().get(0);
		origin.setPosition(parentCellBody.getPosition());
		origin.changed(CHANGED_MOVE);
		changed();
		System.out.println("Axon move");
		return null;
		
	}
	
}
