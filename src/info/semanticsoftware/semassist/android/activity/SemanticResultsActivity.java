package info.semanticsoftware.semassist.android.activity;

import info.semanticsoftware.semassist.android.business.ServerResponseHandler;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

import java.util.Vector;

import net.java.dev.jaxb.array.StringArray;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;

public class SemanticResultsActivity extends Activity {
	
	TableLayout tblResults;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tblResults = (TableLayout) findViewById(R.id.tblResultsLayout);

		String resultsXML = getIntent().getStringExtra("xml");
    	
    	Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(resultsXML);
		for(SemanticServiceResult current: results){
			if (current.mResultType.equals(SemanticServiceResult.ANNOTATION)){
				StringArray annots = new StringArray();
				annots = ServerResponseHandler.createAnnotation(current);
				for(int i=0; i < annots.getItem().size(); i++){
					System.out.println(annots.getItem().get(i));
				}
			}
		}
    	
	}
}
