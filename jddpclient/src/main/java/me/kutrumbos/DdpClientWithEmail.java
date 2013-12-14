package me.kutrumbos;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


import me.kutrumbos.DdpClient;

public class DdpClientWithEmail extends DdpClient {

	private final GmailClient emailClient;
	private final String[] recipList;
	private final String ddpClientName;
	private final String gmailUsername;
	private final String gmailPw;
	
	
	public DdpClientWithEmail(String meteorServerIp, Integer meteorServerPort, String gmailUsername, String gmailPw) 
			throws URISyntaxException {
		this(meteorServerIp, meteorServerPort,gmailUsername,gmailPw,"");
	}

	public DdpClientWithEmail(
			String meteorServerIp, Integer meteorServerPort, String gmailUsername, String gmailPw, String clientName) 
			throws URISyntaxException {
		super(meteorServerIp, meteorServerPort);
		this.gmailUsername = gmailUsername;
		this.gmailPw = gmailPw;
		if(gmailUsername!=null && gmailUsername.compareTo("    ")>0 
				&& gmailPw!=null && gmailPw.compareTo("    ")>0){
				this.emailClient = new GmailClient(gmailUsername, gmailPw);
				this.recipList = new String[]{gmailUsername};
			}else{
				this.emailClient = null;
				this.recipList = new String[]{};
			}
		this.ddpClientName = clientName;
	}

	@Override
	public void connect(){
		super.connect();
		if(emailClient!=null){
			this.emailClient.connect();
		}
	}
	
	@Override
	public void handleError(Exception ex) {
		String errorMsg = "WebSocketClient error: "+ex.getMessage();
		ex.printStackTrace();
		received(errorMsg);
		try {
			if(emailClient!=null){
				emailClient.sendEmail(recipList, "Meteor DDP Client error for client "+ddpClientName, 
						ddpClientName+" called onError at "+Calendar.getInstance().getTime().toString());
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void connectionClosed(int code, String reason, boolean remote) {
		// is this how onClose messages should be handled??
		Map<String,Object> closeMsg = new HashMap<String,Object>();
		closeMsg.put("close", DdpClient.getConnectionClosedMsg());
		closeMsg.put("code", code);
		System.out.println(closeMsg);
		received(gson.toJson(closeMsg));
		try {
			if(emailClient!=null){
				emailClient.sendEmail(recipList, "Meteor DDP Client disconnect for client "+ddpClientName, 
						ddpClientName+" called onDisconnect at "+Calendar.getInstance().getTime().toString());
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public GmailClient getEmailClient() {
		return emailClient;
	}

	public String[] getRecipList() {
		return recipList;
	}

	public String getDdpClientName() {
		return ddpClientName;
	}

	public String getGmailUsername() {
		return gmailUsername;
	}

	public String getGmailPw() {
		return gmailPw;
	}
	
	
}
