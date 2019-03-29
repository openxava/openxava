package org.openxava.hibernate.impl;

import java.util.*;



import org.hibernate.*;

/**
 * Wrapper to calculated size() and isEmpty() fast. <p>
 * 
 * @author Javier Paniza
 */
public class FastSizeList implements List {
	
	private List original;
	private Query query;
	private Query sizeQuery;
	private int size = -1;
	
	
	public FastSizeList(Query query, Query sizeQuery) {
		this.query = query;
		this.sizeQuery = sizeQuery;
	}

	public int size() {
		if (original != null) return original.size();
		if (size == -1) {
			size = ((Number) sizeQuery.uniqueResult()).intValue();
		}
		return size;
	}

	public void clear() {
		getOriginal().clear();	
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Object[] toArray() {
		return getOriginal().toArray();
	}

	public Object get(int index) {
		return getOriginal().get(index);
	}

	public Object remove(int index) {		
		return getOriginal().remove(index);
	}

	public void add(int index, Object element) {
		getOriginal().add(index, element);
	}

	public int indexOf(Object o) {		
		return getOriginal().indexOf(o);
	}

	public int lastIndexOf(Object o) {		
		return getOriginal().lastIndexOf(o);
	}

	public boolean add(Object o) {		
		return getOriginal().add(o);
	}

	public boolean contains(Object o) {		
		return getOriginal().contains(o);
	}

	public boolean remove(Object o) {
		return getOriginal().remove(o);		
	}

	public boolean addAll(int index, Collection c) {		
		return getOriginal().addAll(index, c);
	}

	public boolean addAll(Collection c) {		
		return getOriginal().addAll(c);
	}

	public boolean containsAll(Collection c) {		
		return getOriginal().containsAll(c);
	}

	public boolean removeAll(Collection c) {
		return getOriginal().removeAll(c);
	}

	public boolean retainAll(Collection c) {
		return getOriginal().retainAll(c);
	}

	public Iterator iterator() {
		return getOriginal().iterator();
	}

	public List subList(int fromIndex, int toIndex) {
		return getOriginal().subList(fromIndex, toIndex);
	}

	public ListIterator listIterator() {
		return getOriginal().listIterator();
	}

	public ListIterator listIterator(int index) {
		return getOriginal().listIterator(index);
	}

	public Object set(int index, Object element) {
		return getOriginal().set(index, element);
	}

	public Object[] toArray(Object[] a) {
		return getOriginal().toArray(a);
	}	
	
	private List getOriginal() {
		if (original == null) {
			original = query.list();
		}
		return original;
	}
	
}
