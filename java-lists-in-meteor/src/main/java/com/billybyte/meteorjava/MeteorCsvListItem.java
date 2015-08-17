package com.billybyte.meteorjava;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.billybyte.meteorjava.staticmethods.Utils;
/**
 * Use this class to send List<String[]> csv data to Meteor as SimpleCsv data
 * 
 * @author sarahhartman
 *
 */
public class MeteorCsvListItem extends MeteorBaseListItem{
	private final static DecimalFormat df = new DecimalFormat("00000");
	private final static String IS_HEADER = "isHeader";
	private final String[]  data;
	
	/**
	 * Use this constructor for non-header data (regular csv lines)
	 * @param userId
	 * @param lineNumber
	 */
	public MeteorCsvListItem(String userId,Integer lineNumber,String[] dataArray) {
		super(userId+"_"+df.format(lineNumber), userId);
		this.data = dataArray;
	}
	
	public MeteorCsvListItem(String userId,String[] columnNames){
		super(userId+"_"+IS_HEADER, userId);
		this.data  = columnNames;
	}

	public String[] getData() {
		return data;
	}
	
	/**
	 * Create a List<MeteorCsvListItem> to send to meteor from a regular csv
	 *   (List<String[]>) list.
	 *  HEADER MUST BE RECORD 0 !!!!
	 *  
	 * @param csv
	 * @return List<MeteorCsvListItem>
	 */
	public static List<MeteorCsvListItem> fromCsv(String userId,List<String[]> csv){
		List<MeteorCsvListItem> ret = new ArrayList<MeteorCsvListItem>();
		// get first record
		if(csv==null){
			throw Utils.IllState(MeteorCsvListItem.class, "Null csv passed to fromCsv");
		}
		if(csv.size()<1){
			throw Utils.IllState(MeteorCsvListItem.class, "No data or header csv passed to fromCsv");
		}
		if(csv.size()<2){
			Utils.prtObErrMess(MeteorCsvListItem.class, "Only header passed to fromCsv");
		}
		String[] header = csv.get(0);
		ret.add(new MeteorCsvListItem(userId, header));
		for(int i = 1;i<csv.size();i++){
			ret.add(new MeteorCsvListItem(userId, i, csv.get(i)));
		}
		return ret;
	}

}
