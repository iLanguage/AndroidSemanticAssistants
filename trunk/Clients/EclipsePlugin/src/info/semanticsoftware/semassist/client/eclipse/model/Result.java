package info.semanticsoftware.semassist.client.eclipse.model;

/**  
 * This class provides a medium for the result of service invocations to be displayed in a table form.
 * @author Bahar Sateli 
 */

public class Result {
	
	/** The annotation identifier */
	private String id;
	
	/** The project name of the file containing an annotation */
	private String projectName;
	
	/** The class name of the file containing an annotation */
	private String className;
	
	/** The content of the annotation */
	private String content;
	
	/** The type of the annotation */
	private String type;
	
	/** The starting point of annotation in the file */
	private String start;
	
	/** The end offset of the annotation in the file */
	private String end;
	
	/** The path to the file containing an annotation */
	private String path;
	
	/** The list of features associated with an annotation */
	private String features;
	
	/** The relative path of the file containing an annotation */
	private String relativePath;
	
	/** Constructor. Initializing private variables */
	public Result(String identifier, String projectName, String className, String content, String type, String start, String end, String path, String features, String relativePath, FeatureMap map){
		this.id = identifier;
		this.projectName = projectName;
		this.className = className;
		this.content = content;
		this.type = type;
		this.start = start;
		this.end = end;
		this.path = path;
		this.features = features;
		this.relativePath = relativePath;
	}
	
	/** Returns the project name
	 * @return the project name */
	public String getProjectName(){
		return projectName;
	}
	
	/** Returns the class name
	 * @return class name */
	public String getClassName() {
		return className;
	}
	
	/** Returns the file path
	 * @return the file path */
	public String getFilePath(){
		return path;
	}
	
	/** Returns the annotation's content
	 * @return the annotation's content */
	public String getContent(){
		return content;
	}
	
	/** Returns the annotation's type
	 * @return the annotation's type */
	public String getType() {
		return type;
	}
	
	/** Returns the annotation's start point in the file
	 * @return the annotation's start point */
	public String getStart(){
		return start;
	}
	
	/** Returns the annotation's end offset in the file
	 * @return the annotation's end offset */
	public String getEnd(){
		return end;
	}
	
	/** Returns the annotation's list of features
	 * @return the annotation's list of features */
	public String getFeatures(){
		return features;
	}

	/** Returns the file's relative path to the workspace
	 * @return the file's relative path to the workspace */
	public String getRelativePath() {
		return getProjectName()+ System.getProperty("file.separator") + relativePath;
	}
	
	public String getID(){
		return id;
	}
}
