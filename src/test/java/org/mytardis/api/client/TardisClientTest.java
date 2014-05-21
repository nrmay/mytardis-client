package org.mytardis.api.client;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mytardis.api.client.TardisClient;
import org.mytardis.api.model.Dataset;
import org.mytardis.api.model.DatasetFile;
import org.mytardis.api.model.Experiment;
import org.mytardis.api.model.Parametername;
import org.mytardis.api.model.Schema;
import org.mytardis.api.model.User;

public class TardisClientTest {

	private TardisClient client = null;
	private Logger logger = LogManager.getLogger(this.getClass());
	private String expectedURI = "http://130.220.210.149";
	private String expectedVersion = "/api/v1";

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
	public void testPostExperiment() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);

		// create schema
		Experiment object = new Experiment();
		object.setResourceUri("/api/v1/experiment/");
		object.setCreatedBy("/api/v1/user/1/");
		object.setTitle("test experiment");

		try {
			String uri = client.postObject(object);
			logger.debug("response.resourUri = " + uri);
			assertNotNull("uri is null!", uri);
			assertTrue("uri not an experiment resource!",
					uri.contains(TardisClient.version + "/experiment/"));
		} catch (Exception e) {
			fail("post experiment failed with: " + e.getMessage());
		}

		// finished
		return;
	}

	@Test
	public void testGetParameternames() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		String exp_namespace = "http://org.walroz.wsr/server";
		String job_namespace = "http://org.walroz.wsr/job";
		String iter_namespace = "http://org.walroz.wsr/iteration";

		List<Parametername> names = client.getParameternames(exp_namespace);
		assertNotNull("names is null!", names);
		assertEquals("names count not matched!", Integer.valueOf(10),
				Integer.valueOf(names.size()));

		names = client.getParameternames(job_namespace);
		assertNotNull("names is null!", names);
		assertEquals("names count not matched!", Integer.valueOf(7),
				Integer.valueOf(names.size()));

		names = client.getParameternames(iter_namespace);
		assertNotNull("names is null!", names);
		assertEquals("names count not matched!", Integer.valueOf(2),
				Integer.valueOf(names.size()));
		Schema schema = client.getSchema(iter_namespace);
		assertNotNull("schema is null!", schema);
		for (Parametername name : names) {
			assertNotNull("name.id is null", name.getId());
			assertNotNull("name.name is null", name.getName());
			assertNotNull("name.schema is null", name.getSchema());
			assertEquals("name.schema not matched!", schema.getResourceUri(),
					name.getSchema());
			if (name.getName().equals("id")) {
				assertEquals("fullname [id] not matched!", "Iteration Number",
						name.getFullName());
			} else if (name.getName().equals("name")) {
				assertEquals("fullname [name]  not matched!", "Iteration Name",
						name.getFullName());
			} else {
				fail("name unknown = " + name.getName());
			}
		}

		// finished
		return;
	}

	@Test
	public void testGetSchemas() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		String exp_namespace = "http://org.walroz.wsr/server";
		String job_namespace = "http://org.walroz.wsr/job";
		String iter_namespace = "http://org.walroz.wsr/iteration";

		Schema schema = client.getSchema(exp_namespace);
		assertNotNull("schema is null", schema);
		assertNotNull("schema resourceUri is null!", schema.getResourceUri());
		assertNotNull("schema type is null!", schema.getType());
		assertEquals("schema type not matched!", Integer.valueOf(1),
				schema.getType());

		schema = client.getSchema(job_namespace);
		assertNotNull("schema is null", schema);
		assertNotNull("schema resourceUri is null!", schema.getResourceUri());
		assertNotNull("schema type is null!", schema.getType());
		assertEquals("schema type not matched!", Integer.valueOf(1),
				schema.getType());

		schema = client.getSchema(iter_namespace);
		assertNotNull("schema is null", schema);
		assertNotNull("schema resourceUri is null!", schema.getResourceUri());
		assertNotNull("schema type is null!", schema.getType());
		assertEquals("schema type not matched!", Integer.valueOf(2),
				schema.getType());

		schema = client.getSchema("asdfiopquwe");
		assertNull("schema is not null", schema);

		// finished
		return;
	}

	@Test
	public void testPostSchema() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);

		// create schema
		Schema schema = new Schema();
		schema.setName("test");
		schema.setNamespace("http://org.walroz.wsr/server");
		schema.setResourceUri("/api/v1/schema/2/");
		schema.setType(1);

		try {
			client.postObject(schema);
			fail("post(schema) did not fail!");
		} catch (Exception e) {
			// check message
			assertNotNull("exception.message is null!", e.getMessage());
			assertEquals("exception.message not matched!",
					"response status[501] info[NOT IMPLEMENTED]",
					e.getMessage());
		}

		// finished
		return;
	}

	@Test
	public void testVersion() {
		logger.debug("start!");
		assertNotNull("TardisClient.version is null!", TardisClient.version);
		assertEquals("TardisClient.version not matched!", this.expectedVersion,
				TardisClient.version);
		// finished
		return;
	}

	@Test
	public void testInvalidCredentials() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		// set invalid credentials
		client = new TardisClient(address, "asdaaewfawe", "fdffasdfase");
		List<User> users = null;
		try {
			users = client.getObjects(User.class);
			assertNotNull("users is null", users);
			fail("invalid credentials did not throw an exception");
		} catch (Exception e) {
			assertNotNull("message is null!", e.getMessage());
		}

		// finished
		return;
	}

	@Test
	public void testGetUserByUsername() {
		logger.debug("start!");

		// valid username
		String username = "admin";
		User user = client.getUserByUsername(username);
		assertNotNull("username[" + username + "]: user is null!", user);

		// invalid username
		username = "dummy";
		user = client.getUserByUsername(username);
		assertNull("username[" + username + "]: user is not null!", user);

		// finished
		return;
	}

	@Test
	public void testGetUsers() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		// set invalid credentials
		List<User> users = null;
		try {
			users = client.getObjects(User.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}

		assertNotNull("user is null!", users.get(0));
		assertEquals("user.username not matched!", "admin", users.get(0)
				.getUsername());
		assertEquals("user.first_name not matched!", "Ad", users.get(0)
				.getFirstName());
		assertEquals("user.last_name not matched!", "Ministrator", users.get(0)
				.getLastName());
		assertEquals("user.resourceUri not matched!", "/api/v1/user/1/", users
				.get(0).getResourceUri());

		// finished
		return;
	}

	@Test
	public void testGetExperiments() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		List<Experiment> result = null;
		try {
			result = client.getObjects(Experiment.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}
		assertNotNull("experiments is null!", result);
		assertFalse("experiments is empty!", result.isEmpty());
		assertTrue("count not matched!", (result.size() > 0));

		for (Experiment item : result) {
			logger.debug("item = " + item.toString());
		}
		Experiment experiment = result.get(0);
		assertEquals("experiment.createdBy not matched!", "/api/v1/user/1/",
				experiment.getCreatedBy());

		User user = client.getUser(experiment.getCreatedBy().toString());
		logger.debug("user = " + user.toString());
		assertNotNull("createdBy user in null!", user);
		assertNotNull("user.firstname is null!", user.getFirstName());
		assertNotNull("user.lastname is null!", user.getLastName());
		assertEquals("user.firstname not matched!", "Ad", user.getFirstName());
		assertEquals("user.lastname not matched!", "Ministrator",
				user.getLastName());

		// finished
		return;
	}

	@Test
	public void testGetDatasets() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		List<Dataset> result = null;
		try {
			result = client.getObjects(Dataset.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}
		assertNotNull("datasets is null!", result);
		assertFalse("datasets is empty!", result.isEmpty());
		assertTrue("count not matched!", (result.size() == 1));

		for (Dataset item : result) {
			logger.debug("item = " + item.toString());
		}

		// finished
		return;
	}

	@Test
	public void testGetDatasetFiles() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		// test with a page limit of 0
		List<DatasetFile> result = null;
		try {
			result = client.getObjects(DatasetFile.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}
		assertNotNull("datasetFiles is null!", result);
		assertFalse("datasetFiles is empty!", result.isEmpty());
		assertEquals("count not matched!", Integer.valueOf(2),
				Integer.valueOf(result.size()));

		for (DatasetFile item : result) {
			logger.debug("item.id       = " + item.getId());
			logger.debug("    .filename = " + item.getFilename());
			logger.debug("    .mimetype = " + item.getMimetype());
			logger.debug("    .size     = "
					+ (Integer.parseInt(item.getSize()) / 1024) + " (KB)");
		}

		// finished
		return;
	}

	@Test
	public void testGetDatasetFilesOnePerPage() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		// test with a page limit of 1
		List<DatasetFile> result = null;
		try {
			client.setLimit(1);
			result = client.getObjects(DatasetFile.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}
		assertNotNull("datasetFiles is null!", result);
		assertFalse("datasetFiles is empty!", result.isEmpty());
		assertEquals("count not matched!", Integer.valueOf(2),
				Integer.valueOf(result.size()));

		// finished
		return;
	}

	@Test
	public void testGetDatasetFilesOneHundredPerPage() {
		logger.debug("start!");
		assertNotNull("TardisClient is null!", client);
		assertNotNull("TardisClient.URI is null!", client.getURI());
		assertEquals("TardisClient.URI not matched!", this.expectedURI, client
				.getURI().toString());

		// test with a page limit of 1
		List<DatasetFile> result = null;
		try {
			client.setLimit(100);
			result = client.getObjects(DatasetFile.class);
		} catch (Exception e) {
			fail("valid credentials threw an exception: " + e.getMessage());
		}
		assertNotNull("datasetFiles is null!", result);
		assertFalse("datasetFiles is empty!", result.isEmpty());
		assertEquals("count not matched!", Integer.valueOf(2),
				Integer.valueOf(result.size()));

		// finished
		return;
	}

}
