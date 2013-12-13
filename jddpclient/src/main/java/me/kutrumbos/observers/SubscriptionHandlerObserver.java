package me.kutrumbos.observers;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import me.kutrumbos.enums.DdpMessageField;
import me.kutrumbos.enums.DdpMessageType;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SubscriptionHandlerObserver implements Observer {

	private final BlockingQueue<SubscriptionMessage> queue;
	
	private final static Gson gson = new Gson();
		
	public SubscriptionHandlerObserver(BlockingQueue<SubscriptionMessage> queue) {
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

				if(msgMap!=null&&!msgMap.isEmpty()&&msgMap.containsKey(DdpMessageField.msg.toString())) {
					DdpMessageType msgType = DdpMessageType.valueOf(msgMap.get(DdpMessageField.msg.toString()).toString());
					
					if(msgType==DdpMessageType.added||msgType==DdpMessageType.changed||msgType==DdpMessageType.removed) {
						SubscriptionMessage subMsg = new SubscriptionMessage(msgMap);
						
						if(!queue.offer(subMsg)) {
							System.out.println("queue full");
						}
						
					} else if (msgType==DdpMessageType.ready) {
						System.out.println(msgMap.toString());
					} else if (msgType==DdpMessageType.nosub) {
						System.err.println(msgMap.toString());
					}
					
				}

			} catch (JsonSyntaxException e) {
				System.err.println("Exception while parsing subscription JSON - "+msgString);
			}
			
		}			
	}
}
