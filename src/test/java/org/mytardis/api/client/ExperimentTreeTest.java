package org.mytardis.api.client;

import static org.junit.Assert.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private ExperimentTree tree = null;

	public String address = "130.220.210.149";
	public String user = "admin";
	public String pass = "dtofaam123";

	@Before
	public void setUp() throws Exception {
		// Initialize a client
		client = new TardisClient(address, user, pass);
	}

	@After
	public void tearDown() throws Exception {
		client = null;
		logger.debug("\n");
	}

	/****************
	 * Test Methods *
	 ****************/

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
		tree = this.buildExperiment(client);
		tree.setDatasets(this.buildDatasets(client));

		// post Experiment Tree
		String uri = tree.post();
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
		Date now = new Date();
		String created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(now);
		experiment.setCreatedTime(created);
		experiment.setInstitutionName("RMIT University.");

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
		tree01.setDatasets(this.buildDatasetFiles01(client));
		result.add(tree01);

		// dataset 02
		DatasetTree tree02 = new DatasetTree(client);
		dataset = tree02.getDataset();
		dataset.setDescription("Iteration #02");
		dataset.setDirectory("dataset02");
		tree02.addParameter(ns_iteration, "name", "02");
		tree02.setDatasets(this.buildDatasetFiles02(client));
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
		String filename = "results(1).xml";
		file.setDirectory(this.getDirectory(filename));
		file.setFilename(filename);
		result.add(tree01);

		// group.composite
		DatafileTree tree02 = new DatafileTree(client);
		file = tree02.getDatafile();
		filename = "group(1).composite";
		file.setDirectory(this.getDirectory(filename));
		file.setFilename(filename);
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
		String filename = "results(2).xml";
		file.setDirectory(this.getDirectory(filename));
		file.setFilename(filename);
		result.add(tree01);

		// group.composite
		DatafileTree tree02 = new DatafileTree(client);
		file = tree02.getDatafile();
		filename = "group(2).composite";
		file.setDirectory(this.getDirectory(filename));
		file.setFilename(filename);
		result.add(tree02);

		// finished
		return result;
	}

	private String getDirectory(String filename) {
		String result = null;
		
		URL url = getClass().getResource("/" + filename);
		assertNotNull("Test file[" + filename + "] not found!", url);
		try {
			File file = new File(URLDecoder.decode( url.getFile(), "UTF-8" ) );
			result = file.getParent();
		} catch (UnsupportedEncodingException e) {
			fail("failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

}