package sk.selenium.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CustomDropDownElement extends ExtendedWebElement {
    public CustomDropDownElement(WebElement webElement) {
        super(webElement);
    }

    public void select(String text) {
        webElement
                .findElements(By.xpath(String.format("//li[contains(text(), '%s')]", text)))
                .get(0)
                .click();
    }
}

