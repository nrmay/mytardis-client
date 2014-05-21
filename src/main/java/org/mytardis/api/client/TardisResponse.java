package org.mytardis.api.client;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * A Tardis Response.
 * 
 * @author Nick May 
 * @version 1.0
 * @param <T extends TardisObject> Expected Object Class.
 */
public class TardisResponse<T extends TardisObject> {

	@SerializedName("meta")
	@Expose
	private Meta meta;

	@SerializedName("objects")
	@Expose
	private T[] objects;

	/**
	 * Get the response meta object.
	 * @return Meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 * Set the response meta object.
	 * @param meta : the object containing the response meta-data.
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	/**
	 * Get the object array.
	 * @return T[] : array of TardisObjects.
	 */
	public T[] getObjects() {
		return objects;
	}

	/**
	 * Set the array of objects.
	 * @param objects : array of TardisObjects
	 */
	public void setObjects(T[] objects) {
		this.objects = objects;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

}
