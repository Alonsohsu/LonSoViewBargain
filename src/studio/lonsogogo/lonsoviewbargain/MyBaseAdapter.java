package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyBaseAdapter extends BaseAdapter {	
	private Homepage mainActivity;
	private LayoutInflater myInflater;
	private ArrayList<HashMap<String, String>> list = null;
	private ArrayList<HashMap<String, String>> list3 = null;
	private ViewTag viewTag;
	 
	public MyBaseAdapter(Homepage context, ArrayList<HashMap<String, String>> list, ArrayList<HashMap<String, String>> list3) {
	    //取得 MainActivity 實體
	    this.mainActivity = context;
	    this.myInflater = LayoutInflater.from(context);
	    this.list = list;
	    this.list3 = list3;
	}
	 
	public MyBaseAdapter(OnClickListener onClickListener,
			ArrayList<HashMap<String, String>> shopList,
			ArrayList<HashMap<String, String>> customShopList) {
		this.list = shopList;
	    this.list3 = customShopList;
	}

	public int getCount() {
	    return list.size();
	}
	 
	public Object getItem(int position) {
	    return list.get(position);
	}
	 
	public long getItemId(int position) {
	    return Long.valueOf(list.get(position).get("num"));
	}
	 
	public View getView(int position, View convertView, ViewGroup parent) {
	 
	    if (convertView == null) {
	        // 取得listItem容器 view
	        convertView = myInflater.inflate(R.layout.list_add, null);
	 
	        // 建構listItem內容view
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.list_add_text),
	                (Button) convertView.findViewById(R.id.list_add_add));
	 
	        // 設置容器內容
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	     
	    viewTag.text1.setText(list.get(position).get("name"));
	    //設定按鈕監聽事件及傳入 MainActivity 實體
	    if ( list3 == null )
	    	viewTag.btn1.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
	    viewTag.btn1.setOnClickListener(new ItemButton_Click(this.mainActivity, position));
	     
	    return convertView;
	}
	 
	public class ViewTag {
	    TextView text1;
	    TextView btn1;
	     
	    public ViewTag(TextView textview1, Button button1) {
	        this.text1 = textview1;
	        this.btn1 = button1;
	    }
	}
	 
	//自訂按鈕監聽事件
	class ItemButton_Click implements OnClickListener {
	    private int position;
	    private Homepage mainActivity;
	     
	    ItemButton_Click(Homepage context, int pos) {
	        this.mainActivity = context;
	        position = pos;
	    }
	 
	    public void onClick(View v) {
	    	if ( list3 != null )
	    	{
		        HashMap<String,String> item = new HashMap<String,String>();
		        item.put( "num", list.get(position).get("num") );
				item.put( "id", list.get(position).get("id") );
				item.put( "name", list.get(position).get("name") );
				if ( list3.contains(item) );
				else list3.add( item );
				Toast.makeText(v.getContext(), list.get(position).get("name")+" 新增成功", Toast.LENGTH_SHORT).show();
	    	}
	    	
	    	else
	    	{
	    		HashMap<String,String> item = new HashMap<String,String>();
	    		item.put( "num", list.get(position).get("num") );
				item.put( "id", list.get(position).get("id") );
				item.put( "name", list.get(position).get("name") );
	    		list.remove(item);
	    		notifyDataSetChanged();
	    	}
	    }
	}
}
