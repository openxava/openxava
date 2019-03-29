package org.openxava.validators.meta;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.validators.meta.xmlparse.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaValidators {

	private static Log log = LogFactory.getLog(MetaValidators.class);
	
	private static Collection primitiveTypes;
	private static Map metaValidators;
	private static Map metaValidatorsRequired;
	private static Map metaValidatorsDefault;
	
	public static void _addMetaValidator(MetaValidator newMetaValidator) throws XavaException {
		if (metaValidators == null) {
			throw new XavaException("only_from_parse", "MetaValidators._addMetaValidator");
		}
		metaValidators.put(newMetaValidator.getName(), newMetaValidator);
	}
	
	public static void _addMetaValidatorRequired(MetaValidatorFor newMetaValidator) throws XavaException {
		if (metaValidatorsRequired == null) {
			throw new XavaException("only_from_parse", "MetaValidators._addMetaValidatorRequired");
		}
		if (!Is.emptyString(newMetaValidator.getForType())) {
			metaValidatorsRequired.put(newMetaValidator.getForType(), newMetaValidator);
		}
		else if (!Is.emptyString(newMetaValidator.getForStereotype())) {
			metaValidatorsRequired.put(newMetaValidator.getForStereotype(), newMetaValidator);
		}		
		else {
			throw new XavaException("required_validator_type_or_stereotype_required");
		}
	}
	
	public static void _addMetaValidatorDefault(MetaValidatorFor newMetaValidator) throws XavaException {
		if (metaValidatorsDefault == null) {
			throw new XavaException("only_from_parse", "MetaValidators._addMetaValidatorDefault");
		}
		if (!Is.emptyString(newMetaValidator.getForType())) {
			metaValidatorsDefault.put(newMetaValidator.getForType(), newMetaValidator);
		}
		else if (!Is.emptyString(newMetaValidator.getForStereotype())) {
			metaValidatorsDefault.put(newMetaValidator.getForStereotype(), newMetaValidator);
		}		
		else {
			throw new XavaException("default_validator_type_or_stereotype_required");
		}
	}
	
	
	/**
	 * 
	 * @return Null if not found
	 */
	private static MetaValidatorFor findFromParent(Map metaValidatorsFor, String forType)
		throws XavaException {
		try {
			if (isStereotype(forType)) return null;
			if (isPrimitiveType(forType))
				return null;
			while (!forType.equals("java.lang.Object")) {
				Class superClass = Class.forName(forType).getSuperclass();
				if (superClass == null) return null; // An interface without parent
				forType = superClass.getName();
				MetaValidatorFor v =
					(MetaValidatorFor) metaValidatorsFor.get(forType);
				if (v != null)
					return v;
			}
			return null;
		} 
		catch (ClassNotFoundException ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("class_not_found_searching_validator", forType, ex.getMessage());
		}
	}
	
	private static boolean isStereotype(String forType) {
		return forType.indexOf(".") < 0;
	}
	
	private static boolean isPrimitiveType(String className) {
		return getPrimitiveTypes().contains(className);
	}

	private static Collection getPrimitiveTypes() {
		if (primitiveTypes == null) {
			primitiveTypes = new ArrayList();
			primitiveTypes.add("boolean");
			primitiveTypes.add("byte");
			primitiveTypes.add("char");
			primitiveTypes.add("short");
			primitiveTypes.add("int");
			primitiveTypes.add("long");
			primitiveTypes.add("float");
			primitiveTypes.add("double");
		}
		return primitiveTypes;
	}
	
	/**
	 * @exception XavaException If the validator is not registered or another problem.
	 */
	public static MetaValidator getMetaValidator(String name) throws XavaException {
		if (metaValidators == null) {
			metaValidators = new HashMap();
			metaValidatorsRequired = new HashMap();
			metaValidatorsDefault = new HashMap();
			ValidatorsParser.configureValidators();
		}
		MetaValidator v = (MetaValidator) metaValidators.get(name);
		if (v == null) {
			throw new XavaException("validator_no_registered", name);
		}
		return v;
	}
	
	/**
	 * @return Null if a validator for the clase is not found.
	 */
	public static MetaValidatorFor getMetaValidatorRequiredFor(String typeOrStereotype)
		throws XavaException {
		if (metaValidatorsRequired == null) {
			metaValidators = new HashMap();
			metaValidatorsRequired = new HashMap();
			metaValidatorsDefault = new HashMap();
			ValidatorsParser.configureValidators();
		}
		MetaValidatorFor v =
			(MetaValidatorFor) metaValidatorsRequired.get(typeOrStereotype);
		if (v == null) {
			v = findFromParent(metaValidatorsRequired, typeOrStereotype);
			if (v != null) {
				metaValidatorsRequired.put(typeOrStereotype, v);
			}
		}
		return v;
	}
	
	/**
	 * @return Null if a validator for the clase is not found.
	 */
	public static MetaValidatorFor getMetaValidatorDefaultFor(String typeOrStereotype)
		throws XavaException {
		if (metaValidatorsDefault == null) {
			metaValidators = new HashMap();
			metaValidatorsRequired = new HashMap();
			metaValidatorsDefault = new HashMap();
			ValidatorsParser.configureValidators();
		}
		MetaValidatorFor v =
			(MetaValidatorFor) metaValidatorsDefault.get(typeOrStereotype);
		if (v == null) {
			v = findFromParent(metaValidatorsDefault, typeOrStereotype);
			if (v != null) {
				metaValidatorsDefault.put(typeOrStereotype, v);
			}
		}
		return v;
	}
			
}