package com.billybyte.meteorjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * JsonNestedList provides a nested set of lists which allow the caller
 *   to see all possible values for any given set of previous choices
 *   JsonNestedList end up being json when they are written to mongo or meteor.
 *   The class MeteorValidator holds a JsonNestedList, which the meteor client
 *     uses to help the user make field drop-down selection choices.
 * @author bperlman1
 *
 */
@SuppressWarnings("rawtypes")
public class JsonNestedList {
	private static final String[] SHORTNAME_FIELDS = {"type","exch","sym","curr","year","mon","pc","strike"};
	public static final String KEY_ID = "key";
	public static final String TOP_KEY = "top";
	public static final String TOP_LEVEL = "top";

	// instance variables
	private final String key;
	private final String levelName;
	private final List values;
	
	
	@SuppressWarnings("unused")
	private static final Object[][] DEFAULT_SN_ARRAY = {
			{"FOP","NYMEX","USD","LO",2015,6,"P",52.50},
			{"FOP","NYMEX","USD","LO",2015,6,"P",53.50},
			{"FOP","NYMEX","USD","LO",2015,6,"P",54.50},
			{"FOP","NYMEX","USD","LO",2015,7,"P",52.50},
			{"FOP","NYMEX","USD","LO",2015,7,"P",53.50},
			{"FOP","NYMEX","USD","LO",2015,7,"P",54.50},
			{"FOP","NYMEX","USD","LO",2016,6,"P",62.50},
			{"FOP","NYMEX","USD","LO",2016,6,"P",63.50},
			{"FOP","NYMEX","USD","LO",2016,6,"P",64.50},
			{"FOP","NYMEX","USD","LO",2016,7,"P",62.50},
			{"FOP","NYMEX","USD","LO",2016,7,"P",63.50},
			{"FOP","NYMEX","USD","LO",2016,7,"P",64.50},
			{"FOP","NYMEX","USD","ON",2015,6,"P",52.50},
			{"FOP","NYMEX","USD","ON",2015,6,"P",53.50},
			{"FOP","NYMEX","USD","ON",2015,6,"P",54.50},
			{"FOP","NYMEX","USD","ON",2015,7,"P",52.50},
			{"FOP","NYMEX","USD","ON",2015,7,"P",53.50},
			{"FOP","NYMEX","USD","ON",2015,7,"P",54.50},
			{"FOP","NYMEX","USD","ON",2016,6,"P",62.50},
			{"FOP","NYMEX","USD","ON",2016,6,"P",63.50},
			{"FOP","NYMEX","USD","ON",2016,6,"P",64.50},
			{"FOP","NYMEX","USD","ON",2016,7,"P",62.50},
			{"FOP","NYMEX","USD","ON",2016,7,"P",63.50},
			{"FOP","NYMEX","USD","ON",2016,7,"P",64.50},
			{"FUT","NYMEX","USD","CL",2015,6},
			{"FUT","NYMEX","USD","NG",2015,6},
			{"FUT","NYMEX","USD","HO",2015,6},
			{"FUT","NYMEX","USD","RB",2015,6},
			{"STK","SMART","","*"},
			{"OPT","SMART","USD","*","20[123][5678]","((0[1-9])|([12][0-9])|(3[01]))","[CP]","*"},
		};

	
	/**
	 * 
	 * @param key
	 * @param levelName
	 */
	public JsonNestedList(String key,String levelName){
		this.key = key;
		this.values = new ArrayList();
		this.levelName = levelName;
	}
	
	public DBObject getDbObject(){
		
		DBObject doc = new BasicDBObject();
		if(key!=null && levelName!=null){
			doc.put(KEY_ID, key);
		}else{
			doc.put(KEY_ID, TOP_LEVEL);
		}
		
		if(this.values!=null && values.size()>0){
			// see if this is a list of JsonNestedList values or of real values
			Object firstListValue = values.get(0);
			if(JsonNestedList.class.isAssignableFrom(firstListValue.getClass())){
				List<DBObject> docList = new ArrayList<DBObject>();
				for(Object value:values) {
					docList.add(((JsonNestedList)value).getDbObject());
				}
				doc.put("subItems", docList);	
			}else{
				List<String> stringList = new ArrayList<String>();
				for(Object value:values) {
					stringList.add(value.toString());
				}
				doc.put("subItems",stringList);
			}
			
		}
		return doc;
		
	}

	
	public static Map buildJnestMap(List<String[]> listOfPossChoices){
		Object[][] ll = new Object[listOfPossChoices.size()][listOfPossChoices.get(0).length];
		for(int i = 0;i<listOfPossChoices.size();i++){
			ll[i] = listOfPossChoices.get(i);
		}
		return buildNestedMap(ll);
		// now find the
	}
	
	
	@SuppressWarnings("unchecked")
	private static Map buildNestedMap(Object[][] levelList){
		Map m = new HashMap();
		Map ret = m;
		for(int i = 0;i<levelList.length;i++){
			Object[] levels = levelList[i];
			for(int j=0;j<levels.length-1;j++){
				Object key = levels[j];
				if(key==null){
					key = "";
				}
				Map inner = (Map)m.get(key);
				if(inner==null){
					inner = new HashMap();
					m.put(key, inner);
				}
				m = inner;
			}
			Object lastLevel = levels[levels.length-1];
			m.put(lastLevel, lastLevel);
			m=ret;
		}
		return ret;
	}
	
	public static final JsonNestedList buildJsonNestedList(Object[][] levelList,List<String> levelNames){
		Map nestedMap = buildNestedMap(levelList);
		return buildJsonNestedList(null,nestedMap,levelNames);
	}
		
	public static final JsonNestedList buildJsonNestedList(List<String[]> levelList,List<String> levelNames){
		Object[][] ll = new Object[levelList.size()][levelList.get(0).length];
		for(int i = 0;i<levelList.size();i++){
			ll[i] = levelList.get(i);
		}
		return buildJsonNestedList(ll,levelNames); 
	}
	
	@SuppressWarnings("unchecked")
	private static JsonNestedList buildJsonNestedList(JsonNestedList jnl,Map m,List<String> levelNames ){
		if(jnl==null){
			jnl = new JsonNestedList(TOP_KEY,TOP_LEVEL);
		}
		
		for(Object key : m.keySet()){
			if(key==null) continue;
			Object o = m.get(key);
			if(Map.class.isAssignableFrom(o.getClass())){
				JsonNestedList jnlNew = new JsonNestedList(key.toString(),levelNames.get(0));
				jnl.values.add(buildJsonNestedList(jnlNew,(Map)o,levelNames.subList(1, levelNames.size())));
			}else{
				jnl.values.add(o); // at this point the value and the key are identical, and you have traversed an entire set of branches in map m.
			}
			
		}
		return jnl;
	}
	
	// jonathanrconine@gmail.com
	@SuppressWarnings("unchecked")
	public static List<List> unwind(JsonNestedList jnest,List<List> main, List current){
		List<List> ret =null;
		List currInner = null;
		if(main==null){
			ret  = new ArrayList<List>();
			currInner = new ArrayList();
		}else{
			ret = new ArrayList<List>(main);	
			currInner = new ArrayList(current);
		}
		
		
		if(jnest.key!=null){
			currInner.add(jnest.key);
		}
		for(Object o : jnest.values){
			List oldCurrInner = new ArrayList(currInner);
			if(JsonNestedList.class.isAssignableFrom(o.getClass())){
				JsonNestedList innerJnest = (JsonNestedList)o;
				ret = unwind(innerJnest,ret,currInner);
				
			}else{
				// when you get here, you are finished populating currInner
				currInner.add(o); 
				ret.add(currInner);
				currInner = new ArrayList(oldCurrInner);
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		
		String fileName = (args!=null && args.length>0) ? args[0] : "shortNames.txt";
		Set<String> shortNameSet = readData(fileName);
//		MongoWrapper m = null;
//		try {
//			
//			String mongoIp = args.length >0 ? args[0] : "127.0.0.1";
//			Integer mongoPort = Integer.parseInt(args.length>1 ? args[1] : "27017");
//			
//			m = new MongoWrapper(mongoIp,mongoPort);
//			
//			DB db = m.getDB(IMPLIEDVOL_DB);
//			
//			DBCollection coll = db.getCollection(IMPLIEDVOL_CL);
//
//			DBCursor cursor = coll.find();
//
//			
//			try {
//				
//				while(cursor.hasNext()) {
//					DBObject doc = cursor.next();
//					
//					String shortName = (String) doc.get(MongoWrapper.ID_FIELD);
//					
//					shortNameSet.add(shortName);
//					
//					System.out.println(shortName);
//					
//				}
//				
//			} finally {
//				cursor.close();
//			}
//			
//			
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} 
		
		// create temp csv

		
		String[][] snArray = new String[shortNameSet.size()][];
		int i=0;
		for(String sn : shortNameSet){
			String[] snParts = sn.split("\\.");
			String type = snParts[1];
			String exch = snParts[2];
			String prod = snParts[0];
			String[] arr = null;
			if(type.compareTo("STK")==0){
				arr = new String[3];
				arr[0]=type;
				arr[1] = exch;
				arr[2] = prod;
			}else if(type.compareTo("FUT")==0){
				arr = new String[6];
				arr[0]=type;
				arr[1] = exch;
				arr[2] = prod;
				arr[3] = snParts[3];
				arr[4] = snParts[4].substring(0,4);
				arr[5] = snParts[4].substring(4,6);
			}else if(type.compareTo("OPT")==0 || type.compareTo("FOP")==0){
				arr = new String[8];
				arr[0]=type;
				arr[1] = exch;
				arr[2] = prod;
				arr[3] = snParts[3];
				arr[4] = snParts[4].substring(0,4);
				arr[5] = snParts[4].substring(4,6);
				arr[6] = snParts[5];
				arr[7] = snParts[6]+ (snParts.length>7 ? "."+snParts[7] : "");
			}
			if(arr!=null){
				snArray[i] = arr;
				i +=1;
			}
		}
		
		
//		Map nestedMap = buildNestedMap(snArray);
//		Utils.prt("print map items");
//		CollectionsStaticMethods.prtMapItems(nestedMap);

		
		List<String> levelNames = new ArrayList<String>();//CollectionsStaticMethods.listFromArray(new String[]{"type","exch","sym","curr","year","mon","pc","strike"});
		for(String sn : SHORTNAME_FIELDS){
			levelNames.add(sn);
		}
		JsonNestedList jnest = buildJsonNestedList(snArray,levelNames);
		System.out.println("print unwind");
		List<List> unwound = unwind(jnest, null,null);
		for(List l : unwound){
			String s = "";
			for(Object o : l){
				s += o.toString()+",";
			}
			System.out.println(s.substring(0,s.length()-1));
		}
		// get dbobjects to send to meteor or local db.
//		writeToMongo(jnest, m);
		
		System.out.println("finished");
		
		
		System.exit(0);

	}
	

	
	private static Set<String> readData(String fn){
		Set<String> ret = new TreeSet<String>();
		File file = new File(fn);
		try {
			Reader fr  = new FileReader( file);
			String data=null;
			BufferedReader br = new BufferedReader(fr);
			while((data =br.readLine())!= null){
				ret.add(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}		

	private static final void writeToMeteor(JsonNestedList jnest){
		
	}
}
