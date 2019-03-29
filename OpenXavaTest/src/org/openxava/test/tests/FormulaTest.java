package org.openxava.test.tests;

import java.net.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;



/**
 * 
 * @author Javier Paniza
 */
public class FormulaTest extends ModuleTestBase {
	
	public FormulaTest(String testName) {
		super(testName, "Formula");		
	}
	
	public void testOnSelectElementActionAndSelectedAndSelectedAllAndPaging() throws Exception{
		setConditionValues("L'AJUNTAMENT");
		execute("List.filter");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("selectedIngredientSize", "");
		
		checkRowCollection("ingredients", 0); 
		assertValue("selectedIngredientSize", "1");
		checkRowCollection("ingredients", 1);
		assertValue("selectedIngredientSize", "2");
		checkAllCollection("ingredients");
		assertValue("selectedIngredientSize", "10");
		uncheckAllCollection("ingredients");
		assertValue("selectedIngredientSize", "0");
		
		execute("List.goNextPage", ",collection=ingredients");
		assertCollectionRowCount("ingredients", 3);
		checkRowCollection("ingredients", 10);
		assertValue("selectedIngredientSize", "1");
		checkRowCollection("ingredients", 11);
		assertValue("selectedIngredientSize", "2");
		checkAllCollection("ingredients");
		assertValue("selectedIngredientSize", "3");
	}
	
	public void testOnSelectElementActionAndSelectDeselectFilterWithPaging() throws Exception{
		setConditionValues("L'AJUNTAMENT");
		execute("List.filter");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValueInCollection("ingredients", 0, 1, "AZUCAR"); 
		assertValueInCollection("ingredients", 9, 1, "AZUCAR");
		execute("List.goNextPage", "collection=ingredients");
		assertValueInCollection("ingredients", 2, 1, "CAFE");
		execute("List.goPreviousPage", "collection=ingredients");
		setConditionValues("ingredients", new String[] { "", Ingredient.findByName("CAFE").getOid()});
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 1); 
		assertValueInCollection("ingredients", 0, 1, "CAFE");
		checkRowCollection("ingredients", 0);
		assertValue("selectedIngredientSize", "1");
		setConditionValues("ingredients", new String[] { "", ""});
		execute("List.filter", "collection=ingredients");
		assertValue("selectedIngredientSize", "1");
		assertValueInCollection("ingredients", 0, 1, "AZUCAR");
		assertValueInCollection("ingredients", 9, 1, "AZUCAR");
		assertRowCollectionUnchecked("ingredients", 0);
		checkRowCollection("ingredients", 5);
		assertValue("selectedIngredientSize", "2");
		assertRowCollectionUnchecked("ingredients", 0);
		setConditionValues("ingredients", new String[] { "", Ingredient.findByName("CAFE").getOid()});
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 1);
		assertValueInCollection("ingredients", 0, 1, "CAFE");
		assertRowCollectionChecked("ingredients", 0);
		uncheckRowCollection("ingredients", 0);
		assertRowCollectionUnchecked("ingredients", 0);
		assertValue("selectedIngredientSize", "1");
	};
	
	public void testSelectDeselectAndOrderInACollection() throws Exception {
		assertValueInList(0, 0, "HTML TEST");
		execute("List.viewDetail", "row=0");

		assertCollectionRowCount("ingredients", 2);
		assertValueInCollection("ingredients", 0, 1, "AZUCAR");
		assertValueInCollection("ingredients", 1, 1, "CAFE");
		checkRowCollection("ingredients", 0);
		
		execute("List.orderBy", "property=ingredient.name,collection=ingredients");
		execute("List.orderBy", "property=ingredient.name,collection=ingredients");
		assertValueInCollection("ingredients", 0, 1, "CAFE"); 
		assertValueInCollection("ingredients", 1, 1, "AZUCAR");
		assertRowCollectionUnchecked("ingredients", 0);
		assertRowCollectionChecked("ingredients", 1);
	}
	
	public void testSelectAndFilter() throws Exception {
		assertValueInList(0, 0, "HTML TEST");
		execute("List.viewDetail", "row=0");

		assertCollectionRowCount("ingredients", 2);
		assertValueInCollection("ingredients", 0, 1, "AZUCAR");
		assertValueInCollection("ingredients", 1, 1, "CAFE");
		checkAllCollection("ingredients");

		setConditionValues("ingredients", new String[] { "", Ingredient.findByName("AZUCAR").getOid()});
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 1); 
		assertRowCollectionChecked("ingredients", 0);
		
		setConditionValues("ingredients", new String[] { "", "" });
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 2);
		assertRowCollectionChecked("ingredients", 0);
		assertRowCollectionChecked("ingredients", 1);
	}
	
	public void testPagingInCollection() throws Exception {
		// create objects in database
		Formula formula = Formula.findByName("HTML TEST");
		Ingredient ingredient = Ingredient.findByName("LECHE");
		for (int x = 0; x <= 12; x++){
			FormulaIngredient fi = new FormulaIngredient();			
			fi.setFormula(formula);
			fi.setIngredient(ingredient);
			XPersistence.getManager().persist(fi);
		}
		XPersistence.commit(); 
		
		//
		execute("List.viewDetail", "row=0");
		assertValue("name", "HTML TEST");
		assertCollectionRowCount("ingredients", 10);
		checkRowCollection("ingredients", 0);
		execute("List.goNextPage", "collection=ingredients");
		execute("List.goPreviousPage", "collection=ingredients");
		assertRowCollectionChecked("ingredients", 0);
		
		// remove objects from database
		String sentencia = " DELETE FROM FormulaIngredient WHERE ingredient.oid = :ingredient ";
		Query query = XPersistence.getManager().createQuery(sentencia);
		query.setParameter("ingredient", ingredient.getOid());
		query.executeUpdate();
		XPersistence.commit();
	}
	
	public void testOnSelectElementActionFromAnotherModule() throws Exception {
		changeModule("BeforeGoingToFormula");
		execute("ChangeModule.goFormula");
		
		//
		testOnSelectElementAction(); 
	}
	
	public void testOnSelectElementAction() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertValue("name", "HTML TEST");
		assertCollectionRowCount("ingredients", 2);
		assertValue("selectedIngredientSize", "");
		// selected
		checkRowCollection("ingredients", 0);
		assertNoErrors();
		assertValue("selectedIngredientSize", "1");
		assertValue("selectedIngredientNames", "AZUCAR");
		checkAllCollection("ingredients");
		assertRowCollectionChecked("ingredients", 1);
		assertValue("selectedIngredientSize", "2");
		assertValue("selectedIngredientNames", "AZUCAR,CAFE");
		// deselected
		uncheckRowCollection("ingredients", 0);	
		assertValue("selectedIngredientSize", "1");
		assertValue("selectedIngredientNames", "CAFE");
		uncheckRowCollection("ingredients", 1);	
		assertValue("selectedIngredientSize", "0");
		assertValue("selectedIngredientNames", "");
		// fails to deselect the last selected
		assertRowCollectionUnchecked("ingredients", 0);
		assertRowCollectionUnchecked("ingredients", 1);
		
		// not execute the associated actions if there are no items in the collection
		setConditionValues("ingredients", new String[] { "", "03C6B61AC0A8011600000000AB4E7ACB"} );	// id milk
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 0); 
		uncheckAllCollection("ingredients");
		checkAllCollection("ingredients");
		assertNoErrors();
		assertNoMessages();
	}
	
	public void testImageInsideCollection() throws Exception {
		execute("CRUD.new");		
		setValue("name", "SOMETHING"); 
		execute("Collection.new", "viewObject=xava_view_section0_ingredients");
		execute("ImageEditor.changeImage", "newImageProperty=image"); 
		assertNoErrors();
		assertAction("LoadImage.loadImage"); 		
		String imageUrl = System.getProperty("user.dir") + "/test-images/cake.gif";
		setFileValue("newImage", imageUrl);
		execute("LoadImage.loadImage");
		assertNoErrors();
		
		HtmlPage page = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();		
		URL url = page.getWebResponse().getWebRequest().getUrl(); 
		
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
		
		HtmlImage image = (HtmlImage) page.getElementsByName(decorateId("image")).get(0);  
		String imageURL = null;
		if (image.getSrcAttribute().startsWith("/")) {
			imageURL = urlPrefix + image.getSrcAttribute();
		}
		else {
			String urlBase = Strings.noLastToken(url.getPath(), "/");
			imageURL = urlPrefix + urlBase + image.getSrcAttribute();
		}				
		WebResponse response = getWebClient().getPage(imageURL).getWebResponse();		
		assertTrue("Image not obtained", response.getContentAsString().length() > 0);
		assertEquals("Result is not an image", "image", response.getContentType());		
	}
	
	public void testDependentReferencesAsDescriptionsListWithHiddenKeyInCollection_aggregateCanHasReferenceToModelOfContainerType() throws Exception {		
		execute("CRUD.new");
		setValue("name", "SOMETHING"); 
		execute("Collection.new", "viewObject=xava_view_section0_ingredients");
		assertExists("anotherFormula.oid"); // Reference to a model of 'Formula' type, the same of the container
		
		String [][] ingredients = {
			{ "", "" },
			{ "03C5C64CC0A80116000000009590B64C", "AZUCAR" },
			{ "03C59CF0C0A8011600000000618CC74B", "CAFE" },
			{ "03C6E1ADC0A8011600000000498BC537", "CAFE CON LECHE" },
			{ "03C6B61AC0A8011600000000AB4E7ACB", "LECHE" }, 
			{ "03C6C61DC0A801160000000076765581", "LECHE CONDENSADA"} 
		};
		
		String [][] empty = {
			{ "", "" }
		};
		
		String [][] cafeConLeche = {
				{ "", "" },
				{ "03C5C64CC0A80116000000009590B64C", "AZUCAR" },
				{ "03C59CF0C0A8011600000000618CC74B", "CAFE" },		
				{ "03C6B61AC0A8011600000000AB4E7ACB", "LECHE" }, 		 				
		};
		
		assertValidValues("ingredient.oid", ingredients);
		assertValidValues("accentuate.oid", empty);
		
		setValue("ingredient.oid", "03C6E1ADC0A8011600000000498BC537");
		assertValidValues("ingredient.oid", ingredients);
		assertValidValues("accentuate.oid", cafeConLeche); 
	}
	
	public void testHtmlTextStereotype_editorsInTabDefaulValuest() throws Exception {
		assertNoAction("ListFormat.select", "editor=Charts");
		assertNoAction("ListFormat.select", "editor=List");

		assertValueInList(0, 0, "HTML TEST");
		assertValueInList(0, 1, "Esto es una prueba de HTML Dex oscuro negro y trágico rojo, de muerte y dolor Y largo, verde y marrón como los ojitos del mundo.");
		execute("List.viewDetail", "row=0");
		assertValue("name", "HTML TEST");
		execute("Sections.change", "activeSection=1");
		assertTrue("Expected HTML token not found", getHtml().indexOf("Y largo</strong>,<span style=\"background-color: rgb(153, 204, 0);\"> verde </span>") >= 0);
	}
	
	public void testSingleQuotationMarkAsHtmlValue() throws Exception {
		execute("CRUD.new");
		setValue("name", "L'AJUNTAMENT");
		execute("CRUD.refresh");
		execute("Sections.change", "activeSection=1");
		assertValue("recipe", "L'Ajuntament");
	}
		
}
