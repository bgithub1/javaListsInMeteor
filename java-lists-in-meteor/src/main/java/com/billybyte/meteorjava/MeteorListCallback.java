package com.billybyte.meteorjava;


public interface MeteorListCallback<M> {
	public void onMessage(String messageType,String id,M convertedMessage);
}
