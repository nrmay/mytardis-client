package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.mytardis.api.model.DatasetFile;

public class DatafileTree {
	
	private DatasetFile datafile = new DatasetFile();
	private List<ParametersetTree> parametersets = new ArrayList<ParametersetTree>();
	
	public DatasetFile getDatafile() {
		return datafile;
	}
	public void setDatafile(DatasetFile datafile) {
		this.datafile = datafile;
	}
	public List<ParametersetTree> getParametersets() {
		return parametersets;
	}
	public void setParametersets(List<ParametersetTree> parametersets) {
		this.parametersets = parametersets;
	}
	
}
