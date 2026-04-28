package com.tuempresa.tuaplicacion.formateadores;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.openxava.formatters.LocalDateFormatter;

public class FormateadorFechaLimiteLista extends LocalDateFormatter {

    @Override
    public String format(HttpServletRequest request, Object object) {
        if (object == null) return "";
        LocalDate fecha = (LocalDate) object;
        String fechaFormateada = super.format(request, fecha);
        
        LocalDate hoy = LocalDate.now();
        LocalDate siguienteDiaLaboral = getSiguienteDiaLaboral(hoy);
        LocalDate segundoSiguienteDiaLaboral = getSiguienteDiaLaboral(siguienteDiaLaboral);
        
        if (fecha.equals(hoy)) {
            fechaFormateada = "<span class='deadline-today'>" + fechaFormateada + "</span>";
        } else if (fecha.equals(siguienteDiaLaboral)) {
            fechaFormateada = "<span class='deadline-tomorrow'>" + fechaFormateada + "</span>";
        } else if (fecha.equals(segundoSiguienteDiaLaboral)) {
            fechaFormateada = "<span class='deadline-day-after-tomorrow'>" + fechaFormateada + "</span>";
        }
        
        return fechaFormateada;
    }
    
    private LocalDate getSiguienteDiaLaboral(LocalDate fecha) {
        LocalDate siguiente = fecha.plusDays(1);
        while (siguiente.getDayOfWeek() == DayOfWeek.SATURDAY || 
               siguiente.getDayOfWeek() == DayOfWeek.SUNDAY) {
            siguiente = siguiente.plusDays(1);
        }
        return siguiente;
    }

}
