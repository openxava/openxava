package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;


@Entity
@Getter
@Setter
public class InputMask extends Identifiable{
	
	
	@Mask("00.000.000")
	String staticMask;
	
	@Mask("00000000")
	String numerical;
	
	@Mask("LLLLLL")
	String alphabetical;
	
	@Mask("######")
	String specialChars;
	
	@Mask("AAAAAA")
	String alphanumeric;
	
}
