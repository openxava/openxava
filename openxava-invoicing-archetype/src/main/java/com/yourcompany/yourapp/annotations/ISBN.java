package com.yourcompany.yourapp.annotations; // In 'annotations' package

import java.lang.annotation.*;

import javax.validation.*;
 
@Constraint(validatedBy = com.yourcompany.invoicing.validators.ISBNValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ISBN { // A regular Java annotation definition
 
	boolean search() default true; // To (de)activate web search on validate
	
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
    String message() default "isbn_invalid"; // Message id from i18n file
}