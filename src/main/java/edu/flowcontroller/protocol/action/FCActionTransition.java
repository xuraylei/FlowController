package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FCActionTransition extends FCAction implements Cloneable{
	
	final static byte TERMINAL = - 0x01;
	
	protected byte nextStage;
	
	public FCActionTransition()  {
		super.setType(FCActionType.TRANSITION);
	}
	
	public FCActionTransition(byte n){
		super.setType(FCActionType.TRANSITION);
		this.nextStage = n;
	}
	
	public FCActionTransition setNextStage(byte n){
		this.nextStage = n;
		
		return this;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
		
		out.write(super.serialize());
		out.write(this.nextStage);
	   
		return out.toByteArray();
	}
}
