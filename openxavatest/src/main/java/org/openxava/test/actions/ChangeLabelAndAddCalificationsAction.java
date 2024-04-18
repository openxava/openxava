package org.openxava.test.actions;

import java.math.*;

import org.openxava.actions.*;

/*
 * @author Chungyen Tsai
 */
public class ChangeLabelAndAddCalificationsAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		changeLabel();
		addCalifications();
		getView().refreshCollections();
	}
	
	private void changeLabel() {
		for (int i = 1; i<4; i++) {
			getView().getSubview("califications").setLabelId("oralTestQ" + i, "Q" + i + ".O");
			getView().getSubview("califications").setLabelId("paperTestQ" + i, "Q" + i + ".P");
			getView().getSubview("califications").setLabelId("midtermExamQ" + i, "Q" + i + ".M");
			getView().getSubview("califications").setLabelId("averageQ" + i, "Q" + i);
		}
		getView().getSubview("califications").setLabelId("anualAverage", "Average");
		getView().getSubview("califications").setLabelId("finalExam", "F.E");
	}
	
	private void addCalifications() {
		for (int j = 0; j<50; j++) {
			for (int i = 1; i<4; i++) {
				getView().setValue("califications."+j+".oralTestQ"+i, new BigDecimal(i));
				getView().setValue("califications."+j+".paperTestQ"+i, new BigDecimal(i));
				getView().setValue("califications."+j+".midtermExamQ"+i, new BigDecimal(i));
				getView().setValue("califications."+j+".averageQ"+i, new BigDecimal(i));
			}
			getView().setValue("califications."+j+".anualAverage", new BigDecimal(5));
			getView().setValue("califications."+j+".finalExam", new BigDecimal(6));
		}

	}
	

}
