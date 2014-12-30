package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;

import studio.lonsogogo.lonsoviewbargain.R.drawable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeDataAdapter extends BaseAdapter {	
	private LayoutInflater mInflater;
	public ArrayList<String[]> data;//陣列是存網址及文字說明	
	
	public HomeDataAdapter(Context c,ArrayList<String[]> d) {			
		mInflater = LayoutInflater.from(c);
		data = d;		
	}
	
	public int getCount() {
		return data.size();
	}
	
	public Object getItem(int position) {
		return data.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.all_text, null);
			holder = new ViewHolder();
			holder.text = (TextView) view.findViewById(R.id.all_text_text);
			view.setTag(holder);		
		} else {
			holder = (ViewHolder) view.getTag();
		} 
		
		holder.text.setText(data.get(position)[0]);//顯示文字說明
		return view;
	}
	
	Handler h = new Handler(){//告訴BaseAdapter資料已經更新了
		@Override
		public void handleMessage(Message msg) {
			Log.d("m", "notifyDataSetChanged");
			notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};	
	
	class ViewHolder{
		TextView text;
	}
}