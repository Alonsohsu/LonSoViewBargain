package studio.lonsogogo.lonsoviewbargain;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class Sort {
	private Context con;
	final int SMALL = 0;
	final int LARGE = 1;
	TextTransfer textTransfer;
	
	public Sort(Context c){
	    con = c;
	}

	public void Change( String ary[], int pos1, int pos2 )
	{
		String temp;
		
		temp = ary[pos1];
		ary[pos1] = ary[pos2];
		ary[pos2] = temp;
	}
	
	public void ChangeListAry( ArrayList<HashMap<String,String>> list, int pos1, int pos2, String key )
	{
		HashMap<String,String> item1 = new HashMap<String,String>();
		HashMap<String,String> item2 = new HashMap<String,String>();
			
		item1 = list.get(pos1);
		item2 = list.get(pos2);
		
		list.set(pos1, item2);
		list.set(pos2, item1);
	}
	
	public void SortAryList( int cnt, ArrayList<HashMap<String,String>> list, int op, String compAry )
	{
		for ( int i = 0 ; i < cnt ; i++ )
		{	
			for ( int j = 0 ; j < cnt ; j++ )
			{
				if ( op == SMALL )
				{
					if ( Integer.parseInt(list.get(i).get(compAry)) < Integer.parseInt(list.get(j).get(compAry)) )
					{
						ChangeListAry( list, i, j, "id" );
						ChangeListAry( list, i, j, "listNum" );
						ChangeListAry( list, i, j, "gridNum" );
						ChangeListAry( list, i, j, "name" );
						ChangeListAry( list, i, j, "url" );
						ChangeListAry( list, i, j, "img" );
						ChangeListAry( list, i, j, "hasbuy" );
						ChangeListAry( list, i, j, "price" );
						ChangeListAry( list, i, j, "rate_time" );
					}
				}
				
				else if ( op == LARGE )
				{
					if ( Integer.parseInt(list.get(i).get(compAry)) > Integer.parseInt(list.get(j).get(compAry)) )
					{
						ChangeListAry( list, i, j, "id" );
						ChangeListAry( list, i, j, "listNum" );
						ChangeListAry( list, i, j, "gridNum" );
						ChangeListAry( list, i, j, "name" );
						ChangeListAry( list, i, j, "url" );
						ChangeListAry( list, i, j, "img" );
						ChangeListAry( list, i, j, "hasbuy" );
						ChangeListAry( list, i, j, "price" );
						ChangeListAry( list, i, j, "rate_time" );
					}
				}
			}
		}
	}
	
	public void SortList( int productCount, String itemHasbuy[], String productURL[], 
					   	  String productImgURL[], String productName[], String listNum[],
					   	  String gridNum[], String itemRateTime[], String itemPrice[], int op, String compAry[] )
	{
		for ( int i = 0 ; i < productCount ; i++ )
		{	
			for ( int j = 0 ; j < productCount ; j++ )
			{
				if ( op == SMALL )
				{
					if ( Integer.parseInt(compAry[i]) < Integer.parseInt(compAry[j]) )
					{
						Change( itemHasbuy, i, j );
						Change( productURL, i, j );
						Change( productImgURL, i, j );
						Change( productName, i, j );
						Change( listNum, i, j );
						Change( gridNum, i, j );
						Change( itemRateTime, i, j );
						Change( itemPrice, i, j );
					}
				}
				
				else if ( op == LARGE )
				{
					if ( Integer.parseInt(compAry[i]) > Integer.parseInt(compAry[j]) )
					{
						Change( itemHasbuy, i, j );
						Change( productURL, i, j );
						Change( productImgURL, i, j );
						Change( productName, i, j );
						Change( listNum, i, j );
						Change( gridNum, i, j );
						Change( itemRateTime, i, j );
						Change( itemPrice, i, j );
					}
				}
			}					
		}
	}
}
