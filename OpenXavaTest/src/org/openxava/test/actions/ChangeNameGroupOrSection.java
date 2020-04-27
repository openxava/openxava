package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Ana Andres
 * Created on 15 abr. 2020
 */
public class ChangeNameGroupOrSection extends ViewBaseAction{
	private boolean group = true;

	@Override
	public void execute() throws Exception {
		if (isGroup()) {
//			getView().getGroupView("deliveryData").setTitle("Grupo alternativo");
			getView().setTitle("Grupo alternativo2", "deliveryData");
			getView().setLabelId("number", "otro2");
			System.out.println("[ChangeNameGroupOrSection.execute] cambio...."); // tmp
		}
		else {	// section
			getView().getSectionView(1).setTitle("Sección alternativa");
		}
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

}
