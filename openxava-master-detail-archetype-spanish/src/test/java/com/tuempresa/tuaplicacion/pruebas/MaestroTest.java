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
        setValue("persona.numero", "1"); // Wim Mertens

        // Añadir primer detalle
        assertCollectionRowCount("detalles", 0);
        setValueInCollection("detalles", 0, "item.codigo", "1"); // Aprende OpenXava con ejemplos
        assertValueInCollection("detalles", 0, "precioUnitario", "19,00");
        setValueInCollection("detalles", 0, "cantidad", "5");
        assertValueInCollection("detalles", 0, "importe", "95,00");

        // Añadir segundo detalle
        setValueInCollection("detalles", 1, "item.codigo", "2"); // Learn OpenXava by example
        assertValueInCollection("detalles", 1, "precioUnitario", "19,00");
        setValueInCollection("detalles", 1, "cantidad", "3");
        assertValueInCollection("detalles", 1, "importe", "57,00");

        assertCollectionRowCount("detalles", 2);

        // Verificar totales: suma=152.00, porcentajeIVA=21%, iva=31.92, total=183.92
        assertTotalInCollection("detalles", 0, "importe", "152,00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "31,92");
        assertTotalInCollection("detalles", 3, "importe", "183,92");

        execute("CRUD.save");
        assertNoErrors();
        assertMessage("Maestro creado/a satisfactoriamente");

        // Leer - buscar el maestro creado
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

        // Actualizar - modificar cantidad del primer detalle
        setValueInCollection("detalles", 0, "cantidad", "10");
        assertValueInCollection("detalles", 0, "importe", "190,00");

        // Verificar totales actualizados: suma=247.00, porcentajeIVA=21%, iva=51.87, total=298.87
        assertTotalInCollection("detalles", 0, "importe", "247,00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "51,87");
        assertTotalInCollection("detalles", 3, "importe", "298,87");

        execute("CRUD.save");
        assertNoErrors();
        assertMessage("Maestro modificado/a satisfactoriamente");

        // Verificar la modificación usando la lista
        execute("Mode.list");
        setConditionValues("2026", "99999");
        execute("List.filter");

        execute("List.viewDetail", "row=0");
        assertValue("anyo", "2026");
        assertValue("numero", "99999");
        assertCollectionRowCount("detalles", 2);
        assertValueInCollection("detalles", 0, "cantidad", "10");
        assertValueInCollection("detalles", 1, "cantidad", "3");

        // Verificar que los totales se han persistido correctamente
        assertTotalInCollection("detalles", 0, "importe", "247,00");
        assertTotalInCollection("detalles", 1, "importe", "21");
        assertTotalInCollection("detalles", 2, "importe", "51,87");
        assertTotalInCollection("detalles", 3, "importe", "298,87");

        // Borrar
        execute("CRUD.delete");
        assertMessage("Maestro borrado satisfactoriamente");
    }

    public void testImprimir() throws Exception{
        login("admin", "admin");
        execute("List.viewDetail", "row=0");
        execute("Maestro.imprimir");
        assertContentTypeForPopup("application/pdf");
        assertPopupPDFLine( 0, "MAESTRO");
        assertPopupPDFLine( 1, "Maestro: 2020/1");
        assertPopupPDFLine( 2, "Fecha: 13/08/2020");
        assertPopupPDFLine( 3, "Persona:");
        assertPopupPDFLine( 4, "Número: 5");
        assertPopupPDFLine( 5, "Bill Gates");
        assertPopupPDFLine( 6, "Avenue Mortheast");
        assertPopupPDFLine( 7, "Medina");
        assertPopupPDFLine( 8, "Estados Unidos");
        assertPopupPDFLine( 9, "Código Descripción Cantidad Precio Unitario Importe");
        assertPopupPDFLine(10, "1 Aprende OpenXava con ejemplos 3 19,00 57,00");
        assertPopupPDFLine(11, "3 XavaPro Profesional 10 499,00 4.990,00");
        assertPopupPDFLine(12, "IVA %: 21%");
        assertPopupPDFLine(13, "IVA: 1.059,87");
        assertPopupPDFLine(14, "TOTAL: 6.106,87");
    }
}
