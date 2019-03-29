package org.openxava.converters;



import org.openxava.util.*;


/**
 * In java a valid-values and in database a letter corresponding
 * to the position in the string that it's in property 'letters'. <p>
 * 
 * @author Javier Paniza
 */
public class ValidValuesLetterConverter implements IConverter {

	private String letters;
	

	public Object toDB(Object o) throws ConversionException {	
		if (o == null) return "";
		if (!(o instanceof Integer)) {		
			throw new ConversionException("conversion_db_integer_expected");
		}
		assertLetters();
		int value = ((Integer) o).intValue();
		if (value == 0) return "";
		try {
			return String.valueOf(getLetters().charAt (value - 1));		
		}
		catch (IndexOutOfBoundsException ex) {
			throw new ConversionException("conversion_db_valid_values", new Integer(value), getLetters());
		}
	}
	
	public Object toJava(Object o) throws ConversionException {				
		if (o == null) return new Integer(0);
		if (!(o instanceof String)) {		
			throw new ConversionException("conversion_java_string_expected");
		}
		assertLetters();
		String value  = (String) o;
		if (Is.emptyString(value)) return new Integer(0);
		int idx = getLetters().indexOf(value);
		if (idx < 0) {
			throw new ConversionException("conversion_java_valid_values", value,  getLetters());
		}
		return new Integer(idx + 1);
	}
	
	private void assertLetters() throws ConversionException {
		if (Is.emptyString(getLetters())) {
			throw new ConversionException("conversion_valid_values_letters_required", getClass().getName());
		}
	}

	public String getLetters() {
		return letters;
	}

	public void setLetters(String string) {
		letters = string;
	}

}
