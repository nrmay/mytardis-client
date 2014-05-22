package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.Dataset;

/**
 * A respresentation of a dataset.
 * 
 * @author Nick may
 * @version 1.0
 */
public class DatasetTree extends ParametersetContainer {

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

	public List<DatafileTree> getDatasets() {
		return datafiles;
	}

	public void setDatasets(List<DatafileTree> datafiles) {
		this.datafiles = datafiles;
	}

}
