package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows to choose a point in a map and store it. <p>
 * 
 * The user can mark in any part of the map then the coordinates change. 
 * Also if he types or pastes the coordinates in the field the map and the mark are repositioned.<br>
 * 
 * The data type is String with a length of 50.<br>
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Coordinates
 * &nbsp;@Column(length=50)
 * &nbsp;private String location;
 * </pre>
 * 
 * It's synonymous of @Stereotype("COORDINATES").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Coordinates {
	
}
