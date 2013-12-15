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
/**
 * <p>
 * THIS CLASS ASSUMES 2 IMPORTANT THINGS: <br>
 * FIRST: THAT YOU HAVE DOWNLOADED THE Meteor project SubscriptTables from <br>
 * &nbsp&nbsp github (https://github.com/bgithub1/SubscriptTables). <br>
 *    </p>
 * <p>
 * SECOND: THAT YOU HAVE THE RUN THE METEOR PROJECT EITHER LOCALLY OR <br>
 * &nbsp THAT YOU DEPLOYED IT TO METEOR. <br>
 *    </p>
 * <p>THIRD: THAT YOU HAVE RUN THE main IN THE CLASS: <br>
 * &nbsp com.billybyte.meteorjava.runs.SetUpTableModelsAndSendReceiveLists. <br>
 * &nbsp That main set's up several TableModels, sends them to Meteor, and then <br>
 * &nbsp sends some initial data to Meteor which gets displayed in Meteor templates. <br>
 *  </p>
 * <p> 
 * This class contains a main, which has a simple example of sending, receiving <br>
 * &nbsp and subscribing to data between java and Meteor. <br>
 *   </p>
 * <p>The program uses some static methods in the class  <br>
 * &nbsp com.billybyte.meteorjava.staticmethods.Utils.  These methods are simple to follow. <br>
 * &nbsp They are used to either print lines to the console, or to read csv and xml data. <br>
 * &nbsp You might find methods like Utils.listFromCsv useful if you have to read csv data <br>
 * &nbsp and make it into a java.util.Collection.   <br>
 *    </p>
 *    <p>
 * I've put lots of comments in the code, but it basically does 4 things:<br>
 * &nbsp 1.  It creates a DDP connection to Meteor and logins in to Meteor in the<br>
 *         constructor of MeteorListSendReceive;<br>
 * &nbsp 2.  It reads csv data from a file that is located in the package<br> 
 * &nbsp&nbsp&nbsp com.billybyte.meteorjava.runs.posClassfile.csv.  Using Utils.listFromCsv,<br>
 * &nbsp&nbsp&nbsp I deserialize the csv file into objects of type misc.PositionClass.  However,<br>
 * &nbsp&nbsp&nbsp misc.PositionClass objects do not have all of the fields that I want to <br>
 * &nbsp&nbsp&nbsp display.  Therefore, I convert them into objects of type misc.PosClDetailed <br>
 * &nbsp&nbsp&nbsp so that I can display fields like product, currency and exchange in the<br>
 * &nbsp&nbsp&nbsp Meteor TableModel that I have created for all java objects of type <br>
 * &nbsp&nbsp&nbsp misc.PosClDetailed.  <br>
 * &nbsp 3.  Sends the list of misc.PosClDetailed to Meteor.<br>
 * &nbsp 4.  Reads the list back.<br>
 * &nbsp 5.  Subscribes to the list, and reads it back after via the subscription callback.<br> 
 *   </p>    
 *         
 * @author bperlman1
 *
 */
public class SimpleSendRecPosClDetailed {
	
	public static void main(String[] args) {
		// print out instructions on arguments
		Utils.prt("basic arguments to command line:");
		Utils.prt("[meteorUrl=urlOrMeteor] [meteorPort=portOfMeteor] [adminEmail=admin@admin.com] [adminPass=adminpass]");
		Utils.prt("on the command line, you can arguments in pairs separated by =.  ");
		Utils.prt("For example: \"metUrl=localhost\" \"metPort=3000\" \"adminEmail=admin@something.com\" \"adminPass=adminpass\" ");

		// this little Util just helps read command line args into maps
		Map<String, String> argMap = Utils.getArgPairsSeparatedByChar(args, "=");
		String metUrl = argMap.containsKey("meteorUrl") ? argMap.get("meteorUrl") : "localhost";
		int metPort = argMap.containsKey("meteorPort") ? new Integer(argMap.get("meteorPort")) : 3000;
		String adminEmail = argMap.containsKey("adminEmail") ? argMap.get("adminEmail") : "admin1@demo.com";
		String adminPass = argMap.containsKey("adminPass") ? argMap.get("adminPass")  : "admin1";
		
		// create an instance of MeteorListSendReceive.  This is the most
		//  important class in this project b/c it does all of the communicating
		//  with Meteor.
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
		
		// now send the list to Meteor
		try {
			// send it to Meteor
			mlsr.sendList(pcdList);
			
			// read it back
			List<PosClDetailed> pdcList = mlsr.getList(null);
			Utils.prtListItems(pdcList);
			
			// subscribe to it
			SubscriptionHandler handler = mlsr.subscribeToListData();
			try {
				// wait for Meteor to send back the data on the first 
				//  subscribe.
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// now print out the data that Meteor sent back, which 
			//  was captured in the SubscriptionHandler instance called handler.
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
