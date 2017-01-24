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
import android.widget.Spinner;
import android.widget.Toast;

public class AddClass extends Activity {
	EditText ed1;
	EditText ed2;
	Button b1;
	
	Spinner spinner;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> link_list = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	SQLiteDatabase mydatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_class);
		ed1=(EditText) this.findViewById(R.id.editText1);
		ed2=(EditText) this.findViewById(R.id.editText2);
		b1=(Button) this.findViewById(R.id.button1);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		list=new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
		
		mydatabase = openOrCreateDatabase("attandance_counter",MODE_PRIVATE,null);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(ed1.getText().toString().compareTo("")!=0 && ed2.getText().toString().compareTo("")!=0 && spinner.getSelectedItemPosition()!=-1) {
					mydatabase.execSQL("INSERT INTO class_room VALUES(null,'"+ed1.getText()+"','"+ed2.getText()+"', '"+link_list.get(spinner.getSelectedItemPosition())+"');");
					show_toast("Added Class");
				}
				else {
					show_toast("Something Missing");
				}
			}
		});
		get_sem();
	}
	
	public void get_sem() {
		list.clear();
		Cursor resultSet = mydatabase.rawQuery("Select * from class_semester ORDER BY id DESC",null);
		int i=0;
		resultSet.moveToFirst();
		while(resultSet.isAfterLast() == false){
			list.add(i, resultSet.getString(0)+" . "+resultSet.getString(1));
			link_list.add(i, resultSet.getString(0));
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
