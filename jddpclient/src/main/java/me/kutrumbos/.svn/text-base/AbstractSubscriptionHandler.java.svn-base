package me.kutrumbos;

import java.util.concurrent.BlockingQueue;

import me.kutrumbos.enums.DdpMessageType;
import me.kutrumbos.observers.SubscriptionMessage;

public abstract class AbstractSubscriptionHandler implements Runnable {

	private final BlockingQueue<SubscriptionMessage> queue;
		
	public AbstractSubscriptionHandler(BlockingQueue<SubscriptionMessage> queue) {
		this.queue = queue;
	}

	public abstract void added(SubscriptionMessage subMsg);
	public abstract void changed(SubscriptionMessage subMsg);
	public abstract void removed(SubscriptionMessage subMsg);
	
	@Override
	public void run() {
				
		while(true) {
			
			try {
				
				SubscriptionMessage subMsg = queue.take();
				
				DdpMessageType msgType = subMsg.getMsg();
				
				switch(msgType) {
				
					case added: 
						added(subMsg);
						break;
						
					case changed:
						changed(subMsg);
						break;

					case removed:
						removed(subMsg);
						break;

					default:
						System.err.println("Invalid subscription message type");
						break;
				
				}
								
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
