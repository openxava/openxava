package org.openxava.converters;



import org.openxava.util.*;


/**
 * A bytes array (<tt>byte []</tt>) that it's stored in DB as
 * a object of type <tt>org.openxava.util.Photo</tt>. <p> 
 *
 * Util for save photos in hypersonic, that it allows neither <tt>byte</tt>,
 * nor <tt>Blob</tt>, but <tt>Object</tt>.<br>
 * 
 * @author Javier Paniza
 */
public class ArrayBytesPhotoConverter implements IConverter {

	
	
	public Object toDB(Object o) throws ConversionException {
		if (o == null) return null;
		if (!(o instanceof byte [])) {		
			throw new ConversionException("conversion_db_byte_array_expected");
		}
		byte [] f = (byte []) o;
		return new Photo(f);
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return null;
		if (!(o instanceof Photo)) {		
			throw new ConversionException("conversion_java_photo_expected");
		}
		return ((Photo) o).data;
	}
		
}
