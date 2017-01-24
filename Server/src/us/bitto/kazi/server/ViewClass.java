package us.bitto.kazi.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableLayout;
import android.widget.Toast;

public class ViewClass extends Activity {
	String delete_id;
	ListView lv;
	ArrayList<ListViewData> list = new ArrayList<ListViewData>();
	ArrayList<String> link_list = new ArrayList<String>();
	ListViewAdapter adapter;
	
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<String> link_list1 = new ArrayList<String>();
	ArrayAdapter<String> adapter1;
	
	Button b1;
	
	SQLiteDatabase mydatabase;
	Context ctx;
	Spinner spinner;
	
	TableLayout tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_class);
		b1=(Button) this.findViewById(R.id.button1);
		
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		list1=new ArrayList<String>();
		adapter1 = new ArrayAdapter<String>(this,
                R.layout.spinner_item,list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
		
		
		lv=(ListView) this.findViewById(R.id.listView1);
		adapter = new ListViewAdapter(this.getApplicationContext(), list);
		lv.setAdapter(adapter);

		ctx=this.getApplicationContext();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {


				Intent i=new Intent(getBaseContext(), ViewClassStudent.class);
				i.putExtra("class_id", link_list.get(arg2));
				startActivity(i);
			}	
        });
		
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(spinner.getSelectedItemPosition()!=-1)
				get_data(link_list1.get(spinner.getSelectedItemPosition()));
			}
		});
		

		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu CM, View arg1,
					ContextMenu.ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info =
			            (AdapterView.AdapterContextMenuInfo) menuInfo;
				delete_id=link_list.get(info.position);
				CM.setHeaderTitle("Select The Action"); 
				CM.add("Delete");
			}
		});
		
		
		get_sem();
		//get_data();
	}
	
	@Override 
	public boolean onContextItemSelected(MenuItem item) { 
	 
		mydatabase.execSQL("DELETE FROM class_room WHERE id='"+delete_id+"'");
		mydatabase.execSQL("DELETE FROM class_student WHERE class_id='"+delete_id+"'");
		mydatabase.execSQL("DELETE FROM class_attandance WHERE class_id='"+delete_id+"'");
		mydatabase.execSQL("DELETE FROM class_day WHERE class_id='"+delete_id+"'");
		
	        Toast.makeText(getApplicationContext() , "Deleted", Toast.LENGTH_SHORT).show();
	 
	 
	    return true; 
	} 
	public void get_data(String id) {
		list.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_room WHERE semester_id='"+id+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	      
	      while(resultSet.isAfterLast() == false){
				link_list.add(i, resultSet.getString(0));
                //list.add(i, resultSet.getString(0)+" . "+resultSet.getString(1)+" - "+resultSet.getString(2));
                
				ListViewData lvd=new ListViewData();
				lvd.setData1(resultSet.getString(1));
				lvd.setData2(resultSet.getString(2));
				lvd.setData3("");
				list.add(lvd);
				
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
		list.clear();
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
}
