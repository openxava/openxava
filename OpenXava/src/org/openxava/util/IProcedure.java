package org.openxava.util;

/**
 * Functional interface that does not receive or return any value. <p>
 * 
 * Intended to be used in lambdas.
 * 
 * @since 6.3.2
 * @author Javier Paniza
 */
public interface IProcedure {
	
	void execute() throws Exception;

}
