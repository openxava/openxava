package org.openxava.util;

import java.lang.reflect.*;

import org.json.*;

public class ToJson {
	
	public static JSONObject ObjectToJson(Object objeto) throws IllegalAccessException {
	    JSONObject json = new JSONObject();
	    Field[] campos = objeto.getClass().getDeclaredFields();
	    for (Field campo : campos) {
	        campo.setAccessible(true);
	        json.put(campo.getName(), campo.get(objeto));
	    }
	    return json;
	}

	public static JSONObject oj (Object object) {
		
		JSONObject jsonObject = new JSONObject();

        // Recorre las propiedades del objeto desconocido mediante reflection
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object fieldValue = field.get(object);
                if (fieldValue == null) {
                    jsonObject.put(fieldName, JSONObject.NULL);
                } else {
                    jsonObject.put(fieldName, fieldValue);
                }
            } catch (Exception e) {
                // manejo de excepciones
            }
        }

        // Imprime el objeto JSON resultante
        System.out.println(jsonObject.toString());
		return jsonObject;

	}


    public static void JsonToObject(JSONObject json) {
    	System.out.println(json.toString());
    	JSONObject jsonObj = new JSONObject(json.toString());
    	for (String key : jsonObj.keySet()) {
            System.out.print(key + ": ");
            
            // Obtener el valor de la propiedad
            Object value = jsonObj.get(key);
            
            // Verificar si el valor es un objeto JSON
            if (value instanceof JSONObject) {
                JSONObject subObj = (JSONObject) value;
                
                // Iterar sobre las propiedades del objeto JSON
                for (String subKey : subObj.keySet()) {
                    System.out.print(subKey + ": " + subObj.get(subKey) + ", ");
                }
                System.out.println();
            }
            
            // Verificar si el valor es un array JSON
            else if (value instanceof JSONArray) {
                JSONArray subArr = (JSONArray) value;
                
                // Iterar sobre los elementos del array JSON
                for (int i = 0; i < subArr.length(); i++) {
                    JSONObject subObj = subArr.getJSONObject(i);
                    
                    // Iterar sobre las propiedades del objeto JSON
                    for (String subKey : subObj.keySet()) {
                        System.out.print(subKey + ": " + subObj.get(subKey) + ", ");
                    }
                    System.out.println();
                }
            }
            
            // El valor es un valor primitivo
            else {
                System.out.println(value);
            }
        }
    }
	
}
