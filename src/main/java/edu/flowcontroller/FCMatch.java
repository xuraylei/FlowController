package edu.flowcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

class FCMatch implements Cloneable, Serializable {
	  final static byte MAXIMUM_FILE_NAME = 64; 
	  
	  final static byte HOST_MATCH_ONLY = 1;
	  final static byte NETWORK_MATCH_ONLY = 2; 
	  final static byte HOST_NETWORK_MATCH = 3;
	  
	  final static byte FILE_OP_WRITE = 2;
	  final static byte FILE_OP_WR = 3;
	  final static byte FILE_OP_READ = 4;
	    
	  //mask for match fields
	  byte mask;
	  
	  //fields for host match
	  protected char[] fileName;
	  protected byte fileOP;
	  protected int fd;
	  
	  // lei's code
	  //fields for network match
	  /*
	  int ipSrc;
	  int ipDst;
	  byte protocol;
	  short portSrc;
	  short portDst;
	  */
	// kevin, modify for serialize 
	  IPv4Address ipSrc, ipDst;
	  byte protocol;
	  short portSrc;
	  short portDst;
	  
	  public FCMatch(byte m) {
		  fileName = new char[MAXIMUM_FILE_NAME];
		  
		  this.mask = m;
	  }
	  
	  public void setHostMatch(String f, byte fop, int des) {
		  if (f.length() <= MAXIMUM_FILE_NAME) {
			  // kevin, this statement write only length of a string without \0
			  //		  we should use a fixed length (64 bytes) of app name
			  // lei's code
			  //this.fileName = f.toCharArray();
			  // new code
			  char[] name = f.toCharArray();
			  for(int i=0; i<f.length(); i++)
				  this.fileName[i] = name[i];
		  }
		  //TODO: handle file name over-size cases
		  
		  
		  this.fileOP = fop;
		  this.fd = des;
	  }
	  
	  //public void setNetMatch(int srcIP, int dstIP, byte p, short srcPort, short dstPort) {
	  public void setNetMatch(IPv4Address srcIP, IPv4Address dstIP, byte p, short srcPort, short dstPort) {
		  this.ipSrc = srcIP;
		  this.ipDst = dstIP;
		  this.protocol = p;
		  this.portSrc = srcPort;
		  this.portDst = dstPort;
		  
	  }
	  
	  public  byte[] serialize() throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			out.write(mask);
			// lei's code
			/*
			byte[] appB = new byte[this.fileName.length];
			for (int i = 0; i < appB.length; i++) {
				appB[i] = (byte) fileName[i];
			}
			out.write(appB);
			out.write(fileOP);
			out.write(fd); 
			out.write(ipSrc);
			out.write(ipDst);
			out.write(protocol);
			out.write(portSrc);
			out.write(portDst);
			*/
			
			// kevin, if mask is for FILE (HOST) then only write file match only
			if(mask == HOST_MATCH_ONLY){
				byte[] appB = new byte[this.fileName.length];
				for (int i = 0; i < appB.length; i++) {
					appB[i] = (byte) fileName[i];
				}
				out.write(appB);
				out.write(fileOP);
				// kevin, out.write() writes only a byte or byte stream
				out.write(FlowControllerManager.intToBytes(fd));
			}
			else if(mask == NETWORK_MATCH_ONLY){
				// kevin, out.write() writes only a byte or byte stream
				out.write(ipSrc.getBytes());
				out.write(ipDst.getBytes());
				out.write(protocol);
				// kevin, out.write() writes only a byte or byte stream
				out.write(FlowControllerManager.shortToBytes(portSrc));
				out.write(FlowControllerManager.shortToBytes(portDst));
			}
			else if(mask == HOST_NETWORK_MATCH){
				byte[] appB = new byte[this.fileName.length];
				for (int i = 0; i < appB.length; i++) {
					appB[i] = (byte) fileName[i];
				}
				out.write(appB);
				out.write(fileOP);
				out.write(FlowControllerManager.intToBytes(fd));
				out.write(ipSrc.getBytes());
				out.write(ipDst.getBytes());
				out.write(protocol);
				out.write(FlowControllerManager.shortToBytes(portSrc));
				out.write(FlowControllerManager.shortToBytes(portDst));
			}
	
			
			return out.toByteArray();
		}
		
	  public int getLength() {
		  // kevin, XXX.SIZE returns the number of bits
		  //    	also, fixes the number according to mask
		  // lei's code
		  //return fileName.length + 2 * Byte.SIZE + 3 * Integer.SIZE + 2 * Short.SIZE;
		  
		  if(mask == HOST_MATCH_ONLY){
			  return fileName.length + Byte.SIZE/8 + Integer.SIZE/8;
		  }
		  else if(mask == NETWORK_MATCH_ONLY){
			  return Byte.SIZE/8 + 2 * Integer.SIZE/8 + 2 * Short.SIZE/8;
		  }
		  else { /* if mask == HOST_NETWORK_MATCH */
			  return fileName.length + 2 * Byte.SIZE/8 + 3 * Integer.SIZE/8 + 2 * Short.SIZE/8;
		  }
	  }
	  	  
}

