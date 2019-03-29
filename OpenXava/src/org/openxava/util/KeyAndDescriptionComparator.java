package org.openxava.util;

import java.util.*;




/**
 * @author Javier Paniza
 */
public class KeyAndDescriptionComparator implements Comparator {
	
	private static final KeyAndDescriptionComparator porDescripcion = new KeyAndDescriptionComparator(false);  
	private static final KeyAndDescriptionComparator porClave = new KeyAndDescriptionComparator(true);
	
	private boolean orderByKey;
	
	
	
	private KeyAndDescriptionComparator(boolean ordenadoPorClave) {
		this.orderByKey = ordenadoPorClave;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof KeyAndDescription) || !(o2 instanceof KeyAndDescription)) {
			throw new ClassCastException(XavaResources.getString("keyAndDescription_classcast"));
		}
		KeyAndDescription c1 = (KeyAndDescription) o1;
		KeyAndDescription c2 = (KeyAndDescription) o2;
		if (orderByKey) {
			if (c1.getKey() instanceof Comparable) {
				Comparable cc1 = (Comparable) c1.getKey();
				Comparable cc2 = (Comparable) c2.getKey();
				return cc1.compareTo(cc2);
			}
			else {
				return c1.getKey().toString().compareTo(c2.getKey().toString());
			}
		}
		else {
			return c1.getDescription().toString().compareTo(c2.getDescription().toString());
		}
	}
	
  public static Comparator getByDescription() {
		return porDescripcion;
	}
	
	public static Comparator getByKey() {
	  return porClave;
  }
	
}
