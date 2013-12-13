package me.kutrumbos.observers;

import java.util.Map;

import me.kutrumbos.enums.DdpMessageField;
import me.kutrumbos.enums.DdpMessageType;

public class SubscriptionMessage {

	private final DdpMessageType msg;
	private final String collection;
	private final String id;
	private final String[] cleared;
	private final Map<String,Object> fields;
	
	@SuppressWarnings("unchecked")
	public SubscriptionMessage(Map<String,Object> jsonMap) {
		// TODO should we explicitly declare that this throws invalid argument exceptions?
		if(jsonMap==null||jsonMap.isEmpty()||!jsonMap.containsKey(DdpMessageField.msg.toString())) {
			System.err.println("Invalid json map passed to subscription message constructor");
			throw new IllegalArgumentException();
		}
		
		DdpMessageType msg = DdpMessageType.valueOf(jsonMap.get(DdpMessageField.msg.toString()).toString());
		
		if(!(msg==DdpMessageType.added||msg==DdpMessageType.changed||msg==DdpMessageType.removed)) {
			System.err.println(
					"Json map does not contain a proper subscription data message type (valid types are ADDED, CHANGED & REMOVED");
			throw new IllegalArgumentException();
		}
		
		this.msg = msg;
		this.collection = (String) jsonMap.get(DdpMessageField.collection.toString());
		this.id = (String) jsonMap.get(DdpMessageField.id.toString());
		String[] cleared = null;
		if(jsonMap.containsKey(DdpMessageField.cleared.toString())) cleared = (String[]) jsonMap.get(DdpMessageField.cleared.toString());
		this.cleared = cleared;
		Map<String,Object> fields = null;
		if(jsonMap.containsKey(DdpMessageField.fields.toString())) fields = 
				(Map<String,Object>) jsonMap.get(DdpMessageField.fields.toString());
		this.fields = fields;

	}

	public DdpMessageType getMsg() {
		return msg;
	}

	public String getCollection() {
		return collection;
	}

	public String getId() {
		return id;
	}

	public String[] getCleared() {
		return cleared;
	}

	public Map<String,Object> getFields() {
		
		return fields;
	}

	@Override
	public String toString() {
		String ret = msg+","+collection+","+id;
		if(cleared!=null) ret = ret+","+cleared.toString();
		if(fields!=null) ret = ret+","+fields.toString();
		return ret;
	}
	
}
