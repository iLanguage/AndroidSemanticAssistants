package info.semanticsoftware.semassist.android.activity;

import info.semanticsoftware.semassist.android.business.AnnotationInstance;
import info.semanticsoftware.semassist.android.business.ServerResponseHandler;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SemanticResultsActivity extends Activity {
	
	TableLayout tblResults;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
		
		tblResults = (TableLayout) findViewById(R.id.tblResultsLayout);
		tblResults.setStretchAllColumns(true);
		
		TableRow resultRow;
		TextView txtContent;
		TextView txtType;
		TextView txtStart;
		TextView txtEnd;
		TextView txtFeats;
		String resultsXML = getIntent().getStringExtra("xml");
    	
    	Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(resultsXML);
		for(SemanticServiceResult current: results){
			if (current.mResultType.equals(SemanticServiceResult.ANNOTATION)){
				List<AnnotationInstance> annots = ServerResponseHandler.createAnnotation(current);
				for(int i=0; i < annots.size(); i++){
					resultRow = new TableRow(getApplicationContext());
					
					txtContent = new TextView(getApplicationContext());
					txtContent.setText(annots.get(i).getContent());
					txtContent.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtContent);
					
					txtType = new TextView(getApplicationContext());
					txtType.setText(annots.get(i).getType());
					txtType.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtType);
					
					txtStart = new TextView(getApplicationContext());
					txtStart.setText(annots.get(i).getStart());
					txtStart.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtStart);
					
					txtEnd = new TextView(getApplicationContext());
					txtEnd.setText(annots.get(i).getEnd());
					txtEnd.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtEnd);
					
					txtFeats = new TextView(getApplicationContext());
					txtFeats.setText(annots.get(i).getFeatures());
					txtFeats.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtFeats);					
					
					tblResults.addView(resultRow);
				}
			}
		}
    	
	}
}
