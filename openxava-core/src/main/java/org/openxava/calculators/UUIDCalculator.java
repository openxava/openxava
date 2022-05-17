package org.openxava.calculators;

import java.net.*;
import java.security.*;



import org.openxava.util.*;


/**
 * It generate a Universal Unic IDentifier of 32 alphanumeric characters. <p>
 * 
 * @author Javier Paniza
 */
public class UUIDCalculator implements IModelCalculator {
	
	private Object model;
	private boolean lowerCase = false;
	

	public Object calculate() throws Exception {		
		InetAddress inet = InetAddress.getLocalHost();
		byte [] bytes = inet.getAddress();
		String hexInetAddress = hexFormat(bytes);
		String thisHashCode = hexFormat(System.identityHashCode(model));
		String mid = hexInetAddress + thisHashCode;
		SecureRandom seeder = new SecureRandom();
	 	int node = seeder.nextInt();	 	
		long timeNow = System.currentTimeMillis();
		int timeLow = (int) timeNow & 0xFFFFFFFF;
		String r = (hexFormat(timeNow) + mid + hexFormat(node));		
		return isLowerCase()?r:r.toUpperCase();	 		 	
	}
	
	public String hexFormat(long n) {
		return Strings.fix(Long.toHexString(n), 8, Align.RIGHT,'0');
	}
		
	public String hexFormat(byte[] number) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < number.length; i++) {
			String h = Strings.fix(Integer.toHexString(number[i]), 2, Align.RIGHT, '0');						
			result.append(h);			
		}
		return result.toString();
	}

	public void setModel(Object object) {
		model = object;
	}
	
	public boolean isLowerCase() {
		return lowerCase;
	}

	public void setLowerCase(boolean b) {
		lowerCase = b;
	}

}
