package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MyDataAdapter extends BaseAdapter {	
	private LayoutInflater mInflater;
	public ArrayList<String[]> data;//陣列是存網址及文字說明
	public ArrayList<HashMap<String,String>> favor;//陣列是存網址及文字說明
	GetWebImg ImgCache;
	Context context;
	Homepage mHomepage = new Homepage();
	
	public MyDataAdapter(Context c,ArrayList<String[]> d, ArrayList<HashMap<String,String>> f,GetWebImg cache) {			
		mInflater = LayoutInflater.from(c);
		data = d;	
		context = c;
		favor = f;
		ImgCache = cache;
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
			view = mInflater.inflate(R.layout.list_content, null);
			holder = new ViewHolder();
			holder.text = (TextView) view.findViewById(R.id.list_content_name);
			holder.listNum = (TextView) view.findViewById(R.id.list_content_list_num);
			holder.gridNum = (TextView) view.findViewById(R.id.list_content_grid_num);
			holder.pic = (ImageView) view.findViewById(R.id.list_content_img);
			holder.wait = (ProgressBar) view.findViewById(R.id.list_content_progress);
			holder.btn_add = (Button) view.findViewById(R.id.list_content_add);
			holder.btn_share = (Button) view.findViewById(R.id.list_content_share);
			view.setTag(holder);		
		} else {
			holder = (ViewHolder) view.getTag();
		} 
		holder.text.setText(R.string.downloading_msg);//顯示下載中
		holder.listNum.setText(R.string.downloading_msg);//顯示下載中
		holder.gridNum.setText(R.string.downloading_msg);//顯示下載中
		holder.pic.setVisibility(View.INVISIBLE);
		holder.wait.setVisibility(View.VISIBLE);
		if (ImgCache.IsCache(data.get(position)[1]) == false){//如果圖片沒有暫存
			ImgCache.LoadUrlPic(data.get(position)[1],h);
		}else if (ImgCache.IsDownLoadFine(data.get(position)[1]) == true){//如果已經下載完成，就顯示圖片並把ProgressBar隱藏
			holder.pic.setImageBitmap(ImgCache.getImg(data.get(position)[1]));
			holder.text.setText(data.get(position)[0]);//顯示文字說明
			holder.listNum.setText("下標次數: "+data.get(position)[2]+" 次");//顯示文字說明
			holder.gridNum.setText("直購價錢: "+data.get(position)[3]+" 元");//顯示文字說明
			holder.wait.setVisibility(View.GONE);
			holder.pic.setVisibility(View.VISIBLE);	
			holder.btn_add.setOnClickListener(new ItemButton_Click(position));
			holder.btn_share.setOnClickListener(new ItemButton_Share(position));
		}else{
			//這裡是下載中，什麼事都不用做
		}		
		
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
		TextView gridNum;
		TextView listNum;
		ImageView pic;
		Button btn_add;
		Button btn_share;
		ProgressBar wait;
	}
	
	//自訂按鈕監聽事件
	class ItemButton_Click implements OnClickListener {
	    private int position;
	     
	    ItemButton_Click( int pos) {
	        position = pos;
	    }
	 
	    public void onClick(View v) {
	    	int i;
	    	HashMap<String,String> item = new HashMap<String,String>();
			item.put( "id", Integer.toString(position) );
			item.put( "name", data.get(position)[0] );
			item.put( "img", data.get(position)[1] );
			item.put( "listNum", data.get(position)[2] );
			item.put( "gridNum", data.get(position)[3] );
			item.put( "url", data.get(position)[4] );
			item.put( "hasbuy", data.get(position)[5] );
			item.put( "price", data.get(position)[6] );
			item.put( "rate_time", data.get(position)[7] );
			for ( i = 0 ; i < favor.size() ; i++ )
			{
				if( favor.get(i).get("img").equals(data.get(position)[1]) )
				{					
					Toast.makeText(v.getContext(), "此商品已在升火區", Toast.LENGTH_SHORT).show();
					break;
				}
			}
			if ( i == favor.size() )
			{
				favor.add( item );
				Toast.makeText(v.getContext(), data.get(position)[0]+" 已加入升火區", Toast.LENGTH_SHORT).show();
			}
	    }
	}
	
	class ItemButton_Share implements OnClickListener {
	    private int position;
	    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
        "photo_upload" };
	     
	    ItemButton_Share( int pos) {
	        position = pos;
	    }
	 
	    public void onClick(View v) {
	    	Utility.mFacebook = new Facebook("164527260393739");
	    		Utility.mFacebook.authorize((Activity) context, permissions, 0, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {SessionStore.save(Utility.mFacebook, context);}
				@Override
				public void onFacebookError(FacebookError e) {}
				@Override
				public void onError(DialogError e) {}
				@Override
				public void onCancel() {}   
			});
	    	
	    	Bundle parameters = new Bundle();
			parameters.putString("link", data.get(position)[4]);
			parameters.putString("picture", data.get(position)[1]);

			Utility.mFacebook.dialog(context, "feed", parameters, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {SessionStore.save(Utility.mFacebook, context);}
			@Override
			public void onFacebookError(FacebookError error) {}
			@Override
			public void onError(DialogError e) {}
			@Override
			public void onCancel() {}
			});	
	    }
	}
}