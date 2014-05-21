package org.mytardis.api.client;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.google.gson.reflect.TypeToken;

/**
 * Represents an object returned by the myTardis API. 
 * 
 * @author Nick May 
 * @version 1.0
 */
public abstract class TardisObject {
	
	public abstract String getResourceUri();
	
	/******************
	 * Static Methods *
	 ******************/
	public static final String NO_DEFAULT = "No default provided.";

	private static Map<Class<? extends TardisObject>, String> classes = 
			new HashMap<Class<? extends TardisObject>, String>();

	/**
	 * Register path for each RESTful resource.
	 * @param clazz : the class of the TardisObject associated with a resource.
	 * @param path : the path associated with a resource
	 */
	public static <T extends TardisObject> void register(Class<T> clazz,
			String path) {

		if (clazz != null && path != null && !path.isEmpty()) {
			classes.put(clazz, path);
		}

		// finished
		return;
	}

	/**
	 * Get the path associated with a TardisObject.
	 * @param clazz : the class of the TardisObject.
	 * @return String : the associated path.
	 */
	public static <T extends TardisObject> String path(Class<T> clazz) {
		Logger logger = LogManager.getLogger(clazz);
		logger.debug("start! class = " + clazz);
		String result = null;
		
		for (Class<? extends TardisObject> item : classes.keySet()) {
			if (clazz.isAssignableFrom(item)) {
				result = classes.get(item);
			}
		}

		// finished
		return result;
	}

	/**
	 * Get the Type of the TardisResponse associated with a TardisObject.
	 * @param clazz : the class of the TardisObject.
	 * @return Type : the type of the TardisResponse.
	 */
	public static <T extends TardisObject> Type responseType(Class<T> clazz) {
		Logger logger = LogManager.getLogger(clazz);
		logger.debug("start! class = " + clazz);
		Type result = null;

		if (clazz.isAssignableFrom(Datafileparameter.class)) {
			result = new TypeToken<TardisResponse<Datafileparameter>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Datafileparameterset.class)) {
			result = new TypeToken<TardisResponse<Datafileparameterset>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Dataset.class)) {
			result = new TypeToken<TardisResponse<Dataset>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(DatasetFile.class)) {
			result = new TypeToken<TardisResponse<DatasetFile>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Datasetparameter.class)) {
			result = new TypeToken<TardisResponse<Datasetparameter>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Datasetparameterset.class)) {
			result = new TypeToken<TardisResponse<Datasetparameterset>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Experiment.class)) {
			result = new TypeToken<TardisResponse<Experiment>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Experimentparameter.class)) {
			result = new TypeToken<TardisResponse<Experimentparameter>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Experimentparameterset.class)) {
			result = new TypeToken<TardisResponse<Experimentparameterset>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Location.class)) {
			result = new TypeToken<TardisResponse<Location>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Parametername.class)) {
			result = new TypeToken<TardisResponse<Parametername>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Replica.class)) {
			result = new TypeToken<TardisResponse<Replica>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(Schema.class)) {
			result = new TypeToken<TardisResponse<Schema>>() {
			}.getType();
		}
		if (clazz.isAssignableFrom(User.class)) {
			result = new TypeToken<TardisResponse<User>>() {
			}.getType();
		}

		// finished
		return result;
	}
}
