package com.billybyte.meteorjava;

public class MeteorColumnModel {
// json version
//	var c3 = new tableColumnClass('myDouble','myDouble','myDouble',['myDouble',['myDouble']]);
	private final String colName;
	private final String nameInDoc;
	private final String displayName;
	private final String[] totallingDef;

	public MeteorColumnModel(String colName, String nameInDoc,
			String displayName, String[] totallingDef) {
		super();
		this.colName = colName;
		this.nameInDoc = nameInDoc;
		this.displayName = displayName;
		if(totallingDef==null){
			this.totallingDef = null;
		}else{
			this.totallingDef =  totallingDef;
		}
	}
	public String getColName() {
		return colName;
	}
	public String getNameInDoc() {
		return nameInDoc;
	}
	public String getDisplayName() {
		return displayName;
	}
	public Object geTtotallingDef() {
		return totallingDef;
	}
	
}
