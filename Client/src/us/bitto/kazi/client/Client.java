package us.bitto.kazi.client;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
 
@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @SuppressLint("NewApi") public class Client extends AsyncTask<Void, Void, Void> {
 
   String dstAddress;
   int dstPort;
   String response = "";
   TextView textResponse;
   
   OutputStream outputStream;
   InputStream inputStream;
   WifiManager wifiManager;
   static boolean proceed;
   static byte[] b;
   static String reply_string;
   MainActivity ma;
   static Socket socket;
 
   Client(String addr, int port, TextView textResponse, WifiManager wm, MainActivity a) {
      dstAddress = addr;
      dstPort = port;
      wifiManager = wm;
      this.textResponse = textResponse;
      proceed=false;
      reply_string="";
      ma=a;
      socket = null;
   }
 
   @Override
   protected Void doInBackground(Void... arg0) {
 
      
 
      try {
         socket = new Socket(dstAddress, dstPort);
		  	ma.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//buttonConnect.setVisibility(View.VISIBLE);
					ma.call_attendance();
	      		  	ma.b1.setVisibility(View.GONE);
				}
		  		
		  	});
         outputStream = socket.getOutputStream();
         inputStream = socket.getInputStream();

         response +=read_data();
         ma.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					ma.response.setText("Connected\r\nNow Click Get Attandence to get Attandence\r\n");
				} 
            });
         //textResponse.setText(textResponse.getText()+"\n"+response);
         while(proceed==false) {
        	 
         }
         write_image();
         //write_data(reply_string);
         String s="";
         //s=read_data();
         if(s.contains("off")) {
        	 //wifiManager.setWifiEnabled(false);	
        	 ma.runOnUiThread(new Runnable(){
 				@Override
 				public void run() {
 					ma.response.setText("Attandence Completed");
 					ma.ssmsg.setText("Attandence Completed");
 					ma.ll1.setVisibility(View.GONE);
 					ma.mNsdManager.stopServiceDiscovery(ma.mDiscoveryListener);
 				} 
             });
         }
         else if(s.contains("null")) {
            	 //wifiManager.setWifiEnabled(false);	
            	 ma.runOnUiThread(new Runnable(){
     				@Override
     				public void run() {
     					ma.response.setText("Something Missing");
     					ma.mNsdManager.stopServiceDiscovery(ma.mDiscoveryListener);
     				} 
                 });
         }
         else if(s.contains("done")) {
            	 //wifiManager.setWifiEnabled(false);	
            	 ma.runOnUiThread(new Runnable(){
     				@Override
     				public void run() {
     					ma.response.setText("Already Counted. Dont try.");
     					ma.ssmsg.setText("Already Counted. Dont try.");
     					ma.ll1.setVisibility(View.GONE);
     					ma.mNsdManager.stopServiceDiscovery(ma.mDiscoveryListener);
     				} 
                 });
             }
         else {
        	 ma.runOnUiThread(new Runnable(){
  				@Override
  				public void run() {
  					ma.response.setText("Error Occured");
  					//ma.mNsdManager.stopServiceDiscovery(ma.mDiscoveryListener);
  				} 
              });
         }
         //textResponse.setText(textResponse.getText()+"\n"+response);
         
         //read_data();
 
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         response = "UnknownHostException: " + e.toString();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         response = "IOException: " + e.toString();
      } finally {
         if (socket != null) {
            try {
               socket.close();
               ma.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					ma.buttonConnect.setVisibility(View.GONE);
	       	  		ma.b1.setVisibility(View.VISIBLE);
				} 
               });
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         else {
        	 ma.runOnUiThread(new Runnable(){
 				@Override
 				public void run() {
 					ma.b1.setVisibility(View.VISIBLE);
 					ma.response.setText("Error Connecting");
 				} 
             });
         }
      }
      return null;
   }
 
   @Override
   protected void onPostExecute(Void result) {
      //textResponse.setText(textResponse.getText()+"\r\n"+response);
      //wifiManager.setWifiEnabled(false);
      super.onPostExecute(result);
      
   }
   
   public String read_data() throws IOException {
	   String response="";
	   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
               1024);
       byte[] buffer = new byte[1024];
 
       int bytesRead;
 
         /*
          * notice: inputStream.read() will block if no data return
          */
       while ((bytesRead = inputStream.read(buffer)) != -1) {
    	   response="";
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            
            response += byteArrayOutputStream.toString("UTF-8");
            if(response.compareTo("")!=0) break;
            Log.d("data", "databal: "+byteArrayOutputStream.toString("UTF-8"));
       }
       return response;
   }
   
   public void write_data(String msgReply) throws IOException {
	   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
	   writer.write(msgReply);
	   writer.newLine();
	   writer.flush();
   }
   public void write_image() {
	   DataOutputStream dos = new DataOutputStream(outputStream);
	    if (b.length > 0) {
	        try {
				dos.write(this.b, 0, (this.b.length-1));
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    Log.d("bk11: written", "bk11: "+(this.b.length-1));
   }
}