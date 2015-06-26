package com.billybyte.meteorjava.runs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import misc.TestClass;

import com.billybyte.meteorjava.JsonNestedList;
import com.billybyte.meteorjava.MeteorValidator;
import com.billybyte.meteorjava.staticmethods.Utils;

public class RunJnestCreateForTestClass {
	
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

		
		List<String[]> levelList = Utils.getCSVData(RunJnestCreateForTestClass.class,"testPossNames.csv"); 
		List<String> levelNames = new ArrayList<String>();
		levelNames.add("myLastName");
		levelNames.add("myFirstName");
		JsonNestedList jnest = 
				JsonNestedList.buildJsonNestedList(levelList, levelNames);
		List<List> unwound = JsonNestedList.unwind(jnest, null,null);
		for(List l : unwound){
			String s = "";
			for(Object o : l){
				s += o.toString()+",";
			}
			System.out.println(s.substring(0,s.length()-1));
		}
		
		// TO DO   test this code
		MeteorValidator mvTestClass = new MeteorValidator(
				TestClass.class,
				jnest);
		mvTestClass.sendValidator(
				meteorUrl, meteorPort, adminEmail, adminPass);
	}
}
