package pk.shoplus.thirdpart.wintopay;

public class Order {
	/**
	 * 订单号
	 */
	private String number;

	/**
	 * 订单金额
	 */
	private Double amount;

	/**
	 * 币种类型编号 {@link Currency}
	 */
	private int currency;

	private String language = "en";

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public int getCurrency() {
		return currency;
	}

	public void setCurrency(int currency) {
		this.currency = currency;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
