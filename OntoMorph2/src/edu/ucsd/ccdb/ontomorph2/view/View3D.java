package edu.ucsd.ccdb.ontomorph2.view;

import java.awt.Color;
import java.util.Set;

import org.morphml.metadata.schema.Curve;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;

import edu.ucsd.ccdb.ontomorph2.core.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.Axon;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Label3D;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;
import edu.ucsd.ccdb.ontomorph2.view.scene.AxonView;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveView;
import edu.ucsd.ccdb.ontomorph2.view.scene.DataMeshView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SphereParticlesView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;
import edu.ucsd.ccdb.ontomorph2.view.scene.VolumeView;

/**
 * Wraps the Root Node of the 3D Scene Graph.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class View3D extends Node{
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	private Node curvesNode = null;
	private Node surfacesNode = null;
	private Node meshesNode = null;
	private Node volumesNode = null;
	private Node particlesNode = null;
	private Node atlasNode = null;
	private Node axonsNode = null;
	private Node tipNode = null;
	
	public View3D() {
		super("View3D (Root)");
		slidesNode = new Node("Slides");
		cellsNode = new Node("Cells");
		curvesNode = new Node("Curves");
		surfacesNode = new Node("Surfaces");
		meshesNode = new Node("Meshes");
		volumesNode = new Node("Volumes");
		particlesNode = new Node("Particles");
		atlasNode = new Node("Atlas");
		axonsNode = new Node("Axons");
		tipNode = new BillboardNode("ToolTip");
		
		slidesNode.setLightCombineMode(LightState.OFF);
		cellsNode.setLightCombineMode(LightState.OFF);
		curvesNode.setLightCombineMode(LightState.OFF);
		surfacesNode.setLightCombineMode(LightState.OFF);
		meshesNode.setLightCombineMode(LightState.OFF);
		volumesNode.setLightCombineMode(LightState.OFF);
		//particlesNode.setLightCombineMode(LightState.OFF);
		//atlasNode.setLightCombineMode(LightState.COMBINE_CLOSEST);
		atlasNode.setLightCombineMode(LightState.COMBINE_FIRST);
		
		
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
		this.attachChild(curvesNode);
		this.attachChild(surfacesNode);
		this.attachChild(meshesNode);
		this.attachChild(volumesNode);
		this.attachChild(particlesNode);
		this.attachChild(atlasNode);
		cellsNode.attachChild(axonsNode);	//axons are a child of cells
	}
	
	public void setSlides(Set<Slide> slides) {
		slidesNode.detachAllChildren();
		for(Slide slide : slides){
			SlideView slideView = (SlideView)TangibleViewManager.getInstance().getTangibleViewFor(slide);
			
			if (slide.isVisible())
			{
				if (slideView == null) 
				{
					//implicitly adds the new TangibleView to the TangibleViewManager
					slideView = new SlideView(slide);
				}
				slidesNode.attachChild(slideView);	
			}
			else
			{
				//dont attach the slide to the scene if it's invisible
			}
		}
		this.updateNode(slidesNode);
	}
	
	/**
	 * Detaches all children and then re-adds them from the paramter
	 * @param cells the {@link NeuronMorphology}s that will be added
	 */
	public void setCells(Set<NeuronMorphology> cells) 
	{
		
		cellsNode.detachAllChildren();	//this will also detach all the axons
		
		
		//+++++ readd all the cells ++++++++++++++
		for(NeuronMorphology cell : cells)
		{
			NeuronMorphologyView cellView = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(cell);
			CurveView axonView = null;	
			Axon fiber = null;
			if (cellView == null) 
			{
				//implicitly adds the new TangibleView to the TangibleViewManager
				cellView = new NeuronMorphologyView(cell);
			}
			
			Node n = cellView.getNode();
			
			//cell created, create an axon for it too (Axons are actually curve3Ds)			
			fiber = cell.getAxon();
			if (fiber != null)
			{
				axonView = (CurveView)TangibleViewManager.getInstance().getTangibleViewFor(fiber);
				
				//the cell has an axon defined but it is not a view for it yet, create one
				if (null == axonView)
				{
					//axonView = new CurveView((Curve3D) fiber);	
				}
				
				//add the axon fiber  if there is one
				if ( axonView != null)
				{
					System.out.println(cell.getName() + " has an axon " + cell.getAxon().getName());
				}
			}

			cellsNode.attachChild(n);			
		}
		this.updateNode(cellsNode);
	}
	

	public void setCurves(Set<Curve3D> curves) 
	{
		curvesNode.detachAllChildren();
		for(Curve3D curve : curves) 
		{
			//axons are actually curve3Ds, but we keep them associated with cells and not on the curve node
			//if ( !(curve instanceof Axon) )
			{
				CurveView curveView = (CurveView)TangibleViewManager.getInstance().getTangibleViewFor(curve);
				if (curveView == null) 
				{
					//implicitly adds the new TangibleView to the TangibleViewManager
					curveView = new CurveView(curve);
				}
				
				curvesNode.attachChild(curveView);	
			}
		}
		
	}

	public void setSurfaces(Set<Surface> surfaces) {
		surfacesNode.detachAllChildren();
		for(Surface surf : surfaces) {
			surfacesNode.attachChild(surf.asBezierMesh());
		}
	}

	public void setMeshes(Set<DataMesh> meshes) {
		meshesNode.detachAllChildren();
		for(DataMesh mesh : meshes) {
			DataMeshView meshView = (DataMeshView)TangibleViewManager.getInstance().getTangibleViewFor(mesh);
			if (meshView == null) {
				//implicitly adds the new TangibleView to the TangibleViewManager
				meshView = new DataMeshView(mesh);
			}
			meshesNode.attachChild(meshView);
		}
		this.updateNode(meshesNode);
	}

	public void setVolumes(Set<Volume> volumes) {
		
		volumesNode.detachAllChildren();
		for (Volume vol : volumes) {
			VolumeView volView = new VolumeView(vol);
			volumesNode.attachChild(volView.getNode());
		}
	}
	
	public void addParticles(Set<SphereParticles> particles){
		//System.out.println("add particles " + particles );
		particlesNode.detachAllChildren();
		for(SphereParticles sp: particles){
			System.out.println("particles" + sp);
			SphereParticlesView spView = (SphereParticlesView)TangibleViewManager.getInstance().getTangibleViewFor(sp);
			if (spView == null) {
				System.out.println("null spView2");
				//implicitly adds the new TangibleView to the TangibleViewManager
				spView = new SphereParticlesView(sp);
			}
			particlesNode.attachChild(spView);
		}
	}
	public void addBrainRegions(Set<BrainRegion> regions) {
		for (BrainRegion br: regions) {
			BrainRegionView brView = (BrainRegionView)TangibleViewManager.getInstance().getTangibleViewFor(br);
			if (brView == null) {
				brView = new BrainRegionView(br);
				atlasNode.attachChild(brView);
			}
		}
		updateNode(atlasNode);
	}
	
	public void showToolTipFor(Tangible interest)
	{
	
		//remove the previous tooltip
		tipNode.removeFromParent(); //will unassign the tipNode from its previous parent
		
		//create a new tool tip 
		Label3D label = new Label3D(interest.getName());
		tipNode = label.getBillboard(1f);
		TangibleView tv = TangibleViewManager.getInstance().getTangibleViewFor(interest);
		
		//assign the tool tip to the appropriate node
		if ( tv != null)
		{
			tv.attachChild(tipNode);
			
			//this is a fix for tiny objects
			//TODO: make this more sophisticated by use of camera location
			
			float scale = tv.getLocalScale().lengthSquared();
			
			//if (scale < 1)
			{
				float calc = 0;
				calc = normer(scale); 
	
				System.out.println(scale + " to " + calc);
				tipNode.setLocalScale(calc);
			}
		}
	}
	
	private float normer(float s)
	{
		float calc = (float)s;
		float range = 10000000; 
		calc = calc * range;
		
		calc = FastMath.log((float)calc, 3000); 
		calc = FastMath.LERP(calc, 3000, 100);
		
		
		if (calc < 0)
		{
			return 1;
		}
		return calc;
	}
	public void showSceneMonitor() {
		//for more on this:
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=8159.msg64486#msg64486
		SceneMonitor.getMonitor().registerNode(View.getInstance().getMainViewRootNode(), "Root Node");
		SceneMonitor.getMonitor().showViewer(true);
	}
	
	public void updateNode(Node n) {
		n.updateModelBound();
		n.updateGeometricState(0.5f, false);
		n.updateRenderState();
	}
}
