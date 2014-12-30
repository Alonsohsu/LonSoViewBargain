package studio.lonsogogo.lonsoviewbargain;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet {
	private Context context;
	
	public Internet(Context c) 
	{
		context = c;
	}
	
	public boolean haveInternet()
	{
		boolean result = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info=connManager.getActiveNetworkInfo();
		if (info == null || !info.isConnected())
			result = false;
		else 
		{
			if (!info.isAvailable())
				result =false;
			else
				result = true;
		}
		
		return result;
	}
}
