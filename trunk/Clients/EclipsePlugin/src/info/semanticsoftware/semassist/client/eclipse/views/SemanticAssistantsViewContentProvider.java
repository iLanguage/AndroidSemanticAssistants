package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationInstance;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */
public class SemanticAssistantsViewContentProvider implements IStructuredContentProvider {
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public Object[] getElements(Object parent) {
		@SuppressWarnings("unchecked")
		List<AnnotationInstance> results = (List<AnnotationInstance>) parent;
		return results.toArray();
	}
	public void dispose() {
	}
}