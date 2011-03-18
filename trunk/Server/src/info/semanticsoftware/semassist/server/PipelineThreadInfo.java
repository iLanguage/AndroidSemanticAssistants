package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;

import java.io.File;
import java.io.FilenameFilter;

public class PipelineThreadInfo {
	private String pipelineName;
	private int maxConcurrent;
	private String pipelineAppFileLocation;
	private boolean loadAtStatup;
	
	public PipelineThreadInfo(String pipelineName, int maxConcurrent, boolean loadAtStatup, String pipelineAppFileLocation) {
		setPipelineName(pipelineName);
		setMaxConcurrent(maxConcurrent);
		setLoadAtStatup(loadAtStatup);
		setPipelineAppFileLocation(pipelineAppFileLocation);
	}
	public String getPipelineName() {
		return pipelineName;
	}
	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
	public int getMaxConcurrent() {
		return maxConcurrent;
	}
	public void setMaxConcurrent(int maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}
	public boolean isLoadAtStatup() {
		return loadAtStatup;
	}
	public void setLoadAtStatup(boolean loadAtStatup) {
		this.loadAtStatup = loadAtStatup;
	}
	public void setPipelineAppFileLocation(String pipelineAppFileLocation) {
		this.pipelineAppFileLocation = pipelineAppFileLocation;
	}
	public String getPipelineAppFileLocation() {
		return pipelineAppFileLocation;
	}
	public String getPipelineAppFileName(){
		String retVal = "";
		File dir = new File(getPipelineAppFileLocation());
		if(dir.isDirectory()){
			FilenameFilter filter = new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".xgapp");
			    }
			};
			String[] children = dir.list(filter);
			if(children.length>=1){
				retVal = children[0];
			}
		}else{
			retVal = dir.getName();
		}
		return retVal;
	}
	public File getPipelineAppFile(){
		File f = null;
		File tmp = null;
		String fname = getPipelineAppFileName();
		tmp = new File(getPipelineAppFileLocation());
		if(tmp.isDirectory()){
			if(!fname.equals("")){
				f = new File(getPipelineAppFileLocation() + "/" + fname);
			}
		}else{
			f = new File(getPipelineAppFileLocation());
		}
		return f;
	}
}