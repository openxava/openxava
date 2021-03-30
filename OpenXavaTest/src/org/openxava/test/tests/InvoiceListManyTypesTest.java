package org.openxava.test.tests;

import org.openxava.tab.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceListManyTypesTest extends CustomizeListTestBase {
	
	public InvoiceListManyTypesTest(String testName) {
		super(testName, "InvoiceListManyTypes");		
	}
	
	public void testListConfigurations() throws Exception {
		// Don't separate in several independent tests 
		// because we want test the accumulation of configuration without duplication

		assertListConfigurationsBasicCases(); 
		assertListConfigurationsBooleans();
		assertListConfigurationsEmptyNotEmpty();		
		assertListConfigurationGroups();		
		assertListConfigurationsEnums();
		assertListConfigurationsYearMonthYearMonth(); // TMP FALLA ME QUEDÉ POR AQUÍ: HAY DOS FALLAS
		assertListConfigurationsDescriptionsLists(); 		
		assertListConfigurationsOrdering();
		assertListConfigurationsRanges();
		assertListConfigurationsColumns(); 
		assertListConfigurationsChangeName();
		assertListConfigurationsI18n();
		assertListConfigurationsPersistence();
		assertListConfigurationsRemove();
		assertListConfigurationsNotSavedByDefault();
	}

	private void assertListConfigurationsChangeName() throws Exception { 
		assertListAllConfigurations("Number between 2 and 10", "All", "Email of customer is not empty", "Number between 2 and 12", 
			"Ordered by number", "Ordered by number descending", "Year > 2002 ordered by year descending and number descending", 
			"Ordered by year descending and number descending", "Ordered by year ascending and number descending", 
			"Seller of customer = manuel chavarri", "Year/month of date = 2006/11", "Month of date = 1", "Year of date = 2002", 
			"Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Not paid and name of customer starts with j"); 
		selectListConfiguration("Seller of customer = manuel chavarri");
		assertNoAction("List.saveConfiguration"); 
		execute("List.changeConfiguration"); 
		assertValue("name", "Seller of customer = manuel chavarri");
		setValue("name", "");
		execute("ChangeListConfiguration.change"); 
		assertError("Value for Name is required");
		setValue("name", "Seller Manuel Chavarri");
		execute("ChangeListConfiguration.change"); 
		assertListSelectedConfiguration("Seller Manuel Chavarri");
		assertListAllConfigurations("Seller Manuel Chavarri", "All", "Number between 2 and 10", "Email of customer is not empty", 
			"Number between 2 and 12", "Ordered by number", "Ordered by number descending", 
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Not paid and name of customer starts with j"); 
		assertListRowCount(7);
		selectListConfiguration("Email of customer is not empty");
		execute("List.changeConfiguration"); 
		setValue("name", "Customer with email");
		execute("ChangeListConfiguration.change"); 
		assertListSelectedConfiguration("Customer with email");
		assertListAllConfigurations("Customer with email", "All", "Seller Manuel Chavarri", "Number between 2 and 10", 
			"Number between 2 and 12", "Ordered by number", "Ordered by number descending", 
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Not paid and name of customer starts with j"); 
		assertListRowCount(5);		
		selectListConfiguration("All");
		assertListAllConfigurations("All", "Customer with email", "Seller Manuel Chavarri", "Number between 2 and 10", 
			"Number between 2 and 12", "Ordered by number", "Ordered by number descending", 
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Not paid and name of customer starts with j");  
		assertListRowCount(9);
		selectListConfiguration("Seller Manuel Chavarri");
		assertListSelectedConfiguration("Seller Manuel Chavarri");
		assertListAllConfigurations("Seller Manuel Chavarri", "All", "Customer with email", "Number between 2 and 10", 
			"Number between 2 and 12", "Ordered by number", "Ordered by number descending", 
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Not paid and name of customer starts with j");  
		assertListRowCount(7);
		
		selectListConfiguration("Number between 2 and 10"); // Restoring
	}


	private void assertListConfigurationsColumns() throws Exception { 
		assertListSelectedConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date"); 
		assertLabelInList(8, "Seller of customer"); 
		removeColumn(0);
		assertListColumnCount(8);
		assertLabelInList(0, "Number");
		assertLabelInList(1, "Date"); 
		assertLabelInList(7, "Seller of customer"); 
		
		selectListConfiguration("Number between 2 and 12");
		assertListRowCount(5);		
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date"); 
		assertLabelInList(8, "Seller of customer"); 
		
		selectListConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(8);
		assertLabelInList(0, "Number");
		assertLabelInList(1, "Date"); 
		assertLabelInList(7, "Seller of customer"); 
		
		selectListConfiguration("Email of customer is not empty");
		assertListRowCount(5);
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "Email of customer"); 
		assertLabelInList(5, "Paid");
		assertLabelInList(8, "Seller of customer"); 
		removeColumn(3); // A calculated property, we filter by one on right
		assertListRowCount(5);
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Email of customer");
		assertLabelInList(4, "Paid");
		assertLabelInList(7, "Seller of customer"); 
		
		selectListConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(8);
		assertLabelInList(0, "Number");
		assertLabelInList(1, "Date"); 
		assertLabelInList(7, "Seller of customer"); 
		
		selectListConfiguration("Email of customer is not empty");
		assertListRowCount(5);
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Email of customer");
		assertLabelInList(4, "Paid");
		assertLabelInList(7, "Seller of customer"); 

		execute("List.addColumns");
		checkRow("selectedProperties", "customer.city"); // Read only to test a case
		checkRow("selectedProperties", "comment" ); 
		execute("AddColumns.addColumns");
		assertListRowCount(5);
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Email of customer");
		assertLabelInList(4, "Paid");
		assertLabelInList(7, "Seller of customer"); 
		assertLabelInList(8, "City of customer");
		assertLabelInList(9, "Comment"); 
		assertValue("conditionComparator___3", "not_empty_comparator"); 
		assertValue("conditionValue___8", "");
		
		moveColumn(2, 3);
		assertListRowCount(5);
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Email of customer");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "Paid");				
		assertLabelInList(7, "Seller of customer"); 
		assertLabelInList(8, "City of customer");
		assertLabelInList(9, "Comment"); 
		
		selectListConfiguration("All"); // The columns must be the original ones, it failed
		assertListRowCount(9);
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "Email of customer");
		assertLabelInList(8, "Seller of customer"); 
		
		selectListConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(8);
		assertLabelInList(0, "Number");
		assertLabelInList(1, "Date"); 
		assertLabelInList(7, "Seller of customer"); 
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListSelectedConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date"); 
		assertLabelInList(8, "Seller of customer"); 
		
		selectListConfiguration("Email of customer is not empty");
		assertListRowCount(5);
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Email of customer");
		assertLabelInList(3, "Date");
		assertLabelInList(4, "Paid");		
		assertLabelInList(7, "Seller of customer"); 
		assertLabelInList(8, "City of customer");
		assertLabelInList(9, "Comment"); 
		assertValue("conditionComparator___2", "not_empty_comparator");
		
		removeColumn(1);
		removeColumn(0);
		assertListRowCount(5);
		assertListColumnCount(8);
		assertLabelInList(0, "Email of customer");
		assertLabelInList(1, "Date");
		assertLabelInList(2, "Paid");		
		assertLabelInList(5, "Seller of customer"); 
		assertLabelInList(6, "City of customer");		
		assertLabelInList(7, "Comment"); 
		
		selectListConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date"); 
		assertLabelInList(8, "Seller of customer"); 
	}


	private void assertListConfigurationsRanges() throws Exception {   
		selectListConfiguration("All");
		assertListRowCount(9);
		setConditionComparators("", Tab.RANGE_COMPARATOR);
		setConditionValues("", "2");
		setConditionValuesTo("", "10");
		execute("List.filter");
		assertListSelectedConfiguration("Number between 2 and 10");
		assertListAllConfigurations("Number between 2 and 10", "All", "Ordered by number", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Seller of customer = manuel chavarri", "Year/month of date = 2006/11", 
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid"); 
		assertListRowCount(3);
		saveConfiguration(); 
		
		setConditionValues("", "2"); // The same initial value of other one, to test a bug. 
		setConditionValuesTo("", "12");
		execute("List.filter");
		assertListSelectedConfiguration("Number between 2 and 12");
		assertListAllConfigurations("Number between 2 and 12", "All", "Number between 2 and 10", "Ordered by number", 
			"Ordered by number descending", "Year > 2002 ordered by year descending and number descending", 
			"Ordered by year descending and number descending", "Ordered by year ascending and number descending", 
			"Seller of customer = manuel chavarri", "Year/month of date = 2006/11", "Month of date = 1", "Year of date = 2002", 
			"Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j");  
		assertListRowCount(5); 
		saveConfiguration(); 
		
		selectListConfiguration("Number between 2 and 10");
		assertListSelectedConfiguration("Number between 2 and 10");
		assertListAllConfigurations("Number between 2 and 10", "All", "Number between 2 and 12", "Ordered by number", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Seller of customer = manuel chavarri", "Year/month of date = 2006/11", 
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j"); 
		assertListRowCount(3);
	}

	private void assertListConfigurationsPersistence() throws Exception {
		selectListConfiguration("Year/month of date = 2006/11");
		assertListSelectedConfiguration("Year/month of date = 2006/11");
		assertListAllConfigurations("Year/month of date = 2006/11", "Todos", 
			"Number between 2 and 10", "Seller Manuel Chavarri", "Customer with email",  
			"Number between 2 and 12", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");

		assertListRowCount(2);
		
		resetModule();
		assertListSelectedConfiguration("Year/month of date = 2006/11");
		assertListAllConfigurations("Year/month of date = 2006/11", "Todos", 
			"Number between 2 and 10", "Seller Manuel Chavarri", "Customer with email",  
			"Number between 2 and 12", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");
		assertListRowCount(2);
		
		selectListConfiguration("Ordered by year descending and number descending"); // With ordering, also not the first one, to test a case
		assertListSelectedConfiguration("Ordered by year descending and number descending");
		assertListAllConfigurations("Ordered by year descending and number descending", "Todos", 
			"Year/month of date = 2006/11", "Number between 2 and 10", "Seller Manuel Chavarri", 
			"Customer with email", "Number between 2 and 12", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");
		assertListRowCount(9);
		assertValueInList(3, "year", "2004");
		assertValueInList(3, "number", "12");
		assertValueInList(4, "year", "2004");
		assertValueInList(4, "number", "11");
		assertValueInList(5, "year", "2004");
		assertValueInList(5, "number", "10");
		assertValueInList(6, "year", "2004");
		assertValueInList(6, "number", "9");
		assertValueInList(7, "year", "2004");
		assertValueInList(7, "number", "2");
				
		selectListConfiguration("Number between 2 and 12");
		assertListSelectedConfiguration("Number between 2 and 12");
		assertListAllConfigurations("Number between 2 and 12", "Todos", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Number between 2 and 10", "Seller Manuel Chavarri", 
			"Customer with email", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");
		assertListRowCount(5);
				
		selectListConfiguration("Customer with email"); 
		assertListRowCount(5);
		assertListColumnCount(8);
		assertLabelInList(0, "Correo electrónico de cliente");
		assertLabelInList(1, "Fecha");
		assertLabelInList(2, "Pagada");		
		assertLabelInList(5, "Comercial de cliente"); 
		assertLabelInList(6, "Población de cliente");
		assertLabelInList(7, "Comentario"); 
		assertValue("conditionComparator___0", "not_empty_comparator");
		
		selectListConfiguration("Number between 2 and 10");
		assertListRowCount(3);
		assertListColumnCount(9);
		assertLabelInList(0, "Año");
		assertLabelInList(1, "Número");
		assertLabelInList(2, "Fecha"); 
		assertLabelInList(8, "Comercial de cliente"); 
		
		
		selectListConfiguration("Seller Manuel Chavarri");
		assertListSelectedConfiguration("Seller Manuel Chavarri");
		assertListAllConfigurations("Seller Manuel Chavarri", "Todos", 
			"Number between 2 and 10", "Customer with email", "Number between 2 and 12", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");
		assertListRowCount(7);		
	}
	
	private void assertListConfigurationsRemove() throws Exception { 
		execute("List.changeConfiguration");
		assertValue("name", "Seller Manuel Chavarri");
		execute("ChangeListConfiguration.remove");
		assertMessage("Consulta quitada");
		assertListAllConfigurations("Todos", 
			"Number between 2 and 10", "Customer with email", "Number between 2 and 12", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");		
		assertListRowCount(9);		
	}

	private void assertListConfigurationsNotSavedByDefault() throws Exception { 
		assertListSelectedConfiguration("Todos");
		setConditionValues("2004"); 
		execute("List.filter");
		
		modifyColumns(); // Modifying column should not save configurations per se
		
		assertListSelectedConfiguration("Año = 2004");
		assertListAllConfigurations("Año = 2004", "Todos", 
			"Number between 2 and 10", "Customer with email", "Number between 2 and 12", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");		
		assertListRowCount(5);
		
		assertAction("List.saveConfiguration");
		assertNoAction("List.changeConfiguration");
		clearCondition();
		assertNoAction("List.saveConfiguration");
		assertNoAction("List.changeConfiguration");
		
		setConditionValues("2002"); 
		execute("List.filter");
		assertListSelectedConfiguration("Año = 2002");
		assertListAllConfigurations("Año = 2002", "Todos", 
			"Number between 2 and 10", "Customer with email", "Number between 2 and 12", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");		
		assertListRowCount(1);
		
		resetModule();
		assertListSelectedConfiguration("Todos");
		assertListAllConfigurations("Todos", 
			"Number between 2 and 10", "Customer with email", "Number between 2 and 12", 
			"Ordered by year descending and number descending", "Year/month of date = 2006/11", 
			"Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number",  
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Not paid and name of customer starts with j");		
	}

	private void modifyColumns() throws Exception { 
		removeColumn(5); 						
		execute("List.addColumns");
		checkRow("selectedProperties", "vat");  
		execute("AddColumns.addColumns");
		moveColumn(2, 3);
		execute("List.addColumns");  
		execute("AddColumns.restoreDefault");
	}

	private void assertListConfigurationsI18n() throws Exception {
		setLocale("es");
		assertListSelectedConfiguration("Number between 2 and 10");
		execute("List.orderBy", "property=customer.name");
		assertListSelectedConfiguration("Número entre 2 y 10 ordenado por nombre de cliente"); 
		// We don't save the configuration to test the case: ordering does not save configuration automatically (old behavior)
		// Since v6.5 all configuration names are saved as custom names so they are not translated
		assertListAllConfigurations("Número entre 2 y 10 ordenado por nombre de cliente",
			"Todos", "Number between 2 and 10", "Seller Manuel Chavarri", "Customer with email",  
			"Number between 2 and 12", "Ordered by number descending",   
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Ordered by number", "Year/month of date = 2006/11", 
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty");
		assertListRowCount(3); 
	}

	private void assertListConfigurationsOrdering() throws Exception { 
		clearCondition();
		execute("List.orderBy", "property=number");
		assertListSelectedConfiguration("Ordered by number");
		assertListRowCount(9);
		assertValueInList(0, "number", "1");
		assertValueInList(8, "number", "14");
		saveConfiguration(); 
		
		execute("List.orderBy", "property=number");
		assertListSelectedConfiguration("Ordered by number descending");
		assertListRowCount(9);
		assertValueInList(0, "number", "14");
		assertValueInList(8, "number", "1");
		saveConfiguration(); 
		
		execute("List.orderBy", "property=year");
		assertListSelectedConfiguration("Ordered by year ascending and number descending");
		assertListRowCount(9);
		assertValueInList(1, "year", "2004");
		assertValueInList(1, "number", "12");
		assertValueInList(2, "year", "2004");
		assertValueInList(2, "number", "11");
		assertValueInList(3, "year", "2004");
		assertValueInList(3, "number", "10");
		assertValueInList(4, "year", "2004");
		assertValueInList(4, "number", "9");
		assertValueInList(5, "year", "2004");
		assertValueInList(5, "number", "2");
		saveConfiguration(); 
		
		execute("List.orderBy", "property=year");
		assertListSelectedConfiguration("Ordered by year descending and number descending");
		assertListRowCount(9);
		assertValueInList(3, "year", "2004");
		assertValueInList(3, "number", "12");
		assertValueInList(4, "year", "2004");
		assertValueInList(4, "number", "11");
		assertValueInList(5, "year", "2004");
		assertValueInList(5, "number", "10");
		assertValueInList(6, "year", "2004");
		assertValueInList(6, "number", "9");
		assertValueInList(7, "year", "2004");
		assertValueInList(7, "number", "2");
		saveConfiguration(); 
	
		setConditionComparators(">");
		setConditionValues("2002");
		execute("List.filter");
		assertListSelectedConfiguration("Year > 2002 ordered by year descending and number descending");
		assertListRowCount(8);
		assertValueInList(3, "year", "2004");
		assertValueInList(3, "number", "12");
		assertValueInList(4, "year", "2004");
		assertValueInList(4, "number", "11");
		assertValueInList(5, "year", "2004");
		assertValueInList(5, "number", "10");
		assertValueInList(6, "year", "2004");
		assertValueInList(6, "number", "9");
		assertValueInList(7, "year", "2004");
		assertValueInList(7, "number", "2");
		saveConfiguration(); 
		
		assertListAllConfigurations("Year > 2002 ordered by year descending and number descending", "All",
			"Ordered by year descending and number descending", "Ordered by year ascending and number descending", 
			"Ordered by number descending", "Ordered by number", "Seller of customer = manuel chavarri", 
			"Year/month of date = 2006/11", "Month of date = 1", "Year of date = 2002", "Type of customer = steady", 
			"Type of customer = normal", "Year in group(2002, 2004)", "Year not in group(2002, 2004)", 
			"Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid"); 
		
		selectListConfiguration("Ordered by number descending");
		assertListSelectedConfiguration("Ordered by number descending");
		assertListAllConfigurations("Ordered by number descending", "All", "Year > 2002 ordered by year descending and number descending", 
			"Ordered by year descending and number descending", "Ordered by year ascending and number descending", 
			"Ordered by number", "Seller of customer = manuel chavarri", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid");  
		assertListRowCount(9);
		assertValueInList(0, "number", "14");
		assertValueInList(8, "number", "1");
		
		selectListConfiguration("All");
		assertListRowCount(9);
		assertValueInList(0, "number", "1");
		assertValueInList(8, "number", "1");
		execute("List.orderBy", "property=number"); // We already have this ordering...
		assertListSelectedConfiguration("Ordered by number"); // ...but is not duplicated
		assertNoAction("List.saveConfiguration");
		assertAction("List.changeConfiguration");

		assertListAllConfigurations("Ordered by number", "All", "Ordered by number descending", 
			"Year > 2002 ordered by year descending and number descending", "Ordered by year descending and number descending", 
			"Ordered by year ascending and number descending", "Seller of customer = manuel chavarri", "Year/month of date = 2006/11", 
			"Month of date = 1", "Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid");  
		assertListRowCount(9);
		assertValueInList(0, "number", "1");
		assertValueInList(8, "number", "14");		
	}
	
	private void assertListConfigurationsDescriptionsLists() throws Exception {
		clearCondition();
		setConditionValues("", "", "", "", "", "", "", "1:_:MANUEL CHAVARRI");
		execute("List.filter");
		assertListSelectedConfiguration("Seller of customer = manuel chavarri");
		assertListAllConfigurations("Seller of customer = manuel chavarri", "All", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(7);	
		saveConfiguration(); 

		selectListConfiguration("All");
		assertListSelectedConfiguration("All");		
		assertListAllConfigurations("All", "Seller of customer = manuel chavarri", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(9);
		
		selectListConfiguration("Seller of customer = manuel chavarri");
		assertListSelectedConfiguration("Seller of customer = manuel chavarri");
		assertListAllConfigurations("Seller of customer = manuel chavarri", "All", "Year/month of date = 2006/11", "Month of date = 1", 
			"Year of date = 2002", "Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(7);
	}


	private void assertListConfigurationsYearMonthYearMonth() throws Exception {
		clearCondition();
		setConditionComparators("", "", Tab.YEAR_COMPARATOR); 
		setConditionValues("", "", "2002");
		execute("List.filter");
		assertListSelectedConfiguration("Year of date = 2002");
		assertListAllConfigurations("Year of date = 2002", "All", "Type of customer = steady", "Type of customer = normal", 
			"Year in group(2002, 2004)", "Year not in group(2002, 2004)", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Email of customer is not empty", "Not paid and name of customer starts with j", 
			"Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(1);
		saveConfiguration(); 
		
		setConditionValues("", "", "2006/11");
		setConditionComparators("", "", Tab.YEAR_MONTH_COMPARATOR); 
		assertListSelectedConfiguration("Year/month of date = 2006/11");
		assertListAllConfigurations("Year/month of date = 2006/11", "All", "Year of date = 2002", "Type of customer = steady", 
			"Type of customer = normal", "Year in group(2002, 2004)", "Year not in group(2002, 2004)", 
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(2);		
		saveConfiguration(); 

		setConditionValues("", "", "1");
		setConditionComparators("", "", Tab.MONTH_COMPARATOR); 
		assertListSelectedConfiguration("Month of date = 1");	
		assertListAllConfigurations("Month of date = 1", "All", "Year/month of date = 2006/11", "Year of date = 2002", 
			"Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", "Year not in group(2002, 2004)", 
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(3);
		saveConfiguration(); 
		
		selectListConfiguration("Year/month of date = 2006/11");
		assertListSelectedConfiguration("Year/month of date = 2006/11");
		assertListAllConfigurations("Year/month of date = 2006/11", "All", "Month of date = 1", "Year of date = 2002", 
			"Type of customer = steady", "Type of customer = normal", "Year in group(2002, 2004)", "Year not in group(2002, 2004)", 
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(2);
	}


	private void assertListConfigurationsEnums() throws Exception {
		clearCondition();
		setConditionComparators("", "", "", "", "", "", "=");
		String steady = usesAnnotatedPOJO()?"1":"2";  
		setConditionValues("", "", "", "", "", "", steady); 
		
		execute("List.filter");
		assertListSelectedConfiguration("Type of customer = steady");
		assertListAllConfigurations("Type of customer = steady", "All", "Year in group(2002, 2004)", "Year not in group(2002, 2004)",  
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");		
		
		assertListRowCount(5);
		saveConfiguration(); 

		String normal = usesAnnotatedPOJO()?"0":"1"; 
		setConditionValues("", "", "", "", "", "", normal); 
		execute("List.filter");
		assertListSelectedConfiguration("Type of customer = normal");
		assertListAllConfigurations("Type of customer = normal", "All", "Type of customer = steady", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(4);
		saveConfiguration(); 
		
		selectListConfiguration("Type of customer = steady");
		assertListSelectedConfiguration("Type of customer = steady");
		assertListAllConfigurations("Type of customer = steady", "All", "Type of customer = normal", "Year in group(2002, 2004)", 
			"Year not in group(2002, 2004)", "Email of customer is not empty and not paid", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(5);
	}


	private void assertListConfigurationGroups() throws Exception {
		clearCondition();
		setConditionComparators(Tab.IN_COMPARATOR); 
		setConditionValues("2002, 2004");
		execute("List.filter");
		assertListSelectedConfiguration("Year in group(2002, 2004)");
		assertListAllConfigurations("Year in group(2002, 2004)", "All", "Email of customer is not empty and not paid", 
			"Email of customer is empty", "Email of customer is not empty", "Not paid and name of customer starts with j", 
			"Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(6);
		saveConfiguration(); 
	
		setConditionComparators(Tab.NOT_IN_COMPARATOR); 
		execute("List.filter");
		assertListSelectedConfiguration("Year not in group(2002, 2004)");
		assertListAllConfigurations("Year not in group(2002, 2004)", "All", "Year in group(2002, 2004)", 
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");		
		assertListRowCount(3);	
		saveConfiguration(); 
		
		selectListConfiguration("Year in group(2002, 2004)");
		assertListSelectedConfiguration("Year in group(2002, 2004)");
		assertListAllConfigurations("Year in group(2002, 2004)", "All", "Year not in group(2002, 2004)",  
			"Email of customer is not empty and not paid", "Email of customer is empty", "Email of customer is not empty", 
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(6);
	}


	private void assertListConfigurationsEmptyNotEmpty() throws Exception {
		clearCondition();
		setConditionComparators("=", "=", "=", Tab.NOT_EMPTY_COMPARATOR); // Really we don't want to test this, it is to not rewrite all tests to remove "Email of customer is not empty" because before all the queries were added
		saveConfiguration();
		setConditionComparators("=", "=", "=", Tab.NOT_EMPTY_COMPARATOR, "<>"); // To test not empty combined with boolean(false value) because of a bug
		
		assertListSelectedConfiguration("Email of customer is not empty and not paid");
		assertListAllConfigurations("Email of customer is not empty and not paid", "All", "Email of customer is not empty",  
			"Not paid and name of customer starts with j", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1");
		assertListRowCount(5);
		saveConfiguration(); 
		
		clearCondition();
		setConditionComparators("=", "=", "=", Tab.EMPTY_COMPARATOR); 
		setConditionValues("", "", "", "");
		execute("List.filter");
		assertListSelectedConfiguration("Email of customer is empty");
		assertListAllConfigurations("Email of customer is empty", "All", "Email of customer is not empty and not paid",
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(4);
		saveConfiguration(); 
		
		selectListConfiguration("Email of customer is not empty and not paid");
		assertListSelectedConfiguration("Email of customer is not empty and not paid");
		assertListAllConfigurations("Email of customer is not empty and not paid", "All", "Email of customer is empty", 
			"Email of customer is not empty", "Not paid and name of customer starts with j", "Paid", "Not paid", 
			"Year 2004 and number > 10", "Number = 1");
		assertListRowCount(5);
	}


	private void assertListConfigurationsBooleans() throws Exception {
		setConditionValues("", "", "", "", "true"); 
		setConditionComparators("=", "=", "=", "=", "=");
		execute("List.filter");
		assertListSelectedConfiguration("Paid");
		assertListAllConfigurations("Paid", "All", "Year 2004 and number > 10", "Number = 1"); 
		assertListRowCount(1);	
		saveConfiguration(); 

		setConditionValues("", "", "", "", "true"); 
		setConditionComparators("=", "=", "=", "=", "<>");
		execute("List.filter");
		assertListSelectedConfiguration("Not paid");
		assertListAllConfigurations("Not paid", "All", "Paid", "Year 2004 and number > 10", "Number = 1"); 
		assertListRowCount(8);	
		saveConfiguration(); 
				
		setConditionComparators("=", "=", "=", "=", "<>", Tab.STARTS_COMPARATOR);
		setConditionValues("", "", "", "", "true", "j"); 
		execute("List.filter");
		assertListSelectedConfiguration("Not paid and name of customer starts with j"); // Here the boolean is not the last one
		assertListAllConfigurations("Not paid and name of customer starts with j", "All", "Not paid", "Paid", "Year 2004 and number > 10", "Number = 1"); 
		assertListRowCount(6);
		saveConfiguration(); 
		
		selectListConfiguration("Paid");
		assertListSelectedConfiguration("Paid");
		assertListAllConfigurations("Paid", "All", "Not paid and name of customer starts with j", "Not paid", "Year 2004 and number > 10", "Number = 1"); 
		assertListRowCount(1);

		selectListConfiguration("Not paid and name of customer starts with j");
		assertListSelectedConfiguration("Not paid and name of customer starts with j");
		assertListAllConfigurations("Not paid and name of customer starts with j", "All", "Paid", "Not paid", "Year 2004 and number > 10", "Number = 1"); 
		assertListRowCount(6);
	}

	private void assertListConfigurationsBasicCases() throws Exception {
		assertListSelectedConfiguration("All");
		assertListAllConfigurations("All");
		assertListRowCount(9);
		
		setConditionValues("", "1"); // We try the second one with the first empty to test a bug
		execute("List.filter");
		assertListSelectedConfiguration("Number = 1");
		assertListAllConfigurations("Number = 1", "All"); 
		assertListRowCount(3);
		saveConfiguration(); 
		
		setConditionValues("2004", "10"); 
		setConditionComparators("=", ">"); 
		execute("List.filter");
		assertListSelectedConfiguration("Year = 2004 and number > 10");
		assertListAllConfigurations("Year = 2004 and number > 10", "All", "Number = 1"); 
		assertListRowCount(2);
		assertNoAction("List.changeConfiguration");
		execute("List.saveConfiguration");
		setValue("name", "Year 2004 and number > 10");
		execute("SaveListConfiguration.save");
		assertListSelectedConfiguration("Year 2004 and number > 10");
		assertListAllConfigurations("Year 2004 and number > 10", "All", "Number = 1"); 
		assertListRowCount(2);
		
		selectListConfiguration("Number = 1"); 
		assertListSelectedConfiguration("Number = 1");
		assertListAllConfigurations("Number = 1", "All", "Year 2004 and number > 10"); 
		assertListRowCount(3);	
		assertValue("conditionValue___0", ""); 
		assertValue("conditionValue___1", "1"); 
		assertValue("conditionValue___2", ""); 
		
		selectListConfiguration("All");
		assertListSelectedConfiguration("All");
		assertListAllConfigurations("All", "Number = 1", "Year 2004 and number > 10"); 
		assertListRowCount(9);
		assertValue("conditionValue___0", ""); 
		assertValue("conditionValue___1", ""); 
		assertValue("conditionValue___2", ""); 
				
		selectListConfiguration("Year 2004 and number > 10");
		assertListSelectedConfiguration("Year 2004 and number > 10");
		assertListAllConfigurations("Year 2004 and number > 10", "All", "Number = 1"); 
		assertListRowCount(2);

		setConditionValues("", "1"); // To test not duplicated in combo
		setConditionComparators("=", "="); 
		execute("List.filter");
		assertNoAction("List.saveConfiguration");
		assertAction("List.changeConfiguration");
		assertListSelectedConfiguration("Number = 1");
		assertListAllConfigurations("Number = 1", "All", "Year 2004 and number > 10");
		assertListRowCount(3);
		
		
		setConditionValues("2004", "10"); // To test not duplicated in combo again, curiously it failed the second time
		setConditionComparators("=", ">");
		execute("List.filter");
		assertNoAction("List.saveConfiguration");
		assertAction("List.changeConfiguration");
		assertListSelectedConfiguration("Year 2004 and number > 10");
		assertListAllConfigurations("Year 2004 and number > 10", "All", "Number = 1"); 
		assertListRowCount(2);
	}

	private void saveConfiguration() throws Exception { 
		assertNoAction("List.changeConfiguration");
		execute("List.saveConfiguration");
		execute("SaveListConfiguration.save");
	}
		
}