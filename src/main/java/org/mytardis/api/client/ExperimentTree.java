package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Dataset;
import org.mytardis.api.model.DatasetFile;
import org.mytardis.api.model.Experiment;

/**
 * A representation of an Experiment.
 * 
 * @author Nick May
 * @version 1.0
 * 
 */
public class ExperimentTree extends ParametersetContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private Experiment experiment = new Experiment();
	private List<DatasetTree> datasets = new ArrayList<DatasetTree>();
	private List<String> errors = new ArrayList<String>();

	/******************
	 * public methods *
	 ******************/

	/**
	 * Default constructor.
	 * 
	 * @param client
	 *            : TardisClient.
	 */
	public ExperimentTree(TardisClient client) {
		super();
		this.client = client;
	}

	/**
	 * Verify the experiment, data set and data file trees.
	 * Error messages are written to the errors attribute.
	 * @return boolean : true if no errors.
	 */
	public boolean verify() {
		logger.debug("start!");
		boolean result = false;
		errors = new ArrayList<String>();

		// validate experiment
		if (experiment == null) {
			errors.add("Experiment not found!");
		} else {
			// check attributes
			if (experiment.getTitle() == null
					|| experiment.getTitle().isEmpty()
					|| experiment.getTitle().equals(
							TardisClient.NO_DEFAULT_PROVIDED)) {
				errors.add("Experiment.title: not found.");
			}
			if (experiment.getCreatedBy() != null) {
				errors.add("Experiment.created_by: is not null.");
			}
			if (experiment.getCreatedTime() != null) {
				errors.add("Experiment.created_time: is not null.");
			}
			if (experiment.getId() != null) {
				errors.add("Experiment.id: is not null.");
			}
			if (experiment.getPublicAccess() != null
					&& experiment.getPublicAccess() != 1) {
				errors.add("Experiment.public_access: invalid value = "
						+ experiment.getPublicAccess());
			}
			if (experiment.getResourceUri() != null
					&& !experiment.getResourceUri().equals(
							TardisClient.NO_DEFAULT_PROVIDED)) {
				errors.add("Experiment.resource_uri: is not null.");
			}
			if (experiment.getUpdateTime() != null) {
				errors.add("Experiment.updated_time: is not null.");
			}
		}

		// check parameter sets
		errors.addAll(this.checkParametersetTree(client));

		// validate data sets
		for (DatasetTree dstree : this.getDatasets()) {
			// check parameter sets
			errors.addAll(dstree.checkParametersetTree(client));

			// check data set
			Dataset dataset = dstree.getDataset();
			if (dataset == null) {
				errors.add("Dataset not found!");
			} else {
				// TODO: check attributes

			}

			// validate data files
			for (DatafileTree dftree : dstree.getDatafiles()) {
				// check parameter sets
				errors.addAll(dftree.checkParametersetTree(client));

				// check data file
				DatasetFile datafile = dftree.getDatafile();
				if (datafile == null) {
					errors.add("DatasetFile not found!");
				} else {
					// TODO: check attributes

				}

				// TODO: check file

			}
		}

		// check errors
		if (errors.isEmpty()) {
			result = true;
		}

		// finished
		logger.debug("errors count = " + errors.size());
		logger.debug("errors = " + errors);
		return result;
	}

	/**
	 * Post the Experiment to a myTardis instance.
	 * @return String : the resource Uri of the new experiment.
	 */
	public String post() {
		logger.debug("start");
		String experimentUri = null;

		if (this.verify()) {
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
			// populate children
			this.experiment.setParameterSets(this.getParametersets());

			// post
			try {
				experimentUri = client.postObject(experiment);
				logger.debug("post experiment uri = " + experimentUri);
				if (experimentUri != null) {
					for (DatasetTree item : this.datasets) {
						String datasetUri = item.post(experimentUri);
						logger.debug("post dataset uri = " + datasetUri);
					}
				}

			} catch (Exception e) {
				logger.debug("post failed with: " + e.getMessage());
			}

		} else {
			logger.debug("Experiment is null!");
		}

		// finished
		return experimentUri;
	}

	/***********************
	 * Getters and Setters *
	 ***********************/

	/**
	 * Get the associated experiment object.
	 * 
	 * @return Experiment.
	 */
	public Experiment getExperiment() {
		return experiment;
	}

	/**
	 * Get the List of datasets associated with the experiment.
	 * 
	 * @return List of DatasetTrees.
	 */
	public List<DatasetTree> getDatasets() {
		return datasets;
	}

	/**
	 * Set the List of datasets.
	 * 
	 * @param datasets
	 *            : List of DatasetTrees.
	 */
	public void setDatasets(List<DatasetTree> datasets) {
		this.datasets = datasets;
	}

	/**
	 * Get error messages.
	 * 
	 * @return List of Strings.
	 */
	public List<String> getErrors() {
		return this.errors;
	}

}
