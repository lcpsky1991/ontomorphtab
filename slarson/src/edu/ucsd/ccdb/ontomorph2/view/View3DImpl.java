package edu.ucsd.ccdb.ontomorph2.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;
import edu.ucsd.ccdb.ontomorph2.core.scene.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.Surface;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.MeshViewImpl;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.VolumeViewImpl;

/**
 * Stands in for the Root Node of the 3D Scene Graph.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class View3DImpl extends Node{
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	private Node curvesNode = null;
	private Node surfacesNode = null;
	private Node meshesNode = null;
	private Node volumesNode = null;
	private Node atlasNode = null;
	private Set<NeuronMorphologyView> cells = null;
	private Set<VolumeViewImpl> volumes = null;
	
	public View3DImpl() {
		slidesNode = new Node();
		cellsNode = new Node();
		curvesNode = new Node();
		surfacesNode = new Node();
		meshesNode = new Node();
		volumesNode = new Node();
		atlasNode = new Node();
		
		slidesNode.setLightCombineMode(LightState.OFF);
		cellsNode.setLightCombineMode(LightState.OFF);
		curvesNode.setLightCombineMode(LightState.OFF);
		surfacesNode.setLightCombineMode(LightState.OFF);
		meshesNode.setLightCombineMode(LightState.OFF);
		volumesNode.setLightCombineMode(LightState.OFF);
		atlasNode.setLightCombineMode(LightState.COMBINE_CLOSEST);
		
		cells = new HashSet<NeuronMorphologyView>();
		volumes = new HashSet<VolumeViewImpl>();
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
		this.attachChild(curvesNode);
		this.attachChild(surfacesNode);
		this.attachChild(meshesNode);
		this.attachChild(volumesNode);
		this.attachChild(atlasNode);
	}
	
	public void setSlides(List<Slide> slides) {
		slidesNode.detachAllChildren();
		for(Slide slide : slides){
			slidesNode.attachChild(new SlideView(slide.getImageURL(),slide));
		}
	}
	
	public void setCells(Set<INeuronMorphology> cells) {
		cellsNode.detachAllChildren();
		for(INeuronMorphology cell : cells) {
			NeuronMorphologyView cellView = new NeuronMorphologyView(cell);
			Node n = cellView.getNode();
			cellsNode.attachChild(n);
			this.cells.add(cellView);
		}
	}

	public void setCurves(Set<Curve3D> curves) {
		curvesNode.detachAllChildren();
		for(Curve3D curve : curves) {
			curvesNode.attachChild(curve.asBezierCurve());
		}
		
	}

	public void setSurfaces(Set<Surface> surfaces) {
		surfacesNode.detachAllChildren();
		for(Surface surf : surfaces) {
			surfacesNode.attachChild((Surface)surf);
		}
	}

	public Set<NeuronMorphologyView> getCells() {
		return this.cells;
	}

	public void setMeshes(Set<DataMesh> meshes) {
		meshesNode.detachAllChildren();
		for(DataMesh mesh : meshes) {
			MeshViewImpl meshView = new MeshViewImpl(mesh);
			meshesNode.attachChild(meshView.getNode());
		}
	}

	public void setVolumes(Set<IVolume> volumes) {
		
		volumesNode.detachAllChildren();
		for (IVolume vol : volumes) {
			VolumeViewImpl volView = new VolumeViewImpl(vol);
			this.volumes.add(volView);
			volumesNode.attachChild(volView.getNode());
		}
	}
	
	public Set<VolumeViewImpl> getVolumes() {
		return volumes;
	}

	public void displayBrainRegion(BrainRegion br) {
		//atlasNode.attachChild(br.getMesh());
		TriMesh mesh = br.getTriMesh();
		mesh.setSolidColor(ColorRGBA.blue);
		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		VBOInfo nfo = new VBOInfo(true);
		//nfo.setVBOIndexEnabled(true);
		mesh.setVBOInfo(nfo);
		mesh.setCullMode(SceneElement.CULL_DYNAMIC);

		
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        
        atlasNode.setRenderState(lightState);
		
		atlasNode.attachChild(mesh);
		/*
        AlphaState as = ViewImpl.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	      as.setDstFunction(AlphaState.DB_ONE);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    atlasNode.setRenderState(as);
	    atlasNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	    */
	    atlasNode.updateRenderState();
	    atlasNode.updateGeometricState(5f, true);
	    
	}

	public void unDisplayBrainRegion(BrainRegion br) {
		atlasNode.detachChild(br.getClodMesh());
		br.destroyMesh();

	}
	
}
