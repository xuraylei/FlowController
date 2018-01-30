/* merge manage action into control action
package edu.flowcontroller.protocol.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FCActionManage extends FCAction implements Cloneable{
	
	protected byte mod_action;
	protected byte flow_entry;
	
	public FCActionManage()  {
		super.setType(FCActionType.MANAGEMENT);
	}
	
	public FCActionManage(byte flow){
		super.setType(FCActionType.MANAGEMENT);
		this.flow_entry = flow;
	}
	
	public FCActionManage setFlowEntry(byte flow){
		this.flow_entry = flow;
		
		return this;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream( );
		
		out.write(super.serialize());
		out.write(this.flow_entry);
		out.write(this.mod_action);
	   
		return out.toByteArray();
	}
}
*/