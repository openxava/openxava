package org.openxava.test.actions;

import org.openxava.actions.*;

public class ChangeLabelAndAddCalifications extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		changeLabel();
		addCalifications();
		getView().refreshCollections();
	}
	
	private void changeLabel() {
		for (int i = 1; i<4; i++) {
			getView().getSubview("califications").setLabelId("oralTestQ" + i, "Q1.O");
			getView().getSubview("califications").setLabelId("paperTestQ" + i, "Q1.P");
			getView().getSubview("califications").setLabelId("midtermExamQ" + i, "Q1.M");
		}
		getView().getSubview("califications").setLabelId("anualAverage", "Average");
		getView().getSubview("califications").setLabelId("finalExam", "F.E");
	}
	
	private void addCalifications() {
		for (int j = 0; j<15; j++) {
			for (int i = 1; i<4; i++) {
				getView().setValue("califications."+j+".oralTestQ"+i, i);
				getView().setValue("califications."+j+".paperTestQ"+i, i);
				getView().setValue("califications."+j+".midtermExamQ"+i, i);
			}
			getView().setValue("califications."+j+".anualAverage", "5");
			getView().setValue("califications."+j+".finalExam", "6");
		}

	}
	

}
