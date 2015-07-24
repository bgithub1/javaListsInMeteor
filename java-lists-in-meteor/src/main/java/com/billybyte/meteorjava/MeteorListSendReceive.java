package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import me.kutrumbos.AbstractSubscriptionHandler;
import me.kutrumbos.DdpClientWithEmail;
import me.kutrumbos.observers.SubscriptionHandlerObserver;
import me.kutrumbos.observers.SubscriptionMessage;

import org.java_websocket.WebSocket.READYSTATE;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.billybyte.meteorjava.staticmethods.MeteorCollectionStaticMethods;
import com.billybyte.meteorjava.staticmethods.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.StringMap;
import com.google.gson.stream.MalformedJsonException;

/**
 * 
 * 
 * @author billybyte
 * MeteorListSendReceive is a generic class that will allow you to send and receive lists of 
 * type M (generic) data to and from the Meteor project subscripttables, 
 * which can be found on github at https://github.com/bgithub1/SubscriptTables.
 * 
 * For every type of list that you want to send and receive, you should instantiate
 * one of these classes.  Then call it methods:
 * 	 	sendList: to send java.util.list<M> data to Meteor;
 * 		getList: to get java.util.list<M> data from Meteor;
 * 		subscribeToListData: to subscribe to java.util.list<M> data when it changes on Meteor;
 * 		sendTableModelList: to send java.util.list<TableModel> to Meteor to govern
 * 			the way Meteor displays the java.util.lists.
 * 
 * 
 * 
 *  See com.billybyte.meteorjava.runs.BasicTableModelSetup.java for example usage as to 
 *    sending TableModels, sending java.util.list lists and receiving these lists back
 *    from Meteor either by call a Meteor method or by subscribing to changes on Meteor .
 *    All of the Meteor calls in this class coincide with Meteor methods in the Meteor project
 *    subscripttables, which can be found on github at https://github.com/bgithub1/SubscriptTables.
 *  
 *  See com.billybyte.meteorjava.runs.MeteorSendPeriodChanges.java for another example usage.
 *  
 *
 * @param <M>
 */
public class MeteorListSendReceive<M> {
	private final DdpClientWithEmail ddpClient;
	private final Gson gson = new  Gson();
	private final long timeoutValue = 700000;
	private final Class<M> classOFM;
	private final int millsToWaitForConnect = 300;
	private final int triesToConnect = 3;
	private final AtomicReference<MeteorLoginToken> loginToken = new AtomicReference<MeteorLoginToken>(); 
	private static final int DEFAULT_BLOCKINGQUEUE_CAPACITY = 100;
	
	private static final String regexExpressionToEliminateLeadingZeros = 
			"=[0]{1,}[0-9]{1,}[\\.]{0,1}[0-9]{0,},";
	private static final String regexExpressionWithoutLeadingZeros = 
			"[1-9]{1,1}[0-9]{0,}[\\.]{0,1}[0-9]{0,},";

	/**
	 * 
	 * @param topLevelQueueCapacity - int that defines how deep your Blocking queues should be.  Suggested size is 100
	 * @param classOFM - class of item in java lists.
	 * @param meteorServerIp
	 * @param meteorServerPort
	 * @param emailUsername
	 * @param emailPw
	 * @param notifyEmail
	 * @param notifyEmailPw
	 * @param clientName
	 * @throws URISyntaxException
	 */
	public MeteorListSendReceive(
			int topLevelQueueCapacity,
			Class<M> classOFM,
			String meteorServerIp,
			int meteorServerPort,
			String emailUsername,
			String emailPw,
			String notifyEmail,
			String notifyEmailPw,
			String clientName,
			MeteorLoginToken mltFromPrevLogin) throws URISyntaxException {
		this.ddpClient = new DdpClientWithEmail(meteorServerIp, meteorServerPort, notifyEmail, notifyEmailPw, clientName);
		this.classOFM = classOFM;
		
		start();
		if(mltFromPrevLogin==null){
			MeteorLoginToken mlt = loginToMeteor(emailUsername, emailPw);
			if(mlt==null){
				throw Utils.IllState(this.getClass(), "cannot login to Meteor");
			}
			loginToken.set(mlt);
		}else{
			loginToken.set(mltFromPrevLogin);
		}
	}
	
	public  MeteorListSendReceive(
			MeteorListSendReceive<?> previousMlsr,Class<M> classOfM){
			this.ddpClient = previousMlsr.getDdpClient();
			loginToken.set(previousMlsr.getMeteorLoginToken());
			this.classOFM = classOfM;
			
	}

	
	public MeteorListSendReceive(
			int topLevelQueueCapacity,
			Class<M> classOFM,
			String meteorServerIp,
			int meteorServerPort,
			String emailUsername,
			String emailPw,
			String notifyEmail,
			String notifyEmailPw,
			String clientName) throws URISyntaxException {
		this(topLevelQueueCapacity, classOFM, meteorServerIp, meteorServerPort, emailUsername, emailPw, notifyEmail, notifyEmailPw, clientName,null);
	}
	
	/**
	 *  Abstract observer class that parses Meteor result objects and passes them to caller by
	 *  placing a valid json object that has a "result" field into an AtomicReferernce<T>.
	 *  The caller can then call getReceivedResult() to obtain the result object.
	 *  
	 *   This class allows calls to Meteor methods to be synchronous.  You make a ddpClient call
	 *   and then wait on the countdown latch that you pass to any class that extends MeteorObserver,
	 *   and MeteorObserver will set the countdown latch when it receives a valid "result" json message.
	 *   
	 * @author bperlman1
	 *
	 * @param <T>
	 */
	private abstract class MeteorObserver<T> implements Observer{
		abstract T convert(Observable client, JSONObject result);
		private final CountDownLatch cdl = new CountDownLatch(1);
		private final AtomicReference<T> atomicMess = new AtomicReference<T>(null);
		private final AtomicReference<String> atomicException = new AtomicReference<String>();
		T getReceivedResult(){
			return atomicMess.get();
		}
		
		CountDownLatch getCdl(){
			return this.cdl;
		}

		@Override
		/**
		 * Handle updates from ddpClient
		 * Parse the msg object by finding an element in the json
		 *   that is named "result". If such an element exists, call
		 *   the convert method to convert to it's generic type, and then
		 *   store it in the AtomicReference so that the user can retrieve it.
		 */
		public void update(Observable client, Object msg) {
			JSONObject result =null;
			try {
				Utils.prtObMess(this.getClass(), msg.toString());
				result = getMeteorResultObj(msg.toString());
				if(result==null)return;
				T ret = convert(client, result);
				if(ret==null)return;
				atomicMess.set(ret);
				cdl.countDown();
			} catch (Exception e) {
				System.err.println(this.getClass().getCanonicalName()+"  :  " + e.getMessage());
				System.err.println("meteor message  :  " + result);
				e.printStackTrace();
				atomicException.set(e.getMessage());
				cdl.countDown();
			}
		}
	}
	

	/**
	 * get the "result" object from the json string that Meteor returns when
	 * 	a Meteor method returns something after a call to that method.
	 * 
	 * @param jsonString
	 * @return
	 */
	private JSONObject getMeteorResultObj(String jsonString){
		JSONObject jObj=null;
		try {
			jObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			Utils.prtObErrMess(this.getClass(), e.getMessage());
			e.printStackTrace();
			return null;
		}
		if(jObj==null || !jObj.has("result"))return null;
		
		JSONObject result=null;
		try {
			result = jObj.getJSONObject("result");
		} catch (JSONException e) {
			// make it an object of result:string
			result = new JSONObject();
			result.put("result", jObj.get("result"));
		}
		return result;
	}

	
	/**
	 * General call to ddpClient that then waits for a return msg
	 *  with a "result" field in its json.
	 *  
	 * @param methodName String
	 * @param params Object[]
	 * @param observer extention of MeteorObserver<T>
	 * @return T
	 */
	private <T> T callMeteorSynchronously(String methodName,Object[] params,MeteorObserver<T> observer){
		checkLogin();
		ddpClient.addObserver(observer);
		ddpClient.call(methodName, params);
		try {
			observer.getCdl().await(timeoutValue,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Utils.prtObErrMess(this.getClass(), e.getMessage());
			e.printStackTrace();
		}
		ddpClient.deleteObserver(observer);
		return observer.getReceivedResult();
		
	}

	
	
	private void start(){
		this.ddpClient.connect();
		boolean success = false;
		for(int i = 0;i<triesToConnect;i++){
			try {
				Thread.sleep(millsToWaitForConnect);
				READYSTATE readyState = this.ddpClient.getReadyState(); 
				if(readyState!=READYSTATE.OPEN){
					Utils.prtObMess(this.getClass(),"ddp Client readyState = " + readyState + " .  Trying "+ (i-1) +" more times.");
				}else{
					success = true;
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw Utils.IllState(e);
			}
		}
		if(!success){
			throw Utils.IllState(this.getClass(), "Ddd client " + ddpClient.getDdpClientName() + " can't connect to Meteor server on ip " + ddpClient.getMeteorServerIp() + " and port " +ddpClient.getMeteorServerPort());
		}
	}

	
	/**
	 * Class that contains all of the info that Meteor sends back when you 
	 * get list data
	 * 
	 * @author bperlman1
	 *
	 */
	private class SendListClass{
		
		@SuppressWarnings("unused")
		String className;
		@SuppressWarnings({ "rawtypes", "unused" })
		List list;
		/**
		 * !! important the className and list member variables of this class
		 *    are never referenced "directly".  However, they are referenced
		 *     by Gson.  Therefore, I have added @SupressWarnings above.
		 * @param className
		 * @param list
		 */
		@SuppressWarnings("rawtypes")
		private SendListClass(String className,List list){
			this.className = className;
			this.list = list;
		}
	}

	/**
	 * Observer for receiving the responses that Meteor returns when you 
	 * 	send Meteor new or updated TableModels.
	 * 
	 * @author bperlman1
	 *
	 */
	private class SendTableModelResponseObserver  extends MeteorObserver<String[]>{
		@Override
		String[] convert(Observable client, JSONObject result) {
			return MeteorCollectionStaticMethods.getStringArrayFromJSONObject(result);
		}
	}
	
	public String[] sendList(List<M> dataList) throws InterruptedException{
		return sendList("addJavaListData", dataList);
	}

	public String[] sendTableModelList(List<M> tableModelList) throws InterruptedException{
		return sendList("addMasterTablesFromJava", tableModelList);
		
	}
	private String[] sendList(String meteorMethod,List<M> dataList) throws InterruptedException{
		checkLogin();
		Object[] params = new Object[1];
		SendListClass slc = new SendListClass(classOFM.getName(), dataList);
		params[0] = slc;
		SendTableModelResponseObserver observer = new SendTableModelResponseObserver();
		return callMeteorSynchronously(meteorMethod, params, observer);
	}
	

	/**
	 * Obsever that handles list data that comes back from Meteor after you
	 * 	call getList
	 * @author bperlman1
	 *
	 */
	private class ReceivedDataObserver extends MeteorObserver<List<M>> {
		private ReceivedDataObserver(){
			super();
		}
		
		@Override
		List<M> convert(Observable client, JSONObject result) {
			return getListFromDdpMsg(result);
		}
	}

	
	public List<M> getList(
			Map<String, String> mongoSelectors){
		checkLogin();
		JSONObject selector = new JSONObject();
		if(mongoSelectors==null){
			selector = null;
		}else{
			for(Entry<String, String> entry : mongoSelectors.entrySet()){
				selector.put(entry.getKey(), entry.getValue());
			}
		}
		Object[] params = new Object[2];
		params[0] = classOFM.getName();
		params[1] = selector;
		ReceivedDataObserver observer = new ReceivedDataObserver();
		return callMeteorSynchronously("getJavaListData", params, observer);
	}
	
	
	private List<M> getListFromDdpMsg(JSONObject result) {
		// check to see if there is an error field
		if(result.has("error")){
			// throw error
			String error = result.getString("error");
			throw Utils.IllState(this.getClass(),error);
		}
		String className = result.getString("className");
		String classNameOfListObjects = classOFM.getName();
		List<M> ret=null;
		if(className.compareTo(classNameOfListObjects)!=0){
			Utils.prtObErrMess(this.getClass(),"className returned not equal to class of generic objects");
			
		}else{
			String listString = result.get("list").toString();
			if(listString==null)return null;
			@SuppressWarnings("unchecked")
			List<StringMap<M>> stringMapList = (List<StringMap<M>>)gson.fromJson(listString, List.class);
			ret = new ArrayList<M>();
			for(StringMap<M> sm : stringMapList){
				M m=null;
//				Utils.prtObMess(this.getClass(), sm.toString());
				m = getObject(sm.toString());
				if(m!=null){
					ret.add(m);
				}
			}
		}
		return ret;
	}
	
	private M getObject(String objectToString){
		M m=null;
		try {
			m = gson.fromJson(objectToString,classOFM);
		} catch (JsonSyntaxException e) {
			// try getting rid of some common issues
			// first get rid of { and }
			String smReplace = objectToString.replace("{","");
			smReplace = smReplace.replace("}","");
			// next, break up att=value fields
			String[] partsSepByEqual = smReplace.split(",");
			smReplace = ""; // create a new gson string in smReplace
			// next, make sure that null values (att=, or att=) are replace by ""
			for(String pairSepByEqual:partsSepByEqual){
				String[] tokens = pairSepByEqual.split("=");
				String newTokensSepByEqual = pairSepByEqual;
				if(tokens.length==1){
					newTokensSepByEqual = tokens[0]+"="+"\"\"";
				}
				if(tokens.length==2){
					// check to see that a string with spaces in the
					//   value gets surrounded by quotes
					String[] spaceParts=tokens[1].split(" ");
					if(spaceParts.length>1){
						// replace att=this has spaces
						// with att="this has spaces"
						newTokensSepByEqual = tokens[0]+"="+"\""+tokens[1]+"\"";
					}
				}
				if(newTokensSepByEqual!=null){
					smReplace = smReplace+newTokensSepByEqual+",";
				}
			}
			// get rid of last comma
			smReplace = smReplace.substring(0,smReplace.length()-1);
			// add back in braces {}
			smReplace = "{"+smReplace+"}";
			// try parsing gson again
			// fix to get rid of leading zeros in numbers
			List<String> occurrences = Utils.getRegexMatches(regexExpressionToEliminateLeadingZeros, smReplace);
			for(int i = 0;i<occurrences.size();i++){
				String occurrence = occurrences.get(i);
				List<String> l = Utils.getRegexMatches(regexExpressionWithoutLeadingZeros, occurrence);
				String numWithoutLeadingZeros = "=" + l.get(0) ;
				smReplace = smReplace.replace(occurrence, numWithoutLeadingZeros);
			}
			m = gson.fromJson(smReplace,classOFM);
		} 
		return m;
	}
	
	private class MeteorEmailObject{
		@SuppressWarnings("unused")
		private final String email;
		private MeteorEmailObject(String email){
			this.email = email;
		}
	}
	
	
	private class MeteorLoginObject{
		private MeteorEmailObject user;
		private String password;
		private MeteorLoginObject(String email,String password){
			this.user = new MeteorEmailObject(email);
			this.password = password;
		}
		@Override
		public String toString() {
			return user + ", " + password;
		}
		
	}
	
	private MeteorLoginToken loginToMeteor(String email, String password){
		if(loginToken.get()!=null){
			throw Utils.IllState(this.getClass(), "Alread logged in with login token of : " + loginToken.get().toString());
		}
		CountDownLatch cdl = new CountDownLatch(1);
		ReceiveLoginResponseObservable observer = new ReceiveLoginResponseObservable(cdl);
		MeteorLoginObject userObj = new MeteorLoginObject(email, password);
		Object[] params = {userObj};
		ddpClient.addObserver(observer);
		ddpClient.call("login", params);
		try {
			cdl.await(2000,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Utils.prtObErrMess(this.getClass(), e.getMessage());
			e.printStackTrace();
		}
		ddpClient.deleteObserver(observer);
		return loginToken.get();
	}
	
	private class ReceiveLoginResponseObservable implements Observer {
		CountDownLatch cdl ;

		private ReceiveLoginResponseObservable(CountDownLatch cdl){
			this.cdl = cdl;
		}

		@Override
		public void update(Observable client, Object msg) {
			// see if the msg contains a "token" field
			String msgString = (String)msg;
			Utils.prtObMess(this.getClass(), msgString);
			if(!msgString.contains("token"))return;
			JSONObject jObj = getMeteorResultObj(msgString);
//			@SuppressWarnings("unchecked")
			MeteorLoginToken mlo = (MeteorLoginToken)gson.fromJson(jObj.toString(), MeteorLoginToken.class);
			Utils.prtObMess(this.getClass(), mlo.toString());
			loginToken.set(mlo);
			if(mlo!=null){
				cdl.countDown();
			}
		}
		
	}
	
	/**
	 * subscribeToListData when you are going to wait on CountDownLatch to
	 *   get updates from meteor
	 * @return
	 */
	public SubscriptionHandler subscribeToListData(){
		checkLogin();
		BlockingQueue<SubscriptionMessage> subMsgQueue = new ArrayBlockingQueue<SubscriptionMessage>(1000);
		
		SubscriptionHandlerObserver posObserver = new SubscriptionHandlerObserver(subMsgQueue);
		ddpClient.addObserver(posObserver);
		SubscriptionHandler ret = new SubscriptionHandler(subMsgQueue);
		new Thread(ret).start();
		String className = classOFM.getName();
		Object[] params = {className};
		ddpClient.subscribe(className, params);
		return ret;
		
	}
	
	/**
	 * subscribeToListDataWithCallback when you are going to execute a callback
	 *   function on every list change (add, update, delete) from meteor.
	 * @param callback
	 */
	public void subscribeToListDataWithCallback(MeteorListCallback<M> callback){
		checkLogin();
		BlockingQueue<SubscriptionMessage> subMsgQueue = new ArrayBlockingQueue<SubscriptionMessage>(1000);
		// SubscriptionHandlerObserver uses a blocking queue to get messages from ddpclient
		SubscriptionHandlerObserver observer = new SubscriptionHandlerObserver(subMsgQueue);
		// add observer as an observer to ddpClient
		ddpClient.addObserver(observer);
		// the SubscriptionHandler will take blockingqueue messages from ddpClient and
		//   call the callback routine
		SubscriptionHandler ret = new SubscriptionHandler(subMsgQueue,callback);
		// all of this is done on another thread
		new Thread(ret).start();
		String className = classOFM.getName();
		Object[] params = {className};
		/// now subscribe to changes in the Meteor collection that has the
		//  the same name as the class.
		ddpClient.subscribe(className, params);
	}
	
	
	public class SubscriptionHandler extends AbstractSubscriptionHandler {
		private final Object subScriptListLock = new Object();
		private final List<M> subScriptionList = new ArrayList<M>();
		private final MeteorListCallback<M> callback;
		
		public SubscriptionHandler(BlockingQueue<SubscriptionMessage> subQueue) {
			super(subQueue);
			this.callback=null;
		}
		
		public SubscriptionHandler(BlockingQueue<SubscriptionMessage> subQueue,
				MeteorListCallback<M> callback) {
			super(subQueue);
			this.callback=callback;
		}
		
		// TODO should we check if it is the proper collection or assume we are only getting subscription updates regarding positions

		@Override
		public void added(SubscriptionMessage subMsg) {
			
			Utils.prt("from added: "+subMsg);
			M m = getObject(subMsg.getFields().toString());
			if(m!=null){
				synchronized (subScriptListLock) {
					if(callback==null){
						subScriptionList.add(m);
					}else{
						String id = subMsg.getId();
						callback.onMessage("added",id, m);
					}
				}
			}
			
		}
		
		public List<M> getSubscriptList(){
			synchronized (subScriptListLock) {
				return new ArrayList<M>(this.subScriptionList);
			}
		}

		@Override
		public void changed(SubscriptionMessage subMsg) {

			Utils.prt("from changed: "+subMsg);
			if(callback!=null){
				String id = subMsg.getId();
				M m = getObject(subMsg.getFields().toString());
				callback.onMessage("changed",id, m);
			}

		}

		@Override
		public void removed(SubscriptionMessage subMsg) {
			Utils.prt("from removed: "+subMsg);
			if(callback!=null){
				String id = subMsg.getId();
				callback.onMessage("removed",id,null);
			}
		}

	}
	
	/**
	 * Observer that handles status return from Meteor when you call
	 *   the Meteor method to delete a TableModel
	 *   
	 * @author bperlman1
	 *
	 */
	private class DeleteTableModelObserver extends MeteorObserver<String>{
		@Override
		String convert(Observable client, JSONObject result) {
			
			String s = result.get("result").toString();
			return s;
		}
		
	}
	
	public String deleteMeteorTableModel(String tableModelId){
		Object[] params = {tableModelId};
		DeleteTableModelObserver observer = new DeleteTableModelObserver();
		callMeteorSynchronously("removeMasterTable", params, observer);
		String ret = observer.getReceivedResult();
		return ret;
	}
	
	public void disconnect(){
		checkLogin();
		ddpClient.disconnect();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLoggedIn(){
		return loginToken.get()!=null;
	}
	
	private class RemoveListItemsObserver extends MeteorObserver<String[]>{
		@Override
		String[] convert(Observable client, JSONObject result) {
			return MeteorCollectionStaticMethods.getStringArrayFromJSONObject(result);
		}
	}
	
//	public BlockingQueue<TableChangedByUser> alertTableChangedByUser(
//			int blockingQueueCapacity,
//			Set<String> collectionNamesToWatchFor){
//		int cap = blockingQueueCapacity;
//		if(cap<1){
//			 cap = DEFAULT_BLOCKINGQUEUE_CAPACITY;
//		}
//		final Set<String> collectionNameSet = collectionNamesToWatchFor==null ? null : new HashSet<String>(collectionNamesToWatchFor);
//		final BlockingQueue<TableChangedByUser> ret = new ArrayBlockingQueue<TableChangedByUser>(cap);
//		final MeteorListSendReceive<TableChangedByUser> tableChangedMlsrForSubscribe = new MeteorListSendReceive<TableChangedByUser>(this, TableChangedByUser.class);
//		
//		MeteorListCallback<TableChangedByUser> tableChangedByUserCallback = 
//				new MeteorListCallback<TableChangedByUser>() {
//					@SuppressWarnings({ "unchecked", "rawtypes" })
//					@Override
//					public void onMessage(String messageType, String id,TableChangedByUser convertedMessage) {
//						Utils.prtObMess(this.getClass(), "AlertTableChangedByUser callback: "+messageType);
//						Utils.prtObMess(this.getClass(), "recId: "+id+", record: " + (convertedMessage!=null ? convertedMessage.toString(): "null message"));
//						if(messageType.compareTo("added")==0){
//							String[] userIdAndCollection = id.split("_");
//							String userId = userIdAndCollection[0];
//							String collection = userIdAndCollection[1];
//							if(collection==null)return;
//							if(collectionNameSet!=null && !collectionNameSet.contains(collection)){
//								return;
//							}
//							
//							// Put the list on the blocking queue that we originally sent back to
//							//  the caller of subscribeToTableChangedByUser when we first started above (outside of this runnable).
//							//  The caller should be waiting on this blockingqueue (take) for these List<?> items.
//							boolean okOffer = ret.offer(convertedMessage);
//							if(!okOffer){
//								throw Utils.IllState(this.getClass(), "overflow on offer to blocking queue while receiving TableChangedByUser messages from Meteor ");
//							}
//						}
//					}
//		};
//		Utils.prtObMess(this.getClass(),"About to subscribe to alerts that TableChangedByUser add, updates and removes, which happens after adds and deletes by a Meteor client");
//		tableChangedMlsrForSubscribe.subscribeToListDataWithCallback(tableChangedByUserCallback);
//		Utils.prtObMess(this.getClass(), "kill this process once you are done observing add, update and remove callbacks from Meteor");
//		return ret;
//	}

	
	/**
	 * Subscribe to changes in TableChangedByUser, which gets called whenever any table gets changed for any userId
	 *   When one of the tables that you are watching (in the list collectionNamesToWatchFor), get 
	 *   records of the type classOfRecsToGet.
	 * @param blockingQueueCapacity
	 * @param collectionNamesToWatchFor
	 * @param classOfRecsToGet
	 * @return
	 */
	public  BlockingQueue<List<?>> subscribeToTableChangedByUser(
			int blockingQueueCapacity,
			Set<String> collectionNamesToWatchFor){
		int cap = blockingQueueCapacity;
		if(cap<1){
			 cap = DEFAULT_BLOCKINGQUEUE_CAPACITY;
		}
		final Set<String> collectionNameSet = collectionNamesToWatchFor==null ? null : new HashSet<String>(collectionNamesToWatchFor);
		final BlockingQueue<List<?>> ret = new ArrayBlockingQueue<List<?>>(cap);
		final MeteorListSendReceive<?> finalMlsrToGetPosition = this;
		final MeteorListSendReceive<TableChangedByUser> tableChangedMlsrForSubscribe = new MeteorListSendReceive<TableChangedByUser>(this, TableChangedByUser.class);
		
		// Create an anonymous MeteorListCallback<TableChangedByUser> instance that will
		// respond to TableChangedByUser records from Meteor, then fetch List<?> from
		// Meteor for the collection that was changed - BUT ONLY FOR THE SPECIFIC USERID !!!.
		// 	These List<?> items get offered on to the BlockingQueue<List<?>> that was
		//  originally returned to the caller of subscribeToTableChangedByUser.
		MeteorListCallback<TableChangedByUser> tableChangedByUserCallback = 
				new MeteorListCallback<TableChangedByUser>() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void onMessage(String messageType, String id,TableChangedByUser convertedMessage) {
						Utils.prtObMess(this.getClass(), "TableChangedByUser callback: "+messageType);
						Utils.prtObMess(this.getClass(), "recId: "+id+", record: " + (convertedMessage!=null ? convertedMessage.toString(): "null message"));
						if(messageType.compareTo("added")==0){
							String[] userIdAndCollection = id.split("_");
							String userId = userIdAndCollection[0];
							String collection = userIdAndCollection[1];
							if(collection==null)return;
							if(collectionNameSet!=null && !collectionNameSet.contains(collection)){
								return;
							}
							
							Class<?> clazz=null;
							try {
								clazz = Class.forName(collection);
							} catch (ClassNotFoundException e) {
								if(collection.compareTo("undefined")!=0){
									e.printStackTrace();
									return;
								}else{
									return;
								}
							}

							// Now get the List<?> of records of type clazz from Meteor that
							//  has been changed by a Meteor client.
							MeteorListSendReceive<?> mlsrForCollectionRead = 
									new MeteorListSendReceive(finalMlsrToGetPosition,clazz);
							Map<String, String> selector = new HashMap<String, String>();
							selector.put("userId", userId);
							// Do the get here.
							List<?> dataFromCollection = 
									mlsrForCollectionRead.getList(selector);
							// Put the list on the blocking queue that we originally sent back to
							//  the caller of subscribeToTableChangedByUser when we first started above (outside of this runnable).
							//  The caller should be waiting on this blockingqueue (take) for these List<?> items.
							boolean okOffer = ret.offer(dataFromCollection);
							if(!okOffer){
								throw Utils.IllState(this.getClass(), "overflow on offer to blocking queue while receiving TableChangedByUser messages from Meteor ");
							}
						}
					}
		};
		
		Utils.prtObMess(this.getClass(),"About to subscribe to TableChangedByUser add, updates and removes, which happens after adds and deletes by a Meteor client");
		tableChangedMlsrForSubscribe.subscribeToListDataWithCallback(tableChangedByUserCallback);
		Utils.prtObMess(this.getClass(), "kill this process once you are done observing add, update and remove callbacks from Meteor");
		return ret;
	}

	
	public String[] removeListItems(List<String> listOfIdsToRemove){
		checkLogin();
		String[] arr = listOfIdsToRemove.toArray(new String[]{});
		Object[] params = {classOFM.getName(),arr};
		RemoveListItemsObserver observer = new RemoveListItemsObserver();
		callMeteorSynchronously("removeJavaListItems", params, observer);
		String[] ret = observer.getReceivedResult();
		return ret;
	}
	
	private void checkLogin(){
		if(!isLoggedIn()){
			throw Utils.IllState(this.getClass(), "cannot remove list items without being logged in");
		}
	}


	public String getEmailUsername() {
		return ddpClient.getGmailUsername();
	}


	public String getEmailPw() {
		return ddpClient.getGmailPw();
	}
	
	public MeteorLoginToken getMeteorLoginToken(){
		return loginToken.get();
	}
	
	DdpClientWithEmail getDdpClient(){
		return this.ddpClient;
	}

	Class<M> getClassOFM() {
		return classOFM;
	}
	
}
