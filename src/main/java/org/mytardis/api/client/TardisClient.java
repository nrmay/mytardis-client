package org.mytardis.api.client;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.OpendesktopMimeDetector;
import eu.medsea.mimeutil.detector.WindowsRegistryMimeDetector;

/**
 * A client for the myTardis RESTful API.
 * 
 * @author Nick May
 * @version 1.0
 * 
 */
public class TardisClient {

	public static final String API_VERSION_1 = "/api/v1";
	public static final String URL_ENDING = "/";
	public static final String DOWNLOAD = "download";
	public static final String NO_DEFAULT_PROVIDED = "No default provided.";
	
	private Logger logger = LogManager.getLogger(this.getClass());
	private Gson gson = new Gson();
	private String protocol = "http";
	private String address = "";
	private String user = "";
	private String pass = "";
	private URI uri = null;
	private Integer limit = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private MimeUtil2 mimeUtil = null;
	private Set<String> errors = new HashSet<String>();

	/**********************
	 * Constructors *
	 **********************/

	/**
	 * TardisClient default constructor.
	 * 
	 * @param address
	 *            domain---or ip address---and port of the myTardis server.
	 * @param username
	 *            authentication user.
	 * @param passwd
	 *            authentication password.
	 * @param protocol
	 *            protocol to use for the request.
	 */
	public TardisClient(String address, String username, String passwd, String protocol) {
		super();
		this.address = address;
		this.user = username;
		this.pass = passwd;		
		this.protocol = protocol;
		
		// initialize date format
		sdf.setLenient(false);

		// set up tardis uri
		try {
			this.uri = new URI(this.protocol + "://" + this.address);
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
	
	/**
	 * TardisClient default constructor.
	 * 
	 * @param address
	 *            domain---or ip address---and port of the myTardis server.
	 * @param username
	 *            authentication user.
	 * @param passwd
	 *            authentication password.
	 */
	public TardisClient(String address, String username, String passwd) {
		this(address, username, passwd, "http");
	}

	/***************
	 * Get Methods *
	 ***************/

	/**
	 * Get list of Parameternames by Schema
	 * 
	 * @param namespace
	 *            of schema.
	 * @return List of Parametername objects.
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
	 *            of schema.
	 * @return schema object.
	 */
	public Schema getSchema(String namespace) {
		logger.debug("start!");
		Schema result = null;
		this.clearErrors();
		
		List<Schema> schemas;
		try {
			schemas = this.getObjects(Schema.class);
			if (schemas != null) {
				logger.debug("schemas.count = " + schemas.size());
				for (Schema item : schemas) {
					if (item.getNamespace().equals(namespace)) {
						result = item;
						logger.debug("schema[" + result.getNamespace()
								+ "] found! param.count = ");
						break;
					}
				}
			}
		} catch (Exception e) {
			String message = "get schema failed with: " + e.getMessage();
			logger.debug(message);
			this.addError(message);
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
	 * Get a list of objects for the given TardisObject class.
	 * 
	 * @param clazz
	 *            extends TardisObject.
	 * @return List of TardisObjects.
	 * @throws Exception
	 *             thrown when an invalid response is returned.
	 */
	public <T extends TardisObject> List<T> getObjects(Class<T> clazz)
			throws Exception {
		logger.debug("start!");
		List<T> result = new ArrayList<T>();

		// create request meta
		TardisMeta meta = new TardisMeta();
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
			Response response = target.path(URL_ENDING).queryParam("format", "json").request()
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

	/**
	 * Get an Object by Id
	 * 
	 * @param clazz
	 *            of the object.
	 * @param id
	 *            of the object.
	 * @return TardisObject of the required class.
	 * @throws Exception
	 *             thrown if the object is not found.
	 */
	public <T extends TardisObject> TardisObject getObjectById(Class<T> clazz,
			Integer id) throws Exception {
		logger.debug("start!");
		T result = null;

		// make request
		WebTarget target = this.buildWebTarget(null);
		Response response = target.path(TardisClient.API_VERSION_1)
				.path(TardisObject.path(clazz)).path(id.toString())
				.path(URL_ENDING).queryParam("format", "json")
				.request().get();

		// check response
		this.checkResponse(response);
		result = gson.fromJson(response.readEntity(String.class), clazz);

		// finished
		return result;
	}

	/**
	 * Get an Object by URI
	 * 
	 * @param clazz
	 *            of the object.
	 * @param uri
	 *            of the object as a String.
	 * @return TardisObject of the required class.
	 * @throws Exception
	 *             thrown if the object is not found.
	 */
	public <T extends TardisObject> TardisObject getObjectByUri(Class<T> clazz,
			String uri) throws Exception {
		logger.debug("start!");
		T result = null;

		// make request
		WebTarget target = this.buildWebTarget(null);
		Response response = target.path(uri).queryParam("format", "json")
				.request().get();

		// check response
		this.checkResponse(response);
		result = gson.fromJson(response.readEntity(String.class), clazz);

		// finished
		return result;
	}

	/**
	 * Get the content of a DatasetFile by Id
	 * 
	 * @param id
	 *            of the DatasetFile.
	 * @return content of the required DatasetFile as a String.
	 * @throws Exception
	 *             thrown if the object is not found.
	 */
	public String getDatasetFileContentById(Integer id) throws Exception {
		logger.debug("start!");
		String result = null;

		// make request
		WebTarget target = this.buildWebTarget(null);
		Response response = target.path(TardisClient.API_VERSION_1)
				.path(TardisObject.path(DatasetFile.class)).path(id.toString())
				.path(URL_ENDING).path(DOWNLOAD).path(URL_ENDING)
				.request().get();

		// check response
		this.checkResponse(response);
		result = response.readEntity(String.class);

		// finished
		return result;
	}
	
	/**
	 * Get the content of a DatasetFile by URI
	 * 
	 * @param uri
	 *            of the object as a String.
	 * @return content of the required DatasetFile as a String.
	 * @throws Exception
	 *             thrown if the object is not found.
	 */
	public String getDatasetFileContentByUri(String uri) throws Exception {
		logger.debug("start!");
		String result = null;

		// make request
		WebTarget target = this.buildWebTarget(null);
		Response response = target.path(uri).path(DOWNLOAD).path(URL_ENDING)
				.request().get();
		
		// check response
		this.checkResponse(response);
		result = response.readEntity(String.class);

		// finished
		return result;
	}
	
	/****************
	 * Post Methods *
	 ****************/

	/**
	 * Post an object to Tardis.
	 * 
	 * @param object
	 *            a TardisObject
	 * @return String the object's resource URI.
	 * @throws Exception
	 *             on a failed request.
	 */
	public String postObject(TardisObject object) throws Exception {
		logger.debug("start! json = " + gson.toJson(object));
		String result = null;

		// create meta
		TardisMeta meta = new TardisMeta();
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
				if (location.contains(TardisClient.API_VERSION_1)) {
					String[] parts = location.split(TardisClient.API_VERSION_1);
					result = TardisClient.API_VERSION_1
							+ parts[parts.length - 1];
				}
			}
		}
		// finished
		return result;
	}

	/**
	 * Post an object and file to Tardis.
	 * 
	 * @param object
	 *            a DatasetFile.
	 * @param file
	 *            the file to be uploaded.
	 * @return String the object's resource URI.
	 * @throws Exception
	 *             on a failed request.
	 */
	public String postObjectAndFile(DatasetFile object, File file)
			throws Exception {
		logger.debug("start! json = " + gson.toJson(object));
		String result = null;

		// create meta
		TardisMeta meta = new TardisMeta();
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
				if (location.contains(TardisClient.API_VERSION_1)) {
					String[] parts = location.split(TardisClient.API_VERSION_1);
					result = TardisClient.API_VERSION_1
							+ parts[parts.length - 1];
				}
			}
		}

		// finished
		return result;
	}

	/*******************
	 * Public Methods *
	 *******************/

	/**
	 * Format Date object to Tardis Format.
	 * 
	 * @param date
	 *            Date
	 * @return String in format 'yyyy-MM-ddTHH:mm:ss'
	 */
	public String formatDate(Date date) {
		return this.sdf.format(date);
	}

	/**
	 * Check the String for a valid Date.
	 * 
	 * @param date
	 *            as a String.
	 * @return True only if the date parses successfully.
	 */
	public boolean checkDate(String date) {
		logger.debug("start!");
		boolean result = false;

		try {
			Date parsedDate = sdf.parse(date);
			result = true;
			logger.debug("parsed date = " + parsedDate.toString());
		} catch (ParseException e) {
			logger.debug("parse date[" + date + "] failed with: "
					+ e.getMessage());
		}

		// finished
		return result;
	}

	/**
	 * Get the mime type of a file.
	 * 
	 * @param file
	 *            target.
	 * @return MimeType as a String.
	 */
	public String getMimeType(File file) {
		logger.debug("start!");
		String result = null;

		try {
			if (this.mimeUtil == null) {
				this.mimeUtil = new MimeUtil2();
				this.mimeUtil
						.registerMimeDetector(OpendesktopMimeDetector.class
								.getName());
				this.mimeUtil.registerMimeDetector(MagicMimeMimeDetector.class
						.getName());
				this.mimeUtil.registerMimeDetector(ExtensionMimeDetector.class
						.getName());
				this.mimeUtil
						.registerMimeDetector(WindowsRegistryMimeDetector.class
								.getName());
				// logger.debug("known mimeTypes: "
				// + MimeUtil2.getKnownMimeTypes().toString());
			}

			result = MimeUtil2.getMostSpecificMimeType(
					this.mimeUtil.getMimeTypes(file)).toString();

			logger.debug("file[" + file.getName() + "] mimeType[" + result
					+ "]");

		} catch (Exception e) {
			logger.debug("failed with: " + e.getMessage());
		}

		// finished
		return result;
	}

	/*******************
	 * Private Methods *
	 *******************/

	private WebTarget buildWebTarget(TardisMeta meta) {
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

		if (meta != null && meta.getNext() != null) {
			String path = meta.getNext().split("\\?")[0];
			if (path.startsWith(TardisClient.API_VERSION_1)) {
				result = result.path(path);
			} else {
				result = result.path(TardisClient.API_VERSION_1).path(path);
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
		this.clearErrors();
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
					+ "] info[" + response.getStatusInfo() + "] = "
					+ response.toString();
			logger.debug(message);
			throw new Exception(message);
		} else {
			// other
			String message = "response status[" + response.getStatus()
					+ "] not ok!";
			logger.error(message);
			this.addError(message);
		}
	}

	private void clearErrors() {
		errors = new HashSet<String>();
		return;
	}

	private void addError(String error) {
		if (error != null && !error.isEmpty()) {
			errors.add(error);
		}
		return;
	}

	/***********************
	 * getters and setters *
	 ***********************/

		
	/**
	 * @return protocol used for the requests (default http).
	 */
	public String getProtocol() {
		return this.protocol;
	}
	
	/**
	 * @return URI of myTardis API.
	 */
	public URI getURI() {
		return this.uri;
	}

	/**
	 * Get the Tardis Response page limit.
	 * 
	 * @return Integer page limit.
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * Set Tardis Response page limit
	 * 
	 * @param limit
	 *            as an Integer.
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * Get set of error messages.
	 * 
	 * @return set of Strings.
	 */
	public Set<String> getErrors() {
		return errors;
	}

}
