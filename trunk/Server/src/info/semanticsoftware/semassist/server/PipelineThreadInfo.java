package info.semanticsoftware.semassist.server;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Class is used to extend the information of the GateServiceProcess
 * We store 
 * 		pipelineName : name of the pipeline
 * 		maxConcurrent : maximum number of threads we allow to run concurrently
 * 		number_pooled : maximum number of threads we allow to stay resident in memory
 * 		loadAtStatup : should the pipelines be loaded at server startup
 * 		pipelineAppFileLocation : location of the .gapp file
 *
 */
public class PipelineThreadInfo {
	private String pipelineName;
	private int maxConcurrent;
	private int number_pooled;
	private String pipelineAppFileLocation;
	private boolean loadAtStatup;
	
	public PipelineThreadInfo(String pipelineName, int numberPooled, boolean loadAtStatup, String pipelineAppFileLocation,int maxConcurrent) {
		setPipelineName(pipelineName);
		setMaxConcurrent(maxConcurrent);
		setLoadAtStatup(loadAtStatup);
		setPipelineAppFileLocation(pipelineAppFileLocation);
		setNumberPooled(numberPooled);
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
	public void setNumberPooled(int number_pooled) {
		this.number_pooled = number_pooled;
	}
	public int getNumberPooled() {
		return number_pooled;
	}
}