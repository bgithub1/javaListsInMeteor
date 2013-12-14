package com.billybyte.meteorjava.runs;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import misc.PosClDetailed;
import misc.PositionClass;

import com.billybyte.meteorjava.MeteorListSendReceive;
import com.billybyte.meteorjava.MeteorListSendReceive.SubscriptionHandler;
import com.billybyte.meteorjava.staticmethods.Utils;

public class SimpleSendRecPosClDetailed {
	
	public static void main(String[] args) {
		// print out instructions on arguments
		Utils.prt("basic arguments to command line:");
		Utils.prt("[meteorUrl=urlOrMeteor] [meteorPort=portOfMeteor] [adminEmail=admin@admin.com] [adminPass=adminpass]");
		Utils.prt("on the command line, you can arguments in pairs separated by =.  ");
		Utils.prt("For example: \"metUrl=localhost\" \"metPort=3000\" \"adminEmail=admin@something.com\" \"adminPass=adminpass\" ");

		Map<String, String> argMap = Utils.getArgPairsSeparatedByChar(args, "=");
		String metUrl = argMap.containsKey("meteorUrl") ? argMap.get("meteorUrl") : "localhost";
		int metPort = argMap.containsKey("meteorPort") ? new Integer(argMap.get("meteorPort")) : 3000;
		String adminEmail = argMap.containsKey("adminEmail") ? argMap.get("adminEmail") : "admin1@demo.com";
		String adminPass = argMap.containsKey("adminPass") ? argMap.get("adminPass")  : "admin1";
		
		MeteorListSendReceive<PosClDetailed> mlsr = null;
		try {
			// create the MeteorListSendReceive object, which creates a
			//  ddp client and logins to meteor
			mlsr =
					new MeteorListSendReceive<PosClDetailed>(100, 
							PosClDetailed.class, metUrl, metPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		// get Position.class data and turn it into PosClDetailed.class data
		List<String[]> csvData = Utils.getCSVData(SimpleSendRecPosClDetailed.class, "posClassfile.csv");
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
			// send it to Meteor
			mlsr.sendList(pcdList);
			
			// read it back
			List<PosClDetailed> pdcList = mlsr.getList(null);
			Utils.prtListItems(pdcList);
			
			// subscribe to it
			SubscriptionHandler handler = mlsr.subscribeToListData();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Utils.prtObMess(SimpleSendRecPosClDetailed.class, "printing subscribed collection");
			Utils.prtListItems(handler.getSubscriptList());

			// disconnect
			mlsr.disconnect();
		} catch (InterruptedException e) {
			throw Utils.IllState(e);
		}
		
		System.exit(0);
	}
}
