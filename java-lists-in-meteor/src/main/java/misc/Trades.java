package misc;

import java.math.BigDecimal;
/**
 * Trades has basic trade info.  It does not extend MeteorBaseListItem, but
 * does include an _id field.  Since there is no userId field, every Trades record
 * will be displayed by every user that displays the TableModel that is attached
 * to the misc.Trades collection in Meteor.
 * 
 * @author bperlman1
 *
 */
public class Trades {
	private final String _id;
	private final String myFirstName;
	private final String myLastName;
	private final String shortName;
	private final BigDecimal myQty;
	private final BigDecimal myPrice;
	
	/**
	 * 
	 * @param _id String
	 * @param myFirstName String
	 * @param myLastName String
	 * @param shortName String
	 * @param myQty BigDecimal
	 * @param myPrice BigDecimal
	 */
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
