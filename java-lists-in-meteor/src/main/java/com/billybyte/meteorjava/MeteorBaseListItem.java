package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import misc.PosClDetailed;

import com.billybyte.meteorjava.staticmethods.Utils;

public class MeteorBaseListItem {
	private final String _id;
	private final String userId;
	public MeteorBaseListItem(String _id, String userId) {
		super();
		this._id = _id;
		this.userId = userId;
	}
	public String get_id() {
		return _id;
	}
	public String getUserId() {
		return userId;
	}
	@Override
	public String toString() {
		return _id + ", " + userId;
	}

	public static final <M> List<M> getItemsFromMeteor(
			String meteorUrl,
			Integer meteorPort,
			String adminEmail,
			String adminPass,
			Class<M> itemClass,
 			Map<String,String> selectorMap){
		MeteorListSendReceive<M> mlsr = null;
		try {
			mlsr =
					new MeteorListSendReceive<M>(100, 
							itemClass, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}

		List<M> positionList = mlsr.getList(selectorMap);
		mlsr.disconnect();
		return positionList;
		
	}

	public static <M> void sendItemsToMeteor(
			Class<M> classToSend,			
			String meteorUrl,
			Integer meteorPort,
			String adminEmail,
			String adminPass,
			List<M> listToSend){
		MeteorListSendReceive<M> mlsr = null;
		try {
			mlsr =new MeteorListSendReceive<M>(100, 
					classToSend, meteorUrl, meteorPort, 
					adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		

		try {
			String[] result = mlsr.sendList(listToSend);
			Utils.prt(Arrays.toString(result));
			mlsr.disconnect();
		} catch (InterruptedException e) {
			throw Utils.IllState(e);
			
		}
		
	}
	
	public static <M> void  subscribeToChanges(
			Class<M> classToSend,			
			String meteorUrl,
			Integer meteorPort,
			String adminEmail,
			String adminPass,
			MeteorListCallback<M> callback
			){
		MeteorListSendReceive<M> mlsr = null;
		try {
			mlsr = new MeteorListSendReceive<M>(100, 
					classToSend, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		
		mlsr.subscribeToListDataWithCallback(callback);
	}


}
