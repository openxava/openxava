package org.openxava.calculators;





/**
 * It create a strint that concat string1:string2[:string3]:counter. <p>
 * 
 * Default separator is :  
 * 
 * @author Javier Paniza
 */

public class ConcatOidCalculator
	extends ConcatCalculator
	implements IAggregateOidCalculator {
		
	private int contador;
	
	
	public ConcatOidCalculator() {
		setSeparator(":");
	}

	public void setContainer(Object contenedorKey) {
	}

	public void setCounter(int contador) {
		this.contador = contador;
	}
	
	public Object calculate() throws Exception {
		StringBuffer sb = new StringBuffer((String)super.calculate());
		sb.append(getSeparator());
		sb.append(contador);
		return sb.toString();		
	}


}
