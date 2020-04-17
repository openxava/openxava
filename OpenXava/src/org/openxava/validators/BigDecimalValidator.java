package org.openxava.validators;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.util.Messages;
import org.openxava.util.Strings;

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
        if (bigDecimal.signum() == 0) return;
        
        int decimalLength = bigDecimal.scale();
        int integerLength = bigDecimal.toBigInteger().toString().length();

        if (integerLength > getMaximumIntegerDigits()) {
        	int maximumValue = new Integer("1" + Strings.repeat(maximumIntegerDigits, "0")).intValue();
        	errors.add("greater_than_the_awaited", propertyName, modelName, String.valueOf(maximumValue));
        }
        if (decimalLength > getMaximumFractionDigits()) {
        	errors.add("greater_number_fraction", String.valueOf(getMaximumFractionDigits()), String.valueOf(decimalLength));
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
