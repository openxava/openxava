package org.openxava.util;




/**
 * Wraps a byte array to allow store photos in
 * a database with no support to <code>byte []</code> o
 * BLOBs, but yes <code>java.lang.Object</code>. <p>
 * 
 * Serialize object for long term persistence is discourage,
 * hence use this class only in extreme cases.<br> 
 * 
 * @author Javier Paniza
 */
public class Photo implements java.io.Serializable {

	public byte[] data;
	
	

	public Photo(byte[] data) {
		this.data = data;
	}

}