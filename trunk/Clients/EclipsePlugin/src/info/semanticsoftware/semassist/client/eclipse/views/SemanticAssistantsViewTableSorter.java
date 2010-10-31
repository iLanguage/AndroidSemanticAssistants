package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.model.Result;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class SemanticAssistantsViewTableSorter extends ViewerSorter {
	private int propertyIndex;
	// private static final int ASCENDING = 0;
	private static final int DESCENDING = 1;

	private int direction = DESCENDING;

	public SemanticAssistantsViewTableSorter() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Result r1 = (Result) e1;
		Result r2 = (Result) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = r1.getProjectName().compareTo(r2.getProjectName());
			break;
		case 1:
			rc = r1.getClassName().compareTo(r2.getClassName());
			break;
		case 2:
			rc = r1.getType().compareTo(r2.getType());
			break;
		case 3:
			rc = r1.getContent().compareTo(r2.getContent());
			break;
		case 4:
			rc = r1.getStart().compareTo(r2.getStart());
			break;
		case 5:
			rc = r1.getEnd().compareTo(r2.getEnd());
			break;
		case 6:
			rc = r1.getFeatures().compareTo(r2.getFeatures());
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
