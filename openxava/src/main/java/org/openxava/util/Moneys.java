package org.openxava.util;

import java.text.*;
import java.util.*;

/**
 * tmr doc
 * 
 * @since 7.4
 * @author Javier Paniza
 */
public class Moneys {
	
	public static boolean isCurrencySymbolAtStart(Locale locale) {
		// TMR ME QUEDÉ POR AQUI: FALTA PROBAR ESTO. DESPUÉS DEBERÍA SEGUIR PARA QUE EL prefix/suffix EN EL EDITOR SEA AUTOMATICO PARA DINERO
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        String formattedCurrency = currencyFormat.format(1);
        String currencySymbol = currencyFormat.getCurrency().getSymbol(locale);
        return !formattedCurrency.endsWith(currencySymbol);
	}

}
