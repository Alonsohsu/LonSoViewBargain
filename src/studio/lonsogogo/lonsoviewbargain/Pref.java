package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Pref extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	public static final String PREF = "JU4I_PREF";
	public static final String PAGE_SIZE = "PG_SIZE";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.pref);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	    sp.registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		String temp = sp.getString(key, "5");
		int value = Integer.parseInt(temp);
		if (value < 5 ) {
			storePageCnt(this, "5");
            Toast.makeText(this, "數字必須介於5~20，數字已自動調整為 5", Toast.LENGTH_SHORT).show();
		}
		if ( value > 20 ) {
			storePageCnt(this, "20");
			Toast.makeText(this, "數字必須介於5~20，數字已自動調整為 20", Toast.LENGTH_SHORT).show();
		}
		
		restartActivity();
	}
	
	private void restartActivity() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	
	public static void restoreCustomShopList( Context context, ArrayList<HashMap<String,String>> customShopList ) {
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		int cstm_shop_size = settings.getInt("cstm_shop_size",0);
		
		for( int i = 0 ; i < cstm_shop_size ; i++)
		{
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("num", settings.getString("cstm_shop_num"+i, null));
			item.put("id", settings.getString("cstm_shop_id"+i, null));
			item.put("name", settings.getString("cstm_shop_name"+i, null));
			if ( customShopList.contains(item) );
			else customShopList.add( item );
		}
	}
	
	public static void restoreFavorList( Context context, ArrayList<HashMap<String,String>> favorList ) {
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		int favor_size = settings.getInt("favor_size",0);
		
		for( int i = 0 ; i < favor_size ; i++)
		{
			HashMap<String,String> item = new HashMap<String,String>();
			for ( int j = 0 ; j < PrefPacket.getPdtTagLength() ; j++)
				item.put(PrefPacket.pdtTag[j], settings.getString(PrefPacket.favorTag[j]+i, null));
			if ( favorList.contains(item) );
			else favorList.add( item );
		}
	}
	
	public static String restorePageCnt( Context context ) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString(PAGE_SIZE,"5");
	}
	
	public static void storeCustomShopList( Context context, ArrayList<HashMap<String,String>> customShopList ) {
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		Editor editor = settings.edit();

		for(int i = 0 ; i < customShopList.size() ; i++) {
		         editor.putString("cstm_shop_num"+i,customShopList.get(i).get("num"));
		         editor.putString("cstm_shop_id"+i,customShopList.get(i).get("id"));
		         editor.putString("cstm_shop_name"+i,customShopList.get(i).get("name"));
		}
		editor.putInt("cstm_shop_size",customShopList.size()).commit();
	}
	
	public static void storeFavorList( Context context, ArrayList<HashMap<String,String>> favorList ) {
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		Editor editor = settings.edit();

		for(int i = 0 ; i < favorList.size() ; i++)
			for ( int j = 0 ; j < PrefPacket.getPdtTagLength() ; j++ )
				editor.putString(PrefPacket.favorTag[j]+i,favorList.get(i).get(PrefPacket.pdtTag[j]));
		editor.putInt("favor_size",favorList.size()).commit();
	}
	
	public static void storePageCnt( Context context, String pageCnt ) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putString(PAGE_SIZE, pageCnt).commit();
	}
}
