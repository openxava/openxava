package org.openxava.filters;

/**
 * To filter the parameters value sent to searchs. <p>
 * 
 * Used in tabs (list mode) and in descriptions list. 
 * 
 * @author Javier Paniza
 */
public interface IFilter extends java.io.Serializable {
	
	/**	  
	 * @param o Argument to filter. A object.
	 * @return Argument filtered. Can be a object array.
	 * @throws FilterException Any problem.
	 */
	Object filter(Object o) throws FilterException;
}