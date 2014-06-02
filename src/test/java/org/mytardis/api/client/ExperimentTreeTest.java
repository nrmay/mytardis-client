package org.mytardis.api.client;

import static org.junit.Assert.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mytardis.api.client.ExperimentTree;
import org.mytardis.api.client.TardisClient;
import org.mytardis.api.model.Dataset;
import org.mytardis.api.model.DatasetFile;
import org.mytardis.api.model.Experiment;
import org.mytardis.api.model.User;

public class ExperimentTreeTest {

	private TardisClient client = null;
	private Logger logger = LogManager.getLogger(this.getClass());

	public String address = "130.220.210.149";
	public String user = "admin";
	public String pass = "dtofaam123";
	public Date start = null;
	public Date end = null;

	@Before
	public void setUp() throws Exception {
		// Initialize a client
		client = new TardisClient(address, user, pass);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 05, 07, 10, 32, 0);
		this.start = calendar.getTime();
		calendar.set(2014, 05, 07, 15, 07, 12);
		this.end = calendar.getTime();
		// finished
		return;
	}

	@After
	public void tearDown() throws Exception {
		client = null;
		logger.debug("\n");
		// finished
		return;
	}

	/****************
	 * Test Methods *
	 ****************/

	@Test
	public void testVerifyDatafileTree() {
		logger.debug("start!");

		// create data file
		DatafileTree tree = new DatafileTree(client);
		DatasetFile datafile = tree.getDatafile();
		assertNotNull("new datasetfile is null!", datafile);
		assertNotNull("errors is null", tree.getErrors());
		assertTrue("errors is not empty", tree.getErrors().isEmpty());
		
		// verify new data file
		List<String> errors = tree.checkTree();
		assertNotNull("new datafile: errors is null!", errors);
		logger.debug("new datafile: errors = " + errors.toString());
		assertFalse("new datafile: errors is empty!", errors.isEmpty());
		assertEquals("new datafile: errors count not matched!", Integer.valueOf(5)
				, Integer.valueOf(errors.size()));
		
		// verify required
		// TODO: filename, md5sum, mimetype, sha512sum, size.
		//     : invalid
		
		//     : valid
		
		// verify required
		
		
		// verify optional
		// TODO: createdTime, directory
		//     : invalid
		
		//     : valid
		
		
		// verify invalid
		// TODO: datafile, dataset, id, modificationTime, replicas, resourceUri
		
		// finished
		return;
	}

	@Test
	public void testVerifyDatasetTree() {
		logger.debug("start!");

		// create data set
		DatasetTree tree = new DatasetTree(client);
		Dataset dataset = tree.getDataset();
		assertNotNull("new dataset is null!", dataset);
		assertNotNull("errors is null!", tree.getErrors());
		assertTrue("errors is not empty!", tree.getErrors().isEmpty());

		// verify new data set - missing valid description
		List<String> errors = tree.checkTree();
		assertNotNull("new dataset: errors is null!", errors);
		logger.debug("new dataset: errors = " + errors.toString());
		assertFalse("new dataset: errors is not empty!", errors.isEmpty());
		assertEquals("new dataset: error[0] not matched!",
				"Dataset.description: is null.", errors.get(0));

		// set description
		dataset.setDescription("Test Dataset");
		errors = tree.checkTree();
		assertNotNull("valid desc: errors is null!", errors);
		assertTrue("valid desc: errors is not empty!", errors.isEmpty());

		// check invalid attributes
		dataset.setId(2);
		dataset.setResourceUri("/api/v1/dataset/9999/");
		errors = tree.checkTree();
		assertNotNull("invalids: errors is null!", errors);
		assertFalse("invalids: errors is empty!", errors.isEmpty());
		assertEquals("invalids: error count not matched!", Integer.valueOf(2),
				Integer.valueOf(errors.size()));
		dataset.setId(null);
		dataset.setResourceUri("");
		errors = tree.checkTree();
		assertNotNull("invalids: errors is null!", errors);
		assertTrue("invalids: errors is not empty!", errors.isEmpty());

		// check available attributes - invalid
		dataset.setDirectory("/experiment/test_folder_01");
		List<Integer> ints = new ArrayList<Integer>();
		ints.add(9999);
		dataset.setExperiments(ints);
		dataset.setImmutable(true);
		errors = tree.checkTree();
		assertNotNull("valids: errors is null!", errors);
		assertFalse("valids: errors is empty!", errors.isEmpty());
		assertEquals("valids: error count not matched!", Integer.valueOf(1),
				Integer.valueOf(errors.size()));
		List<String> strs = new ArrayList<String>();
		strs.add("9999");
		dataset.setExperiments(strs);
		errors = tree.checkTree();
		assertNotNull("valids: errors is null!", errors);
		assertFalse("valids: errors is empty!", errors.isEmpty());
		assertEquals("valids: error count not matched!", Integer.valueOf(1),
				Integer.valueOf(errors.size()));
		strs = new ArrayList<String>();
		strs.add("/api/v1/experiment/10004/");
		strs.add("/api/v1/experiment/10005/");
		dataset.setExperiments(strs);
		errors = tree.checkTree();
		assertNotNull("valids: errors is null!", errors);
		assertFalse("valids: errors is not empty!", errors.isEmpty());
		assertEquals("valids: error count not matched!", Integer.valueOf(2),
				Integer.valueOf(errors.size()));

		// finished
		return;
	}

	@Test
	public void testVerifyExperimentTree() {
		logger.debug("start!");

		// create experiment
		ExperimentTree tree = new ExperimentTree(client);
		Experiment exp = tree.getExperiment();
		assertNotNull("new experiment is null!", exp);
		assertNotNull("errors is null!", tree.getErrors());

		// verify new experiment
		assertFalse("verify new experiment did not fail!", tree.verify());
		assertFalse("errors is empty!", tree.getErrors().isEmpty());
		assertEquals("errors length not matched!", Integer.valueOf(1),
				Integer.valueOf(tree.getErrors().size()));
		assertEquals("errors[0] not matched!", tree.getErrors().get(0),
				"Experiment.title: cannot be \'No default provided.\'");

		// check invalid fields not set
		// createdBy, createdTime, id, publicAccess, resourceUri, updateTime.
		exp.setTitle("");
		exp.setCreatedBy("/api/v1/user/1/");
		exp.setCreatedTime(client.formatDate(start));
		exp.setId(1);
		exp.setPublicAccess(2);
		exp.setResourceUri("/api/v1/experiment/1/");
		exp.setUpdateTime(client.formatDate(end));
		// verify
		assertFalse("verify invalid fields did not fail!", tree.verify());
		assertFalse("errors is empty!", tree.getErrors().isEmpty());
		assertEquals("errors length not matched!", Integer.valueOf(7),
				Integer.valueOf(tree.getErrors().size()));
		assertEquals("errors[0] not matched!", "Experiment.title: not found.",
				tree.getErrors().get(0));
		assertEquals("errors[1] not matched!",
				"Experiment.created_by: is not null.", tree.getErrors().get(1));
		assertEquals("errors[2] not matched!",
				"Experiment.created_time: is not null.", tree.getErrors()
						.get(2));
		assertEquals("errors[3] not matched!", "Experiment.id: is not null.",
				tree.getErrors().get(3));
		assertEquals("errors[4] not matched!",
				"Experiment.public_access: invalid value = 2", tree.getErrors()
						.get(4));
		assertEquals("errors[5] not matched!",
				"Experiment.resource_uri: is not null.", tree.getErrors()
						.get(5));
		assertEquals("errors[6] not matched!",
				"Experiment.updated_time: is not null.", tree.getErrors()
						.get(6));

		// check values of available fields: approved, description, endTime,
		// handle, institutionName, locked, startTime, title, url.
		tree = new ExperimentTree(client);
		exp = tree.getExperiment();
		exp.setTitle("Test Available Fields.");
		exp.setDescription(null);
		exp.setHandle(null);
		exp.setInstitutionName(null);
		exp.setStartTime(null);
		exp.setUrl(null);
		assertTrue("verify valid available fields not matched!", tree.verify());

		// check URLs and URIs
		exp.setHandle("dummy uri");
		exp.setUrl("dummy url");
		assertFalse("verify invalid urls did not fail!", tree.verify());
		assertEquals("error count not matched!", Integer.valueOf(2),
				Integer.valueOf(tree.getErrors().size()));
		exp.setHandle("doi:10.1018/a.n.onymous.2014.03.001");
		exp.setUrl("http://address.and.port/webapp/page");
		assertTrue("verify valid urls failed!", tree.verify());

		// check date times
		exp.setStartTime("2014-01-01-01 13:53");
		exp.setEndTime("2014-02-31 27:01");
		assertFalse("verify invalid times did not fail!", tree.verify());
		assertEquals("errors count not matched!", Integer.valueOf(2),
				Integer.valueOf(tree.getErrors().size()));
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 02, 31, 27, 32, 0);
		exp.setStartTime(calendar.getTime());
		assertFalse("verify invalid start date failed!", tree.verify());
		calendar.set(2014, 02, 21, 16, 32, 0);
		exp.setStartTime(calendar.getTime());
		exp.setEndTime("2014-02-21T17:01:00");
		assertTrue("verify valid dates failed!", tree.verify());

		// check invalid parameters
		String ns_server = "http://org.walroz.wsr/server";
		String name = "host";
		String value = "aServer";
		// bad namespace
		tree.addParameter("http://bad/schema", name, value);
		assertFalse("verify invalid namespace failed!", tree.verify());
		// bad name
		tree.clearParameters();
		tree.addParameter(ns_server, "badname", value);
		assertFalse("verify invalid name failed!", tree.verify());
		// bad value
		tree.clearParameters();
		tree.addParameter(ns_server, name, null);
		assertFalse("verify invalid value failed!", tree.verify());
		// valid parameter
		tree.clearParameters();
		tree.addParameter(ns_server, name, value);
		assertTrue("verify valid parameter failed!", tree.verify());

		// finished
		return;
	}

	@Test
	public void testExperiment() {
		logger.debug("start!");
		// get user
		User user = client.getUserByUsername("admin");
		assertNotNull("user is null!", user);

		// create experiment
		ExperimentTree tree = new ExperimentTree(client);
		Experiment exp = tree.getExperiment();
		assertNotNull("new experiment is null!", exp);

		// set valid experiment fields
		exp.setApproved(true);
		exp.setDescription("This is an experiment generated by test case: ExperimentTreeTest.testExperiment().");
		exp.setEndTime(client.formatDate(end));
		exp.setHandle("http://hdl.handle.net/10520/EJC152785");
		exp.setInstitutionName("SCSIT, RMIT Univeristy");
		exp.setLocked(true);
		exp.setStartTime(client.formatDate(start));
		exp.setTitle("Test Experiment (" + UUID.randomUUID().toString() + ")");
		exp.setUrl("http://tardis.walroz.org");

		// post the experiment
		String experimentUri = tree.post();
		assertNotNull("experimentUri is null!", experimentUri);
		logger.debug("experiment uri = " + experimentUri);

		// check experiment created
		List<Experiment> experiments = null;
		try {
			experiments = client.getObjects(Experiment.class);
			assertNotNull("expeiments is null!", experiments);
			assertFalse("experiments is empty!", experiments.isEmpty());
		} catch (Exception e) {
			fail("get experiments failed with: " + e.getMessage());
		}
		boolean found = false;
		for (Experiment item : experiments) {
			if (item.getResourceUri().equals(experimentUri)) {
				found = true;
				logger.debug("times: created[" + item.getCreatedTime()
						+ "] updated[" + item.getUpdateTime() + "] start["
						+ item.getStartTime() + "] end[" + item.getEndTime()
						+ "]");
				logger.debug("data: handle[" + item.getHandle() + "] url["
						+ item.getUrl() + "]");

				// check attributes match
				assertTrue("approved not matched!", item.getApproved());
				assertTrue("locked not matched!", item.getLocked());
				assertEquals("createdBy not matched!", user.getResourceUri(),
						item.getCreatedBy().toString());
				assertEquals("title not matched!", exp.getTitle(),
						item.getTitle());
				assertEquals("description not matched!", exp.getDescription(),
						item.getDescription());
				assertEquals("start time not matched!", exp.getStartTime(),
						item.getStartTime());
				assertEquals("end time not matched!", exp.getEndTime(),
						item.getEndTime());
				assertEquals("handle not matched!", exp.getHandle(),
						item.getHandle());
				assertEquals("url not matched!", exp.getUrl(), item.getUrl());
				assertEquals("institution not matched!",
						exp.getInstitutionName(), item.getInstitutionName());
			}
		}
		assertTrue("experiment uri[" + experimentUri + "] not found!", found);

		// finished
		return;
	}

	@Test
	public void testPostExperimentTree() {
		logger.debug("start!");
		assertNotNull("client is null!", client);

		String createdBy = "admin";
		User createdByUser = client.getUserByUsername(createdBy);

		assertNotNull("createdByUser is null!", createdByUser);
		assertNotNull("createdByUser.resourceId is null!",
				createdByUser.getResourceUri());

		// build experiment tree
		ExperimentTree tree = this.buildExperiment(client);
		tree.setDatasets(this.buildDatasets(client));

		// post Experiment Tree
		String uri = tree.post();
		assertTrue("errors is not empty!", tree.getErrors().isEmpty());
		assertNotNull("response uri is null!", uri);

		// check created
		try {
			List<Experiment> experiments = client.getObjects(Experiment.class);
			assertNotNull("experiments is null!", experiments);
			assertFalse("experiments is empty!", experiments.isEmpty());
			Integer id = null;
			for (Experiment item : experiments) {
				if (item.getResourceUri().equals(uri)
						&& item.getTitle().equals(
								tree.getExperiment().getTitle())) {
					id = item.getId();
					assertEquals("createdByUser not matched!",
							createdByUser.getResourceUri(), item.getCreatedBy());
				}
			}
			assertNotNull("Experiment not found!", id);
		} catch (Exception e) {
			fail("get Experiments failed with: " + e.getMessage());
		}

		// finished
		return;
	}

	/*******************
	 * private methods *
	 *******************/

	private ExperimentTree buildExperiment(TardisClient client) {
		logger.debug("start!");
		ExperimentTree result = new ExperimentTree(client);

		// set experiment attributes
		Experiment experiment = result.getExperiment();
		experiment.setTitle("Test Experiment (" + UUID.randomUUID().toString()
				+ ")");
		experiment.setDescription("Experiment uploaded by myTardis API Client."
				+ " See http://github.com/nrmay/mytardis-client");
		experiment.setInstitutionName("RMIT University.");
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 5, 12, 12, 54, 55);
		experiment.setStartTime(client.formatDate(calendar.getTime()));
		calendar.set(2014, 5, 12, 13, 6, 5);
		experiment.setEndTime(client.formatDate(calendar.getTime()));

		// build parameter sets
		String ns_server = "http://org.walroz.wsr/server";
		result.addParameter(ns_server, "host", "wsrServer1");
		result.addParameter(ns_server, "address", "127.0.0.1");
		result.addParameter(ns_server, "port", "8080");
		result.addParameter(ns_server, "version", "0.6");

		String ns_job = "http://org.walroz.wsr/job";
		result.addParameter(ns_job, "name", "job01");
		result.addParameter(ns_job, "test", "boundingTime");
		result.addParameter(ns_job, "iterations", "8");
		result.addParameter(ns_job, "composite", "parallel-8-base");
		result.addParameter(ns_job, "lower_bound", "0");
		result.addParameter(ns_job, "upper_bound", "1000");

		// finished
		return result;
	}

	private List<DatasetTree> buildDatasets(TardisClient client) {
		logger.debug("start!");
		List<DatasetTree> result = new ArrayList<DatasetTree>();
		String ns_iteration = "http://org.walroz.wsr/iteration";

		// dataset 01
		DatasetTree tree01 = new DatasetTree(client);
		Dataset dataset = tree01.getDataset();
		dataset.setDescription("Iteration #01");
		dataset.setDirectory("dataset01");
		tree01.addParameter(ns_iteration, "name", "01");
		tree01.setDatafiles(this.buildDatasetFiles01(client));
		result.add(tree01);

		// dataset 02
		DatasetTree tree02 = new DatasetTree(client);
		dataset = tree02.getDataset();
		dataset.setDescription("Iteration #02");
		dataset.setDirectory("dataset02");
		tree02.addParameter(ns_iteration, "name", "02");
		tree02.setDatafiles(this.buildDatasetFiles02(client));
		result.add(tree02);

		// finished
		return result;
	}

	private List<DatafileTree> buildDatasetFiles01(TardisClient client) {
		logger.debug("start!");
		List<DatafileTree> result = new ArrayList<DatafileTree>();

		// results.xml
		DatafileTree tree01 = new DatafileTree(client);
		DatasetFile file = tree01.getDatafile();
		file.setFilename("results(1).xml");
		tree01.setFile(this.getFile(file.getFilename()));
		result.add(tree01);

		// group.composite
		DatafileTree tree02 = new DatafileTree(client);
		file = tree02.getDatafile();
		file.setFilename("group(1).composite");
		tree02.setFile(this.getFile(file.getFilename()));
		result.add(tree02);

		// finished
		return result;
	}

	private List<DatafileTree> buildDatasetFiles02(TardisClient client) {
		logger.debug("start!");
		List<DatafileTree> result = new ArrayList<DatafileTree>();

		// results.xml
		DatafileTree tree01 = new DatafileTree(client);
		DatasetFile file = tree01.getDatafile();
		file.setFilename("results(2).xml");
		tree01.setFile(this.getFile(file.getFilename()));
		result.add(tree01);

		// group.composite
		DatafileTree tree02 = new DatafileTree(client);
		file = tree02.getDatafile();
		file.setFilename("group(2).composite");
		tree02.setFile(this.getFile(file.getFilename()));
		result.add(tree02);

		// finished
		return result;
	}

	private File getFile(String filename) {
		File result = null;
		URL url = getClass().getResource("/" + filename);
		assertNotNull("Test file[" + filename + "] not found!", url);

		try {
			result = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

}