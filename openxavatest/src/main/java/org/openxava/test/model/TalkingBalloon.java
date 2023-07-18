package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

//DON'T CHANGE THE KEY STRUCTURE, USED TO TEST A CASE
@Entity
@IdClass(TalkingBalloonKey.class)
@Data
public class TalkingBalloon {
	
    @Id
    @ManyToOne
    Balloon balloon;

    BigDecimal price;
}