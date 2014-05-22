package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container for a list of parameter sets.
 * @author Nick May
 * @version 1.0
 *
 */
public class ParametersetContainer {
	
	private Map<String, ParametersetTree> parametersets = new HashMap<String, ParametersetTree>();
	
	/**
	 * Add an experiment parameter.
	 * 
	 * @param namespace : the schema namespace of the parameter.
	 * @param name : the parameter name.
	 * @param value : the parameter value.
	 */
	public void addParameter(String namespace, String name, String value) {
		ParametersetTree tree = parametersets.get(namespace);
		if (tree == null) {
			tree = new ParametersetTree(namespace);
			this.parametersets.put(namespace,tree);
		}
		tree.addParameter(name, value);
		
		// finished
		return;
	}
	
	/**
	 * Get the list of parameter sets.
	 * @return List of ParametersetTree
	 */
	public List<ParametersetTree> getParametersets() {
		return new ArrayList<ParametersetTree>(parametersets.values());
	}

}
