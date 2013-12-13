package com.billybyte.meteorjava;

public class MeteorLoginToken {
	@SuppressWarnings("unused")
	private String token;
	@SuppressWarnings("unused")
	private Object tokenExpires;
	@SuppressWarnings("unused")
	private String id;
	
	
	
	public MeteorLoginToken(String token, Object tokenExpires, String id) {
		super();
		this.token = token;
		this.tokenExpires = tokenExpires;
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Object getTokenExpires() {
		return tokenExpires;
	}
	public void setTokenExpires(Object tokenExpires) {
		this.tokenExpires = tokenExpires;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return token + ", " + tokenExpires + ", " + id;
	}

}
