package sk;

import org.testng.Assert;
import org.testng.annotations.Test;
import sk.pages.GooglePage;
import sk.selenium.elements.ResultsContainer;

import java.util.List;

public class GoogleSearchTest extends TestBase {
    @Test
    public void testGoogleSearchPage() {
        GooglePage page = openPage(GooglePage.class);
        page.open();
        page.searchFor("Gett Ru");
        sleep(5);
        List<String> titles = page.getResultsTitles();
        ResultsContainer result = page.resultContainer;
        List<ResultsContainer> results = page.resultsContainer;
        Assert.assertTrue(titles.contains("Gett Такси - Gett RU"));
        Assert.assertEquals(results.size(), 10);
    }
}
