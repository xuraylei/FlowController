package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

public class FCActionControl extends FCAction implements Cloneable{
	
	byte actionType;
	IPv4Address ip;
	MacAddress mac;
	
	final static byte ALLOW = 0x01;
	final static byte DENY = 0x02;
	final static byte REDIRECT = 0x03;
	final static byte MIRROR = 0x04;
	final static byte QUARANTINE = 0x05;
	final static byte REPORT = 0x06;
	
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
	
	public FCActionControl setFlowEntry(byte flow){
	
		
		return this;
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
