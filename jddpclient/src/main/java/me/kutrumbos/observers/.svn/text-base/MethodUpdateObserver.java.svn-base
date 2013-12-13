package me.kutrumbos.observers;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import me.kutrumbos.enums.DdpMessageField;
import me.kutrumbos.enums.DdpMessageType;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MethodUpdateObserver implements Observer {

	private final BlockingQueue<List<String>> queue;
	
	private final static Gson gson = new Gson();
	
	public MethodUpdateObserver(BlockingQueue<List<String>> queue) {
		this.queue = queue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable client, Object msg) {

		if(msg instanceof String) {
			
			String msgString = (String) msg;
			
			Map<String, Object> msgMap;
			
			try {
				
				msgMap = gson.fromJson(msgString, Map.class);
				
				if(msgMap.containsKey(DdpMessageField.msg.toString())) {
					
					String msgField = (String) msgMap.get(DdpMessageField.msg.toString());
					
					if(msgField.compareTo(DdpMessageType.updated.toString())==0) {
						
						if(msgMap.containsKey(DdpMessageField.methods.toString())) {
							
							List<String> result = gson.fromJson(gson.toJson(msgMap.get(DdpMessageField.methods.toString())), List.class);
							
							if(!queue.offer(result)) {
								System.out.println("queue full");
							}
							
						} else if(msgMap.containsKey(DdpMessageField.error.toString())) {
							
							System.err.println(msgMap.get(DdpMessageField.error.toString()));
							
						}
					}
				}

			} catch (JsonSyntaxException e) {
				System.err.println("Exception while parsing JSON");
			}
			
		}			
	}
}
