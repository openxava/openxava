package org.openxava.chatvoice.model;

import org.openxava.annotations.LabelFormat;
import org.openxava.annotations.LabelFormatType;

import lombok.*;

/**
 * Transient class for chat functionality
 */
@Getter @Setter
public class Chat {
	
	@org.openxava.chatvoice.annotations.Chat
	@LabelFormat(LabelFormatType.NO_LABEL)
	String chat;
	
}
