package org.openxava.model.meta;

import java.io.*;
import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MetaFinder implements Serializable {
	
	private static Log log = LogFactory.getLog(MetaFinder.class);
	
	private static Map argumentsJBoss11ToEJBQL;
	private static Map argumentsToHQL;
	private static Map tokensToChangeDollarsAndNL;
	 	
	private String name;
	private String arguments;	
	private boolean collection;
	private String condition;
	private String order;
	private MetaModel metaModel;
	
	
	
	public String getArguments() {
		arguments = Strings.change(arguments, "String", "java.lang.String");
		arguments = Strings.change(arguments, "java.lang.java.lang.String", "java.lang.String");
		return arguments;
	}
	
	public Collection getMetaPropertiesArguments() throws XavaException {
		StringTokenizer st = new StringTokenizer(getArguments(), ",");
		Collection result = new ArrayList();
		while (st.hasMoreTokens()) {
			String argument = st.nextToken();
			StringTokenizer argumentSt = new StringTokenizer(argument);
			String type = argumentSt.nextToken().trim();
			String name = argumentSt.nextToken().trim();			
			MetaProperty p = new MetaProperty();
			p.setName(name);
			p.setTypeName(type);
			result.add(p);			
		}
		return result;
	}

	public boolean isCollection() {
		return collection;
	}

	public String getCondition() {
		return condition;
	}

	public String getName() {
		return name;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSupportedForEJB2() throws XavaException {
		return !hasSome3LevelProperty(getCondition()) && 
			!hasSome3LevelProperty(getOrder());
	}
	
	private boolean hasSome3LevelProperty(String sentence) throws XavaException {
		if (sentence == null) return false;
		int i = sentence.indexOf("${");
		int f = 0;
		while (i >= 0) {
			f = sentence.indexOf("}", i + 2);
			if (f < 0) break;
			String property = sentence.substring(i + 2, f);			
			StringTokenizer st = new StringTokenizer(property, ".");
			if (st.countTokens() > 3) {
				log.warn(XavaResources.getString("property_3_level_in_ejb2_finder", property, getName()));
				return true;
			}
			if (st.countTokens() == 3) {
				if (!getMetaModel().getMetaProperty(property).isKey()) {
					log.warn(XavaResources.getString("property_3_level_in_ejb2_finder", property, getName()));
					return true;
				}
			}
			i = sentence.indexOf("${", i + 1);
		}
		return false;
	}
	
	public String getEJBQLCondition() throws XavaException {
		StringBuffer sb = new StringBuffer("SELECT OBJECT(o) FROM ");
		sb.append(getMetaModel().getName());
		sb.append(" o");
		if (!Is.emptyString(this.condition)) {
			sb.append(" WHERE ");			
			String attributesCondition = getMetaModel().getMapping().changePropertiesByCMPAttributes(this.condition);			 
			sb.append(Strings.change(attributesCondition, getArgumentsJBoss11ToEJBQL()));
		}
		if (!Is.emptyString(this.order)) {			
			sb.append(" ORDER BY ");
			sb.append(getMetaModel().getMapping().changePropertiesByCMPAttributes(this.order));
		}
		return sb.toString();
	}
	
	public String getHQLCondition() throws XavaException {		
		return getHQLCondition(true);
	}
	
	private String getHQLCondition(boolean order) throws XavaException {		
		StringBuffer sb = new StringBuffer("from ");
		sb.append(getMetaModel().getName());
		sb.append(" as o");
		if (!Is.emptyString(this.condition)) {			
			sb.append(" where ");
			String condition = transformAggregateProperties(getCondition()); 
			condition = Strings.change(condition, getArgumentsToHQL());						
			sb.append(Strings.change(condition, getTokensToChangeDollarsAndNL()));
		}
		if (order && !Is.emptyString(this.order)) { 		
			sb.append(" order by ");			
			sb.append(Strings.change(transformAggregateProperties(this.order), getTokensToChangeDollarsAndNL()));
		}
		return  sb.toString();
	}
	
	/**
	 * Transforms ${address.street} in ${address_street} if address if an aggregate
	 * of container model.
	 * @param condition
	 * @return
	 */
	private String transformAggregateProperties(String condition) {		
		int i = condition.indexOf("${");
		if (i < 0) return condition;
		StringBuffer result = new StringBuffer(condition.substring(0, i + 2));
		while (i >= 0) {
			int f = condition.indexOf("}", i);			
			String property = condition.substring(i + 2, f); 			
			String transformedProperty = transformAgregateProperty(property);
			result.append(transformedProperty);
			i = condition.indexOf("${", f);
			if (i >= 0) result.append(condition.substring(f, i));			
			else result.append(condition.substring(f));			
		}		
		return result.toString();
	}

	private String transformAgregateProperty(String property) {
		StringBuffer result = new StringBuffer();
		StringTokenizer st = new StringTokenizer(property, ".");
		String member = "";
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			result.append(token);
			if (!st.hasMoreTokens()) break;
			member =  member + token;
			try {
				MetaReference ref = getMetaModel().getMetaReference(member);
				if (ref.isAggregate()) result.append('_');
				else result.append('.');				
			}
			catch (XavaException ex) {
				result.append('.');
			}
			member = member + ".";
		}
		return result.toString();
	}

	public String getHQLCountSentence() throws XavaException {
		StringBuffer sb = new StringBuffer("select count(*) ");
		sb.append(getHQLCondition(false));
		return sb.toString();
	}
	
	
	public MetaModel getMetaModel() {
		return metaModel;
	}

	public void setMetaModel(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	private static Map getArgumentsJBoss11ToEJBQL() {
		if (argumentsJBoss11ToEJBQL == null) {
			argumentsJBoss11ToEJBQL = new HashMap();
			for (int i=0; i<30; i++) {
				argumentsJBoss11ToEJBQL.put("{" + i+ "}", "?" + (i+1));
			}			
		}
		return argumentsJBoss11ToEJBQL;
	}
	
	private static Map getArgumentsToHQL() {
		if (argumentsToHQL == null) {
			argumentsToHQL = new HashMap();
			for (int i=0; i<30; i++) {
				argumentsToHQL.put("{" + i+ "}", ":arg" + i);
			}			
		}
		return argumentsToHQL;
	}
	
	static Map getTokensToChangeDollarsAndNL() {
		if (tokensToChangeDollarsAndNL == null) {
			tokensToChangeDollarsAndNL = new HashMap();
			tokensToChangeDollarsAndNL.put("${", "o.");
			tokensToChangeDollarsAndNL.put("}", "");
			tokensToChangeDollarsAndNL.put("\n", "");			
		}
		return tokensToChangeDollarsAndNL;
	}

	public boolean equals(Object other) {
		if (!(other instanceof MetaFinder)) return false;
		return toString().equals(other.toString());
	}
		
	public int hashCode() {	
		return toString().hashCode();
	}
	
	public String toString() {
		return "Finder: " + getMetaModel().getName() + "." + getName();
	}
	
}
