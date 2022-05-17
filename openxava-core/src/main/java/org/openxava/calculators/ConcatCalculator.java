package org.openxava.calculators;




/** 
 * String concatenation. <p>
 * 
 * @author Javier Paniza
 */ 
public class ConcatCalculator implements ICalculator {
	
	private String separator="";
	private Object string1="";
	private Object string2="";
	private Object string3=null;
	private Object string4=null;
	private Object string5=null;
	
	
	public Object calculate() throws Exception {		
		StringBuffer r = new StringBuffer(string1==null?"":string1.toString());
		r.append(separator);
		r.append(string2);
		if (string3 != null) {
			r.append(separator);
			r.append(string3);
		}
		if (string4 != null) {
			r.append(separator);
			r.append(string4);
		}
		if (string5 != null) {
			r.append(separator);
			r.append(string5);
		}		
		return r.toString();
	}

	public Object getString1() {
		return string1;
	}

	public Object getString2() {
		return string2;
	}

	public String getSeparator() {
		return separator;
	}

	public void setString1(Object string1) {
		this.string1 = string1;
	}
	
	public void setString2(Object string2) {
		this.string2 = string2;
	}

	public void setSeparator(String separador) {
		this.separator = separador;
	}

	public Object getString3() {
		return string3;
	}

	public void setString3(Object string) {
		string3 = string;
	}

	public Object getString4() {
		return string4;
	}

	public void setString4(Object string4) {
		this.string4 = string4;
	}
	

	public Object getString5() {
		return string5;
	}

	public void setString5(Object string5) {
		this.string5 = string5;
	}
	
	public int getInt1() {
		return Integer.parseInt((String)string1);
	}
	
	public void setInt1(int int1) {
		this.string1 = String.valueOf(int1);
	}

	public int getInt2() {
		return Integer.parseInt((String)string2);
	}
	
	public void setInt2(int int2) {
		this.string2 = String.valueOf(int2);
	}

	public int getInt3() {
		return Integer.parseInt((String)string3);
	}
	
	public void setInt3(int int3) {
		this.string3 = String.valueOf(int3);
	}

	public int getInt4() {
		return Integer.parseInt((String)string4);
	}
	
	public void setInt4(int int4) {
		this.string4 = String.valueOf(int4);
	}
	
	public int getInt5() {
		return Integer.parseInt((String)string5);
	}
	
	public void setInt5(int int5) {
		this.string5 = String.valueOf(int5);
	}
		
}
