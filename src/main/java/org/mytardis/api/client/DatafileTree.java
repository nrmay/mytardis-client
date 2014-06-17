package org.mytardis.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
public class DatafileTree extends ParameterSetContainer {

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

		// check required attributes
		// filename, md5sum, mimetype, sha12sum, size
		if (target.getFilename() == null
				|| target.getFilename().isEmpty()
				|| target.getFilename()
						.equals(TardisClient.NO_DEFAULT_PROVIDED)) {
			this.addError("Datafile.filename: is missing!");
		}
		// expect at least one of md5sum or sha12sum
		if ((target.getMd5sum() == null || target.getMd5sum().isEmpty())
				&& (target.getSha512sum() == null || target.getSha512sum()
						.isEmpty())) {
			this.addError("Datafile.checksum: is missing - either md5sum and/or sha12sum.");
		}
		if (target.getMimetype() == null || target.getMimetype().isEmpty()) {
			this.addError("Datafile.mimetype: is missing!");
		}
		if (target.getSize() == null || target.getSize().isEmpty()) {
			this.addError("Datafile.size: is missing!");
		}

		// check file
		try {
			FileInputStream fis = new FileInputStream(this.file);
			if (target.getMd5sum() != null 
					&& !target.getMd5sum().isEmpty()
					&& !target.getMd5sum().equals(DigestUtils.md5Hex(fis))) {
				this.addError("Datafile.md5sum: not matched!");
			}
//			if (target.getSha512sum() != null
//					&& !target.getSha512sum().isEmpty()
//					&& !target.getSha512sum()
//							.equals(DigestUtils.sha512Hex(fis))) {
//				this.addError("Datafile.sha512sum: not matched!");
//			}
			if (target.getMimetype() != null
					&& !target.getMimetype().isEmpty()
					&& !target.getMimetype().equals(
							client.getMimeType(this.getFile()))) {
				this.addError("Datafile.mimetype: not matched!");
			}
			if (target.getSize() != null
					&& !target.getSize().isEmpty()
					&& !target.getSize().equals(
							Long.toString(this.getFile().length()))) {
				this.addError("Datafile.size: not matched!");
			}
		} catch (NullPointerException e1) {
			this.addError("Datafile.file: is null!");
		} catch (FileNotFoundException e2) {
			this.addError("Datafile.file: not found!");
		} catch (IOException e3) {
			this.addError("Datafile.file: not accessible!");
		}

		// check optional attributes
		if (target.getCreatedTime() != null
				&& target.getCreatedTime().getClass() != Date.class) {
			try {
				String datetime = (String) target.getCreatedTime();
				if (!client.checkDate(datetime)) {
					this.addError("Datafile.created_time: is invalid.");
				}
			} catch (Exception e) {
				logger.debug(e.toString());
				this.addError("Datafile.created_time: is invalid.");
			}
		}
		if (target.getDirectory() != null && !target.getDirectory().isEmpty()) {
			// check it is a valid path
			try {
				@SuppressWarnings("unused")
				URI uri = new URI(target.getDirectory());
			} catch (Exception e) {
				logger.debug("failed with: " + e.getMessage());
				this.addError("Datafile.directory: is not a valid path.");
			}
		}

		// check excluded attributes
		if (target.getDatafile() != null
				&& !target.getDatafile().isEmpty()
				&& !target.getDatafile().equals(
						TardisClient.NO_DEFAULT_PROVIDED)) {
			this.addError("Datafile.datafile: is not null!");
		}
		if (target.getDataset() != null) {
			this.addError("Datafile.dataset: is not null!");
		}
		if (target.getModificationTime() != null) {
			this.addError("Datafile.modificationTime: is not null!");
		}
		if (target.getParameterSets() != null) {
			this.addError("Datafile.parameterSets: is not null!");
		}
		if (target.getReplicas() != null) {
			this.addError("Datafile.replicas: is not null!");
		}
		if (target.getResourceUri() != null
				&& !target.getResourceUri().isEmpty()
				&& !target.getResourceUri().equals(
						TardisClient.NO_DEFAULT_PROVIDED)) {
			this.addError("Datafile.resourceUri: is not null!");
		}

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

		if (target != null && this.checkTree().isEmpty()) {
			// set resourceUri
			if (this.target.getResourceUri() != null
					&& this.target.getResourceUri().equals(
							TardisObject.NO_DEFAULT)) {
				this.target.setResourceUri("/api/v1/dataset_file/");
			}

			// set properties
			Calendar now = Calendar.getInstance();
			target.setCreatedTime(client.formatDate(now.getTime()));
			target.setDataset(datasetUri);
			target.setParameterSets(this.getParametersets());

			// post dataset_file
			try {
				logger.debug("target datafile name[" + target.getFilename()
						+ "] mimetype[" + target.getMimetype() + "] md5sum["
						+ target.getMd5sum() + "] sha512sum["
						+ target.getMd5sum() + "] size[" + target.getSize()
						+ "]");

				result = client.postObjectAndFile(target, this.file);
				logger.debug("post datafile: resourceUri = " + result);
			} catch (Exception e) {
				logger.debug("post datafile: failed with - " + e.getMessage());
			}
		}

		// finished
		return result;
	}

	/**
	 * Set the file attributes from the target file, including; 
	 * <b>filename</b>, <b>mimetype</b>, <b>size</b>, and <b>checksums</b> 
	 * (md5sum and sha512sum).
	 * 
	 * @return List of errors as Strings
	 */
	public List<String> setFileAttributes() {
		logger.debug("start!");
		List<String> result = new ArrayList<String>();

		if (this.file != null) {
			// filename
			target.setFilename(this.file.getName());

			// get md5 checksum
			try {
				FileInputStream fis = new FileInputStream(this.file);
				target.setMd5sum(DigestUtils.md5Hex(fis));
//				target.setSha512sum(DigestUtils.sha512Hex(fis));
			} catch (Exception ex) {
				result.add("generate checksums failed with: " + ex.getMessage());
			}

			// get mime type
			target.setMimetype(client.getMimeType(this.file));

			// get file size
			target.setSize(Long.toString(this.file.length()));
		} else {
			result.add("file is null!");
		}

		// finished
		return result;
	}

	/***********************
	 * Getters and Setters *
	 ***********************/

	/**
	 * Get the DatasetFile Object.
	 * 
	 * @return DatasetFile
	 */
	public DatasetFile getDatafile() {
		return target;
	}

	/**
	 * Set the DatasetFile Object.
	 * 
	 * @param datafile
	 *            : DatasetFile instance.
	 */
	public void setDatafile(DatasetFile datafile) {
		this.target = datafile;
	}

	/**
	 * Get the File associated with this DatasetFile object.
	 * 
	 * @return File
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Set the File associated with the DatasetFile object,
	 * and set its associated attributes.
	 * 
	 * @param file
	 *            : File instance.
	 */
	public void setFile(File file) {
		this.file = file;
		this.setFileAttributes();
	}

}
