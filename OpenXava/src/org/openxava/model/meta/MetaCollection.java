package org.openxava.model.meta;

import java.rmi.*;
import java.util.*;



import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaCollection extends MetaMember implements IPropertyValidator {
	
	private final static int SQL = 0;
	private final static int EJB2QL = 1;
	private final static int JBOSSQL = 2;
	private final static int JPA = 3;
		
	private int minimum;
	private int maximum; 
	private String condition;
	private String order;
	private String inverseCollection;  
	private boolean orphanRemoval; 
	private boolean elementCollection;
	private boolean sortable;   
	
	private MetaReference metaReference;
	private Collection metaCalculatorsPostRemove;
	private MetaCalculator metaCalculator;
	
	
	public void addMetaCalculatorPostRemove(MetaCalculator metaCalculator) {
		if (metaCalculatorsPostRemove == null) {
			metaCalculatorsPostRemove = new ArrayList();			
		}
		metaCalculatorsPostRemove.add(metaCalculator);		
	}
	
	public Collection getMetaCalculatorsPostRemove() {
		return metaCalculatorsPostRemove == null?Collections.EMPTY_LIST:metaCalculatorsPostRemove;
	}
	
	public boolean hasPostRemoveCalculators() {
		return !getMetaCalculatorsPostRemove().isEmpty();
	}

	public int getMinimum() {
		return minimum;
	}
	
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public MetaReference getMetaReference() {
		return metaReference;
	}
	public void setMetaReference(MetaReference reference) {
		this.metaReference = reference;
		if (getMetaModel() != null && this.metaReference != null) {
			this.metaReference.setMetaModel(getMetaModel());
		}
	}

	public boolean isAggregate() throws XavaException {
		return getMetaReference().isAggregate();
	}

	public void setMetaModel(MetaModel newContainer) {
		super.setMetaModel(newContainer);
		if (metaReference != null) {
			metaReference.setMetaModel(newContainer);
		}
	}
	/**
	 * @param errors  Object that accumulate the list of validation errors.
	 * @param object Collection to validate. Has to be of type Collection. If null empty collection is assumed.
	 * @param objectName Object that contains the collection to validate.
	 * @param propertyName Name of the collection property within objectName. 
	 */
	public void validate(
		Messages errors,
		Object object,
		String objectName,
		String propertyName)
		throws RemoteException {			
		object = (object == null)?Collections.EMPTY_SET:object;	
		if (!(object instanceof Collection)) {
			throw new IllegalArgumentException(XavaResources.getString("only_validate_collection"));
		}	
		Collection c = (Collection) object;
		if (c.size() < getMinimum()) {
			String elements = XavaResources.getString(getMinimum() == 1?"element":"elements");
			errors.add("minimum_elements", new Integer(getMinimum()), "'" + elements + "'", getName(), getMetaModel().getName());			
		}	
		else if (getMaximum() > 0 && c.size() > getMaximum()) {
			errors.add("maximum_elements", new Integer(getMaximum()), getName(), getMetaModel().getName()); 
		}			
	}
	
	/**
	 * @since 4.3
	 */
	public String removeTotalPropertyPrefix(String totalProperty) {
	    if (isElementCollection()) {
            Class pojoClass = getMetaModel().getPOJOClass();
            while (!pojoClass.equals(Object.class)) {
                String parent = Strings.firstLower(pojoClass.getSimpleName());
                if (totalProperty.startsWith(parent + ".")) {
                    return totalProperty.substring(parent.length() + 1);
                }
                pojoClass = pojoClass.getSuperclass();
            }
            throw new XavaException("total_property_must_be_prefixed", getName(), totalProperty, Strings.firstLower(getMetaModel().getName()), totalProperty);
        }
        else {
            String mappedBy = getMetaReference().getRole();
            if (!totalProperty.startsWith(mappedBy + ".")) {
                throw new XavaException("total_property_must_be_prefixed", getName(), totalProperty, mappedBy, totalProperty); 
            }
            return totalProperty.substring(mappedBy.length() + 1);            
        }
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String string) {
		order = string;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String string) {
		condition = string;
	}

	public String getSQLConditionWithoutChangePropertiesByColumns() throws XavaException {
		if (Is.emptyString(getCondition())) return ""; 
		return changePropertiesThisByArguments(getCondition(), SQL);
	}	
	
	public String getHQLCondition() throws XavaException {
		return getPOJOCondition(JPA); // Since v6.1 (that uses Hibernate 5.3) Hibernate uses JPA format only
	}
	
	public String getJPACondition() throws XavaException {
		return getPOJOCondition(JPA);
	}	
	
	private String getPOJOCondition(int qlType) throws XavaException {
		MetaModel metaModel = getMetaReference().getMetaModelReferenced(); 
		StringBuffer sb = new StringBuffer("from ");
		sb.append(metaModel.getName());
		sb.append(" as o");
		if (!Is.emptyString(this.condition)) {			
			sb.append(" where ");			
			String condition = changePropertiesThisByArguments(getCondition(), qlType);			
			sb.append(Strings.change(condition, MetaFinder.getTokensToChangeDollarsAndNL()));
		}
		if (!Is.emptyString(this.order)) { 		
			sb.append(" order by ");
			sb.append(Strings.change(this.order, MetaFinder.getTokensToChangeDollarsAndNL()));
		}
		return sb.toString();
	}
		
	private String changePropertiesThisByArguments(String source, int qlType) throws XavaException {			
		StringBuffer r = new StringBuffer(source);		
		int i = r.toString().indexOf("${this.");
		int f = 0;			
		int c = 0;
		while (i >= 0) {
			f = r.toString().indexOf("}", i+2);
			if (f < 0) break;			
			String argument = null;
			switch (qlType) {
				case SQL: argument = "?"; break;
				case JPA: argument = "?" + (c++); break;
				case EJB2QL: argument = "?" + (++c); break;
				case JBOSSQL: argument = "{" + (c++) + "}"; break;				
			}			 
			r.replace(i, f+1, argument);
			i = r.toString().indexOf("${this.");
		}			
		return r.toString();
	}
	
	public boolean orderHasQualifiedProperties() { 
		String order = getOrder();
		if (Is.emptyString(order)) return false;
		int i = order.indexOf("${");
		int f = 0;
		while (i >= 0) {
			f = order.indexOf("}", i + 2);
			if (f < 0) break;
			String property = order.substring(i + 2, f);
			if (property.indexOf('.') >= 0)	return true;					
			i = order.indexOf("${", f);
		}		
		return false;
	}
		
	public String getSQLOrder() throws XavaException {		
		if (Is.emptyString(getOrder())) return "";
		return getMetaReference().getMetaModelReferenced().getMapping().changePropertiesByNotQualifiedColumns(getOrder());
	}	
	
	public boolean hasCondition() {
		return !Is.emptyString(getCondition());	
	}
	
	/**
	 * Util to generate EJB code. 	 
	 */
	public String getFinderName() {
		return Strings.firstUpper(getName()) + "Of" + getMetaModel().getName(); 
	}

	/**
	 * Util to generate EJB code.	 
	 */
	public String getFinderArguments() throws XavaException {
		StringBuffer arguments = new StringBuffer();
		Iterator it = getMetaPropertiesFinderArguments(false).iterator();
		while (it.hasNext()) {
			MetaProperty pr = (MetaProperty) it.next();
			arguments.append(pr.getType().getName());
			arguments.append(' ');
			arguments.append(pr.getName());
			if (it.hasNext()) {
				arguments.append(", ");		 	
			}
		}
		return arguments.toString();
	}
	
	/**
	 * These arguments can be used in a condition for extracting the values
	 * of the collection from the total of the elements of the type. <p>
	 * 
	 * That is, if is a collection of line details of an invoice, this method
	 * will return the key of the invoice inside the line detail.<br> 
	 * Useful for creating a SQL condition for extracting the value of the
	 * collection.<br> 
	 * 
	 * @return  Of type <code>String</code>. Never null.
	 */
	public Collection getConditionArgumentsPropertyNames() throws XavaException {  
		Collection result = new ArrayList();
		for (Iterator it = getMetaPropertiesFinderArguments(true, false).iterator(); it.hasNext(); ) {
			MetaProperty p = (MetaProperty) it.next();
			result.add(p.getName());			
		}
		return result;
	}
	
	/**
	 * Util to generate EJB code. 	 
	 */	
	public Collection getMetaPropertiesFinderArguments(boolean withDots) throws XavaException {
		return getMetaPropertiesFinderArguments(withDots, true); 
	}

	private Collection getMetaPropertiesFinderArguments(boolean withDots, boolean withPropertyWithConverterUnderlined) throws XavaException {
		Collection metaPropertiesFinderArguments = new ArrayList();
		String condition = getCondition();
		int i = condition.indexOf("${this.");
		int f = 0;			
		while (i >= 0) {
			f = condition.indexOf("}", i+2);
			if (f < 0) break;
			String propertyName = condition.substring(i+7, f);
			MetaProperty original = getMetaModel().getMetaProperty(propertyName);
			MetaProperty pr = new MetaProperty();
			boolean isQualified = propertyName.indexOf('.') >= 0;
			if (isQualified && !withDots) {
				pr.setName(Strings.change(propertyName, ".", "_"));
			}
			else {
				pr.setName(propertyName);				
			}
			if (withPropertyWithConverterUnderlined && original.getMapping().hasConverter()) {
				if (!isQualified) {
					pr.setName("_" + Strings.firstUpper(pr.getName()));
				}
				if (withDots && isQualified) {
					pr.setTypeName(original.getTypeName());
				}
				else {
					String typeCmp = original.getMapping().getCmpTypeName();
					if (Is.emptyString(typeCmp)) {
						throw new XavaException("tipo_cmp_required", original.getName());
					}
					pr.setTypeName(typeCmp);					
				}
			}
			else {
				pr.setTypeName(original.getTypeName());
			}				
			metaPropertiesFinderArguments.add(pr);
			i = condition.indexOf("${this.",f);
		}						
		return metaPropertiesFinderArguments;
	}
		

	public MetaCalculator getMetaCalculator() {
		return metaCalculator;
	}
	public void setMetaCalculator(MetaCalculator metaCalculator) {
		this.metaCalculator = metaCalculator;
	}
	
	public boolean hasCalculator() {
		return metaCalculator != null;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public void setOrphanRemoval(boolean orphanRemoval) {
		this.orphanRemoval = orphanRemoval;
	}

	public boolean isOrphanRemoval() {
		return orphanRemoval;
	}

	public String getInverseCollection() {
		return inverseCollection;
	}

	public void setInverseCollection(String inverseCollection) {
		this.inverseCollection = inverseCollection;
	}
	
	public boolean hasInverseCollection() { 
		return !Is.emptyString(inverseCollection);
	}

	public boolean isElementCollection() {
		return elementCollection;
	}

	public void setElementCollection(boolean elementCollection) {
		this.elementCollection = elementCollection;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean ordenable) {
		this.sortable = ordenable;
	}

}

