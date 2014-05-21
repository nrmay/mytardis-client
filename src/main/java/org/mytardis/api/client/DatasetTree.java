package org.mytardis.api.client;

import java.util.ArrayList;
import java.util.List;

import org.mytardis.api.model.Dataset;

public class DatasetTree {
	
	private Dataset dataset = new Dataset();
	private List<ParametersetTree> parametersets = new ArrayList<ParametersetTree>();
	private List<DatafileTree> datasets = new ArrayList<DatafileTree>();
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public List<ParametersetTree> getParametersets() {
		return parametersets;
	}
	public void setParametersets(List<ParametersetTree> parametersets) {
		this.parametersets = parametersets;
	}
	public List<DatafileTree> getDatasets() {
		return datasets;
	}
	public void setDatasets(List<DatafileTree> datasets) {
		this.datasets = datasets;
	}
}
