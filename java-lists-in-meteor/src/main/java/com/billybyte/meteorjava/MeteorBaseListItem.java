package com.billybyte.meteorjava;

public class MeteorBaseListItem {
	private final String _id;
	private final String userId;
	public MeteorBaseListItem(String _id, String userId) {
		super();
		this._id = _id;
		this.userId = userId;
	}
	public String get_id() {
		return _id;
	}
	public String getUserId() {
		return userId;
	}
	@Override
	public String toString() {
		return _id + ", " + userId;
	}

}
