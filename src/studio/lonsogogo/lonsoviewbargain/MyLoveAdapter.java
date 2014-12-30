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

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MyLoveAdapter extends BaseAdapter {	
	private Homepage mainActivity;
	private LayoutInflater myInflater;
	private ArrayList<HashMap<String, String>> list = null;
	private ViewTag viewTag;
	private GetWebImg ImgCache;
	Context context;
	Homepage mHomepage = new Homepage();
	 
	public MyLoveAdapter(Homepage c, ArrayList<HashMap<String, String>> list,GetWebImg cache) {
	    //���o MainActivity ����
		context = c; 
	    this.mainActivity = c;
	    this.myInflater = LayoutInflater.from(c);
	    this.list = list;
	    this.ImgCache = cache; 
	}
	 
	public int getCount() {
	    return list.size();
	}
	 
	public Object getItem(int position) {
	    return list.get(position);
	}
	 
	public long getItemId(int position) {
	    return Long.valueOf(list.get(position).get("id"));
	}
	 
	public View getView(int position, View convertView, ViewGroup parent) {
	 
	    if (convertView == null) {
	        // ���olistItem�e�� view
	        convertView = myInflater.inflate(R.layout.list_content, null);
	 
	        // �غclistItem���eview
	        viewTag = new ViewTag(
	        		(ImageView) convertView.findViewById(R.id.list_content_img),
	                (TextView) convertView.findViewById(R.id.list_content_list_num),
	                (TextView) convertView.findViewById(R.id.list_content_grid_num),
	                (TextView) convertView.findViewById(R.id.list_content_name),
	                (Button) convertView.findViewById(R.id.list_content_add),
	                (Button) convertView.findViewById(R.id.list_content_share),
	        		(ProgressBar) convertView.findViewById(R.id.list_content_progress));
	 
	        // �]�m�e�����e
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	    
	    viewTag.text1.setText(R.string.downloading_msg);
	    viewTag.text2.setText(R.string.downloading_msg);
	    viewTag.text3.setText(R.string.downloading_msg);
	    viewTag.pic.setVisibility(View.INVISIBLE);
	    viewTag.wait.setVisibility(View.VISIBLE);
	    viewTag.btn1.setText("����");
		
	     if (ImgCache.IsCache(list.get(position).get("img")) == false){//�p�G�Ϥ��S���Ȧs
			ImgCache.LoadUrlPic(list.get(position).get("img"),h);
		}else if (ImgCache.IsDownLoadFine(list.get(position).get("img")) == true){//�p�G�w�g�U�������A�N��ܹϤ��ç�ProgressBar����
			viewTag.pic.setImageBitmap(ImgCache.getImg(list.get(position).get("img")));
			viewTag.text1.setText("�U�Ц���: "+list.get(position).get("listNum")+" ��");
		    viewTag.text2.setText("���ʻ���: "+list.get(position).get("gridNum")+" ��");
		    viewTag.text3.setText(list.get(position).get("name"));
		    viewTag.pic.setVisibility(View.VISIBLE);
		    viewTag.wait.setVisibility(View.GONE);
		    //�]�w���s��ť�ƥ�ζǤJ MainActivity ����
		    viewTag.btn1.setOnClickListener(new ItemButton_Click(this.mainActivity, position));
		    viewTag.btn_share.setOnClickListener(new ItemButton_Share(this.mainActivity, position));
	     }
		else{
			//�o�̬O�U�����A����Ƴ����ΰ�
			Log.e("test", "downing");
		}
	     
	    return convertView;
	}
	
	Handler h = new Handler(){//�i�DBaseAdapter��Ƥw�g��s�F
		@Override
		public void handleMessage(Message msg) {
			Log.d("m", "notifyDataSetChanged");
			notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};
	 
	public class ViewTag {
		ImageView pic;
	    TextView text1;
	    TextView text2;
	    TextView text3;
	    TextView btn1;
	    Button btn_share;
	    ProgressBar wait;
	     
	    public ViewTag( ImageView img, TextView textview1, TextView textview2, TextView textview3, Button button1, Button button2, ProgressBar wait1) {
	    	this.pic = img;
	        this.text1 = textview1;
	        this.text2 = textview2;
	        this.text3 = textview3;
	        this.btn1 = button1;
	        this.btn_share = button2;
	        this.wait = wait1;
	    }
	}
	 
	//�ۭq���s��ť�ƥ�
	class ItemButton_Click implements OnClickListener {
	    private int position;
	    private Homepage mainActivity;
	     
	    ItemButton_Click(Homepage context, int pos) {
	        this.mainActivity = context;
	        position = pos;
	    }
	 
	    public void onClick(View v) {
    		HashMap<String,String> item = new HashMap<String,String>();
			item.put( "id", list.get(position).get("id") );
			item.put( "listNum", list.get(position).get("listNum") );
			item.put( "gridNum", list.get(position).get("gridNum") );
			item.put( "name", list.get(position).get("name") );
			item.put( "url", list.get(position).get("url") );
			item.put( "img", list.get(position).get("img") );
			item.put( "hasbuy", list.get(position).get("hasbuy") );
			item.put( "price", list.get(position).get("price") );
			item.put( "rate_time", list.get(position).get("rate_time") );
			list.remove(item);
			notifyDataSetChanged();
	    }
	}
	
	class ItemButton_Share implements OnClickListener {
	    private int position;
	    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
        "photo_upload" };
	     
	    ItemButton_Share(Homepage context, int pos) {
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
			parameters.putString("link", list.get(position).get("url"));
			parameters.putString("picture", list.get(position).get("img"));

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
