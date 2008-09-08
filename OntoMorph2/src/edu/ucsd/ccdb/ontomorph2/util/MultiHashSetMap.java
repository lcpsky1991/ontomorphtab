package edu.ucsd.ccdb.ontomorph2.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiMap;

/**
 * Like a MultiHashMap but with a HashSet underlying the values collection instead
 * of an array list.  This makes sure that the values list will not have duplicates
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MultiHashSetMap implements MultiMap {
	
	HashMap<Object, Set<Object>> map = new HashMap<Object, Set<Object>>();

	public Object remove(Object arg0, Object arg1) {
		Set<Object> set = map.get(arg0);
		if (set != null) {
			set.remove(arg1);
			return arg1;
		}
		return null;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		for (Set<Object> set : map.values()) {
			if (set.contains(value))
				return true;
		}
		return false;
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public Object put(Object key, Object value) {
		Set<Object> valueSet = map.get(key);
		if (valueSet == null) {
			valueSet = new HashSet<Object>();
		}
		valueSet.add(value);
		return map.put(key, valueSet);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set keySet() {
		return map.keySet();
	}

	public Collection values() {
		return map.values();
	}

	public Set entrySet() {
		return map.entrySet();
	}

}
