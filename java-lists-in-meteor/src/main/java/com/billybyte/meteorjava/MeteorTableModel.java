package com.billybyte.meteorjava;

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

//	public MeteorTableModel(String nameOfTable,MeteorColumnModel[] tableColumnClassArr) throws ClassNotFoundException{
////		this(Class.forName(classNameOfTable), classNameOfTable, classNameOfTable, tableColumnClassArr);
//		this._id = nameOfTable;
//		this.displayTableName = nameOfTable;
//		this.collectionName = nameOfTable;
//		this.tableColumnClassArr = tableColumnClassArr;
//	}

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
	
	
}
