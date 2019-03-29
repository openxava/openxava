
import org.openxava.util.*;
import org.openxava.component.*;
import org.openxava.model.meta.*;

import java.io.*;
import java.util.*;

/**
 * @author Javier Paniza
 */
abstract public class CodeGenerator {
	
	private String project;
	private String packageName;
	private String javaPackage;
	private String domain;
	private String unqualifiedPackage;
	private String modelPackage;
	private Properties dnas;
	private Properties packages = new Properties();
	private boolean dnasChanged = false;
	private Map toChangeInDNAString;

	
	/**
	 * Overwrite this for code generation by component
	 */
	abstract protected void generate(MetaComponent component, String componentsPath, String file) throws Exception;

	/**
	 * Overwrite this (with super) for generation common to all components
	 */	
	protected void generate(String componentsPath, String [] components) throws Exception {
		// Generate code			
		for (int i = 0; i < components.length; i++) {
			String file = components[i];				
			if (file.endsWith(".xml") || file.endsWith(".XML") || file.endsWith("Xml")) {
				try {					
					generate(componentsPath, file);											
				}
				catch (Exception ex) {					
					ex.printStackTrace();
					System.out.println(XavaResources.getString("generation_xdoclet_code_error", file));
				}
			}								
		}		
	}
	
	private void generate(String componentsPath, String file) throws Exception {
		String componentName = file.substring(0, file.length() - 4);		
		MetaComponent component = MetaComponent.get(componentName);		
		int currentDNA = getCurrentDNA(component);  		
		if (currentDNA != 0 && currentDNA == getOldDNA(component)) return;		
		setDNA(component, currentDNA);
		generate(component, componentsPath, file); 		
	}
	
	protected String getPackagesFile() {
		return "packages.properties";
	}
	
	protected String getDNAFile() {
		return "dnas.properties";
	}
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUnqualifiedPackage() {
		return unqualifiedPackage;
	}

	public void setUnqualifiedPackage(String unqualifiedPackage) {
		this.unqualifiedPackage = unqualifiedPackage;
	}
	
	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}
		
	public String getPackageName() {
		if (packageName == null) {
			packageName = getDomain() + "/" + getUnqualifiedPackage() + "/" + getModelPackage();
		}
		return packageName;
	}

	protected String getJavaPackage() {
		if (javaPackage == null) {
			javaPackage = Strings.change(getPackageName(), "/", ".");
		}
		return javaPackage;
	}
	
	protected void run() throws Exception {		 
		try {	
			XavaPreferences.getInstance().setDuplicateComponentWarnings(false);
			Locale.setDefault(new Locale("en")); 
			String componentsPath = "../" + getProject() + "/components";			
			File dirComponents = new File(componentsPath);			
			String [] components = dirComponents.list();			
			// First load all components
			for (int i = 0; i < components.length; i++) {
				String file = components[i];				
				if (file.endsWith(".xml") || file.endsWith(".XML") || file.endsWith("Xml")) {					
					load(file);
				}								
			}
			
			generate(componentsPath, components);
			
			savePackages(); 
			saveDNAs();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(XavaResources.getString("generation_project_code_error", project));
		}
	}
	
	
	private void load(String file) {
		try {
			String componentName = file.substring(0, file.length() - 4);
			MetaComponent.get(componentName).setPackageName(getJavaPackage());
			packages.put(componentName, getJavaPackage());
		}
		catch (XavaException ex) {
			System.err.println(XavaResources.getString("loading_component_warning", file));			
		}		
	}
	
	private void setDNA(MetaComponent componente, int dna) {
		getDNAs().put(componente.getName(), String.valueOf(dna));
		dnasChanged = true;
	}
	
	private int getOldDNA(MetaComponent component) { 
		String value = getDNAs().getProperty(component.getName());
		if (Is.emptyString(value)) return 0;
		return Integer.parseInt(value);
	}
	
	private Properties getDNAs() { 
		if (dnas == null) {
			dnas = new Properties();
			try {				
				File file = new File("../" + getProject() + "/gen-src-xava/" + getDNAFile());
				if (file.exists()) {				
					FileInputStream is = new FileInputStream(file);					
					dnas.load(is);					
				}
			}
			catch (Exception ex) {
				System.err.println(XavaResources.getString("read_dna_error"));
			}
		}
		return dnas;
	}
	
	private void saveDNAs() {
		if (!dnasChanged) return;
		try {
			FileOutputStream os = new FileOutputStream("../" + getProject() + "/gen-src-xava/" + getDNAFile());
			getDNAs().store(os, "");
		}
		catch (Exception ex) {
			System.err.println(XavaResources.getString("generator.save_dna_error"));
		}
	}
	
	private void savePackages() {		
		try {
			FileOutputStream os = new FileOutputStream("../" + getProject() + "/gen-src-xava/" + getPackagesFile());
			packages.put("package.domain." + getUnqualifiedPackage(), getDomain());
			packages.put("package.model." + getUnqualifiedPackage(), getModelPackage());			
			packages.store(os, "");			
		}
		catch (Exception ex) {
			System.err.println(XavaResources.getString("generator.save_package_error"));
		}
	}	
	
	
	private int getCurrentDNA(MetaComponent component) {
		try {
			String componentPath = "../" + getProject() + "/components/" + component.getName() + ".xml";		
			String xmlText = Resources.loadAsString(componentPath);
			boolean spanish = xmlText.indexOf("<!DOCTYPE componente") >= 0;
			String entity = spanish?"<entidad":"<entity";
			String view = spanish?"<vista":"<view";
			String mapping = spanish?"<mapeo":"<entity-mapping";
			int initIdxModel = xmlText.indexOf(entity);
			int endIdxModel = xmlText.indexOf(view);
			if (endIdxModel < 0) endIdxModel = xmlText.indexOf("<tab");
			if (endIdxModel < 0) endIdxModel = xmlText.indexOf(mapping);
			String textModel = xmlText.substring(initIdxModel, endIdxModel);				
			int iniIdxMapping = xmlText.indexOf(mapping);
			String textMapping = xmlText.substring(iniIdxMapping);
			String textDNA = Strings.change( textModel + textMapping, getToChangeInDNAString());
			return textDNA.hashCode();
		}
		catch (Exception ex) {			
			System.err.println(XavaResources.getString("component_dna_warning", component.getName()));
			return 0;
		}
	}
	
	private Map getToChangeInDNAString() {
		if (toChangeInDNAString == null) {
			toChangeInDNAString = new HashMap();
			toChangeInDNAString.put(" ", "");
			toChangeInDNAString.put("\t", "");
		}
		return toChangeInDNAString;
	}
				
				
	
	public String toDirPackage(String paquete) {
		return Strings.change(paquete, ".", "/");
	}	

}
