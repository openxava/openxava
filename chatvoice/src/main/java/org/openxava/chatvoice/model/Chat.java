package org.openxava.chatvoice.model;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Transient class for chat functionality
 */
@Getter @Setter
public class Chat {
	
	@TextArea
	String message;
	
}
