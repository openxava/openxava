package org.openxava.test.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.openxava.test.tests.byfeature.CollectionTest;

/**
 * @author Javier Paniza
 */
public class SingleTestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Single Test Method Suite");
        suite.addTest(new CollectionTest("testRowActionsGroupInPopUp_openRecordInNewWindow"));
        return suite;
    }
    
    public static void main(String[] args) {
        TestRunner.run(suite());
    }
}
