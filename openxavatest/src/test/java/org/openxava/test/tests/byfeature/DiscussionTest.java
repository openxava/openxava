package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

/**
 * To test @Discussion related issue with Selenium.
 *
 * @author Javier Paniza
 */

public class DiscussionTest extends WebDriverTestBase {

	
	protected void tearDown() throws Exception { // tmr
	}
	
	protected WebDriver createWebDriver(String lang) {
		return new FirefoxDriver();
	}
	
	public void testCommentButtonsNotRespondAfterFocusOnCommentAsFirstActionInDialog() throws Exception {
		goModule("IncidentActivity");
		execute("Reference.createNew", "model=Incident,keyProperty=incident.id");
		wait(getDriver(), By.className("tox-edit-area")); // tmr Quitar el getDriver() del wait
		scrollToBottom();
		WebElement commentArea = getDriver().findElements(By.className("tox-edit-area")).get(1);
		commentArea.click();
		// TMR ME QUEDÉ POR AQUÍ. LO DE ARRIBA FUNCIONA, FALTA COMPROBAR QUE LOS BOTONES APARECE/DESAPARECEN Y EL CANCEL VA
	}	
	
	private void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
	
}
