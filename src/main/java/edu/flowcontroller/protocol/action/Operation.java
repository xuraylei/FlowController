package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Operation {

	protected byte operation_type;
	protected byte value1;
//	protected int value2;
		
	public Operation(){
		this.operation_type = OperationType.UNKOWNN;
		this.value1 = -1;
//		this.value2 = -1;
	}
		
//	public Operation(byte op_type,int v1, int v2){
//		this.operation_type = op_type;
//		this.value1 = v1;
//		this.value2 = v2;
//	}
		
	public Operation(byte op_type,byte v1){
		this.operation_type = op_type;
		this.value1 = v1;
	}
	
	public static int getLength(){
		return Byte.SIZE + Integer.SIZE;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
//		DataOutputStream dos = new DataOutputStream( out );
		
		out.write(operation_type);
		out.write(value1);
//		out.write(value2);

				
		return out.toByteArray();
		}
}