package us.bitto.kazi.server;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<ListViewData> mDataSource;

	public ListViewAdapter(Context context, ArrayList<ListViewData> items) {
		  mContext = context;
		  mDataSource = items;
		  mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = mInflater.inflate(R.layout.list_view, parent, false);
		
		// Get title element
		TextView titleTextView = 
		  (TextView) rowView.findViewById(R.id.textView1);
		 
		// Get subtitle element
		TextView subtitleTextView = 
		  (TextView) rowView.findViewById(R.id.textView2);
		 
		// Get detail element
		TextView detailTextView = 
		  (TextView) rowView.findViewById(R.id.textView3);
		
		titleTextView.setText(mDataSource.get(position).getData1());
		subtitleTextView.setText(mDataSource.get(position).getData2());
		detailTextView.setText(mDataSource.get(position).getData3());
		
		return rowView;
	}

}
