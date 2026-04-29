package com.yourcompany.yourapp.formatters;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.openxava.formatters.LocalDateFormatter;

public class DeadlineDateListFormatter extends LocalDateFormatter {

    @Override
    public String format(HttpServletRequest request, Object object) {
        if (object == null) return "";
        LocalDate date = (LocalDate) object;
        String formattedDate = super.format(request, date);
        
        LocalDate today = LocalDate.now();
        LocalDate nextWorkingDay = getNextWorkingDay(today);
        LocalDate secondNextWorkingDay = getNextWorkingDay(nextWorkingDay);
        
        if (date.equals(today)) {
            formattedDate = "<span class='deadline-today'>" + formattedDate + "</span>";
        } else if (date.equals(nextWorkingDay)) {
            formattedDate = "<span class='deadline-tomorrow'>" + formattedDate + "</span>";
        } else if (date.equals(secondNextWorkingDay)) {
            formattedDate = "<span class='deadline-day-after-tomorrow'>" + formattedDate + "</span>";
        }
        
        return formattedDate;
    }
    
    private LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
               nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

}
