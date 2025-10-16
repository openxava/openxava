package org.openxava.chatvoice.model;

import javax.persistence.Column;

import org.hibernate.validator.constraints.Length;
import org.openxava.annotations.*;

import lombok.*;

/**
 * Transient class for chat functionality
 */
@Getter @Setter
public class Chat {
	
	@LabelFormat(LabelFormatType.SMALL) 
	@Column(length = 200)
	String message;

	@Label
	@LabelFormat(LabelFormatType.NO_LABEL) 
	String response;
	
}
