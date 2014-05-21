
package org.mytardis.api.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mytardis.api.client.TardisObject;


/**
 * An experiment's parameter
 * @author Nick May 
 * @version 1.0
 *
 */
@Generated("org.jsonschema2pojo")
public class Experimentparameter extends TardisObject {

    @SerializedName("datetime_value")
    @Expose
    private Object datetimeValue = null;
    @Expose
    private Integer id;
    @Expose
    private Object name = null;
    @SerializedName("numerical_value")
    @Expose
    private Object numericalValue = null;
    @Expose
    private Object parameterset = null;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri = "No default provided.";
    @SerializedName("string_value")
    @Expose
    private String stringValue;
    @Expose
    private String value = "No default provided.";

    public Object getDatetimeValue() {
        return datetimeValue;
    }

    public void setDatetimeValue(Object datetimeValue) {
        this.datetimeValue = datetimeValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getNumericalValue() {
        return numericalValue;
    }

    public void setNumericalValue(Object numericalValue) {
        this.numericalValue = numericalValue;
    }

    public Object getParameterset() {
        return parameterset;
    }

    public void setParameterset(Object parameterset) {
        this.parameterset = parameterset;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
