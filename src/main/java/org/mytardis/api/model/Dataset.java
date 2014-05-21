
package org.mytardis.api.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mytardis.api.client.TardisObject;


/**
 * A dataset
 * @author Nick May 
 * @version 1.0
 * 
 */
@Generated("org.jsonschema2pojo")
public class Dataset extends TardisObject {

    @Expose
    private String description;
    @Expose
    private String directory;
    @Expose
    private Object experiments = null;
    @Expose
    private Integer id;
    @Expose
    private Boolean immutable = false;
    @SerializedName("parameter_sets")
    @Expose
    private Object parameterSets = null;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri = "No default provided.";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Object getExperiments() {
        return experiments;
    }

    public void setExperiments(Object experiments) {
        this.experiments = experiments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getImmutable() {
        return immutable;
    }

    public void setImmutable(Boolean immutable) {
        this.immutable = immutable;
    }

    public Object getParameterSets() {
        return parameterSets;
    }

    public void setParameterSets(Object parameterSets) {
        this.parameterSets = parameterSets;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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
