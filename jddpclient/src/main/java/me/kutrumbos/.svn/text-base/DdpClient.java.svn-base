package me.kutrumbos;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import me.kutrumbos.enums.DdpMessageField;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;

/**
 * A web-socket based Meteor DDP client
 * @author peterkutrumbos
 *
 */
public class DdpClient extends Observable {

	private int currentId;
	private final Map<Integer,String> identifiers;
	private WebSocketClient wsClient;
	private final String meteorServerAddress;
	
	private final Object webSocketClientLock = new Object();
	protected final Gson gson = new Gson();
	
	private final static String CONN_CLOSE_MSG = "WebSocketClient connection closed";
	private final static String DDP_PROTOCOL_VERSION = "pre1";
		
	/**
	 * Instantiates a Meteor DDP client for the Meteor server located at the supplied IP and port
	 *   (note: running Meteor locally will typically have a port of 3000 but 
	 *   	port 80 is the typical default for publicly deployed servers)
	 * @param meteorServerIp - IP of Meteor server
	 * @param meteorServerPort - Port of Meteor server, if left null it will default to 3000
	 * @throws URISyntaxException
	 */
	public DdpClient(String meteorServerIp, Integer meteorServerPort) throws URISyntaxException{
		if(meteorServerPort == null) meteorServerPort = 3000;
		this.meteorServerAddress = "ws://"+meteorServerIp+":"+meteorServerPort.toString()+"/websocket";
		this.currentId = 0;
		this.identifiers = new ConcurrentHashMap<Integer,String>();
		this.wsClient = initWebSocketClient(meteorServerAddress);
	}
	
	/**
	 * Initializes and returns WebSocket client
	 * @param meteorServerAddress
	 * @return
	 * @throws URISyntaxException
	 */
	private WebSocketClient initWebSocketClient(String meteorServerAddress) throws URISyntaxException{
		return new WebSocketClient(new URI(meteorServerAddress)) {
			
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				connectionOpened();
			}
			
			@Override
			public void onMessage(String message) {
				received(message);
			}
			
			@Override
			public void onError(Exception ex) {
				handleError(ex);
			}
			
			@Override
			public void onClose(int code, String reason, boolean remote) {
				connectionClosed(code, reason, remote);
			}
		};
	}
	
	private WebSocketClient getWsClient(){
		synchronized (webSocketClientLock) {
			return this.wsClient;
		}
	}
	
	/**
	 * Ran on initial web-socket connection, sends back a connection confirmation message 
	 * 	to validate the connection with the Meteor server
	 */
	private void connectionOpened() {
		// reply to Meteor server with connection confirmation message ({"msg": "connect"})
		System.out.println("WebSocket connection opened");
		Map<DdpMessageField,Object> connectMsg = new HashMap<DdpMessageField,Object>();
		connectMsg.put(DdpMessageField.msg, "connect");
		connectMsg.put(DdpMessageField.version, DDP_PROTOCOL_VERSION);
		connectMsg.put(DdpMessageField.support, new String[]{DDP_PROTOCOL_VERSION});		
		send(connectMsg);
	}
	
	/**
	 * Ran when connection is closed
	 * @param code
	 * @param reason
	 * @param remote
	 */
	public void connectionClosed(int code, String reason, boolean remote) {
		// is this how onClose messages should be handled??
//		String closeMsg = CONN_CLOSE_MSG+":"+code+","+reason+","+remote;
		Map<String,Object> closeMsg = new HashMap<String,Object>();
		closeMsg.put("close", CONN_CLOSE_MSG);
		closeMsg.put("code", code);
		System.out.println(closeMsg);
		received(gson.toJson(closeMsg));
	}
	
	/**
	 * Reconnects to the meteor server by instantiating a new underlying Web Socket client
	 */
	public void reconnect() {
		synchronized (webSocketClientLock) {
			try {
				this.wsClient = initWebSocketClient(meteorServerAddress);
				
				Thread.sleep(1000);
				
				this.wsClient.connect();
				// TODO is it ok to catch this here?  the address should already be handled upon construction of the DdpClient
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the DDP Client's connection close message
	 * @return
	 */
	public static String getConnectionClosedMsg(){
		return DdpClient.CONN_CLOSE_MSG;
	}
	
	/**
	 * Error handling for any errors over the web-socket connection
	 * @param ex
	 */
	public void handleError(Exception ex) {
//		String errorMsg = "WebSocketClient error: "+ex.getMessage();
		Map<String,Object> errorObj = new HashMap<String,Object>();
		errorObj.put("error", ex.getMessage());
		ex.printStackTrace();
		received(gson.toJson(errorObj));
	}
	
	/**
	 * Increments and returns the client's current ID
	 * @return
	 */
	private int nextId() {
		return ++currentId;
	}
	
	/**
	 * Registers a client DDP message by storing it in the identifiers map
	 * @param identifier
	 * @return
	 */
	private String registerIdentifier(String identifier) {
		Integer id = nextId();
		identifiers.put(id, identifier);
		return id.toString();
	}
	
	/**
	 * Initiate connection to meteor server
	 */
	public void connect() {
		getWsClient().connect();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to the DDP server in a synchronized fashion
	 * @return Boolean reflecting whether a connection was established successfully
	 * @throws InterruptedException
	 */
	public Boolean syncConnect() throws InterruptedException {
		
		getWsClient().connect();
		int tries = 0;
		while(getWsClient().getReadyState()!=1&&tries<10) {
			System.out.println("Attempting DDP connection attempt "+(tries+1)+"...");
			Thread.sleep(1000);
			tries++;
		}
		
		if(tries==10) {
			return false;
		} else {
			return true;
		}
		
	}

	/**
	 * Retrieve the WebSocket 'readyState'.
	 * This represents the state of the connection.
	 * It returns a numerical value, as per W3C WebSockets specs.
	 * 
	 * @return Returns '0 = CONNECTING', '1 = OPEN', '2 = CLOSING' or '3 = CLOSED'
	 */
	public int getReadyState() {
		return getWsClient().getReadyState();
	}
	
	/**
	 * Disconnect websocket connection with meteor server
	 */
	public void disconnect() {
		getWsClient().close();
	}
	
	/**
	 * Call a meteor method with the supplied parameters
	 * @param method - name of corresponding Meteor method
	 * @param args - arguments to be passed to the Meteor method
	 */
	public String call(String method, Object[] params){
		Map<DdpMessageField,Object> callMsg = new HashMap<DdpMessageField,Object>();
		callMsg.put(DdpMessageField.msg, "method");
		callMsg.put(DdpMessageField.method, method);
		callMsg.put(DdpMessageField.params, params);
		
		String id = registerIdentifier("method,"+method+","+Arrays.toString(params));
		
		callMsg.put(DdpMessageField.id, id);
		
		if(send(callMsg)==1){
			return id;
		} else {
			return null;
		}
		
	}
	
	/**
	 * Subscribe to a Meteor record set with the supplied parameters
	 * @param name - name of the corresponding Meteor subscription
	 * @param params - arguments corresponding to the Meteor subscription
	 */
	public int subscribe(String name, Object[] params) {
		Map<DdpMessageField,Object> subMsg = new HashMap<DdpMessageField,Object>();
		subMsg.put(DdpMessageField.msg, "sub");
		subMsg.put(DdpMessageField.name, name);
		subMsg.put(DdpMessageField.params, params);
		
		String id = registerIdentifier("sub,"+name+","+Arrays.toString(params));
		
		subMsg.put(DdpMessageField.id, id);
		
		return send(subMsg);
	}
	
	/**
	 * Un-subscribe to a record set
	 * @param name - name of the corresponding Meteor subscription
	 */
	public void unsubscribe(String name) {
		Map<DdpMessageField,Object> unsubMsg = new HashMap<DdpMessageField,Object>();
		unsubMsg.put(DdpMessageField.msg, "unsub");
		unsubMsg.put(DdpMessageField.name, name);
		
		String id = registerIdentifier("unsub,"+name);
		
		unsubMsg.put(DdpMessageField.id, id);
		
		send(unsubMsg);
	}
	
	/**
	 * Converts DDP-formatted message to JSON and sends over web-socket
	 * @param msg
	 */
	public int send(Map<DdpMessageField,Object> msg) {
		int connState = getReadyState();
		if (connState == 1) {
			String json = gson.toJson(msg);
			getWsClient().send(json);
		} else {
			System.err.println("DdpClient could not send message - invalid connection state");
		}
		return connState;
	}

	/**
	 * Notifies observers of this DDP client of messages received from the Meteor server 
	 * @param msg
	 */
	public void received(String msg) {
		this.setChanged();
		this.notifyObservers(msg);
	}
	
}
