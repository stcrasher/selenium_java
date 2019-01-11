package sk.selenium.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ResultsContainer extends ElementContainer {

    public ResultsContainer(WebElement rootElement) {
        super(rootElement);
    }

    @FindBy(xpath = ".//h3")
    ExtendedWebElement title;

    @FindBy(xpath = ".//span[@class='st']")
    ExtendedWebElement description;
}
