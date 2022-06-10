package org.openxava.converters;

import java.io.*;

/**
 * This converters must to have properties for fill it before
 * call to <tt>toJava</tt> or <tt>toDB</tt>. <p>
 * 
 * @author Javier Paniza
 */
public interface IMultipleConverter extends Serializable {
	
	/**
	 * First it's required to set value for properties (with data in DB format),
	 * and after call to <tt>toJava</tt> to obtain a Java object created from
	 * this proprerties. <p> 
	 */
	Object toJava() throws ConversionException;
	
	/**
	 * First call to this method sending to it the java object that you
	 * wish to split, and after you can obtain the splited object acceding
	 * to properties. 
	 */	
	void toDB(Object objetoJava) throws ConversionException;

}
