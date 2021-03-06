package edu.ucsd.ccdb.ontomorph2.view.scene;


import com.jme.bounding.BoundingBox;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.batch.GeomBatch;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;

public class CurveView extends TangibleView {

	Node anchors = null;
	//BezierCurve b = null;
	CatmullRomCurve b = null;
	
	public CurveView(Curve3D curve) 
	{
		super(curve);
		super.setName("Curve3DView");
		this.pickPriority = P_LOW;
		//update();
	}

	public void update() 
	{
		//remove everything at the beginning because we can't be guaranteed to have
		//the same BezierCurve from Curve since it gives us a copy every time.
		//(see Curve3D.asBezierCurve() comment.
		
		this.detachChild(this.b);
		
		Curve3D curve = ((Curve3D)getModel());
		
		//error checking
		//if there is no defined curve, no sense in drawing anything
		if ( null == curve ) 
		{
			System.out.println("The curve is Undefined: " + getName());
			return;
		}
		
		this.b = (CatmullRomCurve)curve.getCurve();
		
		
		this.attachChild(this.b);
		
		if (curve.getAnchorPointsVisibility())
		{
			renderAnchorPoints(curve);
		}
		else
		{
			this.detachChild(anchors);
			anchors = null;
		}
		
		//update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps
		this.registerGeometry(this.b);
		
		//no need to remove anchor points because this is done 
		//automatically already
		if (this.getModel().isSelected()) 
		{
			this.highlight();
		}
		else 
		{
			this.unhighlight();
		}
		
		this.b.setModelBound(new BoundingBox());
		this.b.updateModelBound();
		this.b.updateWorldBound();
		this.b.updateRenderState(); 
		this.b.updateGeometricState(5f, true);
		
		
		if (anchors != null) {
			this.anchors.updateModelBound();
			this.anchors.updateRenderState();
			this.anchors.updateGeometricState(5f, true);
		}
		
		
	    this.updateRenderState();
	    this.updateGeometricState(5f, true);
	    this.updateWorldBound();
	    this.updateModelBound();
	}

	private void renderAnchorPoints(Curve3D curve) 
	{
		//first delete all the anchor points
		this.detachChild(anchors);
		anchors = null;
		
		//redraw the points
		if (anchors == null) 
		{
			anchors = new Node();
			
			for (CurveAnchorPoint c : curve.getAnchorPoints()) 
			{
				anchors.attachChild(new CurveAnchorPointView(c));
			}
			this.attachChild(anchors);
		}
	}

	
	@Override
	public void doHighlight() 
	{
		this.b.setSolidColor(TangibleViewManager.highlightSelectedColor);
	}


	@Override
	public void doUnhighlight() 
	{
		this.b.setSolidColor(ColorUtil.convertColorToColorRGBA(this.getModel().getColor()));
		//this.b.setDefaultColor(ColorUtil.convertColorToColorRGBA(((Curve3D)getModel()).getHighlightedColor()));
	}
	
	
	/**
	 * @author caprea
	 * overwrites findPick in so that it can be picked (JME doesnt support picking curves)
     */
    public void findPick(Ray ray, PickResults results) 
    {
        if (getWorldBound() == null || !isCollidable) return;
       
    	//because JME does not have a findPick for Bezier curves, we do what the superclass does (Node)
    	//which is to loop through all the children and call pick results on the children
    	//but in addition, also check the curve
    
        super.findPick(ray, results);
        
        
        
        /*
		   * iterate steps over the entire curve
		   * for each step, get a distance between the ray and the step point
		   * if the distance is small enough then add this curve to the pickresults
		   */
		
		  double minDist = 0.5;	//arbitrary number TODO: should be a global variable that changes with zoom
		  Vector3f p = null;
		  double dist = 0;
		  
		  for (float t=0; t < 1; t = t + 0.001f)
		  {
			  p = b.getPoint(t);
			  dist = ray.distanceSquared(p); 
			  
			  if (dist < minDist)
			  {
				  for (int i=0; i < this.b.getBatchCount(); i++)
		        	{
		        		//results.addPick(ray, this.b.getBatch(i)); //good idea to look through all the batches 
					  	//and call their findPicks than to jump into adding the result 
		        		//ca: I dont know why there is multiple Batchs so I just add them all
		        		//ca: I expect it will usually be only one so getBatch(0) will work in most cases
		        		GeomBatch gb = this.b.getBatch(i); 
		                if ( gb.isEnabled() )
		                {
		                	results.addPick(ray, gb);
		                }
		        	}
			  }
		  }  
        
    }
    
	
}
