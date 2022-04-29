package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.actions.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Views({
	@View( members="type; numberFrom, numberTo; dateFrom, dateTo" ),
	@View( name="Numbers", members="type; numberFrom, numberTo" ), 
	@View( name="Dates", members="type; dateFrom, dateTo" )	
})
public class Ranges extends Identifiable {
	
	
	@OnChange(OnChangeRangesTypeAction.class)
	private Type type;
	public enum Type { ALL, NUMBERS, DATES };

	private int numberFrom;
	private int numberTo;
	
	private Date dateFrom;
	private Date dateTo;
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public int getNumberFrom() {
		return numberFrom;
	}
	public void setNumberFrom(int numberFrom) {
		this.numberFrom = numberFrom;
	}
	public int getNumberTo() {
		return numberTo;
	}
	public void setNumberTo(int numberTo) {
		this.numberTo = numberTo;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}	

}
