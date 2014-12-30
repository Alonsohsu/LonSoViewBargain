package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import studio.lonsogogo.lonsoviewbargain.Homepage.HpgAsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Merchant extends Homepage {
	Parser parser = new Parser(this); // 解析器類別
	
	int merchantID = 0;		// 特約商店的ID
	
	String merchantName = null;
	String content;
	
	String merchantURL[] = {"https://www.facebook.com/pages/%E5%95%BE%EF%BC%94%EF%BD%89%E7%9C%8B%E7%B6%B2%E6%8B%8D/530590463630402?ref=hl",
							"https://www.facebook.com/pages/Feather-Style-%E7%BE%BD%E9%A2%A8%E9%96%A3/158981997594345?fref=ts"};
	
	private Button web;	// List view
	private TextView text;

	ArrayList<String[]> alldata = new ArrayList<String[]>(); // 裝資料的接口
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		merchantID = extras.getInt("MERCHANT_ID");		// 獲取商店序號
		merchantName = extras.getString("MERCHANT_NAME");

		// 顯示廣告
		AdView adView = new AdView(this, AdSize.BANNER, "a15131bf8746a93");
		LinearLayout layout = (LinearLayout)findViewById(R.id.merchant_ad);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		
		this.setTitle(merchantName);
		
		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);	// 隱藏 Tab
		getSupportActionBar().removeAllTabs();
		
		findViews();
		
		MerchantAsyncTask chkVer = new MerchantAsyncTask(this);        // 抓取版本資訊
		chkVer.execute("http://people.cs.nctu.edu.tw/~clhsu/ju4ine/ju4iviewsale/version.html");        // 调用AsyncTask，传入url参数

	}	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    return false;
    }
	
	private void findViews() {
		text = (TextView)findViewById(R.id.merchant_text);
		web = (Button)findViewById(R.id.merchant_web);
	}
	
	class MerchantAsyncTask extends AsyncTask<String, Integer, String> {
		Context context;
        ProgressDialog proDialog;

		public MerchantAsyncTask(Context c) {			
        	proDialog = ProgressDialog.show(c, "請稍候", "資料處理中");
		}
		
		@Override
        protected void onPreExecute(){
        	 super.onPreExecute();
        }
        
        @Override
        protected String doInBackground(String... params) {                // 在后台，下载url网页内容
        	return Download(params);
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {        // 可以与UI控件交互
            super.onProgressUpdate(values);

        }
        
        @Override
        protected void onPostExecute(String result) {        // 可以与UI控件交互
        	super.onPostExecute(result);
        	
        	if ( merchantID == 0 ) 		content = parser.getStrBetweenTag(result, "merchant1:", "</a>" );
    		else if ( merchantID == 1 ) content = parser.getStrBetweenTag(result, "merchant2:", "</a>" );

    		text.setText(content);
    		web.setOnClickListener(new OnClickListener() {
    			 
    			@Override
    			public void onClick(View v) {
    				Uri uri = Uri.parse(merchantURL[merchantID]);
    				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    				startActivity(intent);
    			}
    		});
    		
    		proDialog.dismiss();
        }
	}
}
