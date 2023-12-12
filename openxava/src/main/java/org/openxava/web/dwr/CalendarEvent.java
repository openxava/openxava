package org.openxava.web.dwr;

import lombok.*;


/**
 * 
 * @since 7.1
 * @author Chungyen Tsai
 */


@Getter @Setter
public class CalendarEvent {

	String start;
	String end;
	String title;
	ExtendedProps extendedProps;
	String startName;
	String endName;

    @Override
    public String toString() {
    	return "{" +
    			"extendedProps: {key: \"" 
    			+ extendedProps + "\"" +
                '}' +
                ", start: \"" + start + '\"' +
                ", title: \"" + title + '\"' +
                ", startName: \"" + startName + '\"' +
                '}';
    }
    
    public static class ExtendedProps {
        private String key;

        @Override
        public String toString() {
            return key;
        }
    }
}
