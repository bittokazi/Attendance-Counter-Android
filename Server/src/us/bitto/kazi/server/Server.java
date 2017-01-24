package us.bitto.kazi.server;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
 
public class Server {
   StartClass activity;
   ServerSocket serverSocket;
   String cid="";
   String date="";
   String message = "";
   static final int socketServerPORT = 8678;
   ArrayList<SocketServerReplyThread> ssrt;
   SQLiteDatabase mydatabase;
 
   public Server(StartClass startClass, String cid, String date) {
	  ssrt=new ArrayList<SocketServerReplyThread>();
      this.activity = startClass;
      this.cid=cid;
      this.date=date;
      Thread socketServerThread = new Thread(new SocketServerThread());
      socketServerThread.start();
      mydatabase = activity.openOrCreateDatabase("attandance_counter",activity.getApplicationContext().MODE_PRIVATE,null);
   }
 
   public int getPort() {
      return socketServerPORT;
   }
 
   public void onDestroy() {
      if (serverSocket != null) {
         try {
            serverSocket.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
 
   private class SocketServerThread extends Thread {
 
      int count = 0;
 
      @Override
      public void run() {
         try {
            // create ServerSocket using specified port
            serverSocket = new ServerSocket(socketServerPORT);
 
            while (true) {
               // block the call until connection is created and return
               // Socket object
               Socket socket = serverSocket.accept();
               count++;
               message += "#" + count + " from "
                     + socket.getInetAddress() + ":"
                     + socket.getPort() + "\n";
 
               activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     activity.msg.setText(message);
                  }
               });
 
               //SocketServerReplyThread socketServerReplyThread = 
                     //new SocketServerReplyThread(socket, count);
               ssrt.add(new SocketServerReplyThread(socket, count));
               ssrt.get(ssrt.size()-1).start();
               //socketServerReplyThread.run();
 
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
 
   private class SocketServerReplyThread implements Runnable {
 
      private Socket hostThreadSocket;
      int cnt;
      public Thread t;
      OutputStream outputStream;
      InputStream inputStream;
      int l=0;
 
      SocketServerReplyThread(Socket socket, int c) {
         hostThreadSocket = socket;
         try {
			outputStream = hostThreadSocket.getOutputStream();
			inputStream = hostThreadSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         cnt = c;
      }
      
      public void start ()
      {
          t=new Thread(this,"getlistfromserver"+cnt);
          t.start();
      }
 
      @Override
      public void run() {
         String msgReply = "Hello from Server, you are #" + cnt;
 
         try {
        	write_data(msgReply);
 
            message += "replayed: " + msgReply + "\n";
 
            activity.runOnUiThread(new Runnable() {
 
               @Override
               public void run() {
                  activity.msg.setText(message);
              
               }
            });
            message="";
            int attn=0;
            
            final byte[] im=read_image();
            Log.d("bk11: iv", "bk11: "+im.length);
            
            //Bitmap.Config configBmp = Bitmap.Config.valueOf("lol");
	        //final Bitmap bitmap_tmp = Bitmap.createBitmap(500, 500, configBmp);
	        //ByteBuffer buffer = ByteBuffer.wrap(im);
	        //bitmap_tmp.copyPixelsFromBuffer(buffer);
            
            
            activity.runOnUiThread(new Runnable() {
            	 
                @Override
                public void run() {
                   //activity.iv.setImageBitmap(bitmap_tmp);
                }
             });
            

            		
            		
            /*		
            message += read_data();
            
            if(message.split(">>>")[2].compareTo("")!=0 && message.split(">>>")[1].compareTo("")!=0 && message.split(">>>")[0].compareTo("")!=0) {
            
            boolean found=false;
    		Cursor resultSet = mydatabase.rawQuery("Select * from class_student WHERE class_id='"+cid+"' AND mac='"+message.split(">>>")[2]+"' ORDER BY id DESC",null);
    		int i=0,g=0;
    		String id="";
    		String name="";
    		resultSet.moveToFirst();
    	      
    	      while(resultSet.isAfterLast() == false){
    				found=true;
    				attn=Integer.parseInt(resultSet.getString(5));
    				id=resultSet.getString(4);
    				name=resultSet.getString(3);
                    resultSet.moveToNext();
    			}
    	      if(found==false) {
    	    	  g=1;
    	    	  id=message.split(">>>")[0];
    	    	  name=message.split(">>>")[1];
    	    	  mydatabase.execSQL("INSERT INTO class_student VALUES(null,'"+cid+"','"+message.split(">>>")[2]+"','"+message.split(">>>")[1]+"','"+message.split(">>>")[0]+"','1');");
    	      }
    	      
    	    
    	     
    	      
    	      found=false;
      		resultSet = mydatabase.rawQuery("Select * from class_attandance WHERE class_id='"+cid+"' AND  date='"+date+"' AND mac='"+message.split(">>>")[2]+"' ORDER BY id DESC",null);
      		i=0;
      		resultSet.moveToFirst();
      	      
      	      while(resultSet.isAfterLast() == false){
      				found=true;
                      resultSet.moveToNext();
      			}
      	      if(found==false) {
      	    	  
      	    	attn++;
      	      if (g==0) mydatabase.execSQL("UPDATE class_student SET attn='"+Integer.toString(attn)+"' WHERE std_id='"+id+"'");
      	    	  mydatabase.execSQL("INSERT INTO class_attandance VALUES(null,'"+cid+"','"+id+"','"+message.split(">>>")[2]+"','"+name+"','"+date+"');");
      	    	msgReply = "off";
      	      }
      	      else {
      	    	msgReply = "done";
      	      }
    	      
    	      //attn++;
    	      //mydatabase.execSQL("UPDATE class_student SET attn='"+Integer.toString(attn)+"' WHERE id='"+Integer.toString(id)+"'");
            
            } 
            else {
            	msgReply = "null";
            }
    		*/
            msgReply = "null";
            write_data(msgReply);
            
            activity.runOnUiThread(new Runnable() {
            	 
                @Override
                public void run() {
                   activity.msg.setText(message);
                }
             });
            if(hostThreadSocket.isConnected() || !hostThreadSocket.isClosed()) hostThreadSocket.close();
 
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            message += "Something wrong! " + e.toString() + "\n";
         }
 
         activity.runOnUiThread(new Runnable() {
 
            @Override
            public void run() {
               activity.msg.setText(message);
            }
         });
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
   	   	//writer.close();
      }
      
      public byte[] read_image() {
  	    // Again, probably better to store these objects references in the support class
  	    DataInputStream dis = new DataInputStream(inputStream);

  	    int len=0;
  	    l=0;
		try {
			Log.d("bk11: 1", "bk11: ");
			if(dis.readInt()>0) len = dis.readInt();
			Log.d("bk11: 2", "bk11: "+len);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	    byte[] data = new byte[120000];
  	    if (len > 0) {
  	        try {
				dis.read(data, 0, 120000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  	    }
  	  Log.d("bk11: 4", "bk11: "+data.length);
  	    return data;
  	}
 
   }
 
   public String getIpAddress() {
      String ip = "";
      try {
         Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
               .getNetworkInterfaces();
         while (enumNetworkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = enumNetworkInterfaces
                  .nextElement();
            Enumeration<InetAddress> enumInetAddress = networkInterface
                  .getInetAddresses();
            while (enumInetAddress.hasMoreElements()) {
               InetAddress inetAddress = enumInetAddress
                     .nextElement();
 
               if (inetAddress.isSiteLocalAddress()) {
                  ip += "Server running at : "
                        + inetAddress.getHostAddress();
               }
            }
         }
 
      } catch (SocketException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         ip += "Something Wrong! " + e.toString() + "\n";
      }
      return ip;
   }
}