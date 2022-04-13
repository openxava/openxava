package org.openxava.validators;

/**
 * To be implemented by an entity or property validator to get the message from the annotation.
 * 
 * For example, if you define a validator for a property in this way:
 * <pre>
 * @PropertyValidator(value=BookTitleValidator.class, message="{rpg_book_not_allowed}")
 * String title 
 * </pre>
 * If <code>BookTitleValidator</code> implements <code>IWithMessage</code> <i>rpg_book_not_allowed</i> will 
 * be injected calling <code>setMessage()</code>.
 * 
 * @since 4.6.1
 * @author Javier Paniza
 */
public interface IWithMessage {
	
	void setMessage(String message);

}
