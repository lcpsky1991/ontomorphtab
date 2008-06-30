package edu.ucsd.ccdb.ontomorph2.core.atlas;

import java.awt.Color;
import java.util.List;

import com.jme.scene.BatchMesh;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;

/**
 * Defines an anatomical region of the mouse brain.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class BrainRegion extends Tangible{

	/**
	 * Solid and visible
	 */
	public static final int VISIBLE = 0;
	/**
	 * Not visible
	 */
	public static final int INVISIBLE = 1;
	/**
	 * transparent and visible
	 */
	public static final int TRANSPARENT = 2;

	
	private String name;
	private String abbrev;
	private String parentAbbrev;
	private int regionId;
	private Color color;
	private BatchMesh mesh;
	private AreaClodMesh aMesh;
	private TriMesh tMesh;
	private int visibility = TRANSPARENT;
	
	public BrainRegion(String name, String abbrev, String parentAbbrev, Color c, String regionId, CoordinateSystem co){
		this.name = name;
		this.abbrev = abbrev;
		this.parentAbbrev = parentAbbrev;
		this.color = c;
		this.regionId = Integer.parseInt(regionId);
		
		this.addObserver(SceneObserver.getInstance());
		this.setCoordinateSystem(co);
	}
	
	/**
	 * Sets the visibility state of this brain region.
	 * @param mode - BrainRegion.VISIBLE, BrainRegion.INVISIBLE, BrainRegion.TRANSPARENT
	 */
	public void setVisibility(int mode) {
		if (visibility != mode) {
			visibility = mode;
			changed();
		}
	}
	
	/**
	 * Returns the visibility state of this brain region.
	 * @returns - BrainRegion.VISIBLE, BrainRegion.INVISIBLE, BrainRegion.TRANSPARENT
	 */
	public int getVisibility() {
		return visibility;
	}
	
	public BrainRegion getParent() {
		return ReferenceAtlas.getInstance().getBrainRegion(this.parentAbbrev);
	}

	public String getAbbreviation() {
		return abbrev;
	}
	
	public int getRegionId() {
		return this.regionId;
	}
	
	
	public BatchMesh getMesh() {
		if (mesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			loader.setColor(this.color);
			this.mesh = loader.loadByAbbreviation(this.getAbbreviation());

			if (this.getCoordinateSystem() != null) {
				if (this.getAbsolutePosition() != null) {
					this.mesh.setLocalTranslation(this.getAbsolutePosition());
				} 
				if (this.getAbsoluteRotation() != null) {
					this.mesh.setLocalRotation(this.getAbsoluteRotation());
				}
				if (this.getAbsoluteScale() != null) {
					this.mesh.setLocalScale(this.getAbsoluteScale());
				}
			}
		}
		return mesh;
	}
	
	public AreaClodMesh getClodMesh() {
		if (aMesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			loader.setColor(this.color);
			this.aMesh = loader.loadClodMeshByAbbreviation(this.getAbbreviation());
			if (this.getCoordinateSystem() != null) {
				if (this.getAbsolutePosition() != null) {
					this.aMesh.setLocalTranslation(this.getAbsolutePosition());
				} 
				if (this.getAbsoluteRotation() != null) {
					this.aMesh.setLocalRotation(this.getAbsoluteRotation());
				}
				if (this.getAbsoluteScale() != null) {
					this.aMesh.setLocalScale(this.getAbsoluteScale());
				}
			}
		}
		return aMesh;
	}
	
	public TriMesh getTriMesh() {
		if (tMesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			loader.setColor(this.color);
			this.tMesh = loader.loadTriMeshByAbbreviation(this.getAbbreviation());
			if (this.getCoordinateSystem() != null) {
				if (this.getAbsolutePosition() != null) {
					this.tMesh.setLocalTranslation(this.getAbsolutePosition());
				} 
				if (this.getAbsoluteRotation() != null) {
					this.tMesh.setLocalRotation(this.getAbsoluteRotation());
				}
				if (this.getAbsoluteScale() != null) {
					this.tMesh.setLocalScale(this.getAbsoluteScale());
				}
			}
		}
		return tMesh;
	}

	public void destroyMesh() {
		this.mesh = null;
		System.gc();
	}

	public String getName() {
		return this.name;
	}

}
