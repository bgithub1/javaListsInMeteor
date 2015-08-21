package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.List;


/**
 * This class makes it easy to send csv data to meteor.
 * @author sarahhartman
 *
 */
public class MeteorCsvSendReceive {
	private final MeteorListSendReceive<Object> meteorListSendReceive;
	
	public MeteorCsvSendReceive(
			String meteorServerIp,
			int meteorServerPort,
			String emailUsername,
			String emailPw,
			String notifyEmail,
			String notifyEmailPw,
			String clientName) throws URISyntaxException {
		this.meteorListSendReceive = 
				new MeteorListSendReceive<Object>(
						100, 
						Object.class, 
						meteorServerIp, 
						meteorServerPort, 
						emailUsername, 
						emailPw, 
						notifyEmail, 
						notifyEmailPw, 
						clientName);
	}
	
	public MeteorCsvSendReceive(
			MeteorListSendReceive<?> previousMlsr){
		this.meteorListSendReceive = 
				new MeteorListSendReceive<Object>(previousMlsr, Object.class);
	}
	
	public MeteorListSendReceive<Object> getMeteorListSendReceive(){
		return this.meteorListSendReceive;
	}

	/**
	 * This is a convenience method for sending a Csv MeteorTableModel to meteor
	 *   so that you can then send csv data (List<String[]>) to meteor easily.
	 * @param tableName
	 * @return
	 */
	public String[] sendCsvTableModel(String tableName){
		this.meteorListSendReceive.deleteMeteorTableModel(tableName);
		return this.meteorListSendReceive.sendCsvTableModel(tableName);
	}
	
	/**
	 * Convenience method for sending csv data (List<String[]>) to meteor.
	 *   A MeteorTableModel should have already been sent to meteor using the
	 *   sendCsvTableModel(tableName) method above.
	 *   
	 * @param userId
	 * @param tableName
	 * @param csv
	 * @return
	 */
	public String[] sendCsvData(String userId,String tableName,List<String[]> csv, boolean deleteOldCsvData){
		return this.meteorListSendReceive.sendCsvData(userId, tableName, csv,deleteOldCsvData);
	}
	

}
