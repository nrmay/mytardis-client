package org.mytardis.api.client;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mytardis.api.client.ExperimentTree;
import org.mytardis.api.client.TardisClient;
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
		tree = new ExperimentTree(client);
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

		// build experiment
		tree.setTitle("Test Experiment (" + UUID.randomUUID().toString() + ")");
		tree.setDescription("A test experiment submitted via the API.");
		tree.setInstitution("SCSIT, RMIT University.");

		// build parameter sets
		String ns_server = "http://org.walroz.wsr/server";
		tree.addParameter(ns_server, "host", "wsrServer1");
		tree.addParameter(ns_server, "address", "127.0.0.1");
		tree.addParameter(ns_server, "port", "8080");
		tree.addParameter(ns_server, "version", "0.6");
		
		String ns_job = "http://org.walroz.wsr/job";
		tree.addParameter(ns_job, "name", "job01");
		tree.addParameter(ns_job, "test", "boundingTime");
		tree.addParameter(ns_job, "iterations", "8");
		tree.addParameter(ns_job, "composite", "parallel-8-base");
		tree.addParameter(ns_job, "lower_bound", "0");
		tree.addParameter(ns_job, "upper_bound", "1000");

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
						&& item.getTitle().equals(tree.getTitle())) {
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
}
