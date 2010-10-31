package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.model.Result;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class SemanticAssistantsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

/** 
 * This method reads each element in the table and returns the corresponding text to show in cells.
 * @return A string text for each cell in table 
 * */
@Override
public String getColumnText(Object element, int columnIndex) {
	
	if (!(element instanceof Result)) {
        return null;
	}
	
	Result result = (Result) element;
	switch (columnIndex) {
	case 0:
		return result.getProjectName();
	case 1:
		return result.getClassName();
	case 2:
		return result.getType();
	case 3:
		return result.getContent();
	case 4:
		return result.getStart();
	case 5:
		return result.getEnd();
	case 6:
		return result.getFeatures();
	default:
		throw new RuntimeException("Error in labeling the resource table.");
	}

}

/**
 * This method returns the image corresponding to the column titles.
 * @return An image file to show beside each column title
 * */
public Image getColumnImage(Object obj, int index) {
	return getImage(obj);
}

/**
 * This method returns the image corresponding to each cell content.
 * @return An image file to show beside each cell content
 * */
public Image getImage(Object obj) {
	//return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	return null;
}
}