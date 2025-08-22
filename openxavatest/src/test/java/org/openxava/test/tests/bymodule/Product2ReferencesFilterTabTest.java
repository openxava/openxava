package org.openxava.test.tests.bymodule;

import org.openxava.test.model.Warehouse;

/**
 * tmr Quitar
 */
public class Product2ReferencesFilterTabTest extends EmailNotificationsTestBase {

    public Product2ReferencesFilterTabTest(String testName) {
        super(testName, "Product2");
    }

    public void testReferencesAsDescriptionListUsesFilterOfDefaultTab() throws Exception {
        execute("CRUD.new");
        execute("Product2.changeLimitZone");

        Warehouse key1 = new Warehouse();
        key1.setZoneNumber(1);
        key1.setNumber(1);
        Warehouse key2 = new Warehouse();
        key2.setZoneNumber(1);
        key2.setNumber(2);
        Warehouse key3 = new Warehouse();
        key3.setZoneNumber(1);
        key3.setNumber(3);

        String [][] warehouses = {
                { "", "" },
                { toKeyString(key1), "CENTRAL VALENCIA" },
                { toKeyString(key3), "VALENCIA NORTE" },
                { toKeyString(key2), "VALENCIA SURETE" }
        };
        assertValidValues("warehouse.KEY", warehouses);
    }
}
