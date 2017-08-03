package pk.shoplus.thirdpart.wintopay;

public class Card {
	/**
	 * 卡类型
	 */
	private int type;

	/**
	 * 卡号
	 */
	private String number;

	/**
	 * 安全码
	 */
	private String cvv2;

	/**
	 * 有效期（月份）
	 */
	private String month;

	/**
	 * 有效期（年份）
	 */
	private String year;

	/**
	 * 开户行
	 */
	private String bank;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
}
