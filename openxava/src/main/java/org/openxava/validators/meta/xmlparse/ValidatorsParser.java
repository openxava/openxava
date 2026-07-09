package org.openxava.validators.meta.xmlparse;

import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.openxava.validators.meta.*;
import org.w3c.dom.*;


/**
 * @author Javier Paniza
 * @author Oscar Chamorro
 */
public class ValidatorsParser extends ParserBase {
	
	private final String [] xvalidator_name = { "validator-name", "nombre-validador" };
	private final String [] xvalidator_class = { "validator-class", "clase-validador" };
	private final String [] xrequired_validator = { "required-validator", "validador-requerido" };
	private final String [] xdefault_validator = { "default-validator", "validador-defecto" };
	
	public ValidatorsParser(String urlArchivoXml, int language) {
		super(urlArchivoXml, language);
	}
	
	/**
	* @throws XavaException
	 */
	public static void configureValidators() {
		ValidatorsParser defaultParser = new ValidatorsParser("default-validators.xml", ENGLISH);
		defaultParser.parse();
		ValidatorsParser enParser = new ValidatorsParser("validators.xml", ENGLISH);
		enParser.parse();
		ValidatorsParser esParser = new ValidatorsParser("validadores.xml", ESPANOL);
		esParser.parse();		
	}
	
	/**
	* @throws XavaException
	 */
	private MetaValidator createValidator(Node n) {
		Element el = (Element) n;
		MetaValidator result = new MetaValidator();
		result.setName(el.getAttribute(xname[lang]));
		result.setClassName(el.getAttribute(xclass[lang]));
		return result;
	}
	
	/**
	* @throws XavaException
	 */
	private void addValidatorsFor(Node n, boolean requiredValidators) {
		Element el = (Element) n;
				
		String validatorName = null;
		Element elValidatorName = getElement(el, xvalidator_name[lang]);
		if (elValidatorName != null) {
			validatorName = elValidatorName.getAttribute(xname[lang]);			
		}

		Element elValidatorClass = getElement(el, xvalidator_class[lang]);
		String validatorClass = null;
		if (elValidatorClass != null) {
			validatorClass = elValidatorClass.getAttribute(xclass[lang]);
		}
		
		NodeList l = el.getElementsByTagName(xfor_type[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elForType = (Element) l.item(i);		
			MetaValidatorFor validator = new MetaValidatorFor();
			validator.setValidatorName(validatorName);
			validator.setValidatorClass(validatorClass);
			validator.setForType(elForType.getAttribute(xtype[lang]));
			if (requiredValidators) {
				MetaValidators._addMetaValidatorRequired(validator);
			}
			else {
				MetaValidators._addMetaValidatorDefault(validator);
			}
		}				
		
		l = el.getElementsByTagName(xfor_stereotype[lang]);
		c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elForStereotype = (Element) l.item(i);		
			MetaValidatorFor validator = new MetaValidatorFor();
			validator.setValidatorName(validatorName);
			validator.setValidatorClass(validatorClass);
			validator.setForStereotype(elForStereotype.getAttribute(xstereotype[lang]));
			if (requiredValidators) {
				MetaValidators._addMetaValidatorRequired(validator);
			}
			else {
				MetaValidators._addMetaValidatorDefault(validator);
			}
		}				
		
		l = el.getElementsByTagName(xfor_annotation[lang]);
		c = l.getLength();
		for (int i = 0; i < c; i++) {
			Element elForAnnotation = (Element) l.item(i);		
			MetaValidatorFor validator = new MetaValidatorFor();
			validator.setValidatorName(validatorName);
			validator.setValidatorClass(validatorClass);
			validator.setForAnnotation(elForAnnotation.getAttribute(xannotation[lang]));
			if (requiredValidators) {
				MetaValidators._addMetaValidatorRequired(validator);
			}
			else {
				MetaValidators._addMetaValidatorDefault(validator);
			}
		}
	}
		
	/**
	* @throws XavaException
	 */
	private void createValidators() {
		NodeList l = getRoot().getElementsByTagName(xvalidator[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			MetaValidators._addMetaValidator(createValidator(l.item(i)));
		}
	}
		
	
	/**
	* @throws XavaException
	 */
	private void createRequiredValidators() {
		NodeList l = getRoot().getElementsByTagName(xrequired_validator[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			addValidatorsFor(l.item(i), true);
		}
	}
	
	/**
	* @throws XavaException
	 */
	private void createDefaultValidators() {
		NodeList l = getRoot().getElementsByTagName(xdefault_validator[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			addValidatorsFor(l.item(i), false);
		}
	}	
		
	/**
	* @throws XavaException
	 */
	protected void createObjects() {
		createValidators();
		createRequiredValidators();
		createDefaultValidators();
	}
		
}