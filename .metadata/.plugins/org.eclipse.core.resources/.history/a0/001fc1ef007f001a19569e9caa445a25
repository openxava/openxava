package org.openxava.validators;

import java.math.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * To validate the size of integer digits and fraction digits parts. <p>
 * 
 * @author Ana Andres
 */
public class BigDecimalValidator implements IPropertyValidator {
	private static Log log = LogFactory.getLog(BigDecimalValidator.class);
	
    private int maximumIntegerDigits = 15;
    private int maximumFractionDigits = 2;
    
    public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
    	if (value == null) return;
        BigDecimal bigDecimal = (BigDecimal) value;
        
        int maximumValue = new Integer("1" + Strings.repeat(maximumIntegerDigits, "0")).intValue();
        //
        StringTokenizer st = new StringTokenizer(bigDecimal.toString(), ".");
        int amount = st.countTokens();
        int integer = 0;
        int fraction = 0;
        
        integer = Integer.parseInt(st.nextToken());
        if (amount > 1){
            fraction = Integer.parseInt(st.nextToken());
        }
        //
        if (integer > maximumValue){
           errors.add("greater_than_the_awaited", propertyName, modelName, String.valueOf(maximumValue)); 
        }
        //
        if(fraction > 0){
            int lengthFraction = String.valueOf(fraction).length();
            if (lengthFraction > maximumFractionDigits){
                errors.add("greater_number_fraction", String.valueOf(getMaximumFractionDigits()), String.valueOf(lengthFraction));
            }
        }
    }    
    
    public int getMaximumIntegerDigits() {
        return maximumIntegerDigits;
    }
    
    public void setMaximumIntegerDigits(int maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }
    
    public int getMaximumFractionDigits() {
        return maximumFractionDigits;
    }
    
    public void setMaximumFractionDigits(int maxinumFractionDigits) {
        this.maximumFractionDigits = maxinumFractionDigits;
    }

}
