package edu.ucsd.ccdb.ontomorph2.core.atlas;

import java.awt.Color;
import java.util.Observable;

import com.jme.scene.BatchMesh;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;

public class BrainRegion extends Observable implements ISelectable{

	private String name;
	private String abbrev;
	private String parentAbbrev;
	private Color color;
	private BatchMesh mesh;
	private boolean selected = false;
	
	public BrainRegion(String name, String abbrev, String parentAbbrev, Color c){
		this.name = name;
		this.abbrev = abbrev;
		this.parentAbbrev = parentAbbrev;
		this.color = c;
		
		this.addObserver(SceneObserver.getInstance());
	}
	
	public BrainRegion getParent() {
		return ReferenceAtlas.getInstance().getBrainRegion(this.parentAbbrev);
	}

	public String getAbbreviation() {
		return abbrev;
	}

	public BatchMesh getMesh() {
		if (mesh == null) {
			AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
			this.mesh = loader.loadByAbbreviation(this.getAbbreviation());
			//mesh.setSolidColor(ColorUtil.convertColorToColorRGBA(this.color));
		}
		return mesh;
	}

	public void select() {
		selected = true;
		changed();
	}

	public void unselect() {
		selected = false;
		changed();
	}

	public boolean isSelected() {
		return selected;
	}
	
	protected void changed() {
		setChanged();
		notifyObservers();
	}

	public void destroyMesh() {
		this.mesh = null;
		System.gc();
	}

	public String getName() {
		return this.name;
	}
	
}
