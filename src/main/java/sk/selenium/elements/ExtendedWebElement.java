package sk.selenium.elements;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.openqa.selenium.WebElement;

public class ExtendedWebElement extends Element {

    private static final int WAIT_BETWEEN_TRIES_MILLISECONDS = 1000;
    private static final int ATTEMPTS = 10;

    public ExtendedWebElement(WebElement webElement) {
        super(webElement);
    }

    private void callPreAction() {
        scroll();
    }

    public void click() {
        callPreAction();
        webElement.click();
    }

    public void submit() {
        callPreAction();
        webElement.submit();
    }

    public void sendKeys(CharSequence... keysToSend) {
        callPreAction();
        clear();
        webElement.sendKeys(keysToSend);
    }

    public void clear() {
        webElement.clear();
    }

    public boolean isSelected() {
        try {
            return webElement.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled() {
        try {
            return webElement.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayed() {
        try {
            return webElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scroll() {
        /**
         * This is proxy calling. When you calls toString, the Proxy instance scroll to element and
         * return really toString results
         *
         * @see {@link com.gett.automation.web.selenium.decorator.LocatingElementHandler#invoke}
         */
        webElement.toString();
    }

    public boolean waitUntilDisplayed() {
        return wait(this::isDisplayed, res -> res);
    }

    public boolean waitUntilNotDisplayed() {
        return wait(this::isDisplayed, res -> !res);
    }

    public boolean waitUntilEnabled() {
        return wait(this::isEnabled, res -> res);
    }

    public boolean waitUntilNotEnabled() {
        return wait(this::isEnabled, res -> !res);
    }

    public boolean waitUntilReady() {
        return wait(this::isReady, res -> res);
    }

    public boolean isReady() {
        return isEnabled() && isDisplayed();
    }

    private <R> R wait(
            Callable<R> function,
            Predicate<? super R> predicate,
            int attempts,
            long waitBetweenTriesMilliseconds) {
        R res = null;
        while (attempts-- > 0) {
            try {
                R r = function.call();
                if (predicate.test(r)) {
                    res = r;
                    break;
                }
                Thread.sleep(waitBetweenTriesMilliseconds);
            } catch (Exception ignore) {
                System.out.println(ignore.getMessage());
            }
        }
        return res;
    }

    private <R> R wait(Callable<R> function, Predicate<? super R> predicate) {
        return wait(function, predicate, ATTEMPTS, WAIT_BETWEEN_TRIES_MILLISECONDS);
    }
}

