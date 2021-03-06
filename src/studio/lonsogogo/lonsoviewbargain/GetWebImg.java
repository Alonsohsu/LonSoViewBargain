package studio.lonsogogo.lonsoviewbargain;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class GetWebImg{
	public final int DOWNLOAD_ERROR = 0;
	public final int DOWNLOAD_FINISH = 1;
	private HashMap<String, Bitmap> picmap = new HashMap<String, Bitmap>();//宣告一個HashMap用來存網址及圖片用的
	private Context con;
	
	public GetWebImg(Context c){
	    con = c;
	}
	
	public Bitmap getImg(String u){
		return picmap.get(u);
	}

	//判斷是否有暫存
	public boolean IsCache(String u){
		return picmap.containsKey(u);
	}

	//判斷圖片是否下載成功
	public boolean IsDownLoadFine(String u){
		return (picmap.get(u)!= null) ? true : false;
	}

	//判斷圖片是否下載中
	public boolean IsLoading(String u){
		return (IsCache(u) == true && IsDownLoadFine(u) == false) ? true : false;
	}

	public void LoadUrlPic(final String u, final Handler h) {
		picmap.put(u, null);//放到暫存的空間
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap temp = LoadUrlPic(con, u);
				if (temp == null){//如果下載失敗
					picmap.remove(u);//移出暫存空間
					h.sendMessage(h.obtainMessage(DOWNLOAD_ERROR,null));
				}else{
					picmap.put(u, temp);//存起來
					h.sendMessage(h.obtainMessage(DOWNLOAD_FINISH,temp));
				}
			}
		}).start();
	}
	
	//抓網路的圖
    public synchronized Bitmap LoadUrlPic(Context c, String url) {
        URL imgUrl;
        Bitmap defaultImg = BitmapFactory.decodeResource(con.getResources(),
                studio.lonsogogo.lonsoviewbargain.R.drawable.ic_launcher);
        Bitmap webImg = null;
        try {
            imgUrl = new URL(url);
        }
        catch (MalformedURLException e) {
            Log.d("MalformedURLException",e.toString());
            return defaultImg;//抓不到網路圖時, 讀預設圖片
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) imgUrl.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();   
            InputStream inputStream = httpURLConnection.getInputStream();          
            int length = (int) httpURLConnection.getContentLength();
            int tmpLength = 512;
            int readLen = 0;
            int desPos = 0;
            byte[] img = new byte[length];
            byte[] tmp = new byte[tmpLength];
            if (length != -1) {
                while ((readLen = inputStream.read(tmp)) > 0) {
                    System.arraycopy(tmp, 0, img, desPos, readLen);
                    desPos += readLen;
                }
                webImg = BitmapFactory.decodeByteArray(img, 0,img.length);
            }
            httpURLConnection.disconnect();
        }
        catch (IOException e) {
            Log.d("IOException",e.toString());
            return defaultImg; //抓不到網路圖時, 讀預設圖片
        }
        return webImg;
    }
}