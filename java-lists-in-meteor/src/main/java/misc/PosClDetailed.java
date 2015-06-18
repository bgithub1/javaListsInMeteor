package misc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.billybyte.meteorjava.runs.SimpleSendRecPosClDetailed;
import com.billybyte.meteorjava.staticmethods.Utils;


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
		Integer monthYear = parts.length > 4 ? new Integer(parts[l++]) : null;
		if(monthYear!=null){
			this.year = new Integer(monthYear.toString().substring(0,4));
			this.month = new Integer(monthYear.toString().substring(4,6));
		}else{
			this.year = null;
			this.month = null;
		}
		this.pc = parts.length > 5 ? parts[l++] : null;
		this.strike = parts.length > 6 ? new BigDecimal(parts[l++]) : null;
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

	public static void main(String[] args) {
		// first get csv data
		List<String[]> csvData = Utils.getCSVData(SimpleSendRecPosClDetailed.class, "posClassfile.csv");
		// next convert the csv data into java.util.list<Position>
		List<PositionClass> posList = 
				Utils.listFromCsv(PositionClass.class, csvData);
		// next, make a list of PosClDetailed from the Position objects b/c
		//  PosClDetailed has more fields that I want to show in Meteor.
		List<PosClDetailed> pcdList = new ArrayList<PosClDetailed>();
		for(PositionClass posCl : posList){
			PosClDetailed pcd = new PosClDetailed(
					posCl);
			pcdList.add(pcd);
		}
		for(PosClDetailed pcd : pcdList){
			Utils.prt(pcd.toString());
		}
	}

}
