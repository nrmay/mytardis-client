package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Dataset;
import org.mytardis.api.model.Experiment;

/**
 * A representation of a dataset.
 * 
 * @author Nick may
 * @version 1.0
 */
public class DatasetTree extends TardisObjectContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private Dataset target = new Dataset();
	private List<DatafileTree> datafiles = new ArrayList<DatafileTree>();

	/**
	 * Default Constructor
	 * 
	 * @param client
	 *            : TardisClient
	 */
	public DatasetTree(TardisClient client) {
		super();
		this.client = client;
	}

	/**
	 * Check Tree for Errors.
	 * 
	 * @return List of error messages as Strings.
	 * @throws Exception 
	 */
	public List<String> checkTree() {
		logger.debug("start!");
		this.clearErrors();
		this.checkParametersetTree(client);

		// check dataset
		Dataset dataset = this.getDataset();
		if (dataset == null) {
			this.addError("Dataset: is null.");
		} else {
			if (dataset.getDescription() == null
					|| dataset.getDescription().isEmpty()) {
				logger.debug("Dataset.description = "
						+ dataset.getDescription());
				this.addError("Dataset.description: is null.");
			}
			if (dataset.getId() != null) {
				this.addError("Dataset.id: is not null!");
			}

			if (dataset.getImmutable() == null) {
				this.addError("Dataset.immutable: is null.");
			}
			if (dataset.getResourceUri() != null
					&& !dataset.getResourceUri().isEmpty()
					&& !dataset.getResourceUri().endsWith(
							TardisClient.NO_DEFAULT_PROVIDED)) {
				this.addError("Dataset.resourceUri: is not null.");
			}
			if (dataset.getExperiments() != null) {
				if (!(dataset.getExperiments() instanceof List<?>)) {
					this.addError("Dataset.experiments: is not a List.");
				} else {
					try {
						@SuppressWarnings("unchecked")
						List<String> uris = (List<String>) dataset
								.getExperiments();
						logger.debug("experimentUris = " + uris.toString());
						for (String uri : uris) {
							Experiment experiment = (Experiment) client
									.getObjectByUri(Experiment.class, uri);
							if (experiment == null) {
								this.addError("Dataset.experiments: resource not found.");
							}
						}
					} catch (ClassCastException e) {
						this.addError("Dataset.experiments: is not a List of Strings.");
					} catch (Exception e) {
						this.addError("Dataset.experiments: failed to find a valid experiment for resourceUri.");
						;
					}

				}
			}
		}

		// finished
		logger.debug("errors count = " + this.getErrors().size());
		if (this.getErrors().size() > 0) {
			logger.debug("errors = " + this.getErrors().toString());
		}
		return this.getErrors();
	}

	/**
	 * Post dataset to myTardis
	 * 
	 * @param experimentUri
	 * @return String : dataset uri
	 */
	public String post(String experimentUri) {
		logger.debug("start!");
		String datasetUri = null;
		String[] experiments = { experimentUri };

		if (target != null) {
			// set resourceUri
			if (this.target.getResourceUri() != null
					&& this.target.getResourceUri().equals(
							TardisObject.NO_DEFAULT)) {
				this.target.setResourceUri("/api/v1/dataset/");
			}

			// set properties
			target.setExperiments(experiments);
			target.setParameterSets(this.getParametersets());

			// post dataset
			try {
				datasetUri = client.postObject(target);
				if (this.datafiles != null) {
					for (DatafileTree item : this.datafiles) {
						String datafileUri = item.post(datasetUri);
						logger.debug("post datafile uri = " + datafileUri);
					}
				}
			} catch (Exception e) {
				logger.debug("post dataset failed with: " + e.getMessage());
			}
		}

		// finished
		return datasetUri;
	}

	/***********************
	 * Getters and Setters *
	 ***********************/

	public Dataset getDataset() {
		return target;
	}

	public void setDataset(Dataset dataset) {
		this.target = dataset;
	}

	public List<DatafileTree> getDatafiles() {
		return datafiles;
	}

	public void setDatafiles(List<DatafileTree> datafiles) {
		this.datafiles = datafiles;
	}

}
