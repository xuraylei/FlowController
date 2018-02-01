package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FCActionTrigger  extends FCAction implements Cloneable{
	
	final static byte IMMEDIATE = 0x01;
	final static byte PERIODIC = 0x02;
	
	protected byte triggerType;
	protected int interval;	//in ms
	
	public FCActionTrigger()  {
		super.setType(FCActionType.TRIGGER);
	}
	
	public FCActionTrigger(byte type){
		super.setType(FCActionType.TRIGGER);
		this.triggerType = type;
	}
	
	public FCActionTrigger(byte type, int i){
		super.setType(FCActionType.TRIGGER);
		this.triggerType = type;
		this.interval = i;
	}
	
	public FCActionTrigger setTriggerType(byte type){
		this.triggerType = type;
		
		return this;
	}
	
	public FCActionTrigger setInterval(byte i){
		this.interval = i;
		
		return this;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
		
		out.write(super.serialize());
		out.write(this.triggerType);
		out.write(this.interval);
	   
		return out.toByteArray();
	}
}
