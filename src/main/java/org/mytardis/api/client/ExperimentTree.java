package org.mytardis.api.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
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
public class ExperimentTree extends TardisObjectContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private Experiment experiment = new Experiment();
	private List<DatasetTree> datasets = new ArrayList<DatasetTree>();

	// private List<String> errors = new ArrayList<String>();

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
	 * Verify the experiment, data set and data file trees. Error messages are
	 * written to the errors attribute.
	 * 
	 * @return boolean : true if no errors.
	 */
	public boolean verify() {
		logger.debug("start!");
		boolean result = false;
		List<String> messages = new ArrayList<String>();

		// check tree and parameter sets
		messages.addAll(this.checkTree());

		// validate data sets
		for (DatasetTree dstree : this.getDatasets()) {
			messages.addAll(dstree.checkTree());

			// validate data files
			for (DatafileTree dftree : dstree.getDatafiles()) {
				messages.addAll(dftree.checkTree());
			}
		}

		// check errors
		if (messages.isEmpty()) {
			result = true;
		}

		// finished
		logger.debug("errors count = " + messages.size());
		if (messages.size() > 0) {
			logger.debug("errors = " + messages.toString());
		}
		return result;
	}

	/**
	 * Check the ExperimentTree for errors.
	 * 
	 * @return List of error messages as Strings
	 */
	public List<String> checkTree() {
		logger.debug("start!");
		this.clearErrors();
		this.checkParametersetTree(client);

		// validate experiment
		if (experiment == null) {
			this.addError("Experiment not found!");
		} else {
			// check invalid attributes
			if (experiment.getTitle() == null
					|| experiment.getTitle().isEmpty()) {
				this.addError("Experiment.title: not found.");
			} else {
				if (experiment.getTitle().equals(TardisObject.NO_DEFAULT)) {
					this.addError("Experiment.title: cannot be \'"
							+ TardisObject.NO_DEFAULT + "\'");
				}
			}
			if (experiment.getCreatedBy() != null) {
				this.addError("Experiment.created_by: is not null.");
			}
			if (experiment.getCreatedTime() != null) {
				this.addError("Experiment.created_time: is not null.");
			}
			if (experiment.getId() != null) {
				this.addError("Experiment.id: is not null.");
			}
			if (experiment.getPublicAccess() != null
					&& experiment.getPublicAccess() != 1) {
				this.addError("Experiment.public_access: invalid value = "
						+ experiment.getPublicAccess());
			}
			if (experiment.getResourceUri() != null
					&& !experiment.getResourceUri().equals(
							TardisClient.NO_DEFAULT_PROVIDED)) {
				this.addError("Experiment.resource_uri: is not null.");
			}
			if (experiment.getUpdateTime() != null) {
				this.addError("Experiment.updated_time: is not null.");
			}

			// check valid attributes
			if (experiment.getHandle() != null
					&& !experiment.getHandle().isEmpty()) {
				try {
					@SuppressWarnings("unused")
					URI handle = new URI(experiment.getHandle());
				} catch (URISyntaxException e) {
					this.addError("Experiment.handle: is not a valid URI.");
				}
			}
			if (experiment.getUrl() != null && !experiment.getUrl().isEmpty()
					&& !experiment.getUrl().equals(TardisObject.NO_DEFAULT)) {
				try {
					@SuppressWarnings("unused")
					URL url = new URL(experiment.getUrl());
				} catch (MalformedURLException e) {
					this.addError("Experiment.url: is not a valid URL.");
				}
			}
			if (experiment.getStartTime() != null
					&& experiment.getStartTime().getClass() != Date.class) {
				try {
					String datetime = (String) experiment.getStartTime();
					if (!client.checkDate(datetime)) {
						this.addError("Experiment.start_time: is invalid.");
					}
				} catch (Exception e) {
					logger.debug(e.toString());
					this.addError("Experiment.start_time: is invalid.");
				}
			}
			if (experiment.getEndTime() != null
					&& experiment.getEndTime().getClass() != Date.class) {
				try {
					String datetime = (String) experiment.getEndTime();
					if (!client.checkDate(datetime)) {
						this.addError("Experiment.end_time: is invalid.");
					}
				} catch (Exception e) {
					logger.debug(e.toString());
					this.addError("Experiment.end_time: is invalid.");
				}
			}

		}

		// finished
		return this.getErrors();
	}

	/**
	 * Post the Experiment to a myTardis instance.
	 * 
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

}
