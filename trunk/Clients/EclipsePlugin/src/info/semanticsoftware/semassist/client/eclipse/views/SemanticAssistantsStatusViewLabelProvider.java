package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.utils.Log;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SemanticAssistantsStatusViewLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		Log log = (Log) element;
		switch (columnIndex) {
		case 0:
			return log.getMessage();
		default:
			throw new RuntimeException("Error in labeling the status table.");
		}
	}

}
