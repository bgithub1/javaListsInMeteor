package me.kutrumbos.tests;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import me.kutrumbos.DdpClient;
import me.kutrumbos.observers.MethodResultObserver;
import me.kutrumbos.observers.SubscriptionHandlerObserver;
import me.kutrumbos.observers.SubscriptionMessage;

public class TestResponse {

	public static void main(String[] args) {
		
		String meteorIp = args[0];
		Integer meteorPort = Integer.parseInt(args[1]);
		
		try {
			
			DdpClient ddpClient = new DdpClient(meteorIp, meteorPort);

			BlockingQueue<Positions> queue = new ArrayBlockingQueue<Positions>(500);
			
			QueuePrinter<Positions> queuePrinter = new QueuePrinter<Positions>(queue);
			
			new Thread(queuePrinter).start();
			
			MethodResultObserver<Positions> obs = new MethodResultObserver<Positions>(queue,Positions.class);
			
			BlockingQueue<SubscriptionMessage> subQueue = new ArrayBlockingQueue<SubscriptionMessage>(500);
			
			QueuePrinter<SubscriptionMessage> subQueuePrinter = new QueuePrinter<SubscriptionMessage>(subQueue);
			
			new Thread(subQueuePrinter).start();
			
			SubscriptionHandlerObserver subObs = new SubscriptionHandlerObserver(subQueue);
			
			ddpClient.addObserver(subObs);

			ddpClient.connect();
			
			ddpClient.addObserver(obs);
			
			Thread.sleep(1500);
			
			Object[] callParams = new String[]{"peter.kutrumbos@gmail.com"};
			
			ddpClient.call("get_position", callParams);
			
			Thread.sleep(2000);
			
			Object[] subParams = new String[]{"peter.kutrumbos@gmail.com"};
			
			ddpClient.subscribe("positions", subParams);

			ddpClient.subscribe("allPositions", new Object[]{});
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private static class QueuePrinter<K> implements Runnable {

		private final BlockingQueue<K> queue;
		
		public QueuePrinter(BlockingQueue<K> queue) {
			this.queue = queue;
		}
		
		@Override
		public void run() {
			
			System.out.println("queue printer started");
			
			while(true) {
				
				try {
					K k = queue.take();
					
					System.out.println(k.toString());
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	private static class Positions {
		
		private final String _id;
		private final List<Position> positions;
		private final String user_id;
		
		public Positions(String _id, List<Position> positions, String user_id) {
			this._id = _id;
			this.positions = positions;
			this.user_id = user_id;
		}

		public String get_id() {
			return _id;
		}

		public List<Position> getPositions() {
			return positions;
		}

		public String getUser_id() {
			return user_id;
		}
		
		public String toString() {
			return _id+","+user_id+","+positions.toString();
		}
		
	}
	
	private static class Position {
		
		private final String shortName;
		private final String owner;
		private final String account;
		private final String qty;
		private final String price;
		private final String dealId;
		
		public Position(String shortName, String owner, String account,
				String qty, String price, String dealId) {
			super();
			this.shortName = shortName;
			this.owner = owner;
			this.account = account;
			this.qty = qty;
			this.price = price;
			this.dealId = dealId;
		}

		public String getShortName() {
			return shortName;
		}

		public String getOwner() {
			return owner;
		}

		public String getAccount() {
			return account;
		}

		public String getQty() {
			return qty;
		}

		public String getPrice() {
			return price;
		}

		public String getDealId() {
			return dealId;
		}
		
		public String toString() {
			return shortName+","+owner;
		}
		
	}
	
}
