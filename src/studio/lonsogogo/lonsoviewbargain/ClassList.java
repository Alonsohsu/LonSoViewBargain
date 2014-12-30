package studio.lonsogogo.lonsoviewbargain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class ClassList extends SherlockActivity implements ActionBar.TabListener {
	Internet internet = new Internet(this);
	Homepage mHomepage = new Homepage();
	Parser parser = new Parser(this);
	ClassAsyncTask urlTask;
	
	HomeDataAdapter homeData;
	String newsInfo;
		
	int counter = 0;		// 計 數器
	int classCount = 0;		// 分類數
	
	String className[];	    // 分類名稱
	String shopName;		// 商店名稱
	String optionValue[];	// 排序方式
	String shopID;			// 商店序號
	String ShopURL;			// 分類網址
	
	ListView listView1;	// List view
	private TextView news;
	String newsStr;
	
	ProgressDialog proDialog;

	ArrayList<String[]> alldata = new ArrayList<String[]>(); // 裝資料的接口
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		if ( !internet.haveInternet() )
			mHomepage.openConnectDialog();
		
		else
		{	
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			
			shopID = extras.getString("KEY_SHOP_ID");		// 獲取商店序號
			shopName = extras.getString("KEY_SHOP_NAME");	// 獲取商店名字
			
			this.setTitle(shopName);	// 重設標題
			
			createTab();

			ShopURL = "http://m.tw.bid.yahoo.com/tw/booth/"+shopID+"&from=FP"; 
		
			urlTask = new ClassAsyncTask( homeData, ClassList.this, alldata, listView1, newsInfo);
			urlTask.execute(ShopURL);        // 调用AsyncTask，传入url参数
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    return false;
    }
	
	@Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
    	// 顯示熱門商店
    	if( tab.getPosition() == 0 )
    	{
    		setContentView(R.layout.list_view);
    		// 顯示廣告
			AdView adView = new AdView(this, AdSize.BANNER, "a15131bf8746a93");
			LinearLayout layout = (LinearLayout)findViewById(R.id.list_view_ad);
			layout.addView(adView);
			adView.loadAd(new AdRequest());
    		listView1 = (ListView)findViewById(R.id.list_view_list);
    		homeData = new HomeDataAdapter(this, alldata);
	        listView1.setAdapter(homeData);
	        listView1.setOnItemClickListener(new OnItemClickListener(){
				@Override
		        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					// Switch to class page
					Intent intent = new Intent(ClassList.this, ProductList.class);
					Bundle bundle = new Bundle();
					bundle.putString("KEY_CLASS_NAME", alldata.get(position)[0]);
					bundle.putString("KEY_BARGAIN_URL", alldata.get(position)[1]);
					bundle.putString("KEY_SHOP_ID", shopID);
					intent.putExtras(bundle);
					startActivity(intent);
		       	}
			});
    	}
    	
    	// 顯示自訂商店
    	else if( tab.getPosition() == 1 )
    	{
    		setContentView(R.layout.all_text);
    		news = (TextView)findViewById(R.id.all_text_text);
    		news.setText(urlTask.getText());
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }
    
    private void createTab() {
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().removeAllTabs();
   
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText("商品分類");
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        tab.setText("最新消息");
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
	}
    
    class ClassAsyncTask extends AsyncTask<String, Integer, Document> {
    	int type;
    	Context context;
    	HomeDataAdapter  homeDataAdapter;
    	TextTransfer textTransfer;
    	int classCount;
    	ArrayList<String[]> alldata = new ArrayList<String[]>(); // 裝資料的接口
    	ListView listView;
    	String news;
    	int productCount;	
    	String className[];	
    	String optionValue[];
    	String shopID;
    	
    	ArrayList<HashMap<String,String>> favorList;
    	GetWebImg imgCache;
    	MyDataAdapter myAdapter;
        
    	public ClassAsyncTask( HomeDataAdapter adapter, Context c, ArrayList<String[]> d, ListView listView1, final String n) {
    		homeDataAdapter = adapter;
    		textTransfer = new TextTransfer(c);
    		alldata = d;
    		context = c;
    		listView = listView1;
    		news = n;
    		proDialog = mHomepage.setDialog(c, proDialog, 0);
    		type = 1;
    	}
    	
    	@Override
        protected void onPreExecute(){
        	 super.onPreExecute();
        }
        
        @Override
        protected Document doInBackground(String... params) {                // 在后台，下载url网页内容
        	try {
        		Document doc;
        		
        		do { 
	        		doc = Jsoup.connect(params[0]).get();
	        		
	        		Elements filter = doc.getElementsByClass("bottom");
	    			Elements newsGet = doc.getElementsByClass("content");
	    			news = newsGet.text();
	                Elements links = filter.select("option[value]"); 
	                classCount = 0;
	                for (Element link : links) classCount++;
        		} while( classCount < 2 );
                
				return doc;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
        }
            
        @Override
        protected void onProgressUpdate(Integer... values) {        // 可以与UI控件交互
        	super.onProgressUpdate(values);
        	proDialog.setProgress(values[0]);  
        }
            
        @Override
        protected void onPostExecute(Document result) {        // 可以与UI控件交互
        	super.onPostExecute(result);
        	int i = 0;
        	
			Elements filter = result.getElementsByClass("bottom");
			Elements newsGet = result.getElementsByClass("content");
			news = newsGet.text();
			if (news == "") news = "目前尚無最新消息";
            Elements links = filter.select("option[value]"); 
            
            className = new String[classCount];
            optionValue = new String[classCount];
            for (Element link : links) {
            	publishProgress((100*i)/classCount);  
            	className[i] = link.text();
            	if ( link.val() != "" ) optionValue[i++] = link.val();
            	else					optionValue[i++] = "-1";
            }
        	textTransfer.HTMLCodeToTextAry(className);	// 把HTML code轉換成一般文字 
        	// 加入分類
	    	int counter = 0;
	        while ( counter < classCount )
	        	alldata.add(createData(className[counter], optionValue[counter++]));
	        homeDataAdapter = new HomeDataAdapter(context, alldata);
	        listView.setAdapter(homeDataAdapter);
	        news = textTransfer.HTMLCodeToText(news);
        	
        	proDialog.dismiss();
        }
        
        protected String getText() {
        	return news;
        }
    }
    
    private String[] createData(String text, String value){
		String temp[]={text, value};
    	return temp;
    }
}
