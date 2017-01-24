package us.bitto.kazi.server;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddSem extends Activity {
	EditText ed1;
	ListView lv;
	Button b1;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> link_list = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	SQLiteDatabase mydatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_sem);
		ed1=(EditText) this.findViewById(R.id.editText1);
		b1=(Button) this.findViewById(R.id.button1);
		
		lv=(ListView) this.findViewById(R.id.listView1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		lv.setAdapter(adapter);
		
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(ed1.getText().toString().compareTo("")==0) {
					show_toast("Empty Field");
				}
				else {
					mydatabase.execSQL("INSERT INTO class_semester VALUES(null,'"+ed1.getText()+"');");
					show_toast("Added Semester");
					get_data();
				}
			}
		});
		get_data();
	}
	public void get_data() {
		list.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_semester ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
		while(resultSet.isAfterLast() == false){
			list.add(i, resultSet.getString(0)+" . "+resultSet.getString(1));
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
	public void show_toast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
}
