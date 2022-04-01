package org.openxava.converters;

import java.io.*;

/**
 * @author Javier Paniza
 */
public interface IConverter extends Serializable {
	
	Object toJava(Object o) throws ConversionException;
	
	Object toDB(Object o) throws ConversionException;

}
