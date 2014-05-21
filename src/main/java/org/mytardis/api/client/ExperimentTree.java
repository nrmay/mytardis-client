package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Experiment;

/**
 * A representation of an Experiment.
 * 
 * @author Nick May 
 * @version 1.0
 *
 */
public class ExperimentTree {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private Experiment experiment = new Experiment();
	private List<ParametersetTree> parametersets = new ArrayList<ParametersetTree>();
	private List<DatasetTree> datasets = new ArrayList<DatasetTree>();
		
	/******************
	 * public methods *
	 ******************/

	/**
	 * Default constructor.
	 * 
	 * @param client : TardisClient.
	 */
	public ExperimentTree(TardisClient client) {
		super();
		this.client = client;
	}

	/**
	 * Verify the ExperimentTree and associated DatasetTrees and DatafileTrees.
	 * @return true or false.
	 */
	public boolean verify() {
		logger.debug("start");
		boolean result = false;

		if (this.experiment != null) {
			// set resourceUri
			if (this.experiment.getResourceUri() != null
					&& this.experiment.getResourceUri().equals(
							TardisObject.NO_DEFAULT)) {
				this.experiment.setResourceUri("/api/v1/experiment/");
			}
			if (this.experiment.getUrl() != null
					&& this.experiment.getUrl().equals(TardisObject.NO_DEFAULT)) {
				this.experiment.setUrl(null);
			}
			result = true;
		}

		// finished
		return result;
	}

	/**
	 * Post the Experiment to a myTardis instance.
	 * @return String : the resource Uri of the new experiment.
	 */
	public String post() {
		logger.debug("start");
		String result = null;

		if (this.verify()) {
			// populate parametersets
			this.experiment.setParameterSets(this.parametersets);
			
			// post
			try {
				result = client.postObject(experiment);
			} catch (Exception e) {
				logger.debug("post failed with: " + e.getMessage());
			}

		} else {
			logger.debug("ExperimentTree.post failed to verify!");
		}

		// finished
		return result;
	}

	/***********************
	 * Getters and Setters *
	 ***********************/

	/**
	 * Get the associated experiment object.
	 * @return Experiment.
	 */
	public Experiment getExperiment() {
		return experiment;
	}

	/**
	 * Get the experiment's title.
	 * @return String.
	 */
	public String getTitle() {
		return experiment.getTitle();
	}

	/**
	 * Set the experiment's title.
	 * @param title : as String.
	 */
	public void setTitle(String title) {
		this.experiment.setTitle(title);
	}

	/**
	 * Get the experiment's description.
	 * @return String.
	 */
	public String getDescription() {
		return experiment.getDescription();
	}

	/**
	 * Set the experiments's description.
	 * @param description : as String.
	 */
	public void setDescription(String description) {
		this.experiment.setDescription(description);
	}

	/**
	 * Get the experiment's institution.
	 * @return String : the institution name.
	 */
	public String getInstitution() {
		return this.experiment.getInstitutionName();
	}

	/**
	 * Set the experiment's institution.
	 * @param institution
	 */
	public void setInstitution(String institution) {
		this.experiment.setInstitutionName(institution);
	}

	/**
	 * Add an experiment parameter.
	 * 
	 * @param namespace : the schema namespace of the parameter.
	 * @param name : the parameter name.
	 * @param value : the parameter value.
	 */
	public void addParameter(String namespace, String name, String value) {
		logger.debug("start!");
		ParametersetTree tree = null;
		for (ParametersetTree item: this.parametersets) {
			if (item.getSchema().equals(namespace)) {
				tree = item;
				break;
			}
		}
		if (tree == null) {
			tree = new ParametersetTree(namespace);
			this.parametersets.add(tree);
		}
		tree.addParameter(name, value);
		
		// finished
		return;
	}
	
	/**
	 * Get the List of datasets associated with the experiment.
	 * @return List of DatasetTrees.
	 */
	public List<DatasetTree> getDatasets() {
		return datasets;
	}

	/**
	 * Set the List of datasets.
	 * @param datasets : List of DatasetTrees.
	 */
	public void setDatasets(List<DatasetTree> datasets) {
		this.datasets = datasets;
	}

}
