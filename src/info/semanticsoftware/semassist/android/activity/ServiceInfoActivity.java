package info.semanticsoftware.semassist.android.activity;

import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import net.java.dev.jaxb.array.StringArray;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.data.MediaType;

import info.semanticsoftware.semassist.android.business.ServerResponseHandler;
import info.semanticsoftware.semassist.android.restlet.RequestRepresentation;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServiceInfoActivity extends ListActivity{
	TextView txtServiceDesc;
	EditText txtInput;
	Button btnInvoke;
	String serviceName;
    StringArray names = new StringArray();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invocation);
        
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainlayout);
        
        txtServiceDesc = (TextView) findViewById(R.id.txtServiceDesc);
        txtInput = (EditText) findViewById(R.id.txtInput);
        btnInvoke = (Button) findViewById(R.id.btnInvoke);
        serviceName = getIntent().getStringExtra("serviceName");
        setTitle(serviceName);
        ServiceInfoForClientArray list = SemanticAssistantsActivity.getServices();
        txtServiceDesc.setText("No description available");
        for(int i=0; i < list.getItem().size(); i++){
        	if(list.getItem().get(i).getServiceName().equals(serviceName)){
        		txtServiceDesc.setText(list.getItem().get(i).getServiceDescription());
        		List<GateRuntimeParameter> params = list.getItem().get(i).getParams();
        		for(int j=0; j < params.size(); j++){
        			TextView lblParamName = new TextView(ServiceInfoActivity.this);
        			lblParamName.setText(params.get(j).getParamName());
        			
        			EditText tview = new EditText(ServiceInfoActivity.this);
        	        tview.setText(params.get(j).getDefaultValueString());
        	        LayoutParams txtViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	        tview.setLayoutParams(txtViewLayoutParams);
        	        mainLayout.addView(lblParamName);
        	        mainLayout.addView(tview);

        		}
        		break;
        	}
        }
        
        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
        	Bundle bundle = getIntent().getExtras();
        	txtInput.setText(bundle.getString(Intent.EXTRA_TEXT));
        }
        
        btnInvoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	RequestRepresentation request = new RequestRepresentation(serviceName, null, txtInput.getText().toString());
            	Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);
                Representation response = new ClientResource("http://192.168.4.192:8080/SemAssistRestlet/services/Salam").post(representation);
                try {
                	StringWriter writer = new StringWriter();
                	response.write(writer);
                	String responseString = writer.toString();
                	System.out.println(responseString);
					Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(responseString);
					for(SemanticServiceResult current: results){
						if (current.mResultType.equals(SemanticServiceResult.ANNOTATION)){
							StringArray annots = new StringArray();
							annots = ServerResponseHandler.createAnnotation(current);
							for(int i=0; i < annots.getItem().size(); i++){
								names.getItem().add(annots.getItem().get(i));
							}
						}
					}
					populateNamesList();
				} catch (Exception e) {
					e.printStackTrace();
				}   
            }
        });        
    }
	
    
    private void populateNamesList(){
    	String[] values = null;
       	values = names.getItem().toArray(new String[names.getItem().size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
    }
}
