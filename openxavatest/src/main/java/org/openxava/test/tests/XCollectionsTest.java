package org.openxava.test.tests;

import java.util.*;

import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza 
 */
public class XCollectionsTest extends TestCase {
	
	public void testMove() throws Exception {
		List list = new ArrayList();
		list.add(0);
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		XCollections.move(list, 4, 2);
		assertEquals("[0, 1, 4, 2, 3, 5]", list.toString());
		XCollections.move(list, 2, 4);
		assertEquals("[0, 1, 2, 3, 4, 5]", list.toString());
		XCollections.move(list, 1, 0);
		assertEquals("[1, 0, 2, 3, 4, 5]", list.toString());		
		XCollections.move(list, 0, 1);
		assertEquals("[0, 1, 2, 3, 4, 5]", list.toString());
		XCollections.move(list, 4, 5);
		assertEquals("[0, 1, 2, 3, 5, 4]", list.toString());
		XCollections.move(list, 5, 4);
		assertEquals("[0, 1, 2, 3, 4, 5]", list.toString());		
	}

}
