package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.billybyte.meteorjava.staticmethods.Utils;

public class MeteorValidator extends MeteorBaseListItem{
	// static array of MeteorColumnModel for dummy table mode to 
	//   be used in sendValidator.
	private static final MeteorColumnModel[] DUMMY_TABLECOLUMN_ARRAY = 
		{};
	 

	
	// this field is really of duplicate of the _id (for later delvelopment)	
//	private final 	String classNameOfDataToBeValidated;
//	private final JsonNestedList jnest;
	private final Map jnestMap;
	// this list will be used by the meteor client to control how
	//  the client iterates it's presentation of drop downs
//	private final List<String> fieldValidationOrder;
//	private final Map<String,List<String>> independentFields;
//	private final List<String> freeFields;
	private final String tableName;
	private final List<String> depOrderList;
	private final Map independPossChoicesMap ;
	private final List<String> freeFieldList;
/*
	this.tableName = tableName;
	this.jnestMap = jnestMap;
	this. depOrderList = depOrderList;
	this.independPossChoicesMap = independPossChoicesMap;
	this.freeFieldList = freeFieldList;

 * 	
 */
	public MeteorValidator(
			Class<?> classOfDataToBeValidated,
//			JsonNestedList jnest,
			Map jnestMap,
			List<String> fieldValidationOrder,
			Map<String,List<String>> independentFields,
			List<String> freeFields) {
		super(classOfDataToBeValidated.getCanonicalName(),null);
//		this.jnest = jnest;
		this.jnestMap = jnestMap;
//		this.classNameOfDataToBeValidated = classOfDataToBeValidated.getCanonicalName();
//		this.fieldValidationOrder = fieldValidationOrder;
//		this.independentFields = independentFields;
//		this.freeFields = freeFields;
		this.tableName = this.get_id();
		this.depOrderList = fieldValidationOrder;
		this.independPossChoicesMap = independentFields;
		this.freeFieldList = freeFields;
	}


//	public JsonNestedList getJnest() {
//		return jnest;
//	}

	/**
	 * 
	 * @param meteorUrl
	 * @param meteorPort
	 * @param adminEmail
	 * @param adminPass
	 * @param levelList - lists of all possible combinations of fields
	 * @param levelNames - column names of dependent fields
	 */
	public void sendValidator(
			String meteorUrl,
			int meteorPort,
			String adminEmail,
			String adminPass){
		try {
			
			MeteorListSendReceive<MeteorTableModel> mlsrTableModel = 
					new MeteorListSendReceive<MeteorTableModel>(100, 
							MeteorTableModel.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
//			String error = null;
//			// ********** STEP ONE - REMOVE AND THEN RE-ADD DUMMY TABLE MODEL **************
//			// remove, add then remove again, dummy table model
//			error = mlsrTableModel.deleteMeteorTableModel( MeteorValidator.class.getCanonicalName());
//			if(error!=null && error.compareTo("0")!=0){
//				Utils.prtObErrMess(this.getClass(), error);
//			}
			
			// send a dummy table model, just in case
			List<MeteorTableModel> dummyTableModelList = 
					new ArrayList<MeteorTableModel>();
			MeteorTableModel DUMMY_TABLEMODEL=null;
			DUMMY_TABLEMODEL = new MeteorTableModel(MeteorValidator.class, DUMMY_TABLECOLUMN_ARRAY);
			dummyTableModelList.add(DUMMY_TABLEMODEL);
			mlsrTableModel.sendTableModelList(dummyTableModelList);
			
			// ********** STEP TWO - ADD VALIDATOR DATA **************
			MeteorListSendReceive<MeteorValidator> mlsr = 
					new MeteorListSendReceive<MeteorValidator>(100, 
							MeteorValidator.class, meteorUrl, meteorPort, 
							adminEmail,adminPass,"", "", "tester");
			

			List<MeteorValidator> listToSend = new ArrayList<MeteorValidator>();
			listToSend.add(this);
			try {
				String[] result = mlsr.sendList(listToSend);
				Utils.prt(Arrays.toString(result));
				mlsr.disconnect();
			} catch (InterruptedException e) {
				throw Utils.IllState(e);
				
			}
			
//			// ********** STEP THREE - REMOVE DUMMY TABLE MODEL AGAIN **************
//			error = mlsrTableModel.deleteMeteorTableModel( MeteorValidator.class.getCanonicalName());
//			if(error!=null && error.compareTo("0")!=0){
//				Utils.prtObErrMess(this.getClass(), error);
//			}
			mlsrTableModel.disconnect();
			
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


//	public String getClassNameOfDataToBeValidated() {
//		return classNameOfDataToBeValidated;
//	}
//
//
//	public List<String> getFieldValidationOrder() {
//		return fieldValidationOrder;
//	}
//
//
//	public Map<String,List<String>> getIndependentFields() {
//		return independentFields;
//	}
//
//
//	public List<String> getFreeFields() {
//		return freeFields;
//	}


	public Map getJnestMap() {
		return jnestMap;
	}

	
}
