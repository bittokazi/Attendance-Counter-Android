package us.bitto.kazi.server;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewClassStudent extends Activity {
	String delete_id;
	String d_s_id;
	ListView lv;
	ArrayList<ListViewData> list = new ArrayList<ListViewData>();
	ArrayList<String> link_list = new ArrayList<String>();
	ListViewAdapter adapter;
	String class_id;
	SQLiteDatabase mydatabase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_class_std);
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		
		
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		    	class_id="";
		    } else {
		        class_id=extras.getString("class_id");
		    }
		} else {
			class_id=(String) savedInstanceState.getSerializable("class_id");
		}
		
		
		lv=(ListView) this.findViewById(R.id.listView1);
		adapter = new ListViewAdapter(this.getApplicationContext(), list);
		lv.setAdapter(adapter);
		
		lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu CM, View arg1,
					ContextMenu.ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info =
			            (AdapterView.AdapterContextMenuInfo) menuInfo;
				delete_id=link_list.get(info.position).split(">>>")[0];
				d_s_id=link_list.get(info.position).split(">>>")[1];
				CM.setHeaderTitle("Select The Action"); 
				CM.add("Delete");
			}
		});
		
		get_data();
	}
	@Override 
	public boolean onContextItemSelected(MenuItem item) { 
	 
		mydatabase.execSQL("DELETE FROM class_student WHERE id='"+delete_id+"' AND class_id='"+class_id+"'");
		mydatabase.execSQL("DELETE FROM class_attandance WHERE std_id='"+d_s_id+"' AND class_id='"+class_id+"'");
		
	        Toast.makeText(getApplicationContext() , "Deleted", Toast.LENGTH_SHORT).show();
	 
	 
	    return true; 
	} 
	public void get_data() {
		Cursor resultSet = mydatabase.rawQuery("Select * from class_student WHERE class_id='"+class_id+"' ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
	      
	      while(resultSet.isAfterLast() == false){
	    	  ListViewData lvd=new ListViewData();
				lvd.setData1(resultSet.getString(3));
				lvd.setData2(resultSet.getString(4)+" Mac:"+resultSet.getString(2));
				lvd.setData3("P: "+resultSet.getString(5));
				list.add(lvd);
				link_list.add(i, resultSet.getString(0)+">>>"+resultSet.getString(4));
                //list.add(i, resultSet.getString(0)+" . "+resultSet.getString(3)+" - "+resultSet.getString(5)+" - "+resultSet.getString(2));
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
