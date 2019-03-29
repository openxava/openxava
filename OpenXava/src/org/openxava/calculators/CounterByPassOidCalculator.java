package org.openxava.calculators;




/**
 * @author Javier Paniza
 */
public class CounterByPassOidCalculator implements IAggregateOidCalculator {

	private int counter;
	

	public void setContainer(Object contenedorKey) {
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Object calculate() throws Exception { 
		return new Integer(counter);		
	}

}
