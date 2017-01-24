package us.bitto.kazi.server;

import java.lang.reflect.Method;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @SuppressLint("NewApi") public class MainActivity extends Activity {

	Server server;
	TextView infoip, msg;
	
	WifiManager wifiManager;
	NsdManager mNsdManager;
	RegistrationListener mRegistrationListener;
	String mServiceName;
	
	Button b1;
	Button b2;
	Button b3;
	Button b4;
	Button b5;
	
	 
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      infoip = (TextView) findViewById(R.id.infoip);
	      msg = (TextView) findViewById(R.id.msg);
	      final Context c=this.getApplicationContext();
	      
	      b1=(Button) this.findViewById(R.id.button1);
	      b2=(Button) this.findViewById(R.id.button2);
	      b3=(Button) this.findViewById(R.id.button3);
	      b4=(Button) this.findViewById(R.id.button4);
	      b5=(Button) this.findViewById(R.id.button5);
	      
	      b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				open_activity(AddClass.class);
			}
	      });
	      b2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					open_activity(ViewClass.class);
				}
		  });
	      b3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					open_activity(StartClass.class);
				}
		  });
	      b4.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					open_activity(VIewAtt.class);
				}
		  });
	      b5.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					open_activity(AddSem.class);
				}
		  });
	      
	      SQLiteDatabase mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
	      mydatabase.execSQL("CREATE TABLE IF NOT EXISTS class_semester(id INTEGER PRIMARY KEY ASC,name TEXT)");
	      mydatabase.execSQL("CREATE TABLE IF NOT EXISTS class_room(id INTEGER PRIMARY KEY ASC,name TEXT,section TEXT,semester_id TEXT)");
	      mydatabase.execSQL("CREATE TABLE IF NOT EXISTS class_student(id INTEGER PRIMARY KEY ASC,class_id TEXT,mac TEXT,name TEXT,std_id TEXT,attn TEXT)");
	      mydatabase.execSQL("CREATE TABLE IF NOT EXISTS class_attandance(id INTEGER PRIMARY KEY ASC,class_id TEXT,std_id TEXT,mac TEXT,name TEXT,date TEXT)");
	      mydatabase.execSQL("CREATE TABLE IF NOT EXISTS class_day(id INTEGER PRIMARY KEY ASC,class_id TEXT,date TEXT)");

	      //registerService(8080);
	      
	      //server = new Server(this);
	      //infoip.setText(server.getIpAddress() + ":" + server.getPort());
		
	}


	public void open_activity(Class c) {
		   Intent i=new Intent(this,c);
		   startActivity(i);
	   }
	   
	   @Override
	   protected void onDestroy() {
	      super.onDestroy();
	      //server.onDestroy();
	   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void registerService(int port) {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("NsdChat");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);
        initializeRegistrationListener();
        mNsdManager = (NsdManager) this.getSystemService(this.NSD_SERVICE);

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
}
