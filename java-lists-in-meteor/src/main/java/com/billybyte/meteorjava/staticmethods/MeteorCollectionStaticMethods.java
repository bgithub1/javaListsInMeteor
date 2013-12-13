package com.billybyte.meteorjava.staticmethods;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import com.billybyte.meteorjava.MeteorListSendReceive;

public class MeteorCollectionStaticMethods {
	/**
	 * Send a java list to Meteor.  If the send succeeds, return null;
	 * If it fails, return the reason as a String
	 * @param mlsr MeteorListSendReceive
	 * @return String - null if the send succeeded, otherwise, a string
	 * 		that explains the nature of the failure.
	 */
	public <T> String sendCollectionDataFromCsv(
			Class<T> classOfT,
			MeteorListSendReceive<T> mlsr,
			Class<?> classInSameResourceAsCsvFileOrNull,
			String csvPath
			){
		if(!mlsr.isLoggedIn()){
			return "MeteorListSendReceive not yet logged in.  Must first call MeteorListSendReceive.login(username,password)";
		}
		List<String[]> csvData = Utils.getCSVData(classInSameResourceAsCsvFileOrNull, csvPath);
		List<T> listToSend = 
				Utils.listFromCsv(classOfT, csvData);
		try {
			mlsr.sendList(listToSend);
		} catch (InterruptedException e) {
			return e.getMessage();
			
		}
		return null;
	
	}
	
	public static String[] getStringArrayFromJSONObject(JSONObject jObj)	{
		JSONArray arr = jObj.getJSONArray("result");
		if(arr==null || arr.length()<1)return new String[]{};
		
		String[] ret = new String[arr.length()];
		for(int i= 0;i<arr.length();i++){
			ret[i] = arr.getString(i);
		}
		return ret;
	}
	

}
