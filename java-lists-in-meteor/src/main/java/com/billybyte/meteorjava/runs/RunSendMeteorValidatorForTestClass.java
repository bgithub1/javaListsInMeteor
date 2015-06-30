package com.billybyte.meteorjava.runs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import misc.TestClass;
import misc.Trades;

import com.billybyte.meteorjava.JsonNestedList;
import com.billybyte.meteorjava.MeteorValidator;
import com.billybyte.meteorjava.staticmethods.Utils;

public class RunSendMeteorValidatorForTestClass {
	// sens the misc.Trades MeteorValidator instance to meteor
	public static void main(String[] args) {
		Map<String, String> argPairs = new HashMap<String, String>();
		if(args!=null){
			// find pairs separated by the = sign
			for(String argPair : args){
				String[] pair = argPair.split("=");
				if(pair.length>1){
					argPairs.put(pair[0],pair[1]);
				}
			}
		}
		String meteorUrl = "127.0.0.1";
		int meteorPort = 3000;
		String adminEmail = "admin1@demo.com";
		String adminPass = "admin1";
				
		if(argPairs.containsKey("metUrl")){
			meteorUrl = argPairs.get("metUrl");
		}
		if(argPairs.containsKey("metPort")){
			meteorPort = new Integer(argPairs.get("metPort"));
		}
		if(argPairs.containsKey("adminEmail")){
			adminEmail = argPairs.get("adminEmail");
		}
		if(argPairs.containsKey("adminPass")){
			adminPass = argPairs.get("adminPass");
		}

		
		List<String[]> levelList = Utils.getCSVData(RunSendMeteorValidatorForTestClass.class,"testPossNames.csv"); 
		List<String> levelNames = new ArrayList<String>();
		levelNames.add("myLastName");
		levelNames.add("myFirstName");
		Map jnestMap = new HashMap();
		
		Map<String,List<String>> independentFields = 
				new HashMap<String, List<String>>();
		MeteorValidator mvTestClass = new MeteorValidator(
				TestClass.class,
				jnestMap,levelNames,
				independentFields,
				new ArrayList<String>());
		mvTestClass.sendValidator(
				meteorUrl, meteorPort, adminEmail, adminPass);

		List<String> shortNamePossChoices = Arrays.asList(new String[]{
				"IBM","AAPL","MSFT","GOOG"});
		independentFields.put("shortName", shortNamePossChoices);
		List<String> myPricePossChoices = Arrays.asList(new String[]{
				new BigDecimal("100.00").toString(),
				new BigDecimal("120.00").toString(),
				new BigDecimal("130.00").toString(),
				new BigDecimal("140.00").toString(),
		});
		independentFields.put("myPrice", myPricePossChoices);
		List<String> freeFields = Arrays.asList(new String[]{
				"myQty"
		});
		jnestMap = JsonNestedList.buildJnestMap(levelList);
		MeteorValidator mvTradeClass = new MeteorValidator(
				Trades.class,
				jnestMap,levelNames,
				independentFields,
				freeFields);
		mvTradeClass.sendValidator(
				meteorUrl, meteorPort, adminEmail, adminPass);
	}
}
