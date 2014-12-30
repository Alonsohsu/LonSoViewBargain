package studio.lonsogogo.lonsoviewbargain;

import android.content.Context;


public class TextTransfer {
	private Context context;
	
	public TextTransfer(Context c) {
		context = c;
	}

	public String HTMLCodeToText( String text )
	{
		text = text.replace("</a>", " ");
		text = text.replace("&nbsp;", " ");
		text = text.replace("&#9658;", "->");
		text = text.replace("&#10022;", "*");
		text = text.replace("&raquo;", ">>");
		text = text.replace("&#9829;", " ");
		text = text.replace("&#9654;", ">");
		text = text.replace("&#9655;", ">");
		text = text.replace("&quot;", "\"");
		text = text.replace("&#9664;", "<");
		text = text.replace("&#9665;", "½");    
		text = text.replace("&#65381;", "・");
		text = text.replace("&#39;", "\'");
		text = text.replace("&#10084;", "♥");
		text = text.replace("&#9697;", "v");
		text = text.replace("&#8764;", "~");
		
		return text;
	}
	
	public void HTMLCodeToTextAry( String ary[] )
	{
		for ( int i = 0 ; i < ary.length ; i++ ) { 
			ary[i] = ary[i].replace("</a>", " ");
    		ary[i] = ary[i].replace("&nbsp;", " ");
    		ary[i] = ary[i].replace("&#9658;", "->");
    		ary[i] = ary[i].replace("&#10022;", "*");
    		ary[i] = ary[i].replace("&raquo;", ">>");
    		ary[i] = ary[i].replace("&#9829;", " ");
    		ary[i] = ary[i].replace("&#9654;", ">");
    		ary[i] = ary[i].replace("&#9655;", ">");
    		ary[i] = ary[i].replace("&quot;", "\"");
    		ary[i] = ary[i].replace("&#9664;", "<");
    		ary[i] = ary[i].replace("&#9665;", "½");    
    		ary[i] = ary[i].replace("&#65381;", "・");
    		ary[i] = ary[i].replace("&#39;", "\'");
    		ary[i] = ary[i].replace("&#10084;", "♥");
    		ary[i] = ary[i].replace("&#9697;", "v");
    		ary[i] = ary[i].replace("&#8764;", "~");
		}
	}
	
	public String RemoveText( String text, String removedSymbol )
	{
		text = text.replace(removedSymbol, "");
		return text;
	}
	
	public void RemoveTextAry( String ary[], String removedSymbol )
	{
		for ( int i = 0 ; i < ary.length ; i++ ) {
			ary[i] = ary[i].replace(removedSymbol, "");
		}
	}
}
