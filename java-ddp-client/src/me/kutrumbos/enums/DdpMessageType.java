package me.kutrumbos.enums;

/**
 * Enum corresponding to the valid types of DDP messages
 * @author Pete
 *
 */
public enum DdpMessageType {
	// types related to establishing a DDP connection
	connect, connected, failed,
	// types related to managing subscription data
	sub, unsub, nosub, error, added, changed, removed, ready,
	// types related to remote method invocation
	method, result, updated;
	
}
