package com.billybyte.meteorjava;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.billybyte.meteorjava.staticmethods.Utils;

public class MeteorTableModel {
/**
 * 	Several constructors:
 * first construct some columns using the 
 		var c1 = new tableColumnClass('myFirstName','myFirstName','myFirstName');
		var c2 = new tableColumnClass('myLastName','myLastName','myLastName');
		var c3 = new tableColumnClass('myDouble','myDouble','myDouble',['myDouble',['myDouble']]);
		var m1 = new masterTableClass('misc.TestClass',[c1,c2,c3]);

 * 
 */
	private final String _id;
	private final String displayTableName;
	private final String collectionName;
	private final MeteorColumnModel[] tableColumnClassArr;

	public MeteorTableModel(Class<?> classOfTable,String displayTableName,
			String collectionName,MeteorColumnModel[] tableColumnClassArr) {
		super();
		this._id = classOfTable.getName();
		this.displayTableName = displayTableName;
		this.collectionName = collectionName;
		this.tableColumnClassArr = tableColumnClassArr;
	}

	public MeteorTableModel(Class<?> classOfTable,MeteorColumnModel[] tableColumnClassArr){
		this(classOfTable, classOfTable.getName(), classOfTable.getName(), tableColumnClassArr);
	}


	public MeteorColumnModel[] getTableColumnClassArr() {
		return tableColumnClassArr;
	}

	public String getId() {
		return _id;
	}

	public String getDisplayTableName() {
		return displayTableName;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public static final void sendMeteorTableModels(
			String meteorUrl,
			Integer meteorPort,
			String adminEmail,
			String adminPass,
			MeteorTableModel tm){

		List<MeteorTableModel> mtmList = 
				new ArrayList<MeteorTableModel>();
		mtmList.add(tm);

		MeteorListSendReceive<MeteorTableModel> mlsrTableModel=null;
		try {
			mlsrTableModel = new MeteorListSendReceive<MeteorTableModel>(100, 
					MeteorTableModel.class, meteorUrl, meteorPort, 
					adminEmail,adminPass,"", "", "tester");
		} catch (URISyntaxException e) {
			throw Utils.IllState(e);
		}
		// the mtmList only has one object, but I put in an iteration
		 //  for future use.
		for(MeteorTableModel mtm:mtmList){
			String tableId = mtm.getId();
			String error = mlsrTableModel.deleteMeteorTableModel( tableId);
			if(error!=null && error.compareTo("0")!=0){
				Utils.prtObErrMess(MeteorTableModel.class, error);
			}
		}
		try {
			mlsrTableModel.sendTableModelList(mtmList);
		} catch (InterruptedException e) {
			throw Utils.IllState(e);
		}
	}
	
	public <M> void toXml(Class<M> classOfM,String xmlPath) throws IOException{
		String path = xmlPath==null ? classOfM.getSimpleName()+"TableModel.xml" : xmlPath;
		Utils.writeToXml(this,path);
	}
	
	public static <M> MeteorTableModel fromXml(Class<M> classOfM,String xmlPath){
		String path = xmlPath==null ? classOfM.getSimpleName()+"TableModel.xml" : xmlPath;
		return Utils.getFromXml(MeteorTableModel.class, null, path);
	}
	
}
