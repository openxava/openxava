package org.openxava.validators;

import org.apache.commons.validator.routines.*;
import org.openxava.util.*;

/**
 * 
 * @author Janesh Kodikara
 */

public class URLValidator implements IPropertyValidator {

    private UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

    public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {

        if (value == null || value.toString().length() ==0 ) return;

        if (! urlValidator.isValid(value.toString())) {
           errors.add("url_validation_error", propertyName);
        }
    }

}



