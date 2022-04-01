package org.openxava.model.transients;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 *
 */

@Embeddable
public class ImportColumn {
	
	private String nameInApp;
	
	@Stereotype("LABEL")
	private String headerInFile;
	
	@Stereotype("LABEL")
	private String sampleContent1;
	
	@Stereotype("LABEL")
	private String sampleContent2;

	public String getNameInApp() {
		return nameInApp;
	}

	public void setNameInApp(String nameInApp) {
		this.nameInApp = nameInApp;
	}

	public String getSampleContent1() {
		return sampleContent1;
	}

	public void setSampleContent1(String sampleContent1) {
		this.sampleContent1 = sampleContent1;
	}

	public String getSampleContent2() {
		return sampleContent2;
	}

	public void setSampleContent2(String sampleContent2) {
		this.sampleContent2 = sampleContent2;
	}

	public String getHeaderInFile() {
		return headerInFile;
	}

	public void setHeaderInFile(String headerInFile) {
		this.headerInFile = headerInFile;
	}


	
}
