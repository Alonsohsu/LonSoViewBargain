package studio.lonsogogo.lonsoviewbargain;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class ProductList extends SherlockActivity implements ActionBar.TabListener {
	Homepage mHomepage = new Homepage();
	GetWebImg ImgCache = new GetWebImg(this);
	Parser parser = new Parser(this);
	Sort sorter = new Sort(this);
	TextTransfer textTransfer = new TextTransfer(this);
	Internet internet = new Internet(this);
	PrefPacket mPrefPacket = new PrefPacket();
	
	final int SMALL = 0;
	final int LARGE = 1;
	
	String className;
	String optionValue;
	String urlData;
	String url;
	
	String productName[];
	String productImgURL[];
	String productURL[];
	String listNum[];
	String gridNum[];
	String itemRateTime[];
	String itemPrice[];
	String itemHasbuy[];
	
	int productCount;
	int classID;
	int pageCnt;
	public static int pageSize; // 一頁所包含的商品數
	int nowPage;
	
	Button btn;
	EditText search;
	ProgressDialog proDialog;
	
	int complete = 0;
	String uData;
	
	String shopID;
	ListView listView;
	Spinner fieldSort;
	
	ArrayList<String[]> alldata = new ArrayList<String[]>();
	ArrayAdapter<CharSequence> adapterSort;
	
	MyDataAdapter myAdapter;	// 啾挳陣列
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_list);
		
		PdtAsyncTask2 urlTask;
		
		if ( !internet.haveInternet() )
			mHomepage.openConnectDialog();
		
		else
		{
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
	
			className = extras.getString("KEY_CLASS_NAME");
			optionValue = extras.getString("KEY_BARGAIN_URL");
			shopID = extras.getString("KEY_SHOP_ID");
			
			Log.e("optionValue", optionValue);
			
			this.setTitle(className);
			
			AdView adView = new AdView(this, AdSize.BANNER, "a15131bf8746a93");
			LinearLayout layout = (LinearLayout)findViewById(R.id.sort_list_ad);
			layout.addView(adView);
			adView.loadAd(new AdRequest());
			
			findViews();
			
			if ( optionValue != "-1" ) 
				url = "http://m.tw.bid.yahoo.com/tw/booth/"+shopID+"?sortBy=hasbuy_price&sortMode=asc&sortByCate="+optionValue+"&switchMode=list";
			else 
				url = "http://m.tw.bid.yahoo.com/tw/booth/"+shopID;
			urlTask = new PdtAsyncTask2(this);        // 实例化抽象AsyncTask
	        urlTask.execute(url);        // 调用AsyncTask，传入url参数	        
			
			listView.setAdapter(myAdapter);			
			fieldSort.setAdapter(adapterSort);
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
		        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					Uri uri = Uri.parse(productURL[nowPage*pageSize+position]);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
		       	}
			});			
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Pref.storeFavorList(this, mHomepage.favorList);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
		
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
    	if ( pageCnt != 0 )
    	{
	    	int counter = tab.getPosition()*pageSize;
	    	nowPage = tab.getPosition();
	    	// 清空 Adapter
			while( alldata.size() > 0 )
				alldata.remove(alldata.size() - 1);
			// 顯示熱門商店
	    	if( tab.getPosition() != pageCnt-1 )
	    	{
		        while ( counter < (tab.getPosition()+1)*pageSize )
		        {
		        	alldata.add(createData(productName[counter],productImgURL[counter],listNum[counter], gridNum[counter], 
		        			productURL[counter], itemHasbuy[counter], itemPrice[counter], itemRateTime[counter]));
		        	counter++;
		        }
	    	}
	    	
	    	// 顯示自訂商店
	    	else
	    	{
		        while ( counter < productCount )
		        {
		        	alldata.add(createData(productName[counter],productImgURL[counter],listNum[counter], gridNum[counter], 
		        			productURL[counter], itemHasbuy[counter], itemPrice[counter], itemRateTime[counter]));
		        	counter++;
		        }
	    	}
	    	myAdapter.notifyDataSetChanged();
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }
    
    
    protected void createTab() {
    	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().removeAllTabs();
		pageCnt = productCount/pageSize+1;
		if ( productCount%pageSize == 0 )
			pageCnt--;
   
		for ( int i = 0 ; i < pageCnt ; i++ )
		{
	        ActionBar.Tab tab = getSupportActionBar().newTab();
	        tab.setText("第"+(i+1)+"頁");
	        tab.setTabListener(this);
	        getSupportActionBar().addTab(tab);
		}
	}
	
	public void parser( Document doc )
	{
		int i;

		productURL = new String[productCount];
		productImgURL = new String[productCount];
		productName = new String[productCount];
		listNum = new String[productCount];
		gridNum = new String[productCount];
		itemRateTime = new String[productCount];
		itemPrice = new String[productCount];
		itemHasbuy = new String[productCount];
		
		// productURL, productName
		Elements filter = doc.getElementsByClass("itemTitle");
        Elements links = filter.select("a[href]");
        i = 0;
        for (Element link : links) {
        	productName[i] = link.text();
        	productURL[i++] = link.attr("href");
        }
        
        // productImgURL, listNum, gridNum
        filter = doc.getElementsByClass("itemImg");
        i = 0;
        for (Element link : filter)
        {
        	productImgURL[i] = link.attr("style").split("\\(")[1].split("\\)")[0];
        	String sListNum = link.select("span.listNum").text();
        	String sGridNum = link.select("span.gridNum").text();
        	if ( sListNum == "" ) listNum[i] = "0";
        	else 			 	  listNum[i] = sListNum.split(" ")[0];
        	gridNum[i] = sGridNum.split(" ")[0];
        	i++;
        }
        
        // itemRateTime
        filter = doc.getElementsByClass("itemRate_Time");
        links = filter.select("span");
        i = 0;
        for (Element link : links)
        	itemRateTime[i++] = link.text();
        
        // itemPrice
        filter = doc.getElementsByClass("itemPrice");
        i = 0;
        for (Element link : filter)
        	itemPrice[i++] = link.toString().split("<span>")[1].split("</span>")[0];
        
        // itemHasbuy
        filter = doc.getElementsByClass("itemHasbuy");
        i = 0;
        for (Element link : filter)
        	itemHasbuy[i++] = link.toString().split("<b>")[1].split("</b>")[0];
		
		textTransfer.RemoveTextAry(gridNum, ",");	// 移除逗號
		textTransfer.RemoveTextAry(listNum, ",");	// 移除逗號

		for ( i = 0 ; i < productCount ; i++ )
		{
			productURL[i] = "http://tw.page.bid.yahoo.com"+productURL[i];
			productURL[i] = productURL[i].replace("&#x2F;", "/");
			productImgURL[i] = productImgURL[i].replace("&#x2F;", "/");
			String[] token = productImgURL[i].split("http");
			productImgURL[i] = "http"+token[2];
		}
	}

	private void findViews() {
		listView = (ListView)findViewById(R.id.sort_list_list);
		fieldSort = (Spinner)findViewById(R.id.sort_list_spinner);
		btn = (Button)findViewById(R.id.sort_list_search);
		search = (EditText)findViewById(R.id.sort_list_edit_text);
		btn.setVisibility(View.GONE);
		search.setVisibility(View.GONE);
		adapterSort = ArrayAdapter.createFromResource(this, R.array.sortProduct, android.R.layout.simple_spinner_item);
		adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}	
	
	private String[] createData(String pdtName, String imgURL, String listNum, String gridNum, String url, String hasbuy, 
			String price, String rateTime  ){
    	String temp[] = {pdtName, imgURL, listNum, gridNum, url, hasbuy, price, rateTime };
    	return temp;
    }
	
	private void setListensers() {
		fieldSort.setOnItemSelectedListener(getFeet);
	}
	
	private Spinner.OnItemSelectedListener getFeet = 
			new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position, long id ) {
			switch(parent.getSelectedItemPosition()) {
			case 0:
				sorter.SortList( productCount, itemHasbuy, productURL, productImgURL, productName, 
						listNum, gridNum, itemRateTime, itemPrice, SMALL, gridNum );
				break;
			case 1:
				sorter.SortList( productCount, itemHasbuy, productURL, productImgURL, productName, 
						listNum, gridNum, itemRateTime, itemPrice, LARGE, gridNum );
				break;
			case 2:
				sorter.SortList( productCount, itemHasbuy, productURL, productImgURL, productName, 
						listNum, gridNum, itemRateTime, itemPrice, SMALL, listNum );
				break;
			case 3:
				sorter.SortList( productCount, itemHasbuy, productURL, productImgURL, productName, 
						listNum, gridNum, itemRateTime, itemPrice, LARGE, listNum );
				break;
		/*	case 4:
				myAdapter = new MyDataAdapter(ProductList.this, alldata,favorList, ImgCache);
				listView.setAdapter( myAdapter );   // 顯示升火區
				myAdapter.notifyDataSetChanged();
				break;*/
			}
			refresh();
		}
		public void onNothingSelected(AdapterView parent){
		}
		};
		
	public void refresh() {  
		int counter = nowPage*pageSize;
    	// 清空 Adapter
		while( alldata.size() > 0 )
			alldata.remove(alldata.size() - 1);
		// 顯示熱門商店
    	if( nowPage != pageCnt-1 )
    	{
	        while ( counter < (nowPage+1)*pageSize )
	        {
	        	alldata.add(createData(productName[counter],productImgURL[counter],listNum[counter], gridNum[counter], 
	        			productURL[counter], itemHasbuy[counter], itemPrice[counter], itemRateTime[counter]));
	        	counter++;
	        }
    	}
    	
    	// 顯示自訂商店
    	else
    	{
	        while ( counter < productCount )
	        {
	        	alldata.add(createData(productName[counter],productImgURL[counter],listNum[counter], gridNum[counter], 
	        			productURL[counter], itemHasbuy[counter], itemPrice[counter], itemRateTime[counter]));
	        	counter++;
	        }
    	}
    	myAdapter.notifyDataSetChanged();
    }
		
	class PdtAsyncTask extends AsyncTask<String, Integer, Document> {
		Context context;
		int index;

		public PdtAsyncTask(Context c, int cnt, int idx ) {
			context = c;
			index = idx;
		}

		@Override
	    protected void onPreExecute(){
	    	 super.onPreExecute();
	    }
	    
	    @Override
	    protected Document doInBackground(String... params) {                // 在后台，下载url网页内容
	    	try {
	    		int cnt;
        		Document doc;
        		
        		do { 
        			Log.e("url", params[0]);
	        		doc = Jsoup.connect(params[0]).get();
	        		
	        		Elements filter = doc.getElementsByClass("itemContent");
	        		
	                cnt = 0;
	                for (Element link : filter) {
	                	cnt++;
	                }
        		} while( cnt == 0 );
        		
        		int completeCnt;
        		if ( productCount%20 == 0 ) {
        			completeCnt = productCount/20;
        		}
        		else {
        			completeCnt = productCount/20+1;
        		}
        		publishProgress((100*complete)/completeCnt);  
                
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
	    	Log.e("rrrrr", result.toString());
	    	uData += result.toString();
    		complete++;
    	
    		int completeCnt;
    		if ( productCount%20 == 0 ) {
    			completeCnt = productCount/20;
    		}
    		else {
    			completeCnt = productCount/20+1;
    		}
    		if ( complete == completeCnt )
    		{
    			Document doc = Jsoup.parse(uData);
	    		parser(doc);
	    		sorter.SortList( productCount, itemHasbuy, productURL, productImgURL, productName, 
						listNum, gridNum, itemRateTime, itemPrice, SMALL, gridNum );
	    		myAdapter = new MyDataAdapter(context, alldata,mHomepage.favorList, ImgCache);
	    		createTab();
	    		
	    		listView.setAdapter(myAdapter);
	    		setListensers();
	    		
	    		proDialog.dismiss();
    		}
    		
    		else {
    			PdtAsyncTask urlTask = new PdtAsyncTask(context, productCount, index+20 );
    			String url = "http://m.tw.bid.yahoo.com/tw/booth/"+shopID+"?sortBy=hasbuy_price&sortMode=asc&sortByCate="+optionValue+"&switchMode=list&instore_pattern=&start="+Integer.toString(index+20)+"&loadmore=1";
				urlTask.execute(url);
    		}
	    }
	}
	
	class PdtAsyncTask2 extends AsyncTask<String, Integer, Document> {
		Context context;
		int index;
		
		public PdtAsyncTask2(Context c) {
			context = c;
			proDialog = mHomepage.setDialog(c, proDialog, 1); 
		}
		
		@Override
	    protected void onPreExecute(){
	    	 super.onPreExecute();
	    }
	    
	    @Override
	    protected Document doInBackground(String... params) {                // 在后台，下载url网页内容
	    	try {
	    		int cnt;
        		Document doc;
        		
        		do { 
	        		doc = Jsoup.connect(params[0]).get();
	        		
	        		Elements filter = doc.getElementsByClass("items");
	                Elements links = filter.select("a[href]");
	                cnt = 0;
	                for (Element link : links) {
	                	cnt++;
	                }
        		} while( cnt == 0 );
                
				return doc;
			} catch (IOException e) {
				Log.e("null", null);
				e.printStackTrace();
				return null;
			}
	    }
	        
	    @Override
	    protected void onProgressUpdate(Integer... values) {        // 可以与UI控件交互
	    	super.onProgressUpdate(values);
	    }
	        
	    @Override
	    protected void onPostExecute(Document result) {        // 可以与UI控件交互
	    	super.onPostExecute(result);
	    	Log.e("result", result.toString());
	    	Elements cnt = result.getElementsByClass("itemNum");
	    	 
            productCount = Integer.parseInt(cnt.select("b").text());
            
			uData = "";
			complete = 0;
			
			PdtAsyncTask urlTask = new PdtAsyncTask(context, productCount, 1 );
			String url = "http://m.tw.bid.yahoo.com/tw/booth/"+shopID+"?sortBy=hasbuy_price&sortMode=asc&sortByCate="+optionValue+"&switchMode=list&instore_pattern=&start=1&loadmore=1";
			urlTask.execute(url);
	    }
	}
}