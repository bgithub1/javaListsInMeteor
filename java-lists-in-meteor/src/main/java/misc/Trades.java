package misc;

import java.math.BigDecimal;

public class Trades {
	private final String _id;
	private final String myFirstName;
	private final String myLastName;
	private final String shortName;
	private final BigDecimal myQty;
	private final BigDecimal myPrice;
	
	public Trades(String _id,String myFirstName, String myLastName, String shortName,
			BigDecimal myQty, BigDecimal myPrice) {
		super();
		this._id = _id;
		this.myFirstName = myFirstName;
		this.myLastName = myLastName;
		this.shortName = shortName;
		this.myQty = myQty;
		this.myPrice = myPrice;
	}


	public String get_id() {
		return _id;
	}

	
	public String getMyFirstName() {
		return myFirstName;
	}

	public String getMyLastName() {
		return myLastName;
	}

	public String getShortName() {
		return shortName;
	}

	public BigDecimal getMyQty() {
		return myQty;
	}

	public BigDecimal getMyPrice() {
		return myPrice;
	}


	@Override
	public String toString() {
		return _id + ", " + myFirstName + ", " + myLastName + ", " + shortName
				+ ", " + myQty + ", " + myPrice;
	}
	
}
