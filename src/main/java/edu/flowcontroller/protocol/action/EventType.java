package edu.flowcontroller.protocol.action;

public class EventType {
	
	public static final byte PORT_STATUS = 0x01;
	public static final byte LOCATION = 0x02;
	public static final byte TIME = 0x03;
	public static final byte USER_ROLE = 0x04;
	public static final byte DEVICE_MOD = 0x05;
	public static final byte CONTROLL_STATE = 0x06;
	public static final byte PACKET_COUNT = 0x07;
	public static final byte FILE_COUNT = 0x08;
	public static final byte MEMORY_COUNT = 0x09;
	public static final byte IPC_COUNT = 0x0a;
	public static final byte PACKET_RATE = 0x0b;
	public static final byte FILE_RATE = 0x0c;
	public static final byte MEMORY_RATE = 0x0d;
	public static final byte IPC_RATE = 0x0e;
}
