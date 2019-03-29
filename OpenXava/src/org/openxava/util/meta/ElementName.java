package org.openxava.util.meta;




/**
 * It is not used to define a name, but as utility to
 * break down. <p> 
 * 
 * @author Javier Paniza
 */
public class ElementName {
	
	private String name;
	private int idxDot;
	private boolean idxDotObtained;
	private String containerName;	
	private String unqualifiedName;

	
	
	public ElementName(String name) {
		this.name = name==null?"":name;
	}
	
	public boolean isQualified() {
		return getIdxDot() >= 0;
	}
	
	public String getContainerName() {
		if (containerName == null) {
			containerName = isQualified()?name.substring(0, idxDot):"";
		}
		return containerName;
	}
	
	public String getUnqualifiedName() {
		if (unqualifiedName == null) {
			unqualifiedName = isQualified()?name.substring(idxDot+1):name;
		}
		return unqualifiedName;		
	}
	
	private int getIdxDot() {
		if (!idxDotObtained) {
			idxDot = name.indexOf('.');
			idxDotObtained = true;
		}
		return idxDot;
	}
	
	public String toString() {
		return name;
	}
	

}

