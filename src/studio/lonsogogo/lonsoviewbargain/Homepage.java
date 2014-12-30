package studio.lonsogogo.lonsoviewbargain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

@SuppressWarnings("deprecation")
public class Homepage extends SherlockActivity implements ActionBar.TabListener {
	GetWebImg ImgCache = new GetWebImg(this);	
	Internet internet = new Internet(this);
	Sort sorter = new Sort(this);
	Parser parser = new Parser(this);
	PrefPacket prefProduct = new PrefPacket();

	public static final String PREF = "JU4I_PREF";
	
	final int SMALL = 0;
	final int LARGE = 1;
	
	final int SHOP_SEARCH = 0;
	final int MULTI_SEARCH = 1;
	final int ANNOCEMENT = 2;
	
	final int TAB_ID_HOT_SHOP = 0;
	final int TAB_ID_CSTM_SHOP = 1;
	final int TAB_ID_SRCH_SHOP = 2;
	final int TAB_ID_FAVOR_LIST = 3;
	final int TAB_ID_MERCHANT = 4;
		
	private int tabID;
	private int pageCnt;		// The page count of search shop result
	
	private String sShopCnt;	// The number of search shop
	private String sShopID[];	// The ID of search shop
	private	String sShopName[]; // The name of search shop
	
	int complete ;		// The flag of indicate download is complete
	String uData;		// The data of catch on url
	
	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;
	
	ProgressDialog proDialog;

	private String aboutMsg;	// 關於
	
	private ListView listView;	// List View
	private MyBaseAdapter adapter;
	private SimpleAdapter merchantAdapter;
	private MyBaseAdapter customAdapter;
	private MyLoveAdapter myLoveAdapter;
	
	Spinner fieldSort;
	Button btn;
	EditText search;
	TextView annoce;

	ArrayAdapter<CharSequence> adapterSort;
	MyBaseAdapter adapterSearch;

	ArrayList<HashMap<String,String>> shopList = new ArrayList<HashMap<String,String>>();
	ArrayList<HashMap<String,String>> merchantList = new ArrayList<HashMap<String,String>>();
	static public ArrayList<HashMap<String,String>> customShopList = new ArrayList<HashMap<String,String>>();
	static public ArrayList<HashMap<String,String>> favorList = new ArrayList<HashMap<String,String>>();
	ArrayList<HashMap<String,String>> searchList = new ArrayList<HashMap<String,String>>();
	
	// 宣告選單 ID
	protected static final int MENU_ABOUT = Menu.FIRST;
	protected static final int MENU_MAIL = Menu.FIRST+1;
	protected static final int MENU_SHARE = Menu.FIRST+2;
	protected static final int MENU_LOGOUT = Menu.FIRST+3;
	protected static final int MENU_SETTING = Menu.FIRST+4;
	protected static final int MENU_QUIT = Menu.FIRST+5;
	
	private Handler mHandler;
	
	String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins", "user_about_me" };
	
	ArrayList<String[]> alldata = new ArrayList<String[]>();	// 接收 list view 資訊的陣列
	
	public Homepage(){}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.sort_list);
		
		findViews();
		
		if ( !internet.haveInternet() )
			openConnectDialog();
		
		else
		{
			// 顯示廣告
			AdView adView = new AdView(this, AdSize.BANNER, "a15131bf8746a93");
			LinearLayout layout = (LinearLayout)findViewById(R.id.sort_list_ad);
			layout.addView(adView);
			adView.loadAd(new AdRequest());
			
			// 因為還沒 Create 之前，string.xml 檔還不會變成 Resource 檔案，因此必須寫在 Create 裡面。
			final String [] shopName = getResources().getStringArray(R.array.shopName);
			final String [] shopID = getResources().getStringArray(R.array.shopID);
			final String [] merchantShop = getResources().getStringArray(R.array.merchantShop);
			
			Utility.mFacebook = new Facebook("164527260393739");
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			SessionStore.restore(Utility.mFacebook, this);
		
	        mHandler = new Handler();
			
			//把資料加入ArrayList中
			 for ( int i = 0 ; i < shopName.length ; i++ ) {
				 HashMap<String,String> item = new HashMap<String,String>();
				 item.put( "num", Integer.toString(i) );
				 item.put( "id", shopID[i] );
				 item.put( "name", shopName[i] );
				 shopList.add( item );
			 }
			 
			//把資料加入ArrayList中
			 for ( int i = 0 ; i < merchantShop.length ; i++ ) {
				 HashMap<String,String> item = new HashMap<String,String>();
				 item.put("name", merchantShop[i]);
				 merchantList.add( item );
			 }
			 
			 adapter = new MyBaseAdapter(this, shopList, customShopList); // 將資料傳給接口
			 customAdapter = new MyBaseAdapter(this, customShopList, null); // 將資料傳給接口
			 myLoveAdapter = new MyLoveAdapter(this, favorList, ImgCache);
			 
			//新增SimpleAdapter
			 merchantAdapter = new SimpleAdapter( 
			 this, 
			 merchantList,
			 R.layout.all_text,
			 new String[] { "name" },
			 new int[] { R.id.all_text_text } );
			 	
			 HpgAsyncTask findAnnoce = new HpgAsyncTask(ANNOCEMENT);        // 抓取版本資訊
			 findAnnoce.execute("http://people.cs.nctu.edu.tw/~clhsu/ju4ine/ju4iviewsale/version.html");        // 调用AsyncTask，传入url参数
			 createTab();	// 建立標籤
			 
			 fieldSort.setAdapter(adapterSort);
			 fieldSort.setOnItemSelectedListener(getFeet);
			 listView.setOnItemClickListener(new OnItemClickListener(){
				 @Override
				 public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					 if (tabID == TAB_ID_HOT_SHOP )
					 {
						 Log.e("position", Integer.toString(position));
						 Intent intent = new Intent(Homepage.this, ClassList.class);
						 Bundle bundle = new Bundle();
						 bundle.putString("KEY_SHOP_ID", shopID[position]);
						 bundle.putString("KEY_SHOP_NAME", shopName[position]);
						 intent.putExtras(bundle);
						 startActivity(intent);
					 }
					 
					 else if (tabID == TAB_ID_CSTM_SHOP )
					 {
						 Intent intent = new Intent(Homepage.this, ClassList.class);
						 Bundle bundle = new Bundle();
						 bundle.putString("KEY_SHOP_ID", customShopList.get(position).get("id"));
						 bundle.putString("KEY_SHOP_NAME", customShopList.get(position).get("name"));
						 intent.putExtras(bundle);
						 startActivity(intent);
					 }
					 
					 else if (tabID == TAB_ID_SRCH_SHOP )
					 {
						 Intent intent = new Intent(Homepage.this, ClassList.class);
						 Bundle bundle = new Bundle();
						 bundle.putString("KEY_SHOP_ID", searchList.get(position).get("id"));
						 bundle.putString("KEY_SHOP_NAME", searchList.get(position).get("name"));
						 intent.putExtras(bundle);
						 startActivity(intent);
					 }
					 
					 else if (tabID == TAB_ID_FAVOR_LIST )
					 {
						 Uri uri = Uri.parse(favorList.get(position).get("url"));
						 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						 startActivity(intent);
					 }
					
					 else if( tabID == TAB_ID_MERCHANT )
					 {
						 Intent intent = new Intent(Homepage.this, Merchant.class);
						 Bundle bundle = new Bundle();
						 bundle.putInt("MERCHANT_ID", position);
						 bundle.putString("MERCHANT_NAME", merchantShop[position]);
						 intent.putExtras(bundle);
						 startActivity(intent);
					 }
				 }
			 });
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		Pref.storeCustomShopList(this, customShopList);
		Pref.storeFavorList(this, favorList);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Pref.restoreCustomShopList(this, customShopList);
		Pref.restoreFavorList(this, favorList);
		ProductList.pageSize = Integer.parseInt(Pref.restorePageCnt(this));
		
	}
	
	@Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {	
    	tabID = tab.getPosition();
    	
    	// 先設定成都看不到
    	fieldSort.setVisibility(View.GONE);
		btn.setVisibility(View.GONE);
		search.setVisibility(View.GONE);
		annoce.setVisibility(View.GONE);
		if ( annoce.length() < 10 ) {
			annoce.setText("公告下載中...");
			annoce.setTextColor(0xFF008080);
		}
    	
   		if ( tab.getPosition() == TAB_ID_HOT_SHOP ) {
			 listView.setAdapter( adapter );			// 顯示熱門商店
			 annoce.setVisibility(View.VISIBLE);
   		}
    	else if ( tab.getPosition() == TAB_ID_CSTM_SHOP ) {
    		listView.setAdapter( customAdapter );   // 顯示自訂商店
    	}
    	else if ( tab.getPosition() == TAB_ID_SRCH_SHOP ) {
    		adapterSearch = new MyBaseAdapter(this, searchList, customShopList); // 將資料傳給接口
    		listView.setAdapter(adapterSearch);
    		btn.setVisibility(View.VISIBLE);
    		search.setVisibility(View.VISIBLE);
    		btn.setText("搜尋店家");
    		search.setText("請在此輸入您要新增的店家名稱");
    		search.setOnFocusChangeListener(new OnFocusChangeListener() {
    		    public void onFocusChange(View v, boolean hasFocus) {
    		        if (!hasFocus) {// 失去焦点
    		        	search.setText("請在此輸入您要新增的店家名稱");
    		        } else {
    		        	search.setText("");
    		        }
    		    }
    		});
    		
    		btn.setOnClickListener(new Button.OnClickListener(){ 
                @Override
                public void onClick(View v) {
                	searchList.removeAll(searchList);
                	String encodedurl;
                	
                	try {
                		encodedurl = URLEncoder.encode(search.getText().toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						encodedurl ="";
						e.printStackTrace();
					}
                	
                	String url = "http://tw.store.bid.yahoo.com/tw/search?p="+encodedurl;
                	
                	HpgAsyncTask searchTask = new HpgAsyncTask(Homepage.this, SHOP_SEARCH);        // 抓取版本資訊
                	searchTask.execute(url);        // 调用AsyncTask，传入url参数
                }         
            });
    	}
    	else if ( tab.getPosition() == TAB_ID_FAVOR_LIST ) {
    		listView.setAdapter( myLoveAdapter );   // 顯示升火區
    		fieldSort.setVisibility(View.VISIBLE);
    	}
    	else if ( tab.getPosition() == TAB_ID_MERCHANT ) {
    		listView.setAdapter( merchantAdapter ); // 顯示特約商店
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("選單").setIcon(android.R.drawable.ic_menu_more);;
        sub.add(0, MENU_ABOUT, 0, R.string.menu_about);
        sub.add(0, MENU_MAIL, 0, R.string.menu_mail);
        sub.add(0, MENU_SHARE, 0, R.string.menu_share);
        sub.add(0, MENU_LOGOUT, 0, R.string.menu_logout);
        sub.add(0, MENU_SETTING, 0, R.string.menu_setting);
        sub.add(0, MENU_QUIT, 0, R.string.menu_exit);
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_ABOUT:
				openAboutDialog();
				break;
			case MENU_MAIL:
				Intent intent = new Intent(Homepage.this, SendMail.class);
				startActivity(intent);
				break;
			case MENU_SHARE:
				ShareAPP();
				break;
			case MENU_LOGOUT:
				new Thread(){
		            public void run() {
		            	try {
		            		Utility.mFacebook.logout(getApplicationContext());
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            };
		            }.start();
		            
                Toast.makeText(this, "登出成功", Toast.LENGTH_SHORT).show();
                SessionStore.clear(getApplicationContext());
				break;
			case MENU_SETTING:
				Intent prefIntent = new Intent(Intent.ACTION_VIEW);
				prefIntent.setClass(Homepage.this, Pref.class);
				startActivity(prefIntent);
				break;
			case MENU_QUIT:
				finish();
				break;
			
		}
		return super.onOptionsItemSelected(item);
	}

	private void createTab() {
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
   
		addTab("熱門商家");
		addTab("自訂商家");
		addTab("搜尋店家");
		addTab("升火區");
		addTab("特約商店");
	}
	
	private void findViews() {
		listView = (ListView)findViewById(R.id.sort_list_list);
		fieldSort = (Spinner)findViewById(R.id.sort_list_spinner);
		btn = (Button)findViewById(R.id.sort_list_search);
		search = (EditText)findViewById(R.id.sort_list_edit_text);
		annoce = (TextView)findViewById(R.id.sort_list_text);
		adapterSort = ArrayAdapter.createFromResource(this, R.array.sortFavor, android.R.layout.simple_spinner_item);
		adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}	
	
	private void ShareAPP() {
		Utility.mFacebook = new Facebook("164527260393739");
		
		Utility.mFacebook.authorize(this, permissions, 0, new LoginDialogListener());
    	
    	Bundle parameters = new Bundle();
		parameters.putString("link", "https://play.google.com/store/apps/details?id=studio.lonsogogo.lonsoviewbargain&feature=search_result#?t=W251bGwsMSwyLDEsInN0dWRpby5sb25zb2dvZ28ubG9uc292aWV3YmFyZ2FpbiJd");
		parameters.putString("picture", "https://lh6.ggpht.com/TUlSSHrXVPXAupShoVObM5k74NC0LPk5hFXcTc6xMOLKPksPh2bDNSmzO-RXWW0vN1M=w124");

		Utility.mFacebook.dialog(this, "feed", parameters, new AppRequestsListener());	
	}
	
	private void openAboutDialog() {
		
		aboutMsg = "";
		aboutMsg = "版本: "+ getVersion() + "\n作者: Ju4iNe Stdio\n資訊來源: 雅虎拍賣";
		
		new AlertDialog.Builder(Homepage.this)
		.setTitle(R.string.homepage_about_title)
		.setMessage(aboutMsg)
		.setPositiveButton(R.string.homepage_about_comfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
		.show();
	}
	
	public void openConnectDialog() 
	{
		String connect_err_msg = "偵測不到網路，請檢查網路是否有連線。";
		
		new AlertDialog.Builder(Homepage.this)
		.setTitle(R.string.homepage_connect_err)
		.setMessage(connect_err_msg)
		.setPositiveButton(R.string.homepage_about_comfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
		.show();
	}
	
	private String getVersion()
	{
		PackageManager packageManager = getPackageManager();	// 获取packagemanager的实例
		PackageInfo packInfo;	// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (NameNotFoundException e) {
				packInfo = null;
				e.printStackTrace();
		}
		String version = packInfo.versionName;
		
    	return version;
	}
	
	
	
	private Spinner.OnItemSelectedListener getFeet = 
			new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position, long id ) {
			switch(parent.getSelectedItemPosition()) {
			case 0:
				sorter.SortAryList(favorList.size(), favorList, SMALL, "gridNum");
				break;
			case 1:
				sorter.SortAryList(favorList.size(), favorList, LARGE, "gridNum");
				break;
			case 2:
				sorter.SortAryList(favorList.size(), favorList, SMALL, "listNum");
				break;
			case 3:
				sorter.SortAryList(favorList.size(), favorList, LARGE, "listNum");
				break;
			case 4:
				favorList.clear();
				break;
			}
			refresh();
		}
		public void onNothingSelected(AdapterView parent){
		}
	};
		
	public void refresh() {  	
    	myLoveAdapter.notifyDataSetChanged();
    }  
	
	private void addTab(String text)
	{
		ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(text);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
	}

	class HpgAsyncTask extends AsyncTask<String, Integer, String> {
        Context context;
        String data;
        int type;
        
        public HpgAsyncTask(Context c, int i, int pt) {
			context = c;
        	type = i;   
		}

		public HpgAsyncTask(Context c, int i) {
			context = c;
			proDialog = setDialog(c, proDialog, 0);
        	type = i;
		}
		
		public HpgAsyncTask( int i ) {
        	type = i;
		}
		
		@Override
        protected void onPreExecute() {
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
     
        	
        	if ( type == SHOP_SEARCH) {
        		if ( result.contains("找不到符合"))
        		{
        			Toast.makeText( context, "找不到符合的商店", Toast.LENGTH_SHORT).show();
        			adapterSearch.notifyDataSetChanged();
        			proDialog.dismiss();
        		}
        		
        		else {
        			Log.e("length", Integer.toString(result.length()));
            		// 解析出版本及官方公告
            		sShopCnt = parser.getStrBetweenTagWithFlag(result, "class=\"yaumodule\"", "<em>", "</em>");
            		pageCnt = Integer.parseInt(sShopCnt)/25+1;
            		if ( Integer.parseInt(sShopCnt)%25 == 0 )
            			pageCnt--;
            		
        			sShopID = new String[Integer.parseInt(sShopCnt)];
        			sShopName = new String[Integer.parseInt(sShopCnt)];
        			
            		if ( pageCnt == 1 )
            		{
            			sShopID = parser.getStrAryBetweenTag(result, "http://tw.rd.yahoo.com/referurl/bid/store/srp/list/*http://tw.user.bid.yahoo.com/tw/user/",
            										"?u=", Integer.parseInt(sShopCnt));
            			sShopName = parser.getStrAryBetweenTagWithFlag(result, "http://tw.rd.yahoo.com/referurl/bid/store/srp/list/*http://tw.user.bid.yahoo.com/tw/user/", 
            					"\">", "</a>", Integer.parseInt(sShopCnt));
            			
            			for ( int i = 0 ; i < Integer.parseInt(sShopCnt) ; i++ )
                		{
                			HashMap<String,String> item = new HashMap<String,String>();
             		        item.put( "num", Integer.toString(i));
             				item.put( "id", sShopID[i] );
             				item.put( "name", sShopName[i] );
             				if ( searchList.contains(item) );
            				else searchList.add( item );
                		}
                		adapterSearch.notifyDataSetChanged();
                		proDialog.dismiss();
            		}
            		else
            		{ 
            			uData = "";
            			complete = 0;
            			uData += result;
            			
            			for ( int i = 1, j = 26 ; i < pageCnt ; i++, j+=25 )
            			{
            				String url = "http://tw.store.bid.yahoo.com/tw/search?ei=UTF-8&p="+search.getText().toString()+"&b="+j+"&s=&z=amount&mode=";
            				
            				HpgAsyncTask sTask = new HpgAsyncTask( context, MULTI_SEARCH, 1);
            				sTask.execute(url);
            			}
            		}
        		}
        	}
        	
        	else if ( type == MULTI_SEARCH )
        	{        	
        		complete++;
        		uData += result;
        		if ( complete == pageCnt-1 )
        		{
        			sShopID = parser.getStrAryBetweenTag(uData, "http://tw.rd.yahoo.com/referurl/bid/store/srp/list/*http://tw.user.bid.yahoo.com/tw/user/",
							"?u=", Integer.parseInt(sShopCnt));
        			sShopName = parser.getStrAryBetweenTagWithFlag(uData, "http://tw.rd.yahoo.com/referurl/bid/store/srp/list/*http://tw.user.bid.yahoo.com/tw/user/", 
					"\">", "</a>", Integer.parseInt(sShopCnt));
        			
        			for ( int i = 0 ; i < Integer.parseInt(sShopCnt) ; i++ )
            		{
            			HashMap<String,String> item = new HashMap<String,String>();
         		        item.put( "num", Integer.toString(i));
         				item.put( "id", sShopID[i] );
         				item.put( "name", sShopName[i] );
         				if ( searchList.contains(item) );
        				else searchList.add( item );
            		}
            		adapterSearch.notifyDataSetChanged();
            		proDialog.dismiss();
        		}
        	}
        	
        	else if ( type == ANNOCEMENT )
        	{        	
        		String anc = parser.getStrBetweenTag(result, "Announcement:", "</a>");
        		String ver = parser.getStrBetweenTag(result, "Version:", "</a>");
        		if ( ver.equals(getVersion())) annoce.setText("公告: "+anc);
        		else						   annoce.setText("公告: 新版本已發佈，請到google play更新\n"+anc);
        	}
        }
	}
	
	public String Download(String... params)
	{
		String result = "";
		  
		  DefaultHttpClient client = new DefaultHttpClient();
		  try {
				HttpGet get = new HttpGet(params[0]);
				HttpResponse response = client.execute(get); 
				HttpEntity resEntity = response.getEntity(); 
				if (resEntity != null) {
				 result = toUTF8(resEntity.getContent());
				}         
		  } catch (Exception e) {
		         e.printStackTrace();
		     } finally {
		      client.getConnectionManager().shutdown();
		     }
			 
		  return result;
	}
	
	public ProgressDialog setDialog( Context c, ProgressDialog p, int type ){
		if ( type == 0 ) {
			ProgressDialog dialog;
			dialog = new ProgressDialog(c, 0);
			dialog.setButton("cancel", new DialogInterface.OnClickListener() {  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.cancel();  
	            }  
	        });  
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {  
	            @Override  
	            public void onCancel(DialogInterface dialog) {  
	                finish();  
	            }  
	        });  
			dialog.setCancelable(true);   
			dialog.setTitle("請稍候");
			dialog.setMessage("資料處理中");
			dialog.show();
			return dialog;
		}
		else 
		{
			ProgressDialog dialog;
			dialog = new ProgressDialog(c, 0);
			dialog.setButton("cancel", new DialogInterface.OnClickListener() {  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.cancel();  
	            }  
	        });  
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {  
	            @Override  
	            public void onCancel(DialogInterface dialog) {  
	                finish();  
	            }  
	        });  
			dialog.setCancelable(true);   
			dialog.setTitle("請稍候");
			dialog.setMessage("資料處理中");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
			dialog.setMax(100);
			dialog.show();
			return dialog;
		}
	}
	
	protected final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
            SessionStore.save(Utility.mFacebook, getApplicationContext());
        }

        @Override
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	super.onActivityResult(requestCode, resultCode, data); 
    	Log.e("onActivity", "onActivity");
    	Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);  
    }
    
    public void requestUserData() {
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");
        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
    }
    
    public class UserRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response);

                final String picURL = jsonObject.getJSONObject("picture")
                        .getJSONObject("data").getString("url");
                final String name = jsonObject.getString("name");
                Utility.userUID = jsonObject.getString("id");
                
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * callback for the apprequests dialog which sends an app request to user's
     * friends.
     */
    public class AppRequestsListener extends BaseDialogListener {
        @Override
        public void onComplete(Bundle values) {
            Toast toast = Toast.makeText(getApplicationContext(), "App request sent",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onFacebookError(FacebookError error) {
            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast toast = Toast.makeText(getApplicationContext(), "App request cancelled",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    
    private static String toUTF8(InputStream is){
    	  //InputStream is = resEntity.getContent();
    	        InputStreamReader isr = null;
    	        StringBuffer buffer = new StringBuffer();
    	  try {
    	   isr = new InputStreamReader(is, "utf-8");
    	   
    	   Reader in = new BufferedReader(isr);
    	         
    	   int ch;
    	   while((ch = in.read()) != -1){
    	    buffer.append((char)ch);
    	   }
    	   
    	   isr.close();
    	   is.close();
    	  } catch (UnsupportedEncodingException e) {
    	   e.printStackTrace();
    	  } catch (IOException e){
    	   e.printStackTrace();
    	  }
    	        
    	  
    	  return buffer.toString();
    	 }
}
