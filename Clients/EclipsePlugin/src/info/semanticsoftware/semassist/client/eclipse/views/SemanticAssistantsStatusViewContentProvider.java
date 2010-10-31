package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.utils.Log;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SemanticAssistantsStatusViewContentProvider implements IStructuredContentProvider{

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object parent) {
		@SuppressWarnings("unchecked")
		List<Log> logs = (List<Log>) parent;
		return logs.toArray();
	}

}
