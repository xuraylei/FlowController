package edu.flowcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.projectfloodlight.openflow.protocol.match.Match;

class FCMatch implements Cloneable, Serializable {
	  final static byte MAXIMUM_FILE_NAME = 64; 
	  
	  final static byte HOST_MATCH_ONLY = 1;
	  final static byte NETWORK_MATCH_ONLY = 2; 
	  final static byte HOST_NETWORK_MATCH = 3;
	  
	  final static byte FILE_OP_WRITE = 2;
	  final static byte FILE_OP_READ = 4;
	    
	  //mask for match fields
	  byte mask;
	  
	  //fields for host match
	  protected char[] fileName;
	  protected byte fileOP;
	  protected int fd;
	  
	  //fields for network match
	  int ipSrc;
	  int ipDst;
	  byte protocol;
	  short portSrc;
	  short portDst;

	  
	  public FCMatch(byte m) {
		  fileName = new char[MAXIMUM_FILE_NAME];
		  
		  this.mask = m;
	  }
	  
	  public void setHostMatch(String f, byte fop, int des) {
		  if (f.length() <= MAXIMUM_FILE_NAME) {
			  this.fileName = f.toCharArray();
		  	 
		  }
		  //TODO: handle file name over-size cases
		  
		  
		  this.fileOP = fop;
		  this.fd = des;
	  }
	  
	  public void setNetMatch(int srcIP, int dstIP, byte p, short srcPort, short dstPort) {
		  this.ipSrc = srcIP;
		  this.ipDst = dstIP;
		  this.protocol = p;
		  this.portSrc = srcPort;
		  this.portDst = dstPort;
		  
	  }
	  
	  public  byte[] serialize() throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			out.write(mask);
			
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
			
			return out.toByteArray();
		}
		
	  public int getLength() {
		  return fileName.length + 2 * Byte.SIZE + 3 * Integer.SIZE + 2 * Short.SIZE;
	  }
	  	  
}

