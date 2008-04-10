package edu.ucsd.ccdb.ontomorph2.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.core.ISegment;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;
import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;

public class Structure3DImpl extends Node implements IStructure3D {
	
	HashMap<ISegment, Geometry> segmentToGeom = new HashMap();
	
	public Structure3DImpl(IMorphology morph) {
		if (morph.getRenderOption().equals(IMorphology.RENDER_AS_CYLINDERS)) {
			this.setMorphMLNeuron(this.loadscene(morph), morph.getPosition(), morph.getRotation(), morph.getScale());
		} else if (morph.getRenderOption().equals(IMorphology.RENDER_AS_LINES)) {
			InputStream input = XSLTransformManager.getInstance().convertMorphMLToX3D(morph.getMorphML());
			this.setX3DNeuron(input, morph.getPosition(), morph.getRotation(), morph.getScale());
		}
	}

	public Node getNode() {
		return this;
	}
	
	public void setMorphMLNeuron(Node n, IPosition _position, IRotation _rotation, float _scale) {
		this.detachAllChildren();
		this.attachChild(n);
		
		if (_position != null) {
			this.setLocalTranslation(_position.asVector3f());
		}
		if (_rotation != null) {
			this.setLocalRotation(_rotation.asQuaternion());
		}
		if (_scale != 1) {
			this.setLocalScale(_scale);
		}
	}
	
	public void setX3DNeuron(InputStream input, IPosition _position, IRotation _rotation, float _scale) {
		try {
			X3DLoader converter = new X3DLoader();
			Spatial scene = converter.loadScene(input, null, null);
			
			this.detachAllChildren();
			this.attachChild(scene);
			
			if (_position != null) {
				this.setLocalTranslation(_position.asVector3f());
			}
			if (_rotation != null) {
				this.setLocalRotation(_rotation.asQuaternion());
			}
			if (_scale != 1) {
				this.setLocalScale(_scale);
			}
		} catch (Exception e) {
			//logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
	}
	
	public void setX3DNeuron(URL structureLoc) {
		try {
			this.setX3DNeuron(new FileInputStream(structureLoc.getFile()), null, null, 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//needs to be updated by a controller
	//does not unselect yet
	public void updateSelectedSegments(List<ISegment> segments) {
		for (ISegment seg : segments) {
			Geometry g = segmentToGeom.get(seg);
			g.setColorBuffer(1, BufferUtils.createFloatBuffer(ColorRGBA.red.getColorArray()));
		}
	}
	
	public Node loadscene(IMorphology morph) {
		Node sceneRoot = new Node();
		 /* 
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);	
        sceneRoot.setRenderState(lightState);
        
        for (ISegment seg : morph.getSegments()) {
						        	
        	Vector3f base = new Vector3f();
        	base.x = seg.getProximalPoint()[0];
        	base.y = seg.getProximalPoint()[1];
        	base.z = seg.getProximalPoint()[2];
        	Vector3f apex = new Vector3f();
        	apex.x = seg.getDistalPoint()[0];
        	apex.y = seg.getDistalPoint()[1];
        	apex.z = seg.getDistalPoint()[2];
        	
        	/*
        	 Sphere s1 = new Sphere("my sphere", 10, 10, 0.5f);
        	 s1.setLocalTranslation(base);
        	 s1.setRandomColors();
        	 Sphere s2 = new Sphere("my 2nd sphere", 10, 10, 0.5f);
        	 s2.setLocalTranslation(apex);
        	 s2.setRandomColors();
        	 sceneRoot.attachChild(s1);
        	 sceneRoot.attachChild(s2);
        	 */
        	
        	float scale = 10f;
        	
        	//calculate new center
        	float xCenter = (float)((apex.x - base.x)/2 + base.x);
        	float yCenter = (float)((apex.y - base.y)/2 + base.y);
        	float zCenter = (float)((apex.z - base.z)/2 + base.z);
        	
        	Vector3f center = new Vector3f(xCenter, yCenter, zCenter);
        	
        	Vector3f unit = new Vector3f();
        	unit = apex.subtract(base); // unit = apex - base;
        	float height = unit.length();
        	unit = unit.normalize();
        	
        	float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
        	
        	Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, null, null);
        	//sceneRoot.attachChild(l);						
        	
        	
        	Cylinder cyl = new Cylinder("neuron_cyl", 10, 10, 0.5f, height);
        	cyl.setRadius1(seg.getProximalRadius());
        	cyl.setRadius2(seg.getDistalRadius());
        	
        	Quaternion q = new Quaternion();
        	q.lookAt(unit, Vector3f.UNIT_Y);
        	
        	cyl.setLocalRotation(q);
        	
        	cyl.setLocalTranslation(center);
        	cyl.setRandomColors();
        	
        	//cyl.setLocalScale(scale);
        	segmentToGeom.put(seg, cyl);
        	sceneRoot.attachChild(cyl);

        }
		 return getClodNodeFromParent(sceneRoot);
		//return sceneRoot;
	}
	
	private Node getClodNodeFromParent(Node meshParent) {
	    // Create a node to hold my cLOD mesh objects
	    Node clodNode = new Node("Clod node");
	    // For each mesh in maggie
	    for (int i = 0; i < meshParent.getQuantity(); i++) {
	        // Create an AreaClodMesh for that mesh. Let it compute
	        // records automatically
	        AreaClodMesh acm = new AreaClodMesh("part" + i,
	                (TriMesh) meshParent.getChild(i), null);
	        acm.setLocalTranslation(meshParent.getChild(i).getLocalTranslation());
	        acm.setLocalRotation(meshParent.getChild(i).getLocalRotation());
	        acm.setModelBound(new BoundingSphere());
	        acm.updateModelBound();
	        // Allow 1/2 of a triangle in every pixel on the screen in
	        // the bounds.
	        acm.setTrisPerPixel(.5f);
	        // Force a move of 2 units before updating the mesh geometry
	        acm.setDistanceTolerance(2);
	        // Give the clodMe sh node the material state that the
	        // original had.
	        //acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]); //Note: Deprecated
	        acm.setRenderState(meshParent.getChild(i).getRenderState(RenderState.RS_MATERIAL));
	        // Attach clod node.
	        clodNode.attachChild(acm);
	    }
	    return clodNode;
	}

}
