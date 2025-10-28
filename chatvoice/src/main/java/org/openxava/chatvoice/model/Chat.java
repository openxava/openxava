package org.openxava.chatvoice.model;

import org.openxava.annotations.LabelFormat;
import org.openxava.annotations.LabelFormatType;

import lombok.*;

/**
 * Transient class for chat functionality
 */
@Getter @Setter
public class Chat {
	
	// @LabelFormat(LabelFormatType.SMALL) 
	// @Column(length = 200)
	// String message;

	// @Label
	// @LabelFormat(LabelFormatType.NO_LABEL) 
	// String response;
	
	@org.openxava.chatvoice.annotations.Chat
	@LabelFormat(LabelFormatType.NO_LABEL)
	String chat;
	
}
