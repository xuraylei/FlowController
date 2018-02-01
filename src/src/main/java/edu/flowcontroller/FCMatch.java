package edu.flowcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.projectfloodlight.openflow.protocol.match.Match;

class FCMatch implements Cloneable, Serializable {
	 final static int MAXIMUM_FILE_NAME=20; 
	  //public final HostMatchFields id;
	  
	  protected char[] fileName;
	  
	  public FCMatch() {
		  fileName = new char[MAXIMUM_FILE_NAME];
	  }
	  
	  public FCMatch setHostName(String f) {
		  if (f.length() <= MAXIMUM_FILE_NAME) {
			  this.fileName = f.toCharArray();
		  	 
		  }
		  //TODO: handle file name over-size cases
		  return this;
	  }
	  
	  public String getHostName() {
		  return this.fileName.toString();
	  }
	  
	  public  byte[] serialize() throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] appB = new byte[this.fileName.length];
			for (int i = 0; i < appB.length; i++) {
				appB[i] = (byte) fileName[i];
			}
			out.write(appB);
			return out.toByteArray();
		
			
		}
		
	  public int getLength() {
		  return fileName.length;
	  }
	  
	  
}

//do not need network match any more
/*
public class FCMatch{
	//Match networkMatch;
	
	HostMatch hostMatch;
	
	public FCMatch(Match nm, HostMatch hm) {
		this.networkMatch = nm;
		this.hostMatch = hm;
	}
	}
*/

