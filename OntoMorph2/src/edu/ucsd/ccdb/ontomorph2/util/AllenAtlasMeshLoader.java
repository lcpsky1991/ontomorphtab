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
import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BatchMesh;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.SceneImpl;

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

	int sizeOfUnsignedInt = 4;
	int sizeOfFloat = 4;
	int sizeOfUnsignedShort = 2;
	
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
	
	public TriMesh loadTriMesh(URL filePath) {
		List<TriangleBatch> triangleStrips = loadTriangles(filePath);
		TriMesh triMesh = new TriMesh();
		int i = 0;
		for (TriangleBatch triStrip : triangleStrips) {
			triMesh.addBatch(triStrip);
			triMesh.setVertexBuffer(i, triStrip.getVertexBuffer());
			triMesh.setIndexBuffer(i, triStrip.getIndexBuffer());
			triMesh.setNormalBuffer(i, triStrip.getNormalBuffer());
			i++;
		}
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
        acm.setTrisPerPixel(.5f);
        // Force a move of 2 units before updating the mesh geometry
        acm.setDistanceTolerance(2);
        // Give the clodMe sh node the material state that the
        // original had.
        //acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]); //Note: Deprecated
        acm.setRenderState(t.getRenderState(RenderState.RS_MATERIAL));
        // Attach clod node.
        return acm;
	}
	
	public BatchMesh load(URL filePath) { 
		List<TriangleBatch> triangleStrips = loadTriangles(filePath);
		return new BatchMesh("object", (GeomBatch[])triangleStrips.toArray(new GeomBatch[1]));
	}
	
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
			byte[] numberOfPoints = new byte[sizeOfUnsignedInt];
			file.read(numberOfPoints);
			numOfPoints = convertByteArrayToInt(numberOfPoints);
			
			//read points
			//3 floats for normals followed by 3 floats for coordinates per point
			points = new byte[sizeOfFloat * 6 * numOfPoints];
			file.read(points);

			
			//read number of triangle strips
			byte[] numberOfTriangleStrips = new byte[sizeOfUnsignedInt];
			file.read(numberOfTriangleStrips);
			
			this.processNormalsAndVertices();
			
			//read triangle strips
			int numberOfTriStrips = convertByteArrayToInt(numberOfTriangleStrips);

			for (int i = 0; i < numberOfTriStrips; i++) {
				
				//read number of points in the strip
				byte[] numberOfPointsInStrip = new byte[sizeOfUnsignedShort];
				file.read(numberOfPointsInStrip);
				
				//read point indicies
				int numberOfPtsInStrip = convertByteArrayToInt(numberOfPointsInStrip);
				byte[] triangles = new byte[sizeOfUnsignedInt * numberOfPtsInStrip];
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

	
	/* from (http://darksleep.com/player/JavaAndUnsignedTypes.html)
	 *  What is going on there is that we are promoting a (signed) byte to int, 
	 *  and then doing a bitwise AND operation on it to wipe out everything but 
	 *  the first 8 bits. Because Java treats the byte as signed, if its unsigned 
	 *  value is above > 127, the sign bit will be set, and it will appear to java 
	 *  to be negative. When it gets promoted to int, bits 0 through 7 will be the 
	 *  same as the byte, and bits 8 through 31 will be set to 1. So the 
	 *  bitwise AND with 0x000000FF clears out all of those bits. Note that this could 
	 *  have been written more compactly as;
     *
	 * 0xFF & buf[index]
     * 
     * Java assumes the leading zeros for 0xFF, and the bitwise & operator automatically
     *  promotes the byte to int. But I wanted to be a tad more explicit about it.
     * 
     * The next thing you'll see a lot of is the <<, or bitwise shift left operator. 
     * It's shifting the bit patterns of the left int operand left by as many bits 
     * as you specify in the right operand So, if you have some int foo = 0x000000FF, 
     * then (foo << 8) == 0x0000FF00, and (foo << 16) == 0x00FF0000.
     * 
     * The last piece of the puzzle is |, the bitwise OR operator. Assume you've loaded 
     * both bytes of an unsigned short into separate integers, so you have 0x00000012 and 
     * 0x00000034. Now you shift one of the bytes by 8 bits to the left, so you have 
     * 0x00001200 and 0x00000034, but you still need to stick them together. So you 
     * bitwise OR them, and you have 0x00001200 | 0x00000034 = 0x00001234. This is 
     * then stored into Java's 'char' type.
     * 
     * That's basically it, except that in the case of the unsigned int, you have to 
     * now store it into the long, and you're back up against that sign extension 
     * problem we started with. No problem, just cast your int to long, then do the
     *  bitwise AND with 0xFFFFFFFFL. (Note the trailing L to tell Java this is a 
     *  literal of type 'long' integer.) 
     *  
     *  Modified for little endianness (http://en.wikipedia.org/wiki/Little_endian#Little-endian)
	 */
	private int convertByteArrayToInt(byte[] buf) {
		int firstByte = 0;
		int secondByte = 0;
		int thirdByte = 0;
		int fourthByte = 0;
		
		int out = 0;
		if (buf.length == sizeOfUnsignedInt) {
			long anUnsignedInt = 0;
			
			firstByte = (0x000000FF & ((int)buf[3]));
			secondByte =(0x000000FF & ((int)buf[2]));
			thirdByte = (0x000000FF & ((int)buf[1]));
			fourthByte =(0x000000FF & ((int)buf[0]));
			
			anUnsignedInt = ((long) (firstByte << 24 | secondByte << 16 |
									 thirdByte <<8 | fourthByte))
									 & 0xFFFFFFFL;
			
			out = (int)anUnsignedInt;
		} else if (buf.length == sizeOfUnsignedShort) {
			char anUnsignedShort = 0;
			
			firstByte = (0x000000FF & ((int)buf[1]));
			secondByte = (0x000000FF & ((int)buf[0]));
			
			anUnsignedShort = (char) (firstByte << 8 | secondByte);
			out = (int)anUnsignedShort;
		}
		return out;
	}
	
	//from http://www.captain.at/howto-java-convert-binary-data.php
	private float convertByteArrayToFloat (byte[] arr, int start) {
		int i = 0;
		int len = sizeOfFloat;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}
	
	
	private TriangleBatch createTriangleStrip(int numOfPointsInStrip, byte[] triangles) {
		int[] indices = new int[numOfPointsInStrip];
		int j = 0;
		for (int i = 0; i < triangles.length; i += sizeOfUnsignedInt) {
			//assumes unsigned int length == 4
			byte[] array = {triangles[i], triangles[i+1], triangles[i+2], triangles[i+3]};
			indices[j++] = convertByteArrayToInt(array);
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
		for (int i = 0; i < points.length; i += sizeOfFloat) {
			floats[j++] = convertByteArrayToFloat(points, i);
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
