package edu.flowcontroller.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import edu.flowcontroller.protocol.action.Operation;


//TODO: add any numbers of operations
public class FCPredicate {
	byte length;
	byte event;
	protected List<Operation> operations;
	
	
	public FCPredicate (byte e){
		this.length = 2;
		this.event = e;
		this.operations = new ArrayList<>();
	}
	
	public byte getLength(){
		return this.length; 
	}
	
	public void addOperation(Operation op){
		
		this.operations.add(op);
		this.length += op.getLength();
		
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(length);
		out.write(event);
		for (Operation op : this.operations){
			 out.write(op.serialize());
			
		}			
		return out.toByteArray();
		}
}

