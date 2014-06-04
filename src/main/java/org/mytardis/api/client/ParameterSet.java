package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

/**
 * A set of meta-data parameters.
 * @author Nick May
 * @version 1.0
 */
public class ParameterSet {
	
	@Expose
	private String schema = null;
	@Expose
	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * Default Constructor.
	 * @param schema : the namespace of the schema.
	 */
	public ParameterSet(String schema) {
		super();
		this.schema = schema;
	}
	
	/***********************
	 * Getters and Setters *
	 ***********************/
	
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * Add a parameter to the parameter set.
	 * @param name : parameter name
	 * @param value : parameter value
	 */
	public void addParameter(String name, String value) {
		Parameter param = new Parameter();
		param.setName(name);
		param.setValue(value);
		this.parameters.add(param);
	}

	/************
	 * Builders *
	 ************/

	
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
