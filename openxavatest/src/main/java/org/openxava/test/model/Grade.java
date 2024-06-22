package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
/*
 * @author Chungyen Tsai
 */
@Getter @Setter
@View(members="califications")
public class Grade {

	@ElementCollection
	@EditOnly
	@ListProperties("oralTestQ1, paperTestQ1, midtermExamQ1, averageQ1, oralTestQ2, paperTestQ2, midtermExamQ2, averageQ2, oralTestQ3, paperTestQ3, midtermExamQ3, averageQ3, anualAverage, finalExam")
	Collection<Calification> califications;
	
}
