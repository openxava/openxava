package org.openxava.chatvoice.actions;

import org.openxava.actions.*;
import java.util.*;

public class FilterListAction extends TabBaseAction {

    @Override
    public void execute() throws Exception {
        // Muestra solo la factura 2023/1 en la lista
        //getTab().setConditionValues(Arrays.asList("2023", "1", "", "", "", ""));

        // Muestra todas las factura del 2023 en la lista, si son número usa = para comparar
        getTab().setConditionValues(Arrays.asList("2023", "", "", "", "", ""));

        // Muestra las factura de "Marissa Mayer" en la lista, los String por defecto usan like '%loquesa%'
        getTab().setConditionValues(Arrays.asList("", "", "", "rissa", "", ""));

    }

}
