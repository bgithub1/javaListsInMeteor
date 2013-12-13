package misc;

import java.math.BigDecimal;

public class PositionClass {
	private final String userId;
	private final BigDecimal qty;
	private final String _id;
	private final String account;
	private final String strategy;
	private final String shortName;
	private final BigDecimal price;
	public PositionClass(BigDecimal qty, String _id, String account,
			String strategy, String shortName,BigDecimal price,String userId) {
		super();
		this.userId = userId;
		this.qty = qty;
		this._id = _id;
		this.account = account;
		this.strategy = strategy;
		this.shortName = shortName;
		this.price = price;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public String get_id() {
		return _id;
	}
	public String getAccount() {
		return account;
	}

	public String getUserId() {
		return userId;
	}
	
	
	public String getStrategy() {
		return strategy;
	}
	
	public String getShortName(){
		return this.shortName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	@Override
	public String toString() {
		return getQty() + ", " + get_id() + ", " + getAccount() + ", "
				+ getUserId() + ", " + getStrategy() + ", " + getShortName()
				+ ", " + getPrice();
	}
	
}
