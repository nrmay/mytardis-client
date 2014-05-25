package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Parametername;
import org.mytardis.api.model.Schema;

/**
 * A container for a list of parameter sets.
 * @author Nick May
 * @version 1.0
 * 
 */
public class ParametersetContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private Map<String, ParametersetTree> parametersets = new HashMap<String, ParametersetTree>();

	/**
	 * Add an experiment parameter.
	 * @param namespace : the schema namespace of the parameter.
	 * @param name : the parameter name.
	 * @param value : the parameter value.
	 */
	public void addParameter(String namespace, String name, String value) {
		ParametersetTree tree = parametersets.get(namespace);
		if (tree == null) {
			tree = new ParametersetTree(namespace);
			this.parametersets.put(namespace, tree);
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

	/**
	 * Check schema and parameter names
	 * @param client : the TardisClient.
	 * @return List of Strings, containing error messages.
	 */
	public List<String> checkParametersetTree(TardisClient client) {
		logger.debug("start!");
		List<String> result = new ArrayList<String>();

		for (ParametersetTree tree : this.getParametersets()) {
			// get schema and parameter names
			String namespace = tree.getSchema();
			Schema schema = client.getSchema(namespace);
			List<Parametername> names = client.getParameternames(namespace);

			// check schema
			if (schema == null) {
				result.add("experiment schema[" + namespace + "] not found!");
			} else {
				// check parameter names
				for (Parameter param : tree.getParameters()) {
					boolean found = false;
					for (Parametername item : names) {
						if (item.getName().equals(param.getName())) {
							found = true;
							break;
						}
					}
					if (!found) {
						result.add("schema[" + namespace + "] parameter["
								+ param.getName() + "] not found!");
					}
				}
			}
		}

		// finished
		return result;
	}

}
