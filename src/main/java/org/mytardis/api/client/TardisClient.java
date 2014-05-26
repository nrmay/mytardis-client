package org.mytardis.api.client;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.mytardis.api.model.Datafileparameter;
import org.mytardis.api.model.Datafileparameterset;
import org.mytardis.api.model.Dataset;
import org.mytardis.api.model.DatasetFile;
import org.mytardis.api.model.Datasetparameter;
import org.mytardis.api.model.Datasetparameterset;
import org.mytardis.api.model.Experiment;
import org.mytardis.api.model.Experimentparameter;
import org.mytardis.api.model.Experimentparameterset;
import org.mytardis.api.model.Location;
import org.mytardis.api.model.Parametername;
import org.mytardis.api.model.Replica;
import org.mytardis.api.model.Schema;
import org.mytardis.api.model.User;

import com.google.gson.Gson;

/**
 * A client for the myTardis RESTful API.
 * 
 * @author Nick May
 * @version 1.0
 * 
 */
public class TardisClient {

	public static final String API_VERSION = "/api/v1";
	public static final String NO_DEFAULT_PROVIDED = "No default provided.";
//	public static final int PUBLIC_ACCESS_NONE  = 1;
//	public static final int PUBLIC_ACCESS_METADATA = 2;
//	public static final int PUBLIC_ACCESS_FULL = 3;
	
	private Logger logger = LogManager.getLogger(this.getClass());
	private Gson gson = new Gson();
	private String address = "";
	private String user = "";
	private String pass = "";
	private URI uri = null;
	private Integer limit = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	/**********************
	 * Constructors *
	 **********************/

	/**
	 * TardisClient default constructor.
	 * 
	 * @param address
	 *            : domain---or ip address---and port of the myTardis server.
	 * @param username
	 *            : authentication user.
	 * @param passwd
	 *            : authentication password.
	 */
	public TardisClient(String address, String username, String passwd) {
		super();
		this.address = address;
		this.user = username;
		this.pass = passwd;

		// set up tardis uri
		try {
			this.uri = new URI("http://" + address);
		} catch (URISyntaxException e) {
			logger.error("failed to create tardis url with: " + e.getMessage());
		}

		// register object classes
		TardisObject.register(Datafileparameter.class, "datafileparameter");
		TardisObject.register(Datafileparameterset.class,
				"datafileparameterset");
		TardisObject.register(DatasetFile.class, "dataset_file");
		TardisObject.register(Dataset.class, "dataset");
		TardisObject.register(Datasetparameter.class, "datasetparameter");
		TardisObject.register(Datasetparameterset.class, "datasetparameterset");
		TardisObject.register(Experiment.class, "experiment");
		TardisObject.register(Experimentparameter.class, "experimentparameter");
		TardisObject.register(Experimentparameterset.class,
				"experimentparameterset");
		TardisObject.register(Location.class, "location");
		TardisObject.register(Parametername.class, "parametername");
		TardisObject.register(Replica.class, "replica");
		TardisObject.register(Schema.class, "schema");
		TardisObject.register(User.class, "user");

		// finished
		return;
	}

	/***************
	 * Get Methods *
	 ***************/

	/**
	 * Get list of Parameternames by Schema
	 * 
	 * @param namespace
	 * @return List<Paramtername>
	 */
	public List<Parametername> getParameternames(String namespace) {
		logger.debug("start!");
		List<Parametername> result = new ArrayList<Parametername>();

		Schema schema = this.getSchema(namespace);
		if (schema != null && schema.getResourceUri() != null) {
			try {
				List<Parametername> names = this
						.getObjects(Parametername.class);
				if (names != null) {
					for (Parametername item : names) {
						if (item.getSchema().toString()
								.equals(schema.getResourceUri())) {
							result.add(item);
						}
					}
				}
			} catch (Exception e) {
				logger.debug("get schema failed with: " + e.getMessage());
			}
		}

		// finished
		return result;
	}

	/**
	 * Get an Schema by Namespace
	 * 
	 * @param namespace
	 * @return schema
	 */
	public Schema getSchema(String namespace) {
		logger.debug("start!");
		Schema result = null;

		List<Schema> schemas;
		try {
			schemas = this.getObjects(Schema.class);
			if (schemas != null) {
				for (Schema item : schemas) {
					if (item.getNamespace().toString().equals(namespace)) {
						result = item;
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.debug("get schema failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

	/**
	 * Get a User by Username.
	 * 
	 * @param username
	 * @return User, or null if the username is not found.
	 */
	public User getUserByUsername(String username) {
		logger.debug("start!");
		User result = null;

		try {
			List<User> users = this.getObjects(User.class);
			for (User user : users) {
				if (user.getUsername().equals(username)) {
					result = user;
					break;
				}
			}
		} catch (Exception e) {
			logger.error("failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

	/**
	 * Get a User by resource URI.
	 * 
	 * @param uri
	 *            : the resource uri of the user
	 * @return User : or null if the URI is invalid.
	 */
	public User getUser(String uri) {
		logger.debug("start!");
		User result = null;

		try {
			List<User> users = this.getObjects(User.class);
			if (user != null) {
				if (users.size() > 1) {
					throw new Exception("Duplicate User found for path[" + uri
							+ "]");
				}
				result = users.get(0);
			}
		} catch (Exception e) {
			logger.debug("failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

	/**
	 * Get a list of objects for the given TardisObject class.
	 * 
	 * @param clazz
	 *            extends TardisObject.
	 * @return List of TardisObjects.
	 * @throws Exception
	 *             : thrown when an invalid response is returned.
	 */
	public <T extends TardisObject> List<T> getObjects(Class<T> clazz)
			throws Exception {
		logger.debug("start!");
		List<T> result = new ArrayList<T>();

		// create request meta
		Meta meta = new Meta();
		meta.setNext(TardisObject.path(clazz));
		if (this.limit != null && this.limit > 0) {
			meta.setLimit(this.limit);
		}

		// process requests while next
		while (meta != null && meta.getNext() != null) {
			// increment offset
			meta.incOffset();

			// build target
			WebTarget target = this.buildWebTarget(meta);
			logger.debug("target = " + target.toString());

			// make request
			Response response = target.queryParam("format", "json").request()
					.get();

			// parse response
			this.checkResponse(response);
			TardisResponse<T> tresp = gson.fromJson(
					response.readEntity(String.class),
					TardisObject.responseType(clazz));

			// clear path
			meta = null;
			if (tresp != null) {
				// unpack objects
				if (tresp.getObjects() != null) {
					result.addAll(Arrays.asList((T[]) tresp.getObjects()));
				}
				// check next path
				if (tresp.getMeta() != null) {
					meta = tresp.getMeta();
					logger.debug("new Meta = " + meta.toString());
				}
			}
		}
		// finished
		return result;
	}

	/****************
	 * Post Methods *
	 ****************/

	/**
	 * Post the object to Tardis.
	 * 
	 * @param object
	 *            : TardisObject
	 */
	public String postObject(TardisObject object) throws Exception {
		logger.debug("start! json = " + gson.toJson(object));
		String result = null;

		// create meta
		Meta meta = new Meta();
		if (object != null && object.getResourceUri() != null) {
			meta.setNext(object.getResourceUri());
		} else {
			meta.setNext(TardisObject.path(object.getClass()));
		}

		// build target
		WebTarget target = this.buildWebTarget(meta);
		logger.debug("target = " + target.toString());

		// make request
		Response response = target
				.queryParam("format", "json")
				.request()
				.post(Entity.entity(gson.toJson(object),
						MediaType.APPLICATION_JSON_TYPE));

		// parse response
		this.checkResponse(response);
		if (response != null) {
			logger.debug("response.header = "
					+ response.getHeaderString("location"));
			String location = response.getHeaderString("location");
			if (location != null) {
				if (location.contains(TardisClient.API_VERSION)) {
					String[] parts = location.split(TardisClient.API_VERSION);
					result = TardisClient.API_VERSION + parts[parts.length - 1];
				}
			}
		}
		// finished
		return result;
	}

	public String postMultipart(TardisObject object, File file)
			throws Exception {
		logger.debug("start! json = " + gson.toJson(object));
		String result = null;

		// create meta
		Meta meta = new Meta();
		if (object != null && object.getResourceUri() != null) {
			meta.setNext(object.getResourceUri());
		} else {
			meta.setNext(TardisObject.path(object.getClass()));
		}

		// build target
		WebTarget target = this.buildWebTarget(meta);
		logger.debug("target = " + target.toString());

		// make request
		final BodyPart jsonPart = new FormDataBodyPart("json_data",
				gson.toJson(object), MediaType.APPLICATION_JSON_TYPE);
		final BodyPart filePart = new FileDataBodyPart("attached_file", file);
		FormDataMultiPart multipart = new FormDataMultiPart();
		multipart.bodyPart(jsonPart);
		multipart.bodyPart(filePart);
		logger.debug("multipart mediatype = " + multipart.getMediaType());

		Response response = target
				.register(MultiPartFeature.class)
				.request()
				.post(Entity.entity(multipart,
						MediaType.MULTIPART_FORM_DATA_TYPE));

		// parse response
		this.checkResponse(response);
		if (response != null) {
			logger.debug("response.header = "
					+ response.getHeaderString("location"));
			String location = response.getHeaderString("location");
			if (location != null) {
				if (location.contains(TardisClient.API_VERSION)) {
					String[] parts = location.split(TardisClient.API_VERSION);
					result = TardisClient.API_VERSION + parts[parts.length - 1];
				}
			}
		}

		// finished
		return result;
	}
	
	/**
	 * Format Date object to Tardis Format.
	 * 
	 * @param date : Date
	 * @return String : in format 'yyyy-MM-ddTHH:mm:ss'
	 */
	public String formatDate(Date date) {
		return this.sdf.format(date);
	}

	/*******************
	 * Private Methods *
	 *******************/

	private WebTarget buildWebTarget(Meta meta) {
		logger.debug("start! meta[" + meta + "]");
		WebTarget result = null;

		// build client
		Client client = ClientBuilder.newClient();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(
				this.user, this.pass);

		// add user object
		result = client.target(uri).register(feature)
				.register(MediaType.APPLICATION_JSON_TYPE);

		logger.debug("target = " + result.toString());

		if (meta.getNext() != null) {
			String path = meta.getNext().split("\\?")[0];
			if (path.startsWith(TardisClient.API_VERSION)) {
				result = result.path(path);
			} else {
				result = result.path(TardisClient.API_VERSION).path(path);
			}
			if (meta.getLimit() != null && meta.getLimit() > 0) {
				result = result.queryParam("limit", meta.getLimit());
			}
			if (meta.getOffset() != null && meta.getOffset() > 0) {
				result = result.queryParam("offset", meta.getOffset());
			}

		}

		// finished
		logger.debug("webtarget = " + result.toString());
		return result;
	}

	private void checkResponse(Response response) throws Exception {
		logger.debug("response = " + response.toString());
		if (response.getStatus() == 401) {
			// unauthorized
			String password = this.pass;
			if (this.pass != null && !this.pass.isEmpty()) {
				password = "provided";
			}
			String message = "user[" + this.user + "] with password["
					+ password + "] is unauthorized!";
			logger.error(message);
			throw new Exception(message);
		} else if (response.getStatus() == 200 || response.getStatus() == 201) {
			// ok
			logger.debug("response status[" + response.getStatus() + "] is ok!");
		} else if (response.getStatus() == 500 || response.getStatus() == 501) {
			// server error
			String message = "response status[" + response.getStatus()
					+ "] info[" + response.getStatusInfo() + "] = " + response.toString();
			logger.debug(message);
			throw new Exception(message);
		} else {
			// other
			logger.error("response status[" + response.getStatus()
					+ "] not ok!");
		}
	}
	
	/***********************
	 * getters and setters *
	 ***********************/

	/**
	 * @return URI of myTardis API.
	 */
	public URI getURI() {
		return this.uri;
	}

	/**
	 * @return myTardis server address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return authentication password.
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @return authentication username.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return page limit.
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * Set page limit
	 * 
	 * @param limit
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
