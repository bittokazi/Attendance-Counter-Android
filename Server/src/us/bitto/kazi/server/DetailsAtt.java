package us.bitto.kazi.server;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DetailsAtt extends Activity {
	ListView lv;
	ArrayList<ListViewData> list = new ArrayList<ListViewData>();
	ArrayList<String> link_list = new ArrayList<String>();
	ListViewAdapter adapter;
	
	SQLiteDatabase mydatabase;
	
	String class_id;
	String date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.details_att);
		
		
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		    	class_id="";
		    	date="";
		    } else {
		        class_id=extras.getString("class_id");
		        date=extras.getString("date");
		    }
		} else {
			class_id=(String) savedInstanceState.getSerializable("class_id");
			date=(String) savedInstanceState.getSerializable("date");
		}
		Toast.makeText(this, class_id+" > "+date, Toast.LENGTH_SHORT).show();
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		
		lv=(ListView) this.findViewById(R.id.listView1);
		adapter = new ListViewAdapter(this.getApplicationContext(), list);
		lv.setAdapter(adapter);
		get_data();
	}
	public void get_data() {
		Cursor resultSet = mydatabase.rawQuery("Select * from class_attandance WHERE class_id='"+class_id+"' AND date='"+date+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	      
	      while(resultSet.isAfterLast() == false){
				link_list.add(i, resultSet.getString(0));
				String std_id="";
				String name="";
				
				
				
				Cursor resultSet1 = mydatabase.rawQuery("Select * from class_student WHERE class_id='"+class_id+"' AND id='"+resultSet.getString(2)+"' ORDER BY id DESC",null);
				resultSet1.moveToFirst();
				while(resultSet1.isAfterLast() == false){
					std_id=resultSet1.getString(4);
					name=resultSet1.getString(3);
					resultSet1.moveToNext();
				}
				
				ListViewData lvd=new ListViewData();
				lvd.setData1(resultSet.getString(4));
				lvd.setData2(resultSet.getString(2));
				lvd.setData3("");
				list.add(lvd);
				
                //list.add(i, resultSet.getString(2)+" . "+resultSet.getString(4));
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
}