package edu.ucsd.ccdb.ontomorph2.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.view.scene.BrainRegionView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.MeshViewImpl;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.VolumeView;

/**
 * Stands in for the Root Node of the 3D Scene Graph.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class View3D extends Node{
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	private Node curvesNode = null;
	private Node surfacesNode = null;
	private Node meshesNode = null;
	private Node volumesNode = null;
	private Node atlasNode = null;
	
	public View3D() {
		super("View3D (Root)");
		slidesNode = new Node("Slides");
		cellsNode = new Node("Cells");
		curvesNode = new Node("Curves");
		surfacesNode = new Node("Surfaces");
		meshesNode = new Node("Meshes");
		volumesNode = new Node("Volumes");
		atlasNode = new Node("Atlas");
		
		slidesNode.setLightCombineMode(LightState.OFF);
		cellsNode.setLightCombineMode(LightState.OFF);
		curvesNode.setLightCombineMode(LightState.OFF);
		surfacesNode.setLightCombineMode(LightState.OFF);
		meshesNode.setLightCombineMode(LightState.OFF);
		volumesNode.setLightCombineMode(LightState.OFF);
		atlasNode.setLightCombineMode(LightState.COMBINE_CLOSEST);
		
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
		this.attachChild(curvesNode);
		this.attachChild(surfacesNode);
		this.attachChild(meshesNode);
		this.attachChild(volumesNode);
		this.attachChild(atlasNode);
	}
	
	public void setSlides(Set<Slide> slides) {
		slidesNode.detachAllChildren();
		for(Slide slide : slides){
			slidesNode.attachChild(new SlideView(slide.getImageURL(),slide));
		}
	}
	
	public void setCells(Set<NeuronMorphology> cells) {
		cellsNode.detachAllChildren();
		for(NeuronMorphology cell : cells) {
			NeuronMorphologyView cellView = new NeuronMorphologyView(cell);
			Node n = cellView.getNode();
			cellsNode.attachChild(n);
		}
	}

	public void setCurves(Set<Curve3D> curves) {
		curvesNode.detachAllChildren();
		for(Curve3D curve : curves) {
			curvesNode.attachChild(new CurveView(curve));
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
			MeshViewImpl meshView = new MeshViewImpl(mesh);
			meshesNode.attachChild(meshView.getNode());
		}
	}

	public void setVolumes(Set<Volume> volumes) {
		
		volumesNode.detachAllChildren();
		for (Volume vol : volumes) {
			VolumeView volView = new VolumeView(vol);
			volumesNode.attachChild(volView.getNode());
		}
	}
	
	public void addBrainRegions(Set<BrainRegion> regions) {
		
		for (BrainRegion br: regions) {
			BrainRegionView brView = new BrainRegionView(br, atlasNode);
		}
	}
	
	
	public void showSceneMonitor() {
		//for more on this:
		//http://www.jmonkeyengine.com/jmeforum/index.php?topic=8159.msg64486#msg64486
		SceneMonitor.getMonitor().registerNode(this, "Root Node");
		SceneMonitor.getMonitor().showViewer(true);
	}
	
	protected void addTangible(Tangible introduced)
	{
		System.out.println("See me!");
	}
}
