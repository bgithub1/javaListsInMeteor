package com.billybyte.meteorjava;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.billybyte.meteorjava.staticmethods.Utils;

public class MeteorValidator extends MeteorBaseListItem{
	// static array of MeteorColumnModel for dummy table mode to 
	//   be used in sendValidator.
	private static final MeteorColumnModel[] DUMMY_TABLECOLUMN_ARRAY = 
		{};
	 
	private static final String FREEFIELD_SEPARATOR = "::";
	public final static String REGEX_DECIMAL_MASK = "^[+-]{0,1}[0-9]{0,}\\.{0,1}[0-9]{0,}$";
	public final static String REGEX_INTEGER_MASK = "^[+-]{0,1}[0-9]{0,}$";

	
	// this field is really of duplicate of the _id (for later delvelopment)	
	@SuppressWarnings("rawtypes")
	private final Map jnestMap;
	// this list will be used by the meteor client to control how
	//  the client iterates it's presentation of drop downs
	private final String tableName;
	private final List<String> depOrderList;
	@SuppressWarnings("rawtypes")
	private final Map independPossChoicesMap ;
	private final List<String> freeFieldList;
	private final Map<String,String> regexFieldList;
	
	/**
	 * Constructor for MeteorValidator
	 * @param classOfDataToBeValidated
	 * @param jnestMap
	 * @param dependentFieldOrderList
	 * @param independentFields
	 * @param freeFields
	 */
	@SuppressWarnings("rawtypes")
	public MeteorValidator(
			Class<?> classOfDataToBeValidated,
			Map jnestMap,
			List<String> dependentFieldOrderList,
			Map<String,List<String>> independentFields,
			List<String> freeFields,
			Map<String,String> regexFieldList) {
		super(classOfDataToBeValidated.getCanonicalName(),null);
		this.jnestMap = jnestMap;
		this.tableName = this.get_id();
		this.depOrderList = dependentFieldOrderList;
		this.independPossChoicesMap = independentFields;
		this.freeFieldList = freeFields;
		this.regexFieldList = regexFieldList;
	}

	/**
	 * Constructor for MeteorValidator
	 * @param classOfDataToBeValidated
	 * @param jnestMap
	 * @param dependentFieldOrderList
	 * @param independentFields
	 * @param freeFields
	 */
	@SuppressWarnings("rawtypes")
	public MeteorValidator(
			Class<?> classOfDataToBeValidated,
			Map jnestMap,
			List<String> dependentFieldOrderList,
			Map<String,List<String>> independentFields,
			List<String> freeFields) {
		this(classOfDataToBeValidated, jnestMap, dependentFieldOrderList, 
				independentFields, freeFields,null);
	}


	/**
	 * 
	 * @param meteorUrl
	 * @param meteorPort
	 * @param adminEmail
	 * @param adminPass
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


	@SuppressWarnings("rawtypes")
	public Map getJnestMap() {
		return jnestMap;
	}


	public String getTableName() {
		return tableName;
	}


	public List<String> getDepOrderList() {
		return depOrderList;
	}


	@SuppressWarnings("rawtypes")
	public Map getIndependPossChoicesMap() {
		return independPossChoicesMap;
	}


	public List<String> getFreeFieldList() {
		return freeFieldList;
	}

	public Map<String, String> getRegexFieldList() {
		return regexFieldList;
	}

	
}
