package org.openxava.util;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;

/**
 * To process simple HTML templates.
 * 
 * 
 *
 *	-- Usage --
 *	SimpleTemplater.getInstance().buildOutputUsingStringTemplate(template, parameters);
 *	Replaces ${parameter_name} in the template by its value found in the (Map<String, Object>)parameters.
 *
 *	-- Syntax --	
 *	Simple blocks can be processed. 
 *	Blocks are surrounded by <!-- $$[for|if|ifnot](parameter) --> and <!-- $$end[for|if|ifnot](parameter) -->
 *		$$for(parameter_name) will loop over the items of parameter_name of type Collection<Map<String, Object>>
 *		$$if(parameter_name) will execute only if parameter_name is defined and not empty
 *		$$ifnot(parameter_name) will execute only if parameter_name is not defined or is empty
 *	
 *
 *	-- Example --
 *	String template = "
 *	Dear ${name},
 *	Here is the list of your things:
 *	<!-- $$for(things) -->${name}<!-- $$endfor(things) -->
 *	<!-- $$if(regards) -->Regards,<!-- $$endif(regards) -->
 *	<!-- $$ifnot(regards) -->Sincerely,<!-- $$endifnot(regards) -->
 *	Me";
 *
 *	Map<String, Object> parameters = new HashMap<String, Object>();
 *	parameters.put("name", "You");
 *	parameters.put("regards", "1");
 *	Collection<Map<String, Object>> things = new ArrayList<Map<String, Object>>();
 *	for (int i=0; i<3; i++) {
 *		Map<String, Object> p = new HashMap<String, Object>();
 *		p.put("name", "Thing + i);
 *		things.addElement(p);
 *	}
 *	parameters.put("things", things);
 *
 *	String output = SimpleTemplater.getInstance().buildOutputUsingStringTemplate(template, parameters);
 *	System.out.println(output);
 *
 * @author Laurent Wibaux
 */

public class SimpleTemplater {

	private static Log log = LogFactory.getLog(SimpleTemplater.class);
		
	private boolean templateIsHTML = false;
	
	/**
	 * Returns an instance of SimpleTemplater which will not deal with HTML
	 * 
	 * @return SimpleTemplater
	 */
	public static SimpleTemplater getInstance() {
		return new SimpleTemplater(false);
	}

	/**
	 * Returns an instance of SimpleTemplater which will or will not deal with HTML
	 * 
	 * @param templateIsHTML - set to true if the template that will be fed is HTML
	 * @return SimpleTemplater
	 */
	public static SimpleTemplater getInstance(boolean templateIsHTML) {
		return new SimpleTemplater(templateIsHTML);
	}
		
	/**
	 * Returns a sequence of numbers going from 'from' to 'to', using indexName as key in the map
	 * this sequence can then be added to a Map<String, Object>
	 *  
	 * @param indexName - the name of the parameter
	 * @param from - start of the sequence
	 * @param to - end of the sequence
	 * @return a list of Map<String, Object>
	 */
	public static Collection<Map<String, Object>> getSequence(String indexName, int from, int to) {
		Collection<Map<String, Object>> sequence = new ArrayList<Map<String, Object>>();
		for (int i=from; i<=to; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(indexName, i);
			sequence.add(item);
		}	
		return sequence;
	}
	
	/**
	 * Constructor
	 * 
	 * @param templateIsHTML - set to true if the template that will be fed is HTML
	 * @return SimpleTemplater
	 */
	public SimpleTemplater(boolean templateIsHTML) {
		this.templateIsHTML = templateIsHTML;
	}

	/**
	 * Returns the template with all fields replaced by their values
	 *  
	 * @param templateName - the name of the template to be found in the code archive
	 * @param parameters - Map<String, Object> of the parameters
	 * @return the result of the processing
	 * @throws SimpleTemplaterException - in case of an error in the template or in the passed parameters
	 */
	public String buildOutputUsingResourceTemplate(String templateName, Map<String, Object> parameters) {
		try {
			templateIsHTML = templateNameIsHTML(templateName);
			InputStream templateIS = SimpleTemplater.class.getResourceAsStream(templateName);
			if (templateIS == null) throw new XavaException("template_not_found", templateName); 
			return processInputStreamTemplate(templateIS, parameters);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new XavaException(templateName + ": " + e.getMessage());
		}
	}
	
	/**
	 * Returns the template with all fields replaced by their values
	 *  
	 * @param templateIS - an InputStream
	 * @param parameters - Map<String, Object> of the parameters
	 * @return the result of the processing
	 * @throws SimpleTemplaterException - in case of an error in the template or in the passed parameters
	 */
	public String buildOutputUsingInputStreamTemplate(InputStream templateIS, Map<String, Object>parameters) {
		try {
			return processInputStreamTemplate(templateIS, parameters);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new XavaException(e.getMessage());
		}
	}
	
	/**
	 * Returns the template with all fields replaced by their values
	 *  
	 * @param template - a String containing the template
	 * @param parameters - Map<String, Object> of the parameters
	 * @return the result of the processing
	 * @throws SimpleTemplaterException - in case of an error in the template or in the passed parameters
	 */
	public String buildOutputUsingStringTemplate(String template, Map<String, Object>parameters) {
		try {
			return processBlock(template, parameters);
		} catch (Exception e) {
			log.warn(e.getMessage());
			throw new XavaException(e.getMessage());
		}
	}
		
	private String processInputStreamTemplate(InputStream templateIS, Map<String, Object>parameters) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(templateIS));
			String template = "";
			String line;
			while ((line = br.readLine()) != null) template += line + "\r\n";
			return processBlock(template, parameters);
		} catch (Exception e) {
			throw new XavaException(e.getMessage());
		}
	}

	
	@SuppressWarnings("unchecked")
	private String processBlock(String template, Map<String, Object>parameters) {
		String output = "";
		String remaining = template;
		// parse the file and replace
		int indOf = remaining.indexOf("<!--");
		while (indOf != -1) {
			output += replaceParameters(remaining.substring(0, indOf), parameters);
			remaining = remaining.substring(indOf + "<!--".length());
			indOf = remaining.indexOf("-->");
			if (indOf == -1) throw new XavaException("template_unclosed_comment"); 
			String blockDescriptor = remaining.substring(0, indOf);
			remaining = remaining.substring(indOf + "-->".length());
			String blockManagerTypeAndName = getBlockManagerTypeAndName(blockDescriptor);
			if (blockManagerTypeAndName != null) {
				String blockManagerType = blockManagerTypeAndName.substring(0, 1);
				String blockManagerName = blockManagerTypeAndName.substring(1);
				// find the end of the block
				String blockEndDescriptor = "$$end" + 
					(blockManagerType.equals("F") ? "for" : "") +
					(blockManagerType.equals("I") ? "if" : "") +
					(blockManagerType.equals("N") ? "ifnot" : "") + "(" + blockManagerName + ")";
				int indOfEnd = remaining.indexOf(blockEndDescriptor);
				if (indOfEnd == -1) throw new XavaException("template_unclosed_block", blockDescriptor, blockEndDescriptor); 
				String block = remaining.substring(0, indOfEnd);
				remaining = remaining.substring(indOfEnd);
				indOf = remaining.indexOf("-->");
				if (indOf == -1) throw new XavaException("template_unclosed_comment"); 
				remaining = remaining.substring(indOf+"-->".length());
				indOfEnd = block.lastIndexOf("<!--");
				if (indOfEnd == -1) throw new XavaException("template_unclosed_comment"); 
				// get the block
				block = block.substring(0, indOfEnd);
				// check if the blockManager is defined
				boolean blockManagerIsDefined = false;
				Object blockManager = parameters.get(blockManagerName);
				if (blockManager != null) {
					blockManagerIsDefined = true;
					if (blockManager instanceof Collection<?>) {
						if (((Collection<?>)blockManager).size() == 0) blockManagerIsDefined = false; 
					} else if (blockManager.toString().equals("") || blockManager.toString().equals(SimpleHTMLReportAction.COLLECTION)) {
						blockManagerIsDefined = false; 
					}
				}
				// process the block according to the directive
				if (blockManagerIsDefined && blockManagerType.equals("I")) {
					output += processBlock(block, parameters);
				} else if (blockManagerIsDefined && blockManagerType.equals("F")) {
					if (!(blockManager instanceof Collection<?>)) 
						throw new XavaException("template_param_type_error", blockManagerName); 
					output += processFor(block, (Collection<Map<String, Object>>)blockManager);
				} else if (!blockManagerIsDefined && blockManagerType.equals("N")) {
					output += processBlock(block, parameters);
				}
			} else {
				output += "<!--" + blockDescriptor + "-->";
			}
			indOf = remaining.indexOf("<!--");
		}
		output += replaceParameters(remaining, parameters);
		return output;
	}
	
	private String getBlockManagerTypeAndName(String blockDescriptor) {
		String parameterName = getParameterName(blockDescriptor, "for");
		if (parameterName != null) return "F" + parameterName;
		parameterName = getParameterName(blockDescriptor, "if");
		if (parameterName != null) return "I" + parameterName;
		parameterName = getParameterName(blockDescriptor, "ifnot");
		if (parameterName != null) return "N" + parameterName;
		return null;
	}
	
	private String getParameterName(String blockDescriptor, String condition) {
		String conditionMarker = "$$" + condition + "(";
		int indOf = blockDescriptor.indexOf(conditionMarker);
		if (indOf != -1) {
			String paramName = blockDescriptor.substring(indOf + conditionMarker.length());
			indOf = blockDescriptor.substring(indOf + conditionMarker.length()).indexOf(')');
			if (indOf == -1) throw new XavaException("template_unclosed_block", blockDescriptor, "')'");
			return paramName.substring(0, indOf).trim();			
		} 
		return null;
	}
	
	private String processFor(String template, Collection<Map<String, Object>> forParameters) {
		String output = "";
		for (Map<String, Object> parameters : forParameters) output += processBlock(template, parameters);
		return output;
	}
	
	private String replaceParameters(String template, Map<String, Object>parameters) {
		String output = "";
		String remaining = template;		
		int indOf = remaining.indexOf("${");
		while (indOf != -1) {
			output += remaining.substring(0, indOf);
			remaining = remaining.substring(indOf+2);
			indOf = remaining.indexOf("}");
			if (indOf == -1) throw new XavaException("template_unclosed_parameter");
			String parameterName = remaining.substring(0, indOf);
			remaining = remaining.substring(indOf+1);
			if (parameterName.indexOf(' ') != -1 || parameterName.indexOf('$') != -1  || parameterName.indexOf('{') != -1) 
				throw new XavaException("template_incorrect_parameter_name", parameterName); 
			Object parameterValue = (parameters == null ? null : parameters.get(parameterName));
			if (parameterValue != null) {
				String parameterStringValue = parameterValue.toString().trim();
				if (templateIsHTML) {
					if (parameterStringValue.indexOf("<br>") == -1 && 
							parameterStringValue.indexOf("<br/>") == -1 && 
							parameterStringValue.indexOf("</table>") == -1 &&
							parameterStringValue.indexOf("<p>") == -1 &&
							parameterStringValue.indexOf("</svg>") == -1)
						parameterStringValue = parameterStringValue.replaceAll("\n", "\n<br/>");
				}
				output += parameterStringValue;
			}
			indOf = remaining.indexOf("${");
		}
		output += remaining;
		return output;	
	}
	
	public boolean templateNameIsHTML(String templateName) {
		if (templateName.endsWith(".html")) return true;
		if (templateName.endsWith(".htm")) return true;
		if (templateName.endsWith(".js")) return true;
		return false;
	}
}
