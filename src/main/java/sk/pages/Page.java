package sk.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import sk.selenium.decorator.CustomFieldDecorator;

public class Page {
    protected WebDriver driver;

    public Page(WebDriver driver) {
      this.driver = driver;
      PageFactory.initElements(new CustomFieldDecorator(driver), this);
    }

    public String getTitle() {
    return driver.getTitle();
  }
}
