package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * Test to verify column widths in the Blog module.
 * 
 * @author Cascade
 */
public class BlogColumnWidthTest extends WebDriverTestBase {
	
	public BlogColumnWidthTest(String testName) {
		super(testName);
	}
	
	/**
	 * Tests that the columns in the Blog module list have the expected widths.
	 */
	public void testBlogColumnWidths() throws Exception {
		// Navigate to the Blog module
		goModule("Blog");
				
		// Get the column elements
		WebElement col0 = getDriver().findElement(By.id("ox_openxavatest_Blog__list_col0"));
		WebElement col2 = getDriver().findElement(By.id("ox_openxavatest_Blog__list_col2"));
		WebElement col3 = getDriver().findElement(By.id("ox_openxavatest_Blog__list_col3"));
		
		// Get the widths of the columns
		int col0Width = col0.getSize().getWidth();
		int col2Width = col2.getSize().getWidth();
		int col3Width = col3.getSize().getWidth();
		
		// Assert that the columns have the expected widths

		// Column 0 has not content, so it should be tiny
		assertEquals("Column 0 width should be 68px", 68, col0Width);

		// Column 2 has a very long content, so it should be 700px as a limit
		assertEquals("Column 2 width should be 700px", 700, col2Width);

		// Column 3 has content, so the width adapt to the size of the content
		assertEquals("Column 3 width should be 275px", 275, col3Width);		
	}
}
