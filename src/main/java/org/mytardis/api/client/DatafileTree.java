package org.mytardis.api.client;

import java.io.File;
import java.io.FileInputStream;
import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.digest.DigestUtils;
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

			// get mime type
			File file = new File(target.getDirectory(), target.getFilename());
			String mimeType = new MimetypesFileTypeMap().getContentType(file);

			// get md5 checksum
			try {
				FileInputStream fis = new FileInputStream(file);
				target.setMd5sum(DigestUtils.md5Hex(fis));
			} catch (Exception ex) {
				logger.debug("generate checksum failed with: " + ex.getMessage());
			}

			// set properties
			target.setDataset(datasetUri);
			target.setParameterSets(this.getParametersets());
			target.setMimetype(mimeType);
			target.setDirectory(null);
			target.setSize(Long.toString(file.length()));

			// post dataset_file
			try {
				logger.debug("target datafile directory["
						+ target.getDirectory() + "] name["
						+ target.getFilename() + "] mimetype["
						+ target.getMimetype() + "] checksum["
						+ target.getMd5sum() + "] size (MB)["
						+ target.getSize() + "]  directory["
						+ target.getDirectory() + "] ");

				result = client.postMultipart(target, file);
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
