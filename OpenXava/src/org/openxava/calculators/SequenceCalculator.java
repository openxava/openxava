package org.openxava.calculators;




/**
 * To use sequence database id generation. <p>
 * 
 *  An example of use:
 *  <pre>
 *  &lt;property name="id" key="true" type="int" hidden="true"&gt;
 *		&lt;default-value-calculator class="org.openxava.calculators.SequenceCalculator" on-create="true"&gt;
 *			&lt;set property="sequence" value="XAVATEST_SIZE_ID_SEQ"/&gt;
 *		&lt;/default-value-calculator&gt;
 *	&lt;/property&gt;
 *  </pre>
 *  
 * It does not work with EJB2. It works with Hibernate and EJB3 JPA.
 *  
 * @author Javier Paniza
 */

public class SequenceCalculator implements IHibernateIdGeneratorCalculator, ICalculator {
	
	private String sequence;
	

	public String hbmGeneratorCode() {		
		StringBuffer code = new StringBuffer();
		code.append("<generator class='sequence'>\n");
		code.append("\t\t\t\t<param name='sequence_name'>"); 
		code.append(getSequence());
		code.append("</param>\n");
		code.append("\t\t\t</generator>");
		return code.toString();
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Object calculate() throws Exception { 
		return null;
	}

}
