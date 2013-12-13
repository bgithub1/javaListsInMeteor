package me.kutrumbos.observers;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import me.kutrumbos.enums.DdpMessageField;
import me.kutrumbos.enums.DdpMessageType;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MethodResultObserver<K> implements Observer {

	private final Class<K> clazz;
	private final BlockingQueue<K> queue;
	
	private final static Gson gson = new Gson();
	
	public MethodResultObserver(BlockingQueue<K> queue, Class<K> clazz) {
		this.queue = queue;
		this.clazz = clazz;
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
					
					if(msgField.compareTo(DdpMessageType.result.toString())==0) {
						
						if(msgMap.containsKey(DdpMessageField.result.toString())) {
							
							K result = gson.fromJson(gson.toJson(msgMap.get(DdpMessageField.result.toString())), clazz);
							
							if(!queue.offer(result)) {
								System.out.println("queue full");
							}
						} else if(msgMap.containsKey(DdpMessageField.error.toString())) {
							
							System.err.println(msgMap.get(DdpMessageField.error.toString()));
							
						}
					}
				}

			} catch (JsonSyntaxException e) {
				System.err.println("Exception while parsing method result JSON - "+msgString);
			}
			
		}			
	}
}
