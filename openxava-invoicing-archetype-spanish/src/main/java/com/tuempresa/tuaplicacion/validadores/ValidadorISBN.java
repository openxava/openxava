package com.tuempresa.tuaplicacion.validadores; 

import javax.validation.*;
import javax.ws.rs.client.*; // Para usar JAX-RS

import org.apache.commons.logging.*; // Para usar Log
import org.openxava.util.*;

import com.tuempresa.tuaplicacion.anotaciones.*;
 
public class ValidadorISBN
    implements ConstraintValidator<ISBN, Object> {
	
    private static Log log = LogFactory.getLog(ValidadorISBN.class); // Instancia 'log'
 
    private static org.apache.commons.validator.routines.ISBNValidator
        validador = 
            new org.apache.commons.validator.routines.ISBNValidator();
 
    private boolean buscar; // Almacena la opción buscar
    
    public void initialize(ISBN isbn) { // Lee los atributos de la anotación
        this.buscar = isbn.buscar();
    }
 
    public boolean isValid(Object valor, ConstraintValidatorContext contexto) {
        if (Is.empty(valor)) return true;
        if (!validador.isValid(valor.toString())) return false;
        return buscar ? existeISBN(valor) : true; // Usa 'buscar'
    }
    
    private boolean existeISBN(Object isbn) {
        try {
            // Aquí usamos JAX-RS para llamar al servicio REST
            String respuesta = ClientBuilder.newClient()
                .target("http://openlibrary.org/") // El sitio
                .path("/api/books") // La ruta del servicio
                .queryParam("jscmd", "data") // Los parámetros
                .queryParam("format", "json")
                .queryParam("bibkeys", "ISBN:" + isbn) // El ISBN es un parámetro
                .request()
                .get(String.class); // Una cadena con el JSON
            return !respuesta.equals("{}"); // ¿Está el JSON vacío? Suficiente para nuestro caso
        }
        catch (Exception ex) {
            log.warn("Imposible conectar a openlibrary.org " +
                "para validar el ISBN. Validación fallida", ex);
            return false; // Si hay errores asumimos que la validación falla
        }
    }
    
}