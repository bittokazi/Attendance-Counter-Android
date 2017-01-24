package us.bitto.kazi.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GiveAtttendance extends Activity {
	ImageButton b1;
	TextView tv;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	byte[] b;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.give_attendance);
		b1=(ImageButton) this.findViewById(R.id.imageButton1);
		tv=(TextView) this.findViewById(R.id.textView1);
		
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Client.reply_string=read_data("id_client")+">>>"+read_data("name_client")+">>>"+getMacAddr();
	            Client.proceed=true;
	            finish();
			}
		});
		dispatchTakePictureIntent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
	
	public String read_data(String n) {
    	SharedPreferences sharedPref=this.getSharedPreferences(n, Context.MODE_PRIVATE);
		String s = sharedPref.getString(n, "");
		return s;
	}
	
	@SuppressLint("NewApi") public String getMacAddr() {
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
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        
	        
	        int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            int size = imageBitmap.getRowBytes() * imageBitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            imageBitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = byteBuffer.array();
	        
            
	        Client.b=new byte[byteArray.length];
	        Client.b=byteArray;
	        
	        Bitmap.Config configBmp = Bitmap.Config.valueOf(imageBitmap.getConfig().name());
	        Bitmap bitmap_tmp = Bitmap.createBitmap(512, 512, configBmp);
	        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
	        bitmap_tmp.copyPixelsFromBuffer(buffer);
	        
	        
	        Log.d("bk11: client", "bk11: "+imageBitmap.getConfig().name());
	        b1.setImageBitmap(bitmap_tmp);
	    }
	}
	
}
