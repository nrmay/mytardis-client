
package org.mytardis.api.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mytardis.api.client.TardisObject;


/**
 * A dataset to file relation
 * @author Nick May 
 * @version 1.0
 * 
 */
@Generated("org.jsonschema2pojo")
public class DatasetFile extends TardisObject {

    @SerializedName("created_time")
    @Expose
    private Object createdTime = null;
    @Expose
    private String datafile = "No default provided.";
    @Expose
    private Object dataset = null;
    @Expose
    private String directory;
    @Expose
    private String filename = "No default provided.";
    @Expose
    private Integer id;
    @Expose
    private String md5sum;
    @Expose
    private String mimetype;
    @SerializedName("modification_time")
    @Expose
    private Object modificationTime = null;
    @SerializedName("parameter_sets")
    @Expose
    private Object parameterSets = null;
    @Expose
    private Object replicas = null;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri = "No default provided.";
    @Expose
    private String sha512sum;
    @Expose
    private String size;

    public Object getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Object createdTime) {
        this.createdTime = createdTime;
    }

    public String getDatafile() {
        return datafile;
    }

    public void setDatafile(String datafile) {
        this.datafile = datafile;
    }

    public Object getDataset() {
        return dataset;
    }

    public void setDataset(Object dataset) {
        this.dataset = dataset;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Object getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Object modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Object getParameterSets() {
        return parameterSets;
    }

    public void setParameterSets(Object parameterSets) {
        this.parameterSets = parameterSets;
    }

    public Object getReplicas() {
        return replicas;
    }

    public void setReplicas(Object replicas) {
        this.replicas = replicas;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getSha512sum() {
        return sha512sum;
    }

    public void setSha512sum(String sha512sum) {
        this.sha512sum = sha512sum;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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
