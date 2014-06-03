package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Parametername;
import org.mytardis.api.model.Schema;

/**
 * A container for a list of parameter sets.
 * 
 * @author Nick May
 * @version 1.0
 * 
 */
public abstract class TardisObjectContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private Map<String, ParametersetTree> parametersets = new HashMap<String, ParametersetTree>();
	private List<String> errors = new ArrayList<String>();

	/******************
	 * Public Methods *
	 ******************/

	/**
	 * Add an error message.
	 * 
	 * @param error
	 *            : as a String.
	 */
	public void addError(String error) {
//		logger.debug("start!");
		if (error != null) {
			this.errors.add(error);
		}
		// finished
		return;
	}

	/**
	 * Add a collection of error messages.
	 * 
	 * @param errors
	 *            : as a Collection of Strings.
	 */
	public void addAllErrors(Collection<String> errors) {
		logger.debug("start!");
		if (this.errors != null) {
			this.errors.addAll(errors);
		}
		// finished
		return;
	}

	/**
	 * Clear all current error messages.
	 */
	public void clearErrors() {
		logger.debug("start!");
		this.errors = new ArrayList<String>();
	}

	/**
	 * Get error messages.
	 * 
	 * @return List of errors as Strings.
	 */
	public List<String> getErrors() {
		return this.errors;
	}

	/**
	 * Deletes all parameters and sets.
	 */
	public void clearParameters() {
		logger.debug("start!");

		this.parametersets = new HashMap<String, ParametersetTree>();

		// finished
		return;
	}

	/**
	 * Add an experiment parameter.
	 * 
	 * @param namespace
	 *            : the schema namespace of the parameter.
	 * @param name
	 *            : the parameter name.
	 * @param value
	 *            : the parameter value.
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
	 * 
	 * @return List of ParametersetTree
	 */
	public List<ParametersetTree> getParametersets() {
		return new ArrayList<ParametersetTree>(parametersets.values());
	}

	/**
	 * Check schema and parameter names.
	 * Error messages can be retrieved with the getErrors method.
	 * 
	 * @param client : the TardisClient.
	 */
	public void checkParametersetTree(TardisClient client) {
		logger.debug("start!");
		
		for (ParametersetTree tree : this.getParametersets()) {
			// get schema and parameter names
			String namespace = tree.getSchema();
			Schema schema = client.getSchema(namespace);
			List<Parametername> names = client.getParameternames(namespace);

			// check schema
			if (schema == null) {
				this.addError("experiment schema[" + namespace + "] not found!");
			} else {
				// check parameter names
				for (Parameter param : tree.getParameters()) {
					boolean found = false;
					for (Parametername item : names) {
						if (item.getName().equals(param.getName())) {
							found = true;
							if (param.getValue() == null) {
								this.addError("schema[" + namespace
										+ "] parameter[" + param.getName()
										+ "] value is null!");
							}
							break;
						}
					}
					if (!found) {
						this.addError("schema[" + namespace + "] parameter["
								+ param.getName() + "] not found!");
					}
				}
			}
		}

		// finished
		return;
	}

	/********************
	 * Abstract Methods *
	 ********************/

	/**
	 * Validate the tree.
	 * 
	 * @return True : if no errors found.
	 */
	public abstract List<String> checkTree();

}
