package org.mytardis.api.client;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mytardis.api.model.DatasetFile;

/**
 * A representation of a datafile.
 * 
 * @author Nick May
 * @version 1.0
 */
public class DatafileTree extends ParametersetContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private DatasetFile target = new DatasetFile();

	/**
	 * Default Constructor
	 * 
	 * @param client
	 *            : TardisClient
	 */
	public DatafileTree(TardisClient client) {
		super();
		this.client = client;
	}

	/**
	 * Post to myTardis
	 * 
	 * @param datasetUri
	 *            : String
	 * @return String : datafile uri
	 */
	public String post(String datasetUri) {
		logger.debug("start!");
		String result = null;

		if (target != null) {
			// set resourceUri
			if (this.target.getResourceUri() != null
					&& this.target.getResourceUri().equals(
							TardisObject.NO_DEFAULT)) {
				this.target.setResourceUri("/api/v1/dataset_file/");
			}

			// set properties
			target.setDataset(datasetUri);
			target.setParameterSets(this.getParametersets());

			// post dataset_file
			try {
				File file = new File(target.getDirectory(), target.getFilename());
				FileInputStream stream = new FileInputStream(file);
				result = client.postMultipart(target, stream);
				logger.debug("post datafile = " + result);
			} catch (Exception e) {
				logger.debug("post dataset failed with: " + e.getMessage());
			}
		}

		// finished
		return result;

	}

	/***********************
	 * Getters and Setters *
	 ***********************/

	public DatasetFile getDatafile() {
		return target;
	}

	public void setDatafile(DatasetFile datafile) {
		this.target = datafile;
	}

}
