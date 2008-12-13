package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;


/**
 * <p>This draws a grid of lines on the scene, and also
 * three main lines representing the axes in red, green and blue (red for X, green
 * for Y and blue for Z, so RGB colors correspond to XYZ axis).</p>
 * <p>The grid is a always a square, by default its extent is 40 (along each positive and
 * negative axis) and by default it is lying on the XZ plane (it can be rotated).</p>
 *
 */ 

public class Grid 
{
        /**
         * If drawBehind is false, the grid is tested with the z-buffer, which means that
         * only the visible parts of the grid will be seen and objects in front of the grid will
         * hide it.
         */
        protected static boolean drawBehind = false;
 
        /**
         * If drawAxis is true, Red, Green and Blue axis are drawn for the XYZ axis. Axis are
         * never rotated, so they are always world axis, but they are translated to the grid
         * center.
         */
        protected static boolean drawAxis = true;
 
        /**
         * Center of the grid.
         */
        protected static final Vector3f center = new Vector3f();
 
        /**
         * Space between grid lines.
         */
        protected static float spacing = 2.0f;
 
        /**
         * Draw a bold line every given number of lines.
         */
        protected static int marker = 5;
 
        /**
         * Grid rotation. This is an Euler rotation (X,Y,Z). Values are multiplied by PI
         * before being applied.
         */
        protected static final Vector3f rotatePi = new Vector3f();
 
        /**
         * Grid extent. The extent is applied towards the positive and also negative sides of the grid.
         */
        protected static float extent = 40.0f;
 
        /**
         * Creates the grid. The grid is returned as a Node.
         */
        public static Node buildGeometry() 
        {
 
                Node grid = new Node("grid");
                Node axis = new Node("axis");
                Node axisGrid = new Node("axisGrid");
 
                //Create Grid
                ArrayList<Vector3f> markerVertices = new ArrayList<Vector3f>();
                ArrayList<Vector3f> regularVertices = new ArrayList<Vector3f>();
                for (int i = 0; i * spacing <= extent; i++) {
 
                        if (i % marker > 0) {
                                // Normal line
                                regularVertices.add(new Vector3f(-extent, 0, i * spacing));
                                regularVertices.add(new Vector3f(extent, 0, i * spacing));
                                regularVertices.add(new Vector3f(-extent, 0, -i * spacing));
                                regularVertices.add(new Vector3f(extent, 0, -i * spacing));
                                regularVertices.add(new Vector3f(i * spacing, 0, -extent));
                                regularVertices.add(new Vector3f(i * spacing, 0, extent));
                                regularVertices.add(new Vector3f(-i * spacing, 0, -extent));
                                regularVertices.add(new Vector3f(-i * spacing, 0, extent));
                        } else {
                                // Marker line
                                markerVertices.add(new Vector3f(-extent, 0, i * spacing));
                                markerVertices.add(new Vector3f(extent, 0, i * spacing));
                                markerVertices.add(new Vector3f(-extent, 0, -i * spacing));
                                markerVertices.add(new Vector3f(extent, 0, -i * spacing));
                                if (i != 0) {
                                        markerVertices.add(new Vector3f(i * spacing, 0, -extent));
                                        markerVertices.add(new Vector3f(i * spacing, 0, extent));
                                        markerVertices.add(new Vector3f(-i * spacing, 0, -extent));
                                        markerVertices.add(new Vector3f(-i * spacing, 0, extent));
                                }
                        }
 
                }
 
                Geometry regularGrid = new Line("regularLine", regularVertices
                                .toArray(new Vector3f[] {}), null, null, null);
                
                
                regularGrid.setDefaultColor(ColorRGBA.darkGray);
                
                grid.attachChild(regularGrid);
                Geometry markerGrid = new Line("markerLine", markerVertices
                                .toArray(new Vector3f[] {}), null, null, null);
                regularGrid.setDefaultColor(ColorRGBA.lightGray);
                grid.attachChild(markerGrid);
 
                
                grid.getLocalRotation().fromAngles(FastMath.PI * rotatePi.x,
                                FastMath.PI * rotatePi.y, FastMath.PI * rotatePi.z);
 
                axisGrid.attachChild(grid);
 
                // Create Axis
 
                if (drawAxis) {
 
                        Vector3f xAxis = new Vector3f(extent, 0, 0); //red
                        Vector3f yAxis = new Vector3f(0, extent, 0); //green
                        Vector3f zAxis = new Vector3f(0, 0, extent); //blue
 
                        ColorRGBA[] red = new ColorRGBA[2];
                        red[0] = new ColorRGBA(ColorRGBA.red);
                        red[1] = new ColorRGBA(ColorRGBA.red);
 
                        ColorRGBA[] green = new ColorRGBA[2];
                        green[0] = new ColorRGBA(ColorRGBA.green);
                        green[1] = new ColorRGBA(ColorRGBA.green);
 
                        ColorRGBA[] blue = new ColorRGBA[2];
                        blue[0] = new ColorRGBA(ColorRGBA.blue);
                        blue[1] = new ColorRGBA(ColorRGBA.blue);
 
                        Line lx = new Line("xAxis", new Vector3f[] { xAxis.negate(), xAxis },
                                        null, red, null);
                        Line ly = new Line("yAxis", new Vector3f[] { yAxis.negate(), yAxis },
                                        null, green, null);
                        Line lz = new Line("zAxis", new Vector3f[] { zAxis.negate(), zAxis },
                                        null, blue, null);
 
                        lx.setModelBound(new BoundingBox()); // Important to set bounds to prevent some error
                        lx.updateModelBound();
                        ly.setModelBound(new BoundingBox()); // Important to set bounds to prevent some error
                        ly.updateModelBound();
                        lz.setModelBound(new BoundingBox()); // Important to set bounds to prevent some error
                        lz.updateModelBound();
 
                        axis.attachChild(lx);
                        axis.attachChild(ly);
                        axis.attachChild(lz);
 
                        axisGrid.attachChild(axis);
 
                }
 
                // RenderStates for the whole grid and axis
 
                TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
                                .createTextureState();
                //axisGrid.setTextureCombineMode(TextureCombineMode.Off);
                axisGrid.setTextureCombineMode(Camera.INTERSECTS_FRUSTUM);
                axisGrid.setRenderState(ts);
 
                ZBufferState zs = DisplaySystem.getDisplaySystem().getRenderer()
                                .createZBufferState();
                
                
                if (drawBehind)
                {
                	zs.setFunction(ZBufferState.CF_ALWAYS);
                }
                else
                {
                	zs.setFunction(ZBufferState.CF_LESS);
                }
                
                
                zs.setWritable(false);
                zs.setEnabled(true);
                axisGrid.setRenderState(zs);
 
                //axisGrid.setLightCombineMode(LightCombineMode.Off);
                axisGrid.setLightCombineMode(Camera.INTERSECTS_FRUSTUM);
                
                axisGrid.updateRenderState();
 
                axisGrid.getLocalTranslation().set(center);
                axisGrid.updateGeometricState(0, true);
 
                axisGrid.lock();
 
                return axisGrid;
        }
}