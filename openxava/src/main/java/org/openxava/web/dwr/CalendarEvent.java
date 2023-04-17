package org.openxava.web.dwr;

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
	String key;
	String startName;
	String endName;

    @Override
    public String toString() {
        return "Event [start=" + start + ", end=" + end + ", key=" + key + "]";
    }
}
