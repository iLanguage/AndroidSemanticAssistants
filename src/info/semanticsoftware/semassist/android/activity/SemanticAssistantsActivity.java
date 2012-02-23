package info.semanticsoftware.semassist.android.activity;

import info.semanticsoftware.semassist.android.parser.ServiceParser;
import info.semanticsoftware.semassist.android.restlet.RequestRepresentation;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class SemanticAssistantsActivity extends ListActivity{
	 /** Called when the activity is first created. */
	EditText input;
	ListView list;
	String selectedService;
	TextView lblAvAssist;
	EditText txtInput;
	Button btnInvoke;
	Button btnClear;
	
		private static ServiceInfoForClientArray servicesList;
		ArrayAdapter<String> adapter;
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //tv = new TextView(this);
	        
	        getServicesTask task = new getServicesTask();
	        task.execute(new String[] { "http://semassist.ilanguage.ca:8182/SemAssistRestlet/services" });	

	        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	        setContentView(R.layout.main);
	        lblAvAssist = (TextView) findViewById(R.id.lblAvAssist);
	        input = (EditText) findViewById(R.id.txtInput);
	        input.setOnFocusChangeListener(inputFocusListener);
	        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main);
	        //tv.setBackgroundColor(Color.rgb(100, 149, 237));
	        //tv.setTextSize(25);
	        //setContentView(tv);
	        txtInput = (EditText) findViewById(R.id.txtInput);
	        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
	        	Bundle bundle = getIntent().getExtras();
	        	System.out.println("got content");
	        	txtInput.setText(bundle.getString(Intent.EXTRA_TEXT));
	        }
	        
	        btnInvoke = (Button) findViewById(R.id.btnInvoke);
	        btnInvoke.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	RequestRepresentation request = new RequestRepresentation(selectedService, null, txtInput.getText().toString());
	            	Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);
	                Representation response = new ClientResource("http://semassist.ilanguage.ca:8182/SemAssistRestlet/services/" + selectedService).post(representation);
	                try {
	                	StringWriter writer = new StringWriter();
	                	response.write(writer);
	                	String responseString = writer.toString();
	                	System.out.println(responseString);

	                	Intent intent = new Intent(getBaseContext(), SemanticResultsActivity.class);
	                	intent.putExtra("xml", responseString);
	                    startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}   
	            }
	        });
	    
	        btnClear = (Button) findViewById(R.id.btnClear);
	        btnClear.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	txtInput.setText("");
	            }
	        });
		}
		
		
		@Override
	    public void onStart(){
			super.onStart();
			if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
	        	Bundle bundle = getIntent().getExtras();
	        	System.out.println("got content");
	        	txtInput.setText(bundle.getString(Intent.EXTRA_TEXT));
	        }
		}
	    
	    private class getServicesTask extends AsyncTask<String, Void, String> {
          
			protected String doInBackground(String... urls) {
				try {
					Log.d("SemanticAssistantsActivity", "Sending GET via Restlet");
				    // Prepare the request
					ClientResource resource = new ClientResource(urls[0]);
					ClientInfo info = new ClientInfo(MediaType.TEXT_XML);
					resource.setClientInfo(info);
					StringWriter writer = new StringWriter();
					resource.get(MediaType.TEXT_XML).write(writer);
					
					ServiceParser parser = new ServiceParser(writer.toString());
					
					System.out.println("will parse " + writer.toString());
					servicesList = parser.parseToObject();
					
					return writer.toString();
				} catch (ResourceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "error callService";
			}

			@Override
			protected void onPostExecute(String result) {
				populateServicesList();
			}
		}
	    
	    private void populateServicesList(){
	       String[] values = null;
	       List<String> names = new ArrayList<String>();
	       	for(int i=0; i < servicesList.getItem().size(); i++){
	       		names.add(servicesList.getItem().get(i).getServiceName());
	           }
	      
	       	values = names.toArray(new String[names.size()]);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.row, R.id.label, values);
			setListAdapter(adapter);
	    }
	    
	    @Override
	    protected void onListItemClick(ListView list, View view, int position, long id) {
	    	//Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
			//Intent intent = new Intent(this, ServiceInfoActivity.class);
			//intent.putExtra("serviceName",list.getItemAtPosition(position).toString());
			//startActivity(intent);
	    	list.setVisibility(View.GONE);
	    	lblAvAssist.setVisibility(View.INVISIBLE);
	    	LinearLayout linearLayout =  (LinearLayout) findViewById(R.id.servicesLayout);
	    	String temp = (String) list.getItemAtPosition(position);
	    	selectedService = temp;
	    	linearLayout.addView(getServiceDescLayout());
	    }
	    
	    private View.OnFocusChangeListener inputFocusListener = new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) { 
            	if(hasFocus){
            		//TODO complete this
            	}
            }
	    };
	    	    
	    public static ServiceInfoForClientArray getServices(){
	    	return servicesList;
	    }
	    
	    private LinearLayout getServiceDescLayout(){
	    	
	    	final LinearLayout output = new LinearLayout(this);
	    	final Button btnBack = new Button(this);
	    	btnBack.setText("< All services");
	        btnBack.setId(5);
	        btnBack.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        btnBack.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v) {
	               //btnBack.setVisibility(View.GONE);
	               output.setVisibility(View.GONE);
	               getListView().setVisibility(View.VISIBLE);
	               lblAvAssist.setVisibility(View.VISIBLE);
	        	}
	        });
	        output.addView(btnBack);
	        
	    	TableLayout serviceInfoTbl = new TableLayout(this);
	        output.addView(serviceInfoTbl);

	    	serviceInfoTbl.setColumnShrinkable(1, true);
	    	
	    	/* FIRST ROW */
	    	TableRow rowServiceName = new TableRow(this);
	    	//rowServiceName.setPadding(10, 0, 0, 0);
	    	
	    	TextView lblServiceName = new TextView(this);
	    	lblServiceName.setText(R.string.lblServiceName);
	    	lblServiceName.setTextAppearance(getApplicationContext(), R.style.titleText);
	    	
	    	TextView txtServiceName = new TextView(this);
	    	txtServiceName.setText(selectedService);
	    	txtServiceName.setTextAppearance(getApplicationContext(), R.style.normalText);
	    	txtServiceName.setPadding(10, 0, 0, 0);
	    	
	    	rowServiceName.addView(lblServiceName);
	    	rowServiceName.addView(txtServiceName);
	    	
	    	/* SECOND ROW */
	    	TableRow rowServiceDesc = new TableRow(this);
	    	//rowServiceDesc.setPadding(10, 0, 0, 0);
	    	
	    	TextView lblServiceDesc = new TextView(this);
	    	lblServiceDesc.setText(R.string.lblServiceDesc);
	    	lblServiceDesc.setTextAppearance(getApplicationContext(), R.style.titleText);
	    	
	    	TextView txtServiceDesc = new TextView(this);
	    	txtServiceDesc.setTextAppearance(getApplicationContext(), R.style.normalText);
	    	txtServiceDesc.setPadding(10, 0, 0, 0);
	    	List<GateRuntimeParameter> params = null;
	    	ServiceInfoForClientArray list = getServices();
	        for(int i=0; i < list.getItem().size(); i++){
	        	if(list.getItem().get(i).getServiceName().equals(selectedService)){
	        		txtServiceDesc.setText(list.getItem().get(i).getServiceDescription());
	        		params = list.getItem().get(i).getParams();
	        		break;
	        	}
	        }
	        
	        TextView lblParams = new TextView(this);
	        lblParams.setText(R.string.lblServiceParams);
	        lblParams.setTextAppearance(getApplicationContext(), R.style.titleText);
	        output.addView(lblParams);
	        
	        if(params.size() > 0){
	        	ScrollView scroll = new ScrollView(this);
	        	LayoutParams txtParamsAttrbs = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	        scroll.setLayoutParams(txtParamsAttrbs);
    	        
	        	LinearLayout paramsLayout = new LinearLayout(this);
	        	paramsLayout.setOrientation(LinearLayout.VERTICAL);
	        	scroll.addView(paramsLayout);
	        	for(int j=0; j < params.size(); j++){
        			TextView lblParamName = new TextView(this);
        			lblParamName.setText(params.get(j).getParamName());
        			EditText tview = new EditText(this);
        	        tview.setText(params.get(j).getDefaultValueString());
        	        LayoutParams txtViewLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        	        tview.setLayoutParams(txtViewLayoutParams);
        	        paramsLayout.addView(lblParamName);
        	        paramsLayout.addView(tview); 
	        	}
	        	output.addView(scroll);
	        }else{
    			TextView lblParamName = new TextView(this);
    			lblParamName.setText("No runtime parameters.");
    	        output.addView(lblParamName);
	        }
    
	        rowServiceDesc.addView(lblServiceDesc);
	        rowServiceDesc.addView(txtServiceDesc);
	    	
	    	serviceInfoTbl.addView(rowServiceName);
	    	serviceInfoTbl.addView(rowServiceDesc);
	    	
	    	output.setOrientation(LinearLayout.VERTICAL);
	    	//output.setBackgroundColor(Color.MAGENTA);
	    	output.setGravity(Gravity.TOP);
	    	
	        return output;
	    }
}