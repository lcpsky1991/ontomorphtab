package edu.ucsd.ccdb.ontomorph2.core.data;

import java.net.URI;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

public class WBCProtocolResolver {

	public Object resolveWBCProtocol(URI proto) {
		if (proto.getPath().startsWith("ontology"))  {
			
			SemanticRepository repo = SemanticRepository.getAvailableInstance();
				
			if (proto.getPath().indexOf("class") > 0) {
				
				//get index of the final slash before the name
				int lastSlashIndex = proto.getPath().indexOf('/', proto.getPath().indexOf("class"));
				
				return repo.getSemanticClass(proto.getPath().substring(lastSlashIndex));
			} else if (proto.getPath().indexOf("instance") > 0) {
				
				//get index of the final slash before the name
				int lastSlashIndex = proto.getPath().indexOf('/', proto.getPath().indexOf("instance"));
				
				return repo.getSemanticInstance(proto.getPath().substring(lastSlashIndex));
			}
		} else if (proto.getPath().startsWith("data")) {
			if (proto.getPath().indexOf("morphml") > 0) {
				
				//get index of the final slash before the name
				int lastSlashIndex = proto.getPath().indexOf('/', proto.getPath().indexOf("morphml"));
				
				return DataRepository.getInstance().findMorphMLByName(proto.getPath().substring(lastSlashIndex));
			} else if (proto.getPath().indexOf("mesh") > 0) {
				
			}
		} else if (proto.getPath().startsWith("location")) {
			int lastSlashIndex = proto.getPath().indexOf('/', proto.getPath().indexOf("location"));
			String[] coords = proto.getPath().substring(lastSlashIndex).split("/");
			float x = Float.parseFloat(coords[0]);
			float y = Float.parseFloat(coords[1]);
			float z = Float.parseFloat(coords[2]);
			
			return new PositionVector(x,y,z);
		}
		return null;
	}
}
