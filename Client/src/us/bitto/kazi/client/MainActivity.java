package us.bitto.kazi.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;


import android.net.DhcpInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.DiscoveryListener;
import android.net.nsd.NsdManager.ResolveListener;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi") @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public class MainActivity extends Activity {

	TextView response;
	   EditText editTextAddress, editTextPort;
	   Button buttonConnect, buttonClear,b1;
	   NsdManager mNsdManager;
	   String TAG;
	   String mServiceName;
	   String SERVICE_TYPE;
	   NsdServiceInfo mService;
	   int port;
	   InetAddress host;
	   Context c;
	   WifiManager wifiManager;
	   EditText mac;
	   MainActivity mA;
	   
	   Client myClient;
	   
	   
	   DiscoveryListener mDiscoveryListener;
	   ResolveListener mResolveListener;
	   
	   LinearLayout ll1;
	   LinearLayout ll2;
	   TextView ssmsg;
	   
	   static final int REQUEST_IMAGE_CAPTURE = 1;
	 
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_main);
	      ll1=(LinearLayout) this.findViewById(R.id.ll1);
	      ll2=(LinearLayout) this.findViewById(R.id.ll2);
	      ssmsg=(TextView) this.findViewById(R.id.ssmsg);
	      mA=this;
	      editTextAddress = (EditText) findViewById(R.id.addressEditText);
	      editTextPort = (EditText) findViewById(R.id.portEditText);
	      buttonConnect = (Button) findViewById(R.id.connectButton);
	      b1 = (Button) findViewById(R.id.button1);
	      buttonClear = (Button) findViewById(R.id.clearButton);
	      response = (TextView) findViewById(R.id.responseTextView);
	      mac=(EditText) this.findViewById(R.id.editText1);
	      mac.setText(getMacAddr());
	      
	      editTextAddress.setText(read_data("id_client"));
	      editTextPort.setText(read_data("name_client"));
	      
	      buttonConnect.setVisibility(View.GONE);
	      
	      c=this.getApplicationContext();
	      
	      wifiManager = (WifiManager) c.getSystemService(c.getApplicationContext().WIFI_SERVICE);
	      
	      TAG="nattt";
	      mServiceName="NsdChat"+randInt(1,999999);
	      SERVICE_TYPE="_http._tcp.";
	      
	      mNsdManager = (NsdManager) this.getSystemService(this.NSD_SERVICE);
	      //initializeResolveListener();
	      //initializeDiscoveryListener();
	      //mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
	      
	      
	      
	      
	 
	      buttonConnect.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View arg0) {
	        	write_data("id_client",editTextAddress.getText().toString());
	        	write_data("name_client",editTextPort.getText().toString());
	        	//Client.reply_string=editTextAddress.getText()+">>>"+editTextPort.getText()+">>>"+mac.getText();
	            //Client.proceed=true;
	        	//call_attendance();
	         }
	      });
	 
	      buttonClear.setOnClickListener(new View.OnClickListener() {
	 
	         @Override
	         public void onClick(View v) {
	            response.setText("");
	         }
	      });
	      
	      b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				write_data("id_client",editTextAddress.getText().toString());
	        	write_data("name_client",editTextPort.getText().toString());
				b1.setVisibility(View.GONE);
				mServiceName="NsdChat"+randInt(1,999999);
				initializeResolveListener();
			      initializeDiscoveryListener();
			      mNsdManager.discoverServices(
			    	 SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
			}
		});
	     
	      
	      
	      //getIpAddress();
	   }

	   
	   public void call_attendance() {
		   Intent i=new Intent(this, GiveAtttendance.class);
		   this.startActivity(i);
	   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                
				if (service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    //Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                 //else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    //Log.d(TAG, "Same machine: " + mServiceName);
                //} 
					if (service.getServiceName().contains("AttCounter")){
	                	mNsdManager.resolveService(service, mResolveListener);
	                } 
				}
				
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
                mA.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//buttonConnect.setVisibility(View.VISIBLE);
		      		  	b1.setVisibility(View.VISIBLE);
					}
      		  		
      		  	});
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                mA.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//buttonConnect.setVisibility(View.VISIBLE);
		      		  	b1.setVisibility(View.VISIBLE);
					}
      		  		
      		  	});
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mA.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//buttonConnect.setVisibility(View.VISIBLE);
		      		  	b1.setVisibility(View.VISIBLE);
					}
      		  		
      		  	});
                myClient = new Client("192.168.43.1", 8678, response, wifiManager, mA);
                myClient.execute();
                //mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mA.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//buttonConnect.setVisibility(View.VISIBLE);
		      		  	b1.setVisibility(View.VISIBLE);
					}
      		  		
      		  	});
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }
    
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
            	mA.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//buttonConnect.setVisibility(View.VISIBLE);
		      		  	b1.setVisibility(View.VISIBLE);
					}
      		  		
      		  	});
                Log.e(TAG, "Resolve failed" + errorCode);
				myClient = new Client("192.168.0.199", 8678, response, wifiManager, mA);
				myClient.execute();
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                port = mService.getPort();
                host = mService.getHost();
                myClient = new Client(host.getHostAddress().toString(), port, response, wifiManager, mA);
      		  	myClient.execute();
      		  	
            }
        };
    }
    
    
    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
 
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
 
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
 
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress="";
        macAddress = wInfo.getMacAddress(); 
        return macAddress;
    }
    
    public String read_data(String n) {
    	SharedPreferences sharedPref=this.getSharedPreferences(n, Context.MODE_PRIVATE);
		String s = sharedPref.getString(n, "");
		return s;
	}
	public void write_data(String n, String s) {
		SharedPreferences sharedPref=this.getSharedPreferences(n, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(n, s);
		editor.commit();
	}
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
