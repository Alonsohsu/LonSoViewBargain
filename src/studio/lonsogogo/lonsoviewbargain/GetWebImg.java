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
	private HashMap<String, Bitmap> picmap = new HashMap<String, Bitmap>();//�ŧi�@��HashMap�ΨӦs���}�ιϤ��Ϊ�
	private Context con;
	
	public GetWebImg(Context c){
	    con = c;
	}
	
	public Bitmap getImg(String u){
		return picmap.get(u);
	}

	//�P�_�O�_���Ȧs
	public boolean IsCache(String u){
		return picmap.containsKey(u);
	}

	//�P�_�Ϥ��O�_�U�����\
	public boolean IsDownLoadFine(String u){
		return (picmap.get(u)!= null) ? true : false;
	}

	//�P�_�Ϥ��O�_�U����
	public boolean IsLoading(String u){
		return (IsCache(u) == true && IsDownLoadFine(u) == false) ? true : false;
	}

	public void LoadUrlPic(final String u, final Handler h) {
		picmap.put(u, null);//���Ȧs���Ŷ�
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap temp = LoadUrlPic(con, u);
				if (temp == null){//�p�G�U������
					picmap.remove(u);//���X�Ȧs�Ŷ�
					h.sendMessage(h.obtainMessage(DOWNLOAD_ERROR,null));
				}else{
					picmap.put(u, temp);//�s�_��
					h.sendMessage(h.obtainMessage(DOWNLOAD_FINISH,temp));
				}
			}
		}).start();
	}
	
	//���������
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
            return defaultImg;//�줣������Ϯ�, Ū�w�]�Ϥ�
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
            return defaultImg; //�줣������Ϯ�, Ū�w�]�Ϥ�
        }
        return webImg;
    }
}