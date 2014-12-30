package studio.lonsogogo.lonsoviewbargain;

import android.content.Context;
import android.util.Log;

public class Parser {
	private Context context;
	
	public Parser(Context c) 
	{
		context = c;
	}

	public String getStrBetweenTagWithFlagJump( String Data, String flag, String tag1, String tag2, int jumpCnt ){
		String result;
		
		int i;
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		start = Data.indexOf(flag, tail+1);
		head = Data.indexOf(tag1, start+flag.length()+1);
		
		for ( i = 0 ; i < jumpCnt ; i++ ) {
			head = Data.indexOf(tag1, head+1 );
		}
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		result = Data.substring(head+tag1.length(), tail);
		
		return result;
	}
	
	public int getIntBetweenTag( String Data, String flag, String tag1, String tag2 ){
		int result;
		
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		start = Data.indexOf(flag, tail+1);
		head = Data.indexOf(tag1, start+flag.length()+1);
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		result = Integer.parseInt(Data.substring(head+tag1.length(), tail));
		
		return result;
	}
	
	public String [] getStrAryBetweenTag( String Data, String tag1, String tag2, int counter ){
		String result[] = new String[counter];
		
		int i = 0;
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		do 
		{
			head = Data.indexOf(tag1, tail+1);
			tail = Data.indexOf(tag2, head+tag1.length()+1);
			Log.e("counter", Integer.toString(i));
			result[i++] = Data.substring(head+tag1.length(), tail);
		}while( i < counter );
		
		return result;
	}
	
	public String [] getStrAryBetweenTagAfterFlag( String Data, String flag, String tag1, String tag2, int counter ){
		String result[] = new String[counter];
		
		int i = 0;
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		
		start = Data.indexOf(flag, tail+1);
		head = Data.indexOf(tag1, start+flag.length()+1);
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		Log.e("counter", Integer.toString(i));
		result[i] = Data.substring(head+tag1.length(), tail);
		i++;
		while( i < counter ) 
		{
			head = Data.indexOf(tag1, tail+tag2.length()+1);
			tail = Data.indexOf(tag2, head+tag1.length()+1);
			Log.e("counter", Integer.toString(i));
			result[i] = Data.substring(head+tag1.length(), tail);
			i++;
		}
		
		return result;
	}
	
	public String [] getStrAryBetweenTagAfterTowFlag( String Data, String flag1, String flag2, String tag1, String tag2, int counter ){
		String result[] = new String[counter];
		
		int i = 0;
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		int startEnd = 0; // The search point.
		
		start = Data.indexOf(flag1, tail+1);
		startEnd = Data.indexOf(flag2, start+flag1.length()+1);
		head = Data.indexOf(tag1, startEnd+flag2.length()+1);
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		Log.e("counter", Integer.toString(i));
		result[i] = Data.substring(head+tag1.length(), tail);
		i++;
		while( i < counter ) 
		{
			head = Data.indexOf(tag1, tail+tag2.length()+1);
			tail = Data.indexOf(tag2, head+tag1.length()+1);
			Log.e("counter", Integer.toString(i));
			result[i] = Data.substring(head+tag1.length(), tail);
			i++;
		}
		
		return result;
	}
	
	public String [] getStrAryBetweenTagWithFlag( String Data, String flag, String tag1, String tag2, int counter ){
		String result[] = new String[counter];
		
		int i = 0;
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		
		do 
		{
			start = Data.indexOf(flag, tail+1);
			head = Data.indexOf(tag1, start+flag.length()+1);
			tail = Data.indexOf(tag2, head+tag1.length()+1);
			Log.e("counter", Integer.toString(i));
			result[i] = Data.substring(head+tag1.length(), tail);
			if ( head == -1 )
				result[i] = "0";
			// 特別對listNum做的配置
			else if ( Data.substring(start, head).contains("gridNum") &&
				tag1.equals("<span class=\\\"listNum\\\">")) {
				result[i] = "0";
				tail = Data.indexOf(flag, start+1)-10;
			}
			i++;
		}while( i < counter );
		
		return result;
	}
	
	public String getStrBetweenTagWithFlag( String Data, String flag, String tag1, String tag2 ){
		String result;
		
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		int start = 0;	// The search point.
		
		start = Data.indexOf(flag, tail+1);
		head = Data.indexOf(tag1, start+flag.length()+1);
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		result = Data.substring(head+tag1.length(), tail);
		
		return result;
	}
	
	public String getStrBetweenTag( String Data, String tag1, String tag2 ){
		String result;
		
		int head = 0;	// The string head.
		int tail = 0;	// The string tail.
		
		head = Data.indexOf(tag1, tail+1);
		tail = Data.indexOf(tag2, head+tag1.length()+1);
		result = Data.substring(head+tag1.length(), tail);

		return result;
	}
	
	public int countTagAfterFlag( String Data, String flag, String tag ){
		int counter = 0;
		int start = 0;
		int head = 0;	// The string head.
		
		start = Data.indexOf(flag, 1);	// 從頭開始
		head = Data.indexOf(tag, start+flag.length()+1);
		while (head >= 0) {
			counter++;
			head = Data.indexOf(tag, head+tag.length()+1);
		}
		Log.e("test", Integer.toString(counter));
		return counter;
	}
}
