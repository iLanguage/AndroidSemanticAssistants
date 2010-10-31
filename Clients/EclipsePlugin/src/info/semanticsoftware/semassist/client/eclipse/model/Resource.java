package info.semanticsoftware.semassist.client.eclipse.model;

import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * The files selected to send to Semantic Assistants server are wrapped as a resource.
 * A resource has a file with a list of annotations attached to it.
 *  
 * @author Bahar Sateli
 */
public class Resource{
	
	/** The user selected file in the user's local machine */
	private IFile file;
	
	/** List of annotations to attach to a file */
	private ArrayList<AnnotationInstance> annotationList;
	
	/** Constructor. Initializes the private variables with the input */
	public Resource(IFile file){
		this.file = file;
		this.annotationList = new ArrayList<AnnotationInstance>();
	}
	
	/** Returns the resource's file
	 * @return the resource's file 
	*/
	public IFile getFile(){
		return file;
	}
	
	/** Returns the resource's list of annotations
	 * @return the resource's list of annotations 
	*/
	public ArrayList<AnnotationInstance> getAnnotations(){
		return annotationList;
	}
	
	/** Returns a file's relative path to the workspace
	 * @return the file's relative path to the workspace
	*/
	public static IPath getFileLocation(IFile file){
		String path = file.getProject().getLocation().toString() + System.getProperty("file.separator") + file.getProjectRelativePath().toString();
		IPath ipath = new Path(path);
		return ipath;
	}
}
