
package org.mytardis.api.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mytardis.api.client.TardisObject;


/**
 * A replica
 * @author Nick May 
 * @version 1.0
 * 
 */
@Generated("org.jsonschema2pojo")
public class Replica extends TardisObject {

    @Expose
    private Object datafile = null;
    @Expose
    private Integer id;
    @Expose
    private Object location = null;
    @Expose
    private String protocol;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri = "No default provided.";
    @SerializedName("stay_remote")
    @Expose
    private Boolean stayRemote = false;
    @Expose
    private String url = "No default provided.";
    @Expose
    private Boolean verified = false;

    public Object getDatafile() {
        return datafile;
    }

    public void setDatafile(Object datafile) {
        this.datafile = datafile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public Boolean getStayRemote() {
        return stayRemote;
    }

    public void setStayRemote(Boolean stayRemote) {
        this.stayRemote = stayRemote;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
