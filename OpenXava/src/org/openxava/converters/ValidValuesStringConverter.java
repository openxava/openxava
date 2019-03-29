 package org.openxava.converters;



import org.openxava.util.*;

/**
 * In java a valid-values and in database a string corresponding
 * to the position that it's in property 'words'. 
 * The property words must be a string that contains the chains separating by comma. <p> 
 * 
 * @author Miguel Embuena
 */
public class ValidValuesStringConverter implements IConverter {

	private String words;
	private String chains[];
	

	public Object toDB(Object o) throws ConversionException {
		if (o == null) return "";
		if (!(o instanceof Integer)) {		
			throw new ConversionException("conversion_db_integer_expected");
		}
		assertWords();
		int value = ((Integer) o).intValue();
		if (value == 0) return "";
		chains = getChains();		
		try {
			return chains[value];			
		}
		catch (IndexOutOfBoundsException ex) {
			throw new ConversionException("conversion_db_valid_values", new Integer(value), getWords());
		}
	}
	
	public Object toJava(Object o) throws ConversionException {
		if (o == null) return new Integer(0);
		if (!(o instanceof String)) {		
			throw new ConversionException("conversion_java_string_expected");
		}
		assertWords();
		String value  = (String) o;
		if (Is.emptyString(value)) return new Integer(0);
		chains = getChains();
		for (int chainIndex=1; chainIndex < chains.length; chainIndex++) {
			if (value.equals(chains[chainIndex]))  return new Integer (chainIndex);				 
		}
		
		throw new ConversionException("conversion_java_valid_values", value,  getWords());
	}
	
	private void assertWords() throws ConversionException {
		if (Is.emptyString(getWords())) {
			throw new ConversionException("conversion_valid_values_letters_required", getClass().getName());
		}
	}

	private String[] getChains() {
		 String[] possibleChains;		 
		 int charPosition = 0;
		 int chainIndex = 0;
		 char separator = ',';
		 
		for ( charPosition=0; charPosition < words.length(); charPosition++) {
			if ( words.charAt(charPosition) == separator ) chainIndex++;  
		}				
		
		StringBuffer sb = new StringBuffer();	
		possibleChains = new String[chainIndex + 2];
		possibleChains[0] = "";				
		for (charPosition=0, chainIndex=1; charPosition < words.length(); charPosition++) {
			if ( words.charAt(charPosition) != separator ) {
				sb.append(words.charAt(charPosition));
			}
			else  {				
				possibleChains[chainIndex] = sb.toString();
				sb = new StringBuffer();
				chainIndex++;		
			}
		}				
		possibleChains[chainIndex] = sb.toString();										 
				
		return possibleChains;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

}
