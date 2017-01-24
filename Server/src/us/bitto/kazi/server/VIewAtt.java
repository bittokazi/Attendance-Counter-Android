package us.bitto.kazi.server;

import java.util.ArrayList;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VIewAtt extends Activity {
	String delete_id;
	ListView lv;
	ArrayList<ListViewData> list = new ArrayList<ListViewData>();
	ArrayList<String> link_list = new ArrayList<String>();
	ListViewAdapter adapter;
	
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<String> link_list1 = new ArrayList<String>();
	ArrayAdapter<String> adapter1;
	
	ArrayList<String> list2 = new ArrayList<String>();
	ArrayList<String> link_list2 = new ArrayList<String>();
	ArrayAdapter<String> adapter2;
	
	Button b1;
	Spinner spinner;
	Spinner spinner1;
	
	SQLiteDatabase mydatabase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_att);
		
		b1=(Button) this.findViewById(R.id.button1);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		list1=new ArrayList<String>();
		adapter1 = new ArrayAdapter<String>(this,
				R.layout.spinner_item,list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        
        
        spinner1 = (Spinner) findViewById(R.id.spinner2);
		list2=new ArrayList<String>();
		adapter2 = new ArrayAdapter<String>(this,
				R.layout.spinner_item,list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter2);
		
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		
		lv=(ListView) this.findViewById(R.id.listView1);
		adapter = new ListViewAdapter(this.getApplicationContext(), list);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i=new Intent(getBaseContext(), DetailsAtt.class);
				i.putExtra("class_id", link_list.get(arg2).split(">>>")[0]);
				i.putExtra("date", link_list.get(arg2).split(">>>")[1]);
				startActivity(i);
			}	
        });
		
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	get_sub(link_list1.get(spinner.getSelectedItemPosition()));
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {

		    }

		});
		
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(spinner1.getSelectedItemPosition()!=-1) {
					get_data(link_list2.get(spinner1.getSelectedItemPosition()));
				}
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

		mydatabase.execSQL("DELETE FROM class_attandance WHERE class_id='"+delete_id.split(">>>")[0]+"' AND date='"+delete_id.split(">>>")[1]+"'");
		mydatabase.execSQL("DELETE FROM class_day WHERE class_id='"+delete_id.split(">>>")[0]+"' AND date='"+delete_id.split(">>>")[1]+"'");
		
	        Toast.makeText(getApplicationContext() , "Deleted", Toast.LENGTH_SHORT).show();
	 
	 
	    return true; 
	} 
	public void get_data(String id) {
		list.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_day WHERE class_id='"+id+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	      
	      while(resultSet.isAfterLast() == false){
	    	  String name="";
	    	  String sec="";
	    	  Cursor resultSet1 = mydatabase.rawQuery("Select * from class_room WHERE id='"+resultSet.getString(1)+"' ORDER BY id DESC",null);
	    	  	resultSet1.moveToFirst();
	    	  	while(resultSet1.isAfterLast() == false){
	    	  		name=resultSet1.getString(1);
	    	  		sec=resultSet1.getString(2);
	    	  		resultSet1.moveToNext();
	    	  	}
				link_list.add(i, resultSet.getString(1)+">>>"+resultSet.getString(2));
				
				ListViewData lvd=new ListViewData();
				lvd.setData1(name);
				lvd.setData2("Date: "+resultSet.getString(2));
				lvd.setData3("Sec: "+sec);
				list.add(i,lvd);
               
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
	
	public void get_sub(String id) {
		list2.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_room WHERE semester_id='"+id+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
		while(resultSet.isAfterLast() == false){
			list2.add(i, resultSet.getString(0)+" . "+resultSet.getString(1));
			link_list2.add(i, resultSet.getString(0));
            i++;
            resultSet.moveToNext();
		}
		runOnUiThread(new Runnable() {
    		@Override
    	    public void run() {
    			adapter2.notifyDataSetChanged();
    		}
    	});
	}
}