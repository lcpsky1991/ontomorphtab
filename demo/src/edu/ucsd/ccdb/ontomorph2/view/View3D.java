package edu.ucsd.ccdb.ontomorph2.view;

import java.util.Set;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;

import edu.ucsd.ccdb.ontomorph2.core.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveView;
import edu.ucsd.ccdb.ontomorph2.view.scene.DataMeshView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SphereParticlesView;
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
	 * Detaches all children and then re-adds them fromt he paramter
	 * @param cells the {@link NeuronMorphology}s that will be added
	 */
	public void setCells(Set<NeuronMorphology> cells) {
		cellsNode.detachAllChildren();
		for(NeuronMorphology cell : cells) {
			NeuronMorphologyView cellView = (NeuronMorphologyView)TangibleViewManager.getInstance().getTangibleViewFor(cell);
			if (cellView == null) {
				//implicitly adds the new TangibleView to the TangibleViewManager
				cellView = new NeuronMorphologyView(cell);
			}

			Node n = cellView.getNode();
			cellsNode.attachChild(n);
		}
		this.updateNode(cellsNode);
	}

	/**
	 * added for {@link ContextMenu}s New Cell command, because it is more efficient to add ONE than remake all
	 * @param cell the ({@link NeuronMorphology}s to be added
	 * @deprecated
	 */
	public void addOneCell(NeuronMorphology cell)
	{
		NeuronMorphologyView cv = new NeuronMorphologyView(cell);
		Node n = cv.getNode();
		cellsNode.attachChild(n);
	}
	
	public void setCurves(Set<Curve3D> curves) {
		curvesNode.detachAllChildren();
		for(Curve3D curve : curves) {
			CurveView curveView = (CurveView)TangibleViewManager.getInstance().getTangibleViewFor(curve);
			if (curveView == null) {
				//implicitly adds the new TangibleView to the TangibleViewManager
				curveView = new CurveView(curve);
			}
			curvesNode.attachChild(curveView);
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
		System.out.println("add particles " + particles );
		//particlesNode.detachAllChildren();
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
	}
	
	
	public void showSceneMonitor() {
		//for more on this:
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=8159.msg64486#msg64486
		SceneMonitor.getMonitor().registerNode(View.getInstance().getMainViewRootNode(), "Root Node");
		SceneMonitor.getMonitor().showViewer(true);
	}
	
	public void updateNode(Node n) {
		n.updateGeometricState(0.5f, false);
		n.updateWorldBound();
		n.updateRenderState();
	}
}