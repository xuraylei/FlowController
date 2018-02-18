package edu.flowcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.flowcontroller.protocol.action.FCAction;

public class FlowControllerPolicy {
	
	byte numStage;
	FlowControllerRule[] rules;
	
	FlowControllerPolicy(byte num){
		this.numStage = num;
		rules = new FlowControllerRule[num];
	}
	
	public void addPolicy(byte stage, FlowControllerRule rule) {
		rules[stage-1] = rule;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(numStage);
		
		
		for(FlowControllerRule r : rules) {
			out.write(r.serialize());
		}
		
		return out.toByteArray();
	
		
	}
}
