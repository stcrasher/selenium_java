package sk.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

public class GooglePage extends Page {
    String baseUrl = "http://www.google.com";

    public GooglePage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(name = "q")
    private WebElement searchBox;

    @FindBy(css = "div.r > a > h3")
    private List<WebElement> results;

    public void searchFor(String text) {
        searchBox.sendKeys(text);
        searchBox.submit();
    }

    public List<String> getResultsTitles() {
        ArrayList titles = new ArrayList();
        for (WebElement result : results) {
            String t = result.getText();
//            System.err.print(t + "\n");
            titles.add(t);
        }
        return titles;
    }

    public void open() {
        driver.navigate().to(baseUrl);
    }
}
