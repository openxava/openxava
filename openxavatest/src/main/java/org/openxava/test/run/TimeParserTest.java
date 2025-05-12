package org.openxava.test.run;

import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.util.*;

import org.openxava.util.*;

/**
 * tmr Quitar
 * Test class to parse time strings using different locales.
 */
public class TimeParserTest {

    private static DateTimeFormatter zhTimeFormat;

    public static void xmain(String[] args) {
        String horaEnIngles = "1:00 PM";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        
        LocalTime hora = LocalTime.parse(horaEnIngles, formatter);

        System.out.println("Hora convertida: " + hora); // Imprime: 13:00
    }
    
    public static void main(String[] args) {
        // Crear un tiempo de ejemplo (1:00 AM) y formatearlo con el patrón exacto que se usa para analizar
        LocalTime sampleTime = LocalTime.of(1, 0);
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            null, FormatStyle.SHORT, Chronology.ofLocale(Locale.ENGLISH), Locale.ENGLISH);
        DateTimeFormatter exactFormatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        String expectedFormat = sampleTime.format(exactFormatter);
        System.out.println("Java 21 expects this exact format for 1:00 AM: " + expectedFormat);
        
        // El código original para mostrar el AM/PM
        LocalTime specificTime = LocalTime.of(15, 0);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("a", Locale.ENGLISH);
        String formattedTimePM = specificTime.format(timeFormat);
        System.out.println("Formatted time PM: " + formattedTimePM);

        //String timeString = "1:00 p. m.";
        //String timeString = "1:00 p. m.";
        String timeString = "1:00 PM";
        
        System.out.println("Parsing time string: " + timeString);
        
        // Test with "en" locale
        Locale enLocale = Locale.ENGLISH;
        System.out.println("\nUsing locale: " + enLocale);
        try {
            DateTimeFormatter formatter = getTimeFormat(enLocale);
            System.out.println("Time pattern: " + getTimePattern(enLocale));
            System.out.println("Formatter: " + formatter);
            LocalTime time = LocalTime.parse(timeString, formatter);
            System.out.println("Parsed time: " + time);
        } catch (Exception e) {
            System.out.println("Error parsing with en locale: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    private static boolean isZhFormat() {
        return false;
    }
    
    private static DateTimeFormatter getTimeFormat(Locale locale) {
        if (isZhFormat()) return zhTimeFormat;
        System.out.println("TimeFormatter.getTimeFormat() Locale=" + locale);
        return DateTimeFormatter.ofPattern(getTimePattern(locale), Locale.ENGLISH);
    }
    
    private static String getTimePattern(Locale locale) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale);
        System.out.println("Time pattern: " + pattern);
        return pattern;
    }
}
