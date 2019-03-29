package org.openxava.test.tests;

import org.hibernate.envers.*;
import org.openxava.test.model.*;

/**
 * @author Jeromy Altuna
 */
public class ExamTest extends ExamBaseTest {
	
	public ExamTest(String testName) {
		super(testName, "Exam");
	}
	
	public void testCreateExamWithAtLeastOneQuestion() throws Exception {
		execute("Mode.list"); 
		assertListRowCount(0);
		execute("CRUD.new");
		setValue("name", "ADMISSION");
		execute("CRUD.save");
		assertError("It's required at least 1 element in Questioning of Exam");
		execute("Collection.new", "viewObject=xava_view_questioning");
		setValue("name", "QUESTION 1");
		execute("Collection.save");
		assertMessagesCount(2);
		assertMessage("Exam created successfully");
		assertMessage("Question created successfully");
		execute("Mode.list");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("name", "ADMISSION");
		assertValueInCollection("questioning", 0, "name", "QUESTION 1");
		execute("CRUD.delete");
		assertMessage("Exam deleted successfully");
	}
	
	public void testRemoveElementsFromQuestioningAndLeaveAtLeastOneQuestion() throws Exception {
		setValue("name", "ADMISSION");
		execute("Collection.new", "viewObject=xava_view_questioning");
		for (int i = 1; i <= 3; i++){
			setValue("name", "" + i);
			execute("Collection.saveAndStay");
			assertMessage("Question created successfully"); 
		}
		execute("Collection.hideDetail");
		assertValue("name", "ADMISSION");
		assertCollectionRowCount("questioning", 3);
		
		checkAllCollection("questioning");
		execute("Collection.removeSelected", "viewObject=xava_view_questioning");
		assertError("It's required at least 1 element in Questioning of Exam"); 
		uncheckRowCollection("questioning", 0);
		uncheckRowCollection("questioning", 1);
		execute("Collection.removeSelected", "viewObject=xava_view_questioning");
		assertMessage("Question deleted from database");
		assertCollectionRowCount("questioning", 2);
		execute("Collection.edit", "row=1,viewObject=xava_view_questioning");
		execute("Collection.remove");
		assertMessage("Question deleted from database");
		assertCollectionRowCount("questioning", 1);
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_questioning");
		assertError("It's required at least 1 element in Questioning of Exam");
		execute("Collection.edit", "row=0,viewObject=xava_view_questioning");
		execute ("Collection.remove");
		assertError("It's required at least 1 element in Questioning of Exam");
		execute("Collection.hideDetail");
		assertCollectionRowCount("questioning", 1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testAudit() throws Exception {
		setValue("name", "ADMISSION");
		execute("Collection.new", "viewObject=xava_view_questioning");
		setValue("name", "QUESTION 1");
		execute("Collection.save");
		assertNoErrors();
		Number rn = getLastRevisionNumber();
		assertValueInAuditTable("name", "ADMISSION", Exam.class, rn);
		assertValueInAuditTable("name", "QUESTION 1", Question.class, rn);
		assertRevTypeInAuditTable(RevisionType.ADD, Exam.class, rn);
		assertRevTypeInAuditTable(RevisionType.ADD, Question.class, rn);
		
		execute("Collection.edit", "row=0,viewObject=xava_view_questioning");
		setValue("name", "QUESTION 2");
		execute("Collection.save");
		rn = getLastRevisionNumber();
		assertValueInAuditTable("name", "QUESTION 2", Question.class, rn);
		assertRevTypeInAuditTable(RevisionType.MOD, Question.class, rn);
		
		execute("CRUD.delete");
		rn = getLastRevisionNumber();
		assertValueInAuditTable("name", null, Exam.class, rn);
		assertValueInAuditTable("name", null, Question.class, rn);
		assertRevTypeInAuditTable(RevisionType.DEL, Exam.class, rn);
		assertRevTypeInAuditTable(RevisionType.DEL, Question.class, rn);
	}

	@Override
	protected String getPersistenceUnit() {
		return "junit";
	}

	@Override
	protected String getDefaultSchema() {
		if (getDatasource() == Datasource.REAL) return "XAVATEST";
		if (getDatasource() == Datasource.SIMULATION) return "SIMULATION";
		return null;
	}
}
