package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.billybyte.meteorjava.staticmethods.Utils;

public class MeteorValidator extends MeteorBaseListItem{
	// static array of MeteorColumnModel for dummy table mode to 
	//   be used in sendValidator.
	private static final MeteorColumnModel[] DUMMY_TABLECOLUMN_ARRAY = 
		{};
	 

	
	
	// the _id field should be the tableName
	private final 	String classNameOfDataToBeValidated;
	private final JsonNestedList jnest;
	public MeteorValidator(
			Class<?> classOfDataToBeValidated,
			JsonNestedList jnest) {
		super(MeteorValidator.class.getSimpleName(),null);
		this.jnest = jnest;
		this.classNameOfDataToBeValidated = classOfDataToBeValidated.getCanonicalName();
	}


	public JsonNestedList getJnest() {
		return jnest;
	}

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
			String error = null;
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


	public String getClassNameOfDataToBeValidated() {
		return classNameOfDataToBeValidated;
	}

}
