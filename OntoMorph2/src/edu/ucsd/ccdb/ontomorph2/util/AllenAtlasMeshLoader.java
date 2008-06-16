package edu.ucsd.ccdb.ontomorph2.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.BatchMesh;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;

/**
 * A pure java implementation of a mesh reader that can read the format that 
 * the Allen Brain Institute has encoded meshes that are derived from an atlas
 * of the mouse brain.
 * 
 * Spec for the file type says that it is encoded the following way:
 * 
 * The mesh file format is simply a list of points and normals 
 * followed by a list of indices describing triangle strips. 
 * (http://en.wikipedia.org/wiki/Triangle_strip) 
 * The byte order is little endian. 
 * (http://en.wikipedia.org/wiki/Little_endian#Little-endian)

<code>
unsigned int numberOfPoints
array of points
{
     float normal[3]
     float coordinate[3]
}

unsigned int numberOfTriangleStrips
array of triangleStrips
{
    unsigned short numberOfPointsInStrip
    array of indices into point array
    {
       unsigned int index
    }
}
</code>
 * 
 * @author stephen
 *
 */
public class AllenAtlasMeshLoader {
	
	//a byte array of points, 
	//alternating 3 floats for normals, 
	//3 floats for coordinates
	byte[] points = null;
	
	int numOfPoints = 0;
	private List<Vector3f> normals;
	private List<Vector3f> vertices;
	
	List<TriangleBatch> triangleStrips = null;
	private FloatBuffer verticesBuff;
	private FloatBuffer normalsBuff;
	private ColorRGBA color = null;
	private FloatBuffer colorBuff = null;
	
	public BatchMesh loadByAbbreviation(String abbrev) {
		try {
			return load(new File(SceneImpl.allenMeshDir + abbrev + ".msh").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public AreaClodMesh loadClodMeshByAbbreviation(String abbrev) { 
		try {
			return loadClodMesh(new File(SceneImpl.allenMeshDir + abbrev + ".msh").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TriMesh loadTriMeshByAbbreviation(String abbrev) { 
		try {
			return loadTriMesh(new File(SceneImpl.allenMeshDir + abbrev + ".msh").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public TriMesh loadTriMesh(URL filePath) {
		List<TriangleBatch> triangleStrips = loadTriangles(filePath);
		TriMesh triMesh = new TriMesh();
		
		triMesh.setVertexBuffer(0, this.getVerts());
		triMesh.setNormalBuffer(0, this.getNormals());
		int[] triMeshIndices = new int[0];
		for (TriangleBatch triStrip : triangleStrips) {
			IntBuffer buf = triStrip.getIndexBuffer();
			
			//create temporary array that is the length of the current indices array
			//plus the length of the array of new indices to add
			//newIndicies is just for this iteration of the for loop
			int[] triStripIndices = new int[buf.limit()];
			buf.get(triStripIndices);
			
			// have to convert a tri strip representation of indices
			// to a tri mesh representation of indices
			int triMeshLength = (triStripIndices.length - 2)*3;
			
			//populate the tmp array from the current end of the indices array
			//tmp will get set to be the indicies array at the end of the loop
			int[] tmp = Arrays.copyOf(triMeshIndices, triMeshLength + triMeshIndices.length);
			
			
			int j = 0;
			for (int i = 0; i < triMeshLength;) {
				//Indicies in the triStripIndices are like: ABCDEFG
				//We need to change them to be ABC CBD CDE EDF EFG(note swapping of ordering)
				int offset = triMeshIndices.length;
				if (i < 3) {
					tmp[i+offset] = triStripIndices[j];
					i++;
					j++;
				} else { 
					if (i % 2 == 0) {
						//implements CDE and EFG given that CBD precedes CDE and EDF precedes EFG
						int twoAgo = tmp[i+offset-3];
						int oneAgo = tmp[i+offset-1];
						tmp[i+offset] = twoAgo;
						tmp[i+offset + 1] = oneAgo;
					} else if (i%2 == 1) {
						//implements CBD and EDF given that ABC precedes CBD and CDE precedes EDF
						int twoAgo = tmp[i+offset-2];
						int oneAgo = tmp[i+offset-1];
						tmp[i+offset] = oneAgo;
						tmp[i+offset + 1] = twoAgo;
					}
					tmp[i+offset + 2] = triStripIndices[j];
					i+=3;
					j++;
				}
			}
			triStripIndices = null;
			triMeshIndices = tmp;
		}
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(triMeshIndices);
		triMesh.setIndexBuffer(0, indexBuffer);
		return triMesh;
	}
	
	
	public AreaClodMesh loadClodMesh(URL filePath) {
		TriMesh t = loadTriMesh(filePath);
		
		AreaClodMesh acm = new AreaClodMesh(t.getName(), (TriMesh) t, null);
        acm.setLocalTranslation(t.getLocalTranslation());
        acm.setLocalRotation(t.getLocalRotation());
        acm.setModelBound(new BoundingSphere());
        acm.updateModelBound();
        // Allow 1/2 of a triangle in every pixel on the screen in
        // the bounds.
        acm.setTrisPerPixel(.1f);
        // Force a move of 2 units before updating the mesh geometry
        acm.setDistanceTolerance(1);
        // Give the clodMe sh node the material state that the
        // original had.
        //acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]); //Note: Deprecated
	    
        //acm.setRenderState(t.getRenderState(RenderState.RS_MATERIAL));
        acm.setVBOInfo(new VBOInfo(true));
        // Attach clod node.
        return acm;
	}
	
	public BatchMesh load(URL filePath) { 
		List<TriangleBatch> triangleStrips = loadTriangles(filePath);
		return new BatchMesh("object", (GeomBatch[])triangleStrips.toArray(new GeomBatch[1]));
	}
	
	/*
	 * This is the method that is doing the main algorithmic work of loading the
	 * mesh into memory.
	 */
	private List<TriangleBatch> loadTriangles(URL filePath) {
		triangleStrips = new ArrayList<TriangleBatch>();
		File fi;
		try {
			fi = new File(filePath.toURI());
			
			
			if (fi == null || !fi.canRead()) {
				throw new OMTException("Can't open ABEMesh! " + fi.toString(), null);
			}
			
			FileInputStream file = new FileInputStream(fi);
			
			//read number of points (stored as unsigned int)
			byte[] numberOfPoints = new byte[BitMath.sizeOfUnsignedInt];
			file.read(numberOfPoints);
			numOfPoints = BitMath.convertByteArrayToInt(numberOfPoints);
			
			//read points
			//3 floats for normals followed by 3 floats for coordinates per point
			points = new byte[BitMath.sizeOfFloat * 6 * numOfPoints];
			file.read(points);

			
			//read number of triangle strips
			byte[] numberOfTriangleStrips = new byte[BitMath.sizeOfUnsignedInt];
			file.read(numberOfTriangleStrips);
			
			this.processNormalsAndVertices();
			
			//read triangle strips
			int numberOfTriStrips = BitMath.convertByteArrayToInt(numberOfTriangleStrips);

			for (int i = 0; i < numberOfTriStrips; i++) {
				
				//read number of points in the strip
				byte[] numberOfPointsInStrip = new byte[BitMath.sizeOfUnsignedShort];
				file.read(numberOfPointsInStrip);
				
				//read point indicies
				int numberOfPtsInStrip = BitMath.convertByteArrayToInt(numberOfPointsInStrip);
				byte[] triangles = new byte[BitMath.sizeOfUnsignedInt * numberOfPtsInStrip];
				file.read(triangles);
				
				if (numberOfPtsInStrip > 0) {
					triangleStrips.add(createTriangleStrip(numberOfPtsInStrip, triangles));
				}
			}
		
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return triangleStrips;
	}

	
	
	private TriangleBatch createTriangleStrip(int numOfPointsInStrip, byte[] triangles) {
		int[] indices = new int[numOfPointsInStrip];
		int j = 0;
		for (int i = 0; i < triangles.length; i += BitMath.sizeOfUnsignedInt) {
			//assumes unsigned int length == 4
			byte[] array = {triangles[i], triangles[i+1], triangles[i+2], triangles[i+3]};
			indices[j++] = BitMath.convertByteArrayToInt(array);
		}
		 
	    TriangleBatch tbstrip = new TriangleBatch();
	    tbstrip.setMode(TriangleBatch.TRIANGLE_STRIP);
	    tbstrip.setVertexBuffer(getVerts());
	    tbstrip.setNormalBuffer(getNormals());
	    tbstrip.setColorBuffer(getColorBuffer());
	    //tbstrip.setTextureBuffer(texCoords, 0);
	    
	    tbstrip.setIndexBuffer(BufferUtils.createIntBuffer(indices));
	    
	    return tbstrip;
	}


	private void processNormalsAndVertices() {
		
		float[] floats = new float[numOfPoints*6];
		int j = 0;
		for (int i = 0; i < points.length; i += BitMath.sizeOfFloat) {
			floats[j++] = BitMath.convertByteArrayToFloat(points, i);
		}
		
		points = null; //make space
		
		normals = new ArrayList<Vector3f>();
		vertices = new ArrayList<Vector3f>();
				
		for (int k = 0; k < floats.length; k += 6) {
			
			normals.add(new Vector3f(floats[k], floats[k+1], floats[k+2]));
			vertices.add(new Vector3f(floats[k+3], floats[k+4], floats[k+5]));
		}
		floats = null;//make space
	}

	private FloatBuffer getVerts() {
		if (verticesBuff == null ) {
			verticesBuff = BufferUtils.createFloatBuffer((Vector3f[]) vertices.toArray( new Vector3f[1]));
			vertices = null; // make space
		}
		return verticesBuff;
	}
	
	private FloatBuffer getNormals() {
		if (normalsBuff == null ) {
			normalsBuff = BufferUtils.createFloatBuffer((Vector3f[]) normals.toArray( new Vector3f[1]));
			normals = null; // make space
		}
		return normalsBuff;
	}
	

	private FloatBuffer getColorBuffer() {
		if (this.color != null) {
			if (colorBuff == null) {
				ColorRGBA[] array = new ColorRGBA[numOfPoints];
				for (int i = 0; i < numOfPoints; i++) {
					array[i] = this.color.clone();
				}
				colorBuff = BufferUtils.createFloatBuffer(array);
			}
			return colorBuff;
		}
		return null;
	}

	/** 
	 * Sets the color that all subsequent meshes will be loaded to have
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = ColorUtil.convertColorToColorRGBA(color);
	}
	
}
