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
public class ExperimentTree extends ParametersetContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private Experiment experiment = new Experiment();
	private List<DatasetTree> datasets = new ArrayList<DatasetTree>();

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
	 * Post the Experiment to a myTardis instance.
	 * 
	 * @return String : the resource Uri of the new experiment.
	 */
	public String post() {
		logger.debug("start");
		String experimentUri = null;

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
