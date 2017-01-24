package us.bitto.kazi.server;




import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @SuppressLint("NewApi")  public class StartClass extends Activity {
	
	boolean service_started=false;
	boolean server_started=false;
	Server server;
	TextView infoip, msg;
	
	Button b1;
	Button b2;
	EditText et;
	Spinner spinner;
	
	SQLiteDatabase mydatabase;
	ArrayAdapter<String> adapter;
	List<String> list;
	ArrayList<String> link_list = new ArrayList<String>();
	
	Spinner spinner1;
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<String> link_list1 = new ArrayList<String>();
	ArrayAdapter<String> adapter1;
	
	
	WifiManager wifiManager;
	NsdManager mNsdManager;
	RegistrationListener mRegistrationListener;
	String mServiceName;
	ImageView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start_class);
		b1=(Button) this.findViewById(R.id.button1);
		b2=(Button) this.findViewById(R.id.button2);
		et=(EditText) this.findViewById(R.id.editText1);
		
		iv=(ImageView) this.findViewById(R.id.imageView1);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		
		infoip = (TextView) findViewById(R.id.infoip);
		msg = (TextView) findViewById(R.id.msg);
	    final Context c=this.getApplicationContext();
		
		list=new ArrayList<String>();
		
		adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        
        spinner1 = (Spinner) findViewById(R.id.spinner2);
		list1=new ArrayList<String>();
		adapter1 = new ArrayAdapter<String>(this,
				R.layout.spinner_item,list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        
        mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        et.setText(currentDateandTime);
        
        b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(spinner.getSelectedItemPosition()!=-1 && spinner1.getSelectedItemPosition()!=-1)
				start_class();
			}
		});
        
        b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				view_details();
			}
		});
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	get_data(link_list1.get(spinner1.getSelectedItemPosition()));
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {

		    }

		});
        
        get_sem();
	}
	
	public void view_details() {
		Intent i=new Intent(getBaseContext(), DetailsAtt.class);
		i.putExtra("class_id", link_list.get(spinner.getSelectedItemPosition()));
		i.putExtra("date", et.getText().toString());
		startActivity(i);
	}
	public void start_class() {
		boolean found=false;
		Cursor resultSet = mydatabase.rawQuery("Select * from class_day WHERE class_id='"+link_list.get(spinner.getSelectedItemPosition())+"' AND date='"+et.getText().toString()+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	      
	      while(resultSet.isAfterLast() == false){
				found=true;
                resultSet.moveToNext();
			}
	      if(found==false) {
	    	  mydatabase.execSQL("INSERT INTO class_day VALUES(null,'"+link_list.get(spinner.getSelectedItemPosition())+"','"+et.getText()+"');");
	      }
	    
	      infoip.setText("Starting NSD and Server..");
	      
		
		server_started=true;
	    server = new Server(this,link_list.get(spinner.getSelectedItemPosition()),et.getText().toString());
	    if(service_started==false) registerService(8678);
	    
	    this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				infoip.setText(server.getIpAddress() + ":" + server.getPort());
			}
	    });
	}
	public void get_data(String id) {
		list.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_room WHERE semester_id='"+id+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	    while(resultSet.isAfterLast() == false){
	    	list.add(i, resultSet.getString(0)+"."+resultSet.getString(1)+"-"+resultSet.getString(2));
	    	link_list.add(i, resultSet.getString(0));
	    	i++;
	    	resultSet.moveToNext();
	    }
	    runOnUiThread(new Runnable() {
    		@Override
    	    public void run() {
    			adapter.notifyDataSetChanged();
    		}
    	});
	}
	
	
	public void get_sem() {
		list1.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_semester ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
		while(resultSet.isAfterLast() == false){
			list1.add(i, resultSet.getString(0)+" . "+resultSet.getString(1));
			link_list1.add(i, resultSet.getString(0));
            i++;
            resultSet.moveToNext();
		}
		runOnUiThread(new Runnable() {
    		@Override
    	    public void run() {
    			adapter1.notifyDataSetChanged();
    		}
    	});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(service_started==true) tearDown();
		if(server_started==true)server.onDestroy();
	}
	
	
	public void registerService(int port) {
		tearDown();
		initializeRegistrationListener();
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("AttCounter");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);
        mNsdManager =  (NsdManager) getSystemService(NSD_SERVICE);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }
    
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
                service_started=true;
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };
    }
    
    
    
    public void tearDown() {
        if (mRegistrationListener != null) {
            try {
                mNsdManager.unregisterService(mRegistrationListener);
            } finally {
            }
            mRegistrationListener = null;
        }
    }
}
