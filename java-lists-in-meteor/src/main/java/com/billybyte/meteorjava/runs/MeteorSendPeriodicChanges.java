package com.billybyte.meteorjava.runs;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import misc.PosClDetailed;
import misc.PositionClass;
import misc.Trades;

import com.billybyte.meteorjava.MeteorListSendReceive;
import com.billybyte.meteorjava.staticmethods.Utils;

/**
 * Read a csfile, randomly update the qty field and then send it to meteor
 * Sleep a little bit, then do it again
 * 
 * The csv files that you read are resources in the same package as this class.
 * @author bperlman1
 *
 */
public class MeteorSendPeriodicChanges {
	private final String tradesCsvFileName = "testfile.csv";
	private final String posCsvFileName = "posClassfile.csv";
	private final long sleepTimeMills;
	private final MeteorListSendReceive<Trades> mlsrTrades;
	private final MeteorListSendReceive<PosClDetailed> mlsrPos;
	
	public MeteorSendPeriodicChanges(String classNameToProcess,String metUrl, int metPort,
			String adminEmail, String adminPass, long sleepTimeMills) {
		super();
		this.sleepTimeMills = sleepTimeMills;
		try {
			mlsrTrades = classNameToProcess.compareTo(Trades.class.getName())==0 ? 
					new MeteorListSendReceive<Trades>(100, Trades.class, metUrl, metPort, adminEmail, adminPass, "", "", "looper") :
					null;
			mlsrPos = classNameToProcess.compareTo(PosClDetailed.class.getName())==0 ?
					new MeteorListSendReceive<PosClDetailed>(100, PosClDetailed.class, 
							metUrl, metPort, adminEmail, adminPass, "", "", "looper") :
					null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw Utils.IllState(this.getClass(), Utils.stackTraceAsString(e));
		}
	}
	
	/**
	 * execute method that you call to start it all up.  Can be called from 
	 * Spring IOC
	 */
	public void execute(){
		try {
			List<String[]> tradesCsv = Utils.getCSVData(this.getClass(), tradesCsvFileName);
			List<Trades> tList = Utils.listFromCsv(Trades.class, tradesCsv);
			List<PosClDetailed> pList = detailedPosFromPos();
			BigDecimal amtToChgQty = new BigDecimal(-5);
			BigDecimal negOne = new BigDecimal(-1);
			while(true){
				amtToChgQty = amtToChgQty.multiply(negOne); 
				if(mlsrTrades!=null){
					List<Trades> newTList = buildNewTradesList(amtToChgQty, tList);
					String[] result = mlsrTrades.sendList(newTList);
					prtResult(result,Trades.class.getName());
				}
				if(mlsrPos!=null){
					List<PosClDetailed> newPList = buildNewPosClDetailList(amtToChgQty, pList);
					String[] result  = mlsrPos.sendList(newPList);
					prtResult(result,PosClDetailed.class.getName());
				}
				Thread.sleep(sleepTimeMills);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void prtResult(String[] result,String className){
		if(result==null || result.length<1){
			Utils.prtObMess(this.getClass(), "success sending trades for: "+className);
		}else{
			Utils.prtObErrMess(this.getClass(), "problems sending trades for: "+className);
			for(String prob:result){
				Utils.prtObErrMess(this.getClass(),prob);
			}
		}
	}
	
	private List<PosClDetailed> detailedPosFromPos(){
		List<String[]> posCsv = Utils.getCSVData(this.getClass(), posCsvFileName);
		List<PositionClass> posList = 
				Utils.listFromCsv(PositionClass.class, posCsv);
		// make a list of PosClDetailed
		List<PosClDetailed> pcdList = new ArrayList<PosClDetailed>();
		for(PositionClass posCl : posList){
			PosClDetailed pcd = new PosClDetailed(
					posCl);
			pcdList.add(pcd);
		}
		return pcdList;
	}
	
	private List<Trades> buildNewTradesList(BigDecimal amtToChgQty,List<Trades> tList){
		List<Trades> ret = new ArrayList<Trades>();
		for(Trades t:tList){
			Trades newT = new Trades(
					t.get_id(), 
					t.getMyFirstName(), 
					t.getMyLastName(), 
					t.getShortName(), 
					t.getMyQty().add(amtToChgQty), 
					t.getMyPrice());
			ret.add(newT);
		}
		return ret;
	}
	
	private List<PosClDetailed> buildNewPosClDetailList(BigDecimal amtToChgQty,List<PosClDetailed> pList){
		List<PosClDetailed> ret = new ArrayList<PosClDetailed>();
		for(PosClDetailed p:pList){
			PosClDetailed newP = new PosClDetailed(
					p.getQty().add(amtToChgQty), 
					p.get_id(), 
					p.getAccount(), 
					p.getStrategy(), 
					p.getShortName(), 
					p.getPrice(), 
					p.getUserId());
			ret.add(newP);
		}
		return ret;
	}
	/**
	 * 
	 * @param args
	 *  arg0 - meteor url (e.g. localhost)
	 *  arg1 - meteor port (e.g 3000)
	 *  arg2 - adminEmail (myadminEmail@myemail.com 
	 *  		see the Meteor project SubscriptTables, file serverDefs.js.  
	 *  		The adminEmail that you login in as must match the email address in
	 *  		the adminEmail field of serverDefs.js)
	 *  arg3 - adminPass (myadminpass)
	 *  arg4 - sleeptime in mills before each update (e.g. 4000 for 4 seconds)
	 *  tOrP - "t" = modify the meteor mongo collection that coincides with java.util.List<misc.Trades>
	 *         "p" = modify the meteor mongo collection that coincides with java.util.List<misc.PosClDetailed>
	 *         
	 *   Example command line:
	 *   	"metUrl=localhost" "metPort=3000" "adminEmail=myAdmin@admin.com" "adminPass=myPass" "sleepTimeMills=4000" "tOrP=t"
	 *   
	 *         
	 */
	public static void main(String[] args) {
		Map<String, String> argMap = Utils.getArgPairsSeparatedByChar(args, "=");
		String metUrl = argMap.get("metUrl");
		int metPort = new Integer(argMap.get("metPort"));
		String adminEmail = argMap.get("adminEmail");
		String adminPass = argMap.get("adminPass");
		long sleepTimeMills = new Long(argMap.get("sleepTimeMills"));
		String tOrP = argMap.get("tOrP");
		String className = tOrP.compareTo("t")==0 ? Trades.class.getName() :
			PosClDetailed.class.getName();
		MeteorSendPeriodicChanges mspc = 
				new  MeteorSendPeriodicChanges(className,metUrl, metPort, 
						adminEmail, adminPass, sleepTimeMills);
		mspc.execute();
	}
}
