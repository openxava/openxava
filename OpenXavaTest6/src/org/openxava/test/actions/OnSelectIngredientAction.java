package org.openxava.test.actions;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.OnSelectElementBaseAction;
import org.openxava.test.model.FormulaIngredient;
import org.openxava.util.Is;
import org.openxava.util.Strings;

/**
 * Create on 14/04/2009 (18:37:04)
 * 
 * @autor Ana Andr�s 
 */
public class OnSelectIngredientAction extends OnSelectElementBaseAction {
	private static Log log = LogFactory.getLog(OnSelectIngredientAction.class);
	
	public void execute() throws Exception { 
		// size
		int size = getView().getValueInt("selectedIngredientSize");
		size = isSelected() ? size + 1 : size - 1;		
		getView().setValue("selectedIngredientSize", new Integer(size));
		// names
		String names = getView().getValueString("selectedIngredientNames");
		
		// if the collection has many elements, you get the object from the tab of the viewCollection
		// FormulaIngredient formula = (FormulaIngredient) getView().getSubView("ingredients").
		//		getCollectionTab().getTableModel().getObjectAt(getRow());
		FormulaIngredient formula = 
			(FormulaIngredient) getCollectionElementView().getCollectionObjects().get(getRow());
		
		if (isSelected()){
			String newName = formula.getIngredient().getName();
			getView().setValue("selectedIngredientNames", Is.empty(names) ? newName : names + "," + newName); 
		}
		else{
			Collection collectionNames = Strings.toCollection(names);
			collectionNames.remove(formula.getIngredient().getName());
			getView().setValue("selectedIngredientNames", Strings.toString(collectionNames)); 
		}
	}

}
