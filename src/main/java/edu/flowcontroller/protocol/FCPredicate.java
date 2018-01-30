package edu.flowcontroller.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import edu.flowcontroller.protocol.action.Operation;


//TODO: add any numbers of actions
public class FCPredicate {
	byte size;
	byte event;
	protected Operation[] operations;
	
	
	public FCPredicate (byte e){
		this.size = (byte) this.getLength();
		this.event = e;
		this.operations = new Operation[2];
	}
	
	public static int getLength(){
		return Integer.SIZE + Byte.SIZE + 2*Operation.getLength(); 
		//return 15;
	}
	
	public FCPredicate addOperation(byte event, Operation operation1, Operation operation2){
		this.event = event;
		this.operations[0] = operation1;
		this.operations[1] = operation2;
		
		return this;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(size);
		out.write(event);
		for (Operation op : this.operations){
			 out.write(op.serialize());
			
		}			
		return out.toByteArray();
		}
}

