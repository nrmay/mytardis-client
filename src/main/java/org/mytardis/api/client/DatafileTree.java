package org.mytardis.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;

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
public class DatafileTree extends TardisObjectContainer {

	private Logger logger = LogManager.getLogger(this.getClass());
	private TardisClient client = null;
	private DatasetFile target = new DatasetFile();
	private File file = null;

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
	 * Check Tree for Errors.
	 * 
	 * @return List of error messages as Strings.
	 */
	public List<String> checkTree() {
		logger.debug("start!");
		this.clearErrors();
		this.checkParametersetTree(client);
		
		// TODO: implement...
		
		// finished
		return this.getErrors();
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
			
			// set create date
			Calendar now = Calendar.getInstance();
			target.setCreatedTime(client.formatDate(now.getTime()));

			// get mime type
			try {
				logger.debug("probeContentType = " + Files.probeContentType(this.file.toPath()));
			} catch (IOException ioe) {
				logger.debug("failed to get content type with: " + ioe.getMessage());
			}
			String mimeType = new MimetypesFileTypeMap()
					.getContentType(this.file);

			// get md5 checksum
			try {
				FileInputStream fis = new FileInputStream(this.file);
				target.setMd5sum(DigestUtils.md5Hex(fis));
				target.setSha512sum(DigestUtils.sha512Hex(fis));
			} catch (Exception ex) {
				logger.debug("generate checksum failed with: "
						+ ex.getMessage());
			}

			// set properties
			target.setDataset(datasetUri);
			target.setParameterSets(this.getParametersets());
			target.setMimetype(mimeType);
			target.setSize(Long.toString(this.file.length()));

			// post dataset_file
			try {
				logger.debug("target datafile name[" + target.getFilename()
						+ "] mimetype[" + target.getMimetype() 
						+ "] md5sum[" + target.getMd5sum() 
						+ "] sha512sum[" + target.getMd5sum() 
						+ "] size[" + target.getSize() + "]");

				result = client.postMultipart(target, this.file);
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

	/**
	 * Get the DatasetFile Object.
	 * @return DatasetFile
	 */
	public DatasetFile getDatafile() {
		return target;
	}

	/**
	 * Set the DatasetFile Object.
	 * @param datafile : DatasetFile instance.
	 */
	public void setDatafile(DatasetFile datafile) {
		this.target = datafile;
	}

	/**
	 * Get the File associated with this DatasetFile object.
	 * @return File
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Set the File associated with the DatasetFile object.
	 * @param file : File instance.
	 */
	public void setFile(File file) {
		this.file = file;
	}

}
