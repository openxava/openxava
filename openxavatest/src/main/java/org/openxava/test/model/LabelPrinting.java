package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.annotations.File;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Getter @Setter
public class LabelPrinting {

	@File(acceptFileTypes=".label")
	@Column(length = 32)
	String definition;

}
