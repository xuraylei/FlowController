package edu.flowcontroller.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.flowcontroller.FlowControllerRule;
import edu.flowcontroller.protocol.action.FCAction;

public class PolicyInstallationMessage {
	
	byte numStage;
	FlowControllerRule[] policies;
	
	PolicyInstallationMessage(byte num){
		this.numStage = num;
		policies = new FlowControllerRule[num];
	}
	
	public void addPolicy(byte stage, FlowControllerRule fcp) {
		policies[stage] = fcp;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(numStage);
		
		
		for(FlowControllerRule p : policies) {
			out.write(p.serialize());
		}
		
		return out.toByteArray();
	
		
	}
}
