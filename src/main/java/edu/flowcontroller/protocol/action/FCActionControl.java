package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

public class FCActionControl extends FCAction implements Cloneable{
	
	byte actionType;
	IPv4Address ip;
	MacAddress mac;
	
	public final static byte ALLOW = 1;
	public final static byte DENY = 2;
	public final static byte REDIRECT = 4;
	public final static byte MIRROR = 8;
	public final static byte QUARANTINE = 16;
	public final static byte REPORT = 32;
	
	public FCActionControl()  {
		super.setType(FCActionType.CONTROL);
	}
	
	public FCActionControl(byte at){
		super.setType(FCActionType.CONTROL);
		
		this.actionType = at;
		
	}
	
	public FCActionControl(byte at, IPv4Address ip, MacAddress mac){
		super.setType(FCActionType.CONTROL);
		
		this.actionType = at;
		this.ip = ip;
		this.mac = mac;
		
	}
	
	public void setActionType(byte t){
		if ((actionType & t) == 0) {	// the type is unset, then set it
			actionType += t;
		}
	}
	
	public void setRedirectionDevice( IPv4Address ip, MacAddress mac){

		this.ip = ip;
		this.mac = mac;
		
	}
	
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
		
		out.write(super.serialize());
		out.write(this.actionType);
		out.write(this.ip.getBytes());
		out.write(this.mac.getBytes());
	   
		return out.toByteArray();
	}
}
