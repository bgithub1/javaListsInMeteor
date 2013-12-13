package com.billybyte.meteorjava.runs;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import misc.PosClDetailed;
import misc.PositionClass;
import misc.Trades;

import com.billybyte.meteorjava.MeteorListSendReceive;
import com.billybyte.meteorjava.MeteorTableModel;
import com.billybyte.meteorjava.MeteorListSendReceive.SubscriptionHandler;
import com.billybyte.meteorjava.staticmethods.Utils;
/**
 * Set up some Example TableModels and Data in Meteor using the
 *   MeteorListSendReceive class.
 *   
 * @author bperlman1
 *
 */
public class BasicTableModelSetup {
	String meteorUrl = "localhost";
	int meteorPort = 3000;
	String adminEmail = "admin1@demo.com";
	String adminPass = "admin1";
	boolean doRemoveTradeItems = true;
	boolean doRemovePosItems = true;
	boolean doTableModelsCreate= true;
	boolean doSendTradeData = true;
	boolean doSendPosData = true;
	boolean doReadTradeData = true;
	boolean doReadPosData = true;
	boolean doPosSubscription = true;

	public static void main(String[] args) {
		Utils.prt("on the command line, you can enter -> metUrl=localhost metPort=3000 adminEmail=admin@something.com adminPass=adminpass");
		Map<String, String> argPairs = new HashMap<String, String>();
		if(args!=null){
			// find pairs separated by the = sign
			for(String argPair : args){
				String[] pair = argPair.split("=");
				if(pair.length>1){
					argPairs.put(pair[0],pair[1]);
				}
			}
		}
		BasicTableModelSetup btms = new BasicTableModelSetup();
		if(argPairs.containsKey("metUrl")){
			btms.meteorUrl = argPairs.get("metUrl");
		}
		if(argPairs.containsKey("metPort")){
			btms.meteorPort = new Integer(argPairs.get("metPort"));
		}
		if(argPairs.containsKey("adminEmail")){
			btms.adminEmail = argPairs.get("adminEmail");
		}
		if(argPairs.containsKey("adminPass")){
			btms.adminPass = argPairs.get("adminPass");
		}
		
		btms.removeTradeItems();
		btms.removePosItems();
		btms.tableModelsCreate();
		btms.sendTradeData();
		btms.sendPosData();
		btms.readTradeData();
		btms.readPosData();
		btms.posSubscription();
		System.exit(0);
	}
	
	
	
	/**
	 * remove trade items, ONLY If email is admin.
	 */
	private void removeTradeItems(){
		try {
			if(!doRemoveTradeItems)return;
			MeteorListSendReceive<Trades> mlsr = 
					new MeteorListSendReceive<Trades>(100, 
							Trades.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			// get all records b/c getting records with the admin user gets all records
			Map<String, String> mongoSelectors = null;
			List<Trades> receivedList = 
					mlsr.getList(mongoSelectors);
			if(receivedList.size()<1)return;
			Utils.prtListItems(receivedList);
			List<String> idList = new ArrayList<String>();
			for(Trades t : receivedList){
				idList.add(t.get_id());
			}
			
			String[] errors = mlsr.removeListItems(idList);
			if(errors!=null && errors.length>0)
			Utils.prtObMess(this.getClass(), Arrays.toString(errors));
			mlsr.disconnect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}

	private void removePosItems(){
		try {
			if(!doRemovePosItems)return;
			MeteorListSendReceive<PosClDetailed> mlsr = 
					new MeteorListSendReceive<PosClDetailed>(100, 
							PosClDetailed.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			// get all records for this user, and every other public records
			Map<String, String> mongoSelectors = null;
			List<PosClDetailed> receivedList = 
					mlsr.getList(mongoSelectors);
			if(receivedList.size()<1)return;
			Utils.prtListItems(receivedList);
			List<String> idList = new ArrayList<String>();
			for(PosClDetailed t : receivedList){
				idList.add(t.get_id());
			}
			
			String[] errors = mlsr.removeListItems(idList);
			if(errors!=null && errors.length>0)
			Utils.prtObMess(this.getClass(), Arrays.toString(errors));
			mlsr.disconnect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * get a list of misc.Trades class instances from Meteor.
	 * Those instances were originally sent using the test method
	 * testSendPosData().  The Meteor collection name coincides with the
	 * full class name (misc.Trades).
	 */
	private void readTradeData(){
		if(!doReadTradeData)return;
		// example usage
		// get data from meteor
		try {
			MeteorListSendReceive<Trades> mlsr = 
					new MeteorListSendReceive<Trades>(100, 
							Trades.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			// get all records for this user, and every other public records
			Map<String, String> mongoSelectors = null;
			List<Trades> receivedList = 
					mlsr.getList(mongoSelectors);
			Utils.prtListItems(receivedList);
			mlsr.disconnect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private void sendTradeData(){
		if(!doSendTradeData)return;
		try {
			MeteorListSendReceive<Trades> mlsr = 
					new MeteorListSendReceive<Trades>(100, 
							Trades.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			List<String[]> csvData = Utils.getCSVData(this.getClass(), "testFile.csv");
			List<Trades> listToSend = 
					Utils.listFromCsv(Trades.class, csvData);
			try {
				String[] result = mlsr.sendList(listToSend);
				Utils.prt(Arrays.toString(result));
				mlsr.disconnect();
			} catch (InterruptedException e) {
				throw Utils.IllState(e);
				
			}
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
	}

	
	private void tableModelsCreate(){
		if(!doTableModelsCreate)return;
		// get list of table modesl from xml file
		@SuppressWarnings("unchecked")
		List<MeteorTableModel> mtmList = Utils.getFromXml(List.class, this.getClass(), "positionMeteorTableModels.xml");
		// now send them to Meteor server
		try {
			MeteorListSendReceive<MeteorTableModel> mlsr = 
					new MeteorListSendReceive<MeteorTableModel>(100, 
							MeteorTableModel.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			// remove old tables
			for(MeteorTableModel mtm:mtmList){
				String tableId = mtm.getId();
				String error = mlsr.deleteMeteorTableModel( tableId);
				if(error!=null && error.compareTo("0")!=0){
					Utils.prtObErrMess(this.getClass(), error);
				}
			}
			mlsr.sendTableModelList(mtmList);
			mlsr.disconnect();
		} catch (Exception e) {
			throw Utils.IllState(e);
		}
		
	}
	
	private void sendPosData(){
		// send the data to 2 different displays
		if(!doSendPosData)return;
		MeteorListSendReceive<PosClDetailed> mlsr = null;
		try {
			mlsr = 
					new MeteorListSendReceive<PosClDetailed>(100, 
							PosClDetailed.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		List<String[]> csvData = Utils.getCSVData(this.getClass(), "posClassfile.csv");
		List<PositionClass> posList = 
				Utils.listFromCsv(PositionClass.class, csvData);
		// make a list of PosClDetailed
		List<PosClDetailed> pcdList = new ArrayList<PosClDetailed>();
		for(PositionClass posCl : posList){
			PosClDetailed pcd = new PosClDetailed(
					posCl);
			pcdList.add(pcd);
		}
		
		try {
			mlsr.sendList(pcdList);
			mlsr.disconnect();
		} catch (InterruptedException e) {
			throw Utils.IllState(e);
		}
	}
	
	private void readPosData(){
		if(!doReadPosData)return;
		MeteorListSendReceive<PosClDetailed> mlsr = null;
		try {
			mlsr = 
					new MeteorListSendReceive<PosClDetailed>(100, 
							PosClDetailed.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		
		

		List<PosClDetailed> pdcList = mlsr.getList(null);
		Utils.prtListItems(pdcList);
		mlsr.disconnect();
	}
	
	@SuppressWarnings("unchecked")
	public void posSubscription(){
		if(!doPosSubscription)return;
		MeteorListSendReceive<PosClDetailed> mlsr = null;
		try {
			mlsr = 
					new MeteorListSendReceive<PosClDetailed>(100, 
							PosClDetailed.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		
		@SuppressWarnings("rawtypes")
		SubscriptionHandler handler = mlsr.subscribeToListData();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.prtObMess(this.getClass(), "printing subscribed collection");
		Utils.prtListItems(handler.getSubscriptList());
		mlsr.disconnect();
	}

}
