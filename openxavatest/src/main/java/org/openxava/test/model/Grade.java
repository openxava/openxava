package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
public class Grade {

	@ElementCollection
	@EditOnly
	@ListProperties("oralTestQ1, paperTestQ1, midtermExamQ1, oralTestQ2, paperTestQ2, midtermExamQ2, oralTestQ3, paperTestQ3, midtermExamQ3, anualAverage, finalExam")
	Collection<Calification> califications;
	
}
