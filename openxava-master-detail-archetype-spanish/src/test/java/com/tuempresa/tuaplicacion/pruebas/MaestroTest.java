package com.tuempresa.tuaplicacion.pruebas;

import org.openxava.tests.*;

public class MaestroTest extends ModuleTestBase {

    public MaestroTest(String testName) {
        super(testName, "Maestro");
    }

    public void testCrearLeerActualizarBorrar() throws Exception {
        login("admin", "admin");

        // Crear
        execute("CRUD.new");
        setValue("anyo", "2026");
        setValue("numero", "99999");
        setValue("persona.numero", "1");

        // Añadir primer detalle
        assertCollectionRowCount("detalles", 0);
        setValueInCollection("detalles", 0, "item.codigo", "1");
        assertValueInCollection("detalles", 0, "precioUnitario", "19.00");
        setValueInCollection("detalles", 0, "cantidad", "5");
        assertValueInCollection("detalles", 0, "importe", "95.00");

        // Añadir segundo detalle
        setValueInCollection("detalles", 1, "item.codigo", "2");
        assertValueInCollection("detalles", 1, "precioUnitario", "19.00");
        setValueInCollection("detalles", 1, "cantidad", "3");
        assertValueInCollection("detalles", 1, "importe", "57.00");

        assertCollectionRowCount("detalles", 2);

        // Totales: suma=152.00, porcentajeIVA=21%, iva=31.92, total=183.92
        assertTotalInCollection("detalles", 0, "importe", "152.00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "31.92");
        assertTotalInCollection("detalles", 3, "importe", "183.92");

        execute("CRUD.save");
        assertNoErrors();

        // Leer
        execute("Mode.list");
        execute("CRUD.new");
        setValue("anyo", "2026");
        setValue("numero", "99999");
        execute("CRUD.refresh");
        assertNoErrors();
        assertValue("anyo", "2026");
        assertValue("numero", "99999");
        assertValue("persona.numero", "1");
        assertCollectionRowCount("detalles", 2);
        assertValueInCollection("detalles", 0, "cantidad", "5");
        assertValueInCollection("detalles", 1, "cantidad", "3");

        // Actualizar
        setValueInCollection("detalles", 0, "cantidad", "10");
        assertValueInCollection("detalles", 0, "importe", "190.00");

        // Totales esperados: suma=247.00, IVA=51.87, total=298.87
        assertTotalInCollection("detalles", 0, "importe", "247.00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "51.87");
        assertTotalInCollection("detalles", 3, "importe", "298.87");

        execute("CRUD.save");
        assertNoErrors();

        // Verificar en lista
        execute("Mode.list");
        setConditionValues("2026", "99999");
        execute("List.filter");
        execute("List.viewDetail", "row=0");
        assertValue("anyo", "2026");
        assertValue("numero", "99999");
        assertCollectionRowCount("detalles", 2);
        assertValueInCollection("detalles", 0, "cantidad", "10");
        assertValueInCollection("detalles", 1, "cantidad", "3");

        // Totales persistidos
        assertTotalInCollection("detalles", 0, "importe", "247.00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "51.87");
        assertTotalInCollection("detalles", 3, "importe", "298.87");

        // Borrar
        execute("CRUD.delete");
    }

    public void testImprimir() throws Exception {
        login("admin", "admin");
        execute("List.viewDetail", "row=0");
        execute("Maestro.imprimir");
        assertContentTypeForPopup("application/pdf");
        assertPopupPDFLine(0, "MAESTRO");
        assertPopupPDFLine(3, "Persona:");
        assertPopupPDFLine(9, "Código Descripción Cantidad Precio Unitario Importe");
        assertPopupPDFLine(12, "IVA %:");
        assertPopupPDFLine(14, "TOTAL:");
    }
}
