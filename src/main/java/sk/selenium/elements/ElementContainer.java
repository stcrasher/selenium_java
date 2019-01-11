package sk.selenium.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import sk.selenium.decorator.CustomFieldDecorator;

public abstract class ElementContainer {
    public WebElement rootElement;

    public ElementContainer(WebElement rootElement) {
        this.rootElement = rootElement;
        PageFactory.initElements(new CustomFieldDecorator(this.rootElement), this);
    }
}
