package studio.lonsogogo.lonsoviewbargain;

public class PrefPacket {
	public static String []pdtTag = {"id", "listNum", "gridNum", "name", "url", "img", "hasbuy", "price", "rate_time" };
	public static String []favorTag = {"favor_list_id", "favor_list_list_num", "favor_list_grid_num", "favor_list_name", "favor_list_url", 
						"favor_list_img", "favor_list_hasbuy", "favor_list_price", "favor_list_rate_time"};
	
	public PrefPacket() {}
	
	public static int getPdtTagLength() {
		return pdtTag.length;
	}
}
