package misc;

import java.math.BigDecimal;


public class PosClDetailed extends PositionClass {
	private final String prod;
	private final String type;
	private final String exch;
	private final String curr;
	private final Integer year;
	private final Integer month;
	private final String pc;
	private final BigDecimal strike;
	
	public PosClDetailed(BigDecimal qty, String _id, String account,
			String strategy, String shortName, BigDecimal price, String userId) {
		super(qty, _id, account, strategy, shortName, price, userId);
		String[] parts = shortName.split("\\.");
		int l = 0;
		this.prod = parts[l++];
		this.type = parts[l++];
		this.exch = parts[l++];
		this.curr = parts.length > 3 ? parts[l++] : "USD";
		this.year = parts.length > 4 ? new Integer(parts[l++]) : null;
		this.month = parts.length > 5 ? new Integer(parts[l++]) : null;
		this.pc = parts.length > 6 ? parts[l++] : null;
		this.strike = parts.length > 7 ? new BigDecimal(parts[l++]) : null;
	}
	
	public PosClDetailed(PositionClass pci){
		this(pci.getQty(), pci.get_id(), pci.getAccount(), 
				pci.getStrategy(), pci.getShortName(), pci.getPrice(), pci.getUserId());
	}
	

	public String getProd() {
		return prod;
	}

	public String getType() {
		return type;
	}

	public String getExch() {
		return exch;
	}

	public String getCurr() {
		return curr;
	}

	public Integer getYear() {
		return year;
	}

	public Integer getMonth() {
		return month;
	}

	public String getPc() {
		return pc;
	}

	public BigDecimal getStrike() {
		return strike;
	}

	@Override
	public String toString() {
		return getProd() + ", " + getType() + ", " + getExch() + ", "
				+ getCurr() + ", " + getYear() + ", " + getMonth() + ", "
				+ getPc() + ", " + getStrike() + ", " + super.toString();
	}


}
