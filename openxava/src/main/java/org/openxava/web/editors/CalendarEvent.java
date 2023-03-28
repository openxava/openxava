package org.openxava.web.editors;

import java.util.*;

import lombok.*;


/**
 * @since 7.1
 * @author Chungyen Tsai
 */


@Getter @Setter
public class CalendarEvent {

	String start;
	String end;
	String title;
	String row;
	
	String startLabel;
	String endLabel;
	
	//no usado aun
	Date startAsDate;
	
}
