package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;

import lombok.*;


/**
 * 
 * @author Javier Paniza
 */

// DON'T CHANGE THE KEY STRUCTURE, USED TO TEST A CASE
@Entity
@IdClass(BalloonKey.class)
@Data
public class Balloon implements Serializable {
	
    @Id
    @ManyToOne
    private Color color;
    
    // NO ADDITIONAL KEYS, TO TEST A CASE
        
}