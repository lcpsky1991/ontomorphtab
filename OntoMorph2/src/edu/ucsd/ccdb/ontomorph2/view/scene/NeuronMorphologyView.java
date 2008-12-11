package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.awt.Color;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.curve.CurveController;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.geometryinstancing.GeometryBatchInstance;
import com.jme.scene.geometryinstancing.GeometryBatchInstanceAttributes;
import com.jme.scene.geometryinstancing.instance.GeometryBatchCreator;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.AreaUtils;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.INeuronMorphologyPart;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTDiscreteLodNode;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Visualizes a neuron morphology.  Describes the 3D structure 
 * of a biological object in a format that can be easily visualized in a 3D viewer (X3D?)
 * 
 * Need to implement selection handlers.  One click should select a segment, while a double-click
 * should select the whole cell
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 *
 */
public class NeuronMorphologyView extends TangibleView{
	
	private static final Logger logger = Logger.getLogger(AreaClodMesh.class
			.getName());
	
	private float distTolerance = 1f;
	
	private float lastDistance = 0f;
	
	int cableResolution = 1;
	
	NeuronMorphology currentMorph = null;
	
	CurveController _cc = null;
	
	Map<BigInteger, List<Geometry>> subPartMap = null;
	Map<Geometry, BigInteger> subPartReverseMap = null;
	
	public NeuronMorphologyView(NeuronMorphology morph) {
		super(morph);
		super.setName("Neuron Morphology View");
		currentMorph = morph;
		subPartMap = new HashMap<BigInteger, List<Geometry>>();
		subPartReverseMap = new HashMap<Geometry, BigInteger>();
		this.setMorphMLNeuron(this.loadscene(morph), morph);
		this.pickPriority = P_HIGH;
	}
	
	public Node getNode() {
		return this;
	}
	
	public void setMorphMLNeuron(Node n, NeuronMorphology morph) {
		this.detachAllChildren();
		this.attachChild(n);
		
		if (morph.getCurve() != null) {
			_cc = new CurveController(morph.getCurve().getCurve(), this);
			_cc.setAutoRotation(true);
			_cc.setUpVector(morph.getUpVector());
			_cc.update(morph.getTime());
		}
		if (morph.getPosition() != null) {
			this.setLocalTranslation(morph.getPosition().asVector3f());
		}
		if (morph.getRotation() != null) {
			this.setLocalRotation(morph.getRotation().asMatrix3f());
		}
		if (morph.getScale() != null) {
			this.setLocalScale(morph.getScale());
		}
		if (morph.getLookAtPosition() != null) {
			this.lookAt(morph.getLookAtPosition().asVector3f(), Vector3f.UNIT_X);
		}
		
		
	}
	
	private Node loadscene(NeuronMorphology morph) {
		long tick = Log.tick();
		Node sceneRoot = new Node("Neuron Morphology Root");
		/* 
		 * Check the LightState. If none has been passed, create a new one and
		 * attach it to the scene root
		 */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
		lightState.setEnabled(true);	
		sceneRoot.setRenderState(lightState);
		
		
		
		Node node = new Node("morphology");
		String renderOption = morph.getRenderOption();
		
		if (renderOption.equals(NeuronMorphology.RENDER_AS_LOD) || 
				renderOption.equals(NeuronMorphology.RENDER_AS_LOD_2)) {
			 node = new OMTDiscreteLodNode(new DistanceSwitchModel(10));
		}
		
		if (renderOption.equals(NeuronMorphology.RENDER_AS_DETAILED_BOXES)) {
			node.attachChild(this.getGeometryInstancedBoxes(morph));
		} else {
			
//			loop over all cables, and depending on the render option, 
			//attach the appropriate rendering to the node
			//map all the geometries to this NeuronMorphologyView
			for (int i = 0; i < morph.getCableCount(); i++) {
				INeuronMorphologyPart part = morph.getCable(i);
				
				renderParts(node, part, renderOption);
			}
		}
	
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		node.updateGeometricState(5f, false);
		node.updateRenderState();
		
		sceneRoot.attachChild(node);	
		sceneRoot.setModelBound(new BoundingBox());
		sceneRoot.updateModelBound();
		sceneRoot.updateGeometricState(5f, false);
		sceneRoot.updateRenderState();
		Log.tock("NeuronMorphology.loadScene() took", tick);
		return sceneRoot;
	}
	
	private void renderParts(Node node, INeuronMorphologyPart part, String renderOption) {
		if (renderOption.equals(NeuronMorphology.RENDER_AS_LINES)) {			
			Geometry g = this.getLine(part);
			this.registerGeometry(g);
			this.registerGeometryToCable(g, part);
			node.attachChild(g);
			
		} else if (renderOption.equals(NeuronMorphology.RENDER_AS_CYLINDERS)) {

			Geometry g = this.getCylinder(part);
			
			this.registerGeometry(g);
			this.registerGeometryToCable(g, part);
			node.attachChild(g);
			
		} else if (renderOption.equals(NeuronMorphology.RENDER_AS_DETAILED_BOXES)) {
			
			List<Geometry> subCylinders = this.getSubCylinders(part);
			for (Geometry g: subCylinders) {
				this.registerGeometries(subCylinders);
				this.registerGeometryToCable(g, part);
				node.attachChild(g);
			}
		}
		else if (renderOption.equals(NeuronMorphology.RENDER_AS_LOD)) {
			
			Geometry g = this.getCylinder(part);
			
			this.registerGeometry(g);
			this.registerGeometryToCable(g, part);
			List<Geometry> l = new ArrayList<Geometry>();
			l.add(g);
			((OMTDiscreteLodNode)node).addDiscreteLodNodeChild(0, l, 0, 1000);
			
			g = this.getLine(part);
			
			this.registerGeometry(g);
			this.registerGeometryToCable(g, part);
			l = new ArrayList<Geometry>();
			l.add(g);
			((OMTDiscreteLodNode)node).addDiscreteLodNodeChild(1, l, 1000, 10000);
			
		} else if (renderOption.equals(NeuronMorphology.RENDER_AS_LOD_2)){
			
			List<Geometry> subCylinders = this.getSubCylinders(part);
			for (Geometry g: subCylinders) {
				this.registerGeometries(subCylinders);
				this.registerGeometryToCable(g, part);
			}
			((OMTDiscreteLodNode)node).addDiscreteLodNodeChild(0, subCylinders, 0, 800);
			
			Geometry g = this.getLine(part);
			this.registerGeometry(g);
			this.registerGeometryToCable(g, part);
			List<Geometry> l = new ArrayList<Geometry>();
			l.add(g);
			((OMTDiscreteLodNode)node).addDiscreteLodNodeChild(1, l, 800, 10000);
			node.updateGeometricState(0.5f, false);
			node.updateRenderState();
		}
	}
	
	//maps and reverse maps a geometry with an INeuronMorphologyPart
	private void registerGeometryToCable(Geometry g, INeuronMorphologyPart part) {
		List<Geometry> l = this.subPartMap.get(part.getId());
		if (l == null) {
			l = new ArrayList<Geometry>();
			this.subPartMap.put(part.getId(), l);
		} 
		l.add(g);
		this.subPartReverseMap.put(g, part.getId());
	}
	
	public BigInteger getCableIdFromGeometry(Geometry parentGeom) {
		return this.subPartReverseMap.get(parentGeom);
	}
	
	protected void selectLevelOfDetail(Renderer r) {
		int target = chooseCableResolution(r);
		if (target == 0) {
			if (cableResolution == Integer.MAX_VALUE) {
				reload();
			}
		} else if (target == Integer.MAX_VALUE){
			if (cableResolution == 1) {
				reload();
			}
		}
	}
	
	
	public void update() 
	{
		super.update();
		updateModelBound();
		//reload(); //uncomment this and highlighting will work again
	}
	
	protected void reload()
	{
		super.update();
		this.detachAllChildren();
		this.attachChild(loadscene(currentMorph));
		updateModelBound();
	}
	
	public NeuronMorphology getMorphology() {
		return currentMorph;
	}
	
	/**
	 * This function is used during rendering to choose the correct target record for the
	 * AreaClodMesh acording to the information in the renderer.  This should not be called
	 * manually.  Instead, allow it to be called automatically during rendering.
	 * @param r The Renderer to use.
	 * @return the target record this AreaClodMesh will use to collapse vertexes.
	 */
	private int chooseCableResolution(Renderer r) {
		if (this.getWorldBound() == null) {
			logger.warning("NeuronMorphologyView found with no Bounds.");
			return 0;
		}
		
		float newDistance = getWorldBound().distanceTo(
				r.getCamera().getLocation());
		if (Math.abs(newDistance - lastDistance) <= distTolerance)
			return cableResolution; // we haven't moved relative to the model, send the old measurement back.
		if (lastDistance > newDistance && cableResolution == 1)
			return cableResolution; // we're already at the lowest setting and we just got closer to the model, no need to keep trying.
		if (lastDistance < newDistance && cableResolution == Integer.MAX_VALUE)
			return cableResolution; // we're already at the highest setting and we just got further from the model, no need to keep trying.
		
		lastDistance = newDistance;
		
		// estimate area of polygon via bounding volume
		float area = AreaUtils.calcScreenArea(getWorldBound(), lastDistance, r
				.getWidth());
		if (area > (640*480/2)) {
			cableResolution = Integer.MAX_VALUE;
		} else {
			cableResolution = 1;
		}
		return cableResolution;
	}
	
	
	
	public void doHighlight() {
		Stack<Spatial> queue = new Stack<Spatial>();
		
		queue.addAll(this.getChildren());
		while(!queue.isEmpty()) {
			Spatial s = queue.pop();
			if (s instanceof Geometry) {
				((Geometry)s).setSolidColor(ColorRGBA.yellow);
			} else if (s instanceof Node) {
				for (Spatial ns : ((Node)s).getChildren())
					queue.push(ns);
			}
		}
	}
	
	
	public void doUnhighlight() {
		Stack<Spatial> queue = new Stack<Spatial>();
		
		queue.addAll(this.getChildren());
		while(!queue.isEmpty()) 
		{
			Spatial s = queue.pop();
			if (s instanceof Geometry) 
			{
				
				//FIXME: remove try/catch by solving the bug
				//this try/catch block to resolve an issue introduced by the tooltip
				//an extra update is made by the tooltip which causes a nullPointer expcetion
				//this is NOT fixed, this is a graceful-degredation
				try 
				{
					BigInteger id = getCableIdFromGeometry((Geometry)s);
					ColorRGBA c = ColorUtil.convertColorToColorRGBA(getMorphology().getCable(id).getColor());
					((Geometry)s).setSolidColor(c);	
				}
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				
			}
			else if (s instanceof Node) 
			{
				for (Spatial ns : ((Node)s).getChildren())
					queue.push(ns);
			}
		}
	}
	
	private Vector3f getSegmentCenter(INeuronMorphologyPart part) {
		Vector3f base = part.getBase();
		Vector3f apex = part.getApex();
		
//		calculate new center
		float xCenter = (float)((apex.x - base.x)/2 + base.x);
		float yCenter = (float)((apex.y - base.y)/2 + base.y);
		float zCenter = (float)((apex.z - base.z)/2 + base.z);
		
		return new Vector3f(xCenter, yCenter, zCenter);
	}
	
	private Vector3f getSegmentDirection(INeuronMorphologyPart part) {
		Vector3f base = part.getBase();
		Vector3f apex = part.getApex();
		
		Vector3f unit = new Vector3f();
		unit = apex.subtract(base); // unit = apex - base;

		return unit;
	}
	
	//returns a cylinder of the position, size, and radius of
	//the given INeuronMorphologyPart
	private Cylinder getCylinder(INeuronMorphologyPart part) {
		
		Vector3f center = this.getSegmentCenter(part);
		
		
		Vector3f direction = getSegmentDirection(part);
		float height = direction.length();
		Vector3f unit = direction.normalize();
		
		Cylinder cyl = new Cylinder("neuron_cyl", 2, 4, 0.5f, height);
		cyl.setRadius1(part.getBaseRadius());
		cyl.setRadius2(part.getApexRadius());
		//cyl.setColorBuffer(2, colorBuffer);
		
		
		AlphaState as = View.getInstance().getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
		cyl.setRenderState(as);
		cyl.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		
		
		Quaternion q = new Quaternion();
		q.lookAt(unit, Vector3f.UNIT_Y);

		cyl.setLocalRotation(q);
		
		cyl.setLocalTranslation(center);
		Color c = part.getColor();
		cyl.setSolidColor(ColorUtil.convertColorToColorRGBA(c));
		//cyl.updateRenderState();
		cyl.updateGeometricState(0.5f, false);
		return cyl;
	}
	
	
	//Render this INeuronMorphologyPart as a series of cylinders corresponding to the underlying
	//individual segments of this segment group
	private List<Geometry> getSubCylinders(INeuronMorphologyPart part) {
		List<Geometry> l = new ArrayList<Geometry>();
		
		for (int i = 0; i < part.getSubPartCount(); i++) {
			INeuronMorphologyPart seg = (INeuronMorphologyPart)part.getSubPart(i);
			Cylinder c = this.getCylinder(seg);
			l.add(c);
		}	
		return l;
	}
	
	
	// render this INeuronMorphologyPart as a Line
	private Line getLine(INeuronMorphologyPart part) {
		
		Vector3f base = part.getBase();
		Vector3f apex = part.getApex();
		
		float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
		
		//Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, colorBuffer, null);
		Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, null, null);


		l.setSolidColor(ColorUtil.convertColorToColorRGBA(part.getColor()));
		l.updateRenderState();
		l.updateGeometricState(5f, false);
		
		return l;
		
	}
	
	/**
     * Make a neuron tree a Geometry Batch to make it more efficient
     * 
     * experimental code.. doesn't work correctly yet.
     */
    private TriMesh getGeometryBatchedCylinders(NeuronMorphology morph) {

        // The batch geometry creator
        GeometryBatchCreator geometryBatchCreator = new GeometryBatchCreator();

        // Loop that creates instances for each cable.. could loop over all segments too.
        for (int i = 0; i < morph.getCableCount(); i++) {
			INeuronMorphologyPart part = morph.getCable(i);
			GeometryBatchInstanceAttributes attributes = new GeometryBatchInstanceAttributes(new Vector3f(), 
					new Vector3f(), new Vector3f(), ColorRGBA.white);
			
                // Cylinder instance (batch and attributes)
                GeometryBatchInstance instance =
                        new GeometryBatchInstance(this.getCylinder(part).getBatch(0), attributes);

                // Add the instance
                geometryBatchCreator.addInstance(instance);
        }

        // Create a TriMesh
        TriMesh mesh = new TriMesh();
        TriangleBatch batch = mesh.getBatch(0);
        batch.setModelBound(new BoundingBox());

        // Create the batch's buffers
        batch.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices()), 0);
        batch.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));

        // Commit the instances to the mesh batch
        geometryBatchCreator.commit(batch);

        // Return the mesh
        return mesh;
    }
	
	/**
     * Use geometry instancing to create a mesh containing a number of cylinder
     * instances
     * 
     * experimental code.. doesn't work correctly yet.
     */
    private TriMesh getGeometryInstancedCylinders(NeuronMorphology morph) {
        // A cylinder that will be instantiated
    	// currently these values are pretty random starting points.
    	Cylinder cyl = new Cylinder("neuron_cyl", 2, 4, 0.5f, 10f);

        // The batch geometry creator
        GeometryBatchCreator geometryBatchCreator = new GeometryBatchCreator();

        // Loop that creates instances for each cable.. could loop over all segments too.
        for (int i = 0; i < morph.getCableCount(); i++) {
			INeuronMorphologyPart part = morph.getCable(i);
                // Box instance attributes
                GeometryBatchInstanceAttributes attributes =
                        new GeometryBatchInstanceAttributes(
                        		// Translation
                                this.getSegmentCenter(part),
                                // Scale
                                /* scale doesn't really accomplish we want since 
                                 * cylinders have radius1 and radius2 ... we want to tune these
                                 * separately.  I posted about this here:
                                 * http://www.jmonkeyengine.com/jmeforum/index.php?topic=8799.msg68203
                                 */ 
                                new Vector3f(1,1,1),  
                               
                                // Rotation
                                this.getSegmentDirection(part).normalize(),
                                /* This doesn't seem to be working correctly either.*/
                                //Color
                                ColorUtil.convertColorToColorRGBA(part.getColor()));    

                // Cylinder instance (batch and attributes)
                GeometryBatchInstance instance =
                        new GeometryBatchInstance(cyl.getBatch(0), attributes);

                // Add the instance
                geometryBatchCreator.addInstance(instance);
        }

        // Create a TriMesh
        TriMesh mesh = new TriMesh();
        TriangleBatch batch = mesh.getBatch(0);
        batch.setModelBound(new BoundingBox());

        // Create the batch's buffers
        batch.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices()), 0);
        batch.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));

        // Commit the instances to the mesh batch
        geometryBatchCreator.commit(batch);

        // Return the mesh
        return mesh;
    }
    
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     */
    private TriMesh getGeometryInstancedBoxes(NeuronMorphology morph) {
        // A box that will be instantiated
        Box box = new Box("Box", new Vector3f(-0.5f, -0.5f, -0.5f),
                          new Vector3f(0.5f, 0.5f, 0.5f));

        // The batch geometry creator
        GeometryBatchCreator geometryBatchCreator = new GeometryBatchCreator();

        // Loop that creates instances for each cable.. could loop over all segments too.
        for (int i = 0; i < morph.getCableCount(); i++) {
        	INeuronMorphologyPart part = morph.getCable(i);
        	// Box instance attributes
        	GeometryBatchInstanceAttributes attributes =
        		new GeometryBatchInstanceAttributes(
        				this.getSegmentCenter(part),
        				// Translation
        				new Vector3f(1, this.getSegmentCenter(part).length(), 1),
        				// Scale
        				this.getSegmentDirection(part).normalize(), /**THIS ROTATION IS WRONG>> HOW DO WE FIX IT??!**/
        				// Rotation
        				ColorUtil.convertColorToColorRGBA(part.getColor()));    // Color
        	
        	// Box instance (batch and attributes)
        	GeometryBatchInstance instance =
        		new GeometryBatchInstance(box.getBatch(0), attributes);
        	
        	// Add the instance
        	geometryBatchCreator.addInstance(instance);
        }
 

        // Create a TriMesh
        TriMesh mesh = new TriMesh();
        TriangleBatch batch = mesh.getBatch(0);
        batch.setModelBound(new BoundingBox());

        // Create the batch's buffers
        batch.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices()), 0);
        batch.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));

        
        // Commit the instances to the mesh batch
        geometryBatchCreator.commit(batch);

        // Return the mesh
        return mesh;
    }

    protected List<Geometry> getGeometryFromCableId(BigInteger id) {
    	return this.subPartMap.get(id);
    }
    
    public void highlightCable(BigInteger id) {
    	for (Geometry g : this.getGeometryFromCableId(id)) {
    		g.setSolidColor(ColorRGBA.yellow);
    		g.updateGeometricState(0.5f, false);
    	}
    }

    public void unhighlightCable(BigInteger id) {
    	ColorRGBA color = ColorUtil.convertColorToColorRGBA(this.getMorphology().getCable(id).getColor());
    	for (Geometry g : this.getGeometryFromCableId(id)) {
    		g.setSolidColor(color);
    		g.updateGeometricState(0.5f, false);
    	}
    }
	
}
