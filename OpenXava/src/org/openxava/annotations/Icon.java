package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A field that display an icon, the user can change the icon. <p>
 * 
 * The icon is stored as an id from <a href="https://materialdesignicons.com/">Material Design Icons</a><br>
 *  
 * The user can change the icon with a dialog to choose among all available icons.<br>
 * 
 * The data type is String with a length of 40.<br>
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Icon
 * &nbsp;@Column(length=40)
 * &nbsp;private String icon;
 * </pre>
 * 
 * It's synonymous of @Stereotype("ICON").
 *
 * @since 6.6
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Icon {
	
}
