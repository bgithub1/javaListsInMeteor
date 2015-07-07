package com.billybyte.meteorjava;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.billybyte.meteorjava.staticmethods.Utils;

public class TableChangedByUser {
	private final String _id;
	private final String action;
	public TableChangedByUser(String _id, String action) {
		super();
		this._id = _id;
		this.action = action;
	}
	public String get_id() {
		return _id;
	}
	public String getAction() {
		return action;
	}
	
	public static void subscribeToChanges(
			String meteorUrl,
			Integer meteorPort,
			String adminEmail,
			String adminPass,
			final BlockingQueue<Map<String,TableChangedByUser>> blockingQueue
			){
		MeteorListCallback<TableChangedByUser> callback = 
				new MeteorListCallback<TableChangedByUser>() {

					@Override
					public void onMessage(String messageType, String id,
							TableChangedByUser convertedMessage) {
						Utils.prtObMess(this.getClass(), " callback: "+messageType);
						Utils.prtObMess(this.getClass(), "recId: "+id+", record: " + (convertedMessage!=null ? convertedMessage.toString(): "null message"));
						Map<String, TableChangedByUser> ret = 
								new HashMap<String, TableChangedByUser>();
						ret.put(id, convertedMessage);
						boolean result = 
								blockingQueue.offer(ret);
						if(!result){
							Utils.prtObErrMess(TableChangedByUser.class, "no room in blockingQueue");
						}
					}

		};
		
		MeteorBaseListItem.subscribeToChanges(TableChangedByUser.class,
				meteorUrl, meteorPort, adminEmail, adminPass,callback);
	}
}
