
package org.mytardis.api.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mytardis.api.client.TardisObject;


/**
 * A parameter name
 * @author Nick May 
 * @version 1.0
 *
 */
@Generated("org.jsonschema2pojo")
public class Parametername extends TardisObject {

    @Expose
    private String choices;
    @SerializedName("comparison_type")
    @Expose
    private Integer comparisonType = 1;
    @SerializedName("data_type")
    @Expose
    private Integer dataType = 2;
    @SerializedName("full_name")
    @Expose
    private String fullName = "No default provided.";
    @Expose
    private Integer id;
    @Expose
    private Boolean immutable = false;
    @SerializedName("is_searchable")
    @Expose
    private Boolean isSearchable = false;
    @Expose
    private String name = "No default provided.";
    @Expose
    private Integer order = 9999;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri = "No default provided.";
    @Expose
    private Object schema = null;
    @Expose
    private String units;

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public Integer getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(Integer comparisonType) {
        this.comparisonType = comparisonType;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Boolean getIsSearchable() {
        return isSearchable;
    }

    public void setIsSearchable(Boolean isSearchable) {
        this.isSearchable = isSearchable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public Object getSchema() {
        return schema;
    }

    public void setSchema(Object schema) {
        this.schema = schema;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
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
