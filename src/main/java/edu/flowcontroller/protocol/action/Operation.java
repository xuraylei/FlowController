package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Operation {
	protected byte operation_type;
	protected byte value1;
		
	public Operation(){
		this.operation_type = OperationType.UNKOWNN;
		this.value1 = -1;
	}
		
		
	public Operation(byte op_type,byte v1){
		this.operation_type = op_type;
		this.value1 = v1;
	}
	
	public static int getLength(){
		// kevin, Byte.SIZE returns the number of bits
		// lei's code
		//return Byte.SIZE + Byte.SIZE;
		return Byte.SIZE/8 + Byte.SIZE/8;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
		
		out.write(operation_type);
		out.write(value1);

				
		return out.toByteArray();
		}
}