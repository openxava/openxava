package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * tmr
 * @author Javier Paniza
 */

public class ArticleDescriptionSelectionTest extends ModuleTestBase {
		
	public ArticleDescriptionSelectionTest(String testName) {
		super(testName, "ArticleDescriptionSelection");		
	}
			
	public void testOnChangeActionOnReferenceWithReferenedModelWithSearchKeyOnNotIdReference() throws Exception {
		assertValue("articleDescription.article.number", "");
		assertValue("articleDescription.article.description", "");
		assertValue("articleDescription.description", "");
		
		setValue("articleDescription.article.number", "1");
		
		assertValue("articleDescription.article.number", "1");
		assertValue("articleDescription.article.description", "MORCILLA DE BURGOS");
		assertValue("articleDescription.description", "PARA COMER CON PAN");
		
		assertMessage("OnChangeVoidAction executed");
	}
	
}
