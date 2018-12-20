package sk;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class TestBase {
    protected static WebDriver driver;

    @BeforeSuite
    public void setupSuite() {
      WebDriverManager.chromedriver().setup();
    }

    @BeforeClass
    public void setupDriver() {
      driver = new ChromeDriver();
    }

    @AfterClass
    public void teardownSuite() {
      driver.quit();
    }

    public static <T> T openPage(Class<T> pageCLass) {
        try {
            try {
                Constructor<T> constructor = pageCLass.getConstructor(WebDriver.class);
                return constructor.newInstance(driver);
            } catch (NoSuchMethodException var3) {
                return pageCLass.newInstance();
            }
        } catch (InstantiationException var4) {
            throw new RuntimeException(var4);
        } catch (IllegalAccessException var5) {
            throw new RuntimeException(var5);
        } catch (InvocationTargetException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static void sleep (Integer seconds) {
        Long time = seconds * 1000L;
        try { Thread.sleep(time); } catch (InterruptedException e) {}
    }
}