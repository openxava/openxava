package org.openxava.test.tests;

import java.util.*;
import org.openxava.util.*;
import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class LabelsTest extends TestCase {
	
	public void testAnyEqual() throws Exception {
		Locale es = new Locale("es");
		assertEquals("La bota roja", Labels.get("laBotaRoja", es)); // laBotaRoja is not registered as label
		Labels.put("laBotaRoja", es, "La bota roja es coja");
		assertEquals("La bota roja es coja", Labels.get("laBotaRoja", es));
	}

	
}
