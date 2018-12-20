package sk.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class Page {
    protected WebDriver driver;

    public Page(WebDriver driver) {
      this.driver = driver;
      PageFactory.initElements(driver, this);
    }

    public String getTitle() {
    return driver.getTitle();
  }
}
