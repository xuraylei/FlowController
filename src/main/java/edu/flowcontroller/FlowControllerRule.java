package edu.flowcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

import edu.flowcontroller.protocol.FCPredicate;
import edu.flowcontroller.protocol.action.FCAction;
import edu.flowcontroller.protocol.action.Operation;

class AppGroup{
	byte appGroup;
	final static byte TRUST = 0x01;
	final static byte UNKNOWN = 0x02;
	final static byte THIRDPARTY = 0x03;
	
	public AppGroup(byte ag){
		this.appGroup = ag;
	}
	
	public byte getAppGroup() {
		return this.appGroup;
	}
	
	public int getLength() {
		return Byte.SIZE;
	}
}

class DeviceGroup{
	byte deviceGroup;
	
	final static byte TRUST = 0x01;
	final static byte UNKNOWN = 0x02;
	final static byte UNAUTHORIZED = 0x03;
	
	public DeviceGroup(byte dg){
		this.deviceGroup = dg;
	}
	
	public byte getDeviceGroup() {
		return this.deviceGroup;
	}
	
	public int getLength() {
		return Byte.SIZE;
	}
	
}

class DeviceID{
	IPv4Address ip;
	MacAddress mac;
	
	public DeviceID(MacAddress m, IPv4Address i) {
		this.mac = m;
		this.ip = i;
	}
	
	public int getLength() {
		return ip.getLength() + mac.getLength();
	}
}

class Object{
	//ANY means wildcards for app/process name
	final static String ANY = "any";
	final static String NET = "net";
	final static String PROCESS = "process";
	final static String FILE = "file";
	final static String MEM = "mem";
	
	char[] app = new char[10]; // the size for app name is 10 bytes
	DeviceID device;  
	
	AppGroup appGroup;
	DeviceGroup deviceGroup;
	
	public int getLength() {
		return app.length + device.getLength() + appGroup.getLength() + deviceGroup.getLength();
	}
	
	public Object(String appName, IPv4Address deviceIP, MacAddress deviceMAC, AppGroup appGroup, DeviceGroup deviceGroup) {
		this.app = appName.toCharArray();
		this.device = new DeviceID(deviceMAC, deviceIP);
		
		this.appGroup = appGroup;
		this.deviceGroup = deviceGroup;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] appB = new byte[this.app.length];
		for (int i = 0; i < appB.length; i++) {
			appB[i] = (byte) app[i];
		}
		out.write(appB);
		out.write(device.ip.getBytes());
		out.write(device.mac.getBytes());
		out.write(appGroup.getAppGroup());
		out.write(deviceGroup.getDeviceGroup());
		return out.toByteArray();
	
	}
}
	
public class FlowControllerRule {
	byte len;
	Object srcObj;
	Object dstObj;
	
	FCMatch match;
	byte numPredicate;
	byte numAction;
	List<FCPredicate> predicates;
	List<FCAction> actions;
	
	public FlowControllerRule(Object src, Object dst, FCMatch m){
		this.srcObj = src;
		this.dstObj = dst;
		this.match = m;
		this.len = (byte) (this.srcObj.getLength() + this.dstObj.getLength() + match.getLength());
		this.predicates = new ArrayList();
		this.actions = new ArrayList();
	}
	
	public FlowControllerRule addPredicate(FCPredicate predicate){
		this.predicates.add(predicate);
		this.len += predicate.getLength();
		return this;
	}
	
	public FlowControllerRule addAction(FCAction action){
		this.actions.add(action);
		this.len += action.getLength();
		return this;
	}
	
	public byte getLength() {
		return this.len;
	}
	
	public  byte[] serialize() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write(srcObj.serialize());
		out.write(dstObj.serialize());
		out.write(match.serialize());
		
		out.write(numPredicate);
		out.write(numAction);
		
		for(FCPredicate p : predicates) {
			out.write(p.serialize());
		}
		
		for(FCAction ac: actions) {
			out.write(ac.serialize());
		}
		
		return out.toByteArray();
	
		
	}
	
	
}

