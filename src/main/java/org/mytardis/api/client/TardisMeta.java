package org.mytardis.api.client;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Encapsulates the meta-data associated with a Tardis Response.
 * 
 * @author Nick May 
 * @version 1.0
 *
 */
public class TardisMeta {

	@SerializedName("limit")
	@Expose
	private Integer limit;

	@SerializedName("next")
	@Expose
	private String next;

	@SerializedName("offset")
	@Expose
	private Integer offset;

	@SerializedName("previous")
	@Expose
	private String previous;

	@SerializedName("total_count")
	@Expose
	private Integer totalCount;
	
	/******************
	 * Public Methods *
	 ******************/

	/**
	 * Get the page limit.
	 * @return Integer: limit.
	 */
	public Integer getLimit() {
		if (limit == null) {
			limit = new Integer(0);
		}
		return limit;
	}

	/**
	 * Set the page limit.
	 * @param limit
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * Get the uri of the next page.
	 * @return String: next.
	 */
	public String getNext() {
		return next;
	}

	/**
	 * Set the uri of the next page.
	 * @param next
	 */
	public void setNext(String next) {
		this.next = next;
	}

	/**
	 * Get the page offset counter.
	 * @return Integer: offset.
	 */
	public Integer getOffset() {
		return offset;
	}

	/**
	 * Set the page offset counter.
	 * @param offset
	 */
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	/**
	 * Increment the page offset counter.
	 * @return Integer: incremented offset
	 */
	public Integer incOffset() {
		if (this.offset == null) {
			this.offset = new Integer(0);
		} else {
			this.offset++;
		}
		return this.getOffset();
	}

	/**
	 * Get the uri of the previous page.
	 * @return String: previous.
	 */
	public String getPrevious() {
		return previous;
	}

	/**
	 * Set the uri of the previous page.
	 * @param previous 
	 */
	public void setPrevious(String previous) {
		this.previous = previous;
	}

	/**
	 * Get the total count across all pages;
	 * @return Integer: total.
	 */
	public Integer getTotalCount() {
		return totalCount;
	}

	/**
	 * Set the total count across all pages;
	 * @param totalCount
	 */
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
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
