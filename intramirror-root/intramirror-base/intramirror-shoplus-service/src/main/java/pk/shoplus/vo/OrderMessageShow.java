package pk.shoplus.vo;

import java.util.Date;

/**
*@author 作者：zhangxq
*
*/
public class OrderMessageShow {

	public String user_message;
	
	public String shop_message;
	
	public Date shop_message_updated_at;
	
	public String ordered_message;
	
	public String payed_message;
	
	public String finished_message;

	public String getUser_message() {
		return user_message;
	}

	public String getShop_message() {
		return shop_message;
	}

	public Date getShop_message_updated_at() {
		return shop_message_updated_at;
	}

	public String getOrdered_message() {
		return ordered_message;
	}

	public String getPayed_message() {
		return payed_message;
	}

	public String getFinished_message() {
		return finished_message;
	}
	
	
}

