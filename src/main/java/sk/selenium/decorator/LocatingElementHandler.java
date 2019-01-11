package sk.selenium.decorator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class LocatingElementHandler implements InvocationHandler {

    private final ElementLocator locator;

    public LocatingElementHandler(ElementLocator locator) {
        this.locator = locator;
    }

    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        WebElement element;
        System.out.println("Looking for the element {}" + locator.toString());
        try {
            element = locator.findElement();
        } catch (NoSuchElementException e) {
            if ("toString".equals(method.getName())) {
                return "Proxy element for: " + locator.toString();
            }
            throw e;
        }

        System.out.println("Element was found");
        if ("getWrappedElement".equals(method.getName())) {
            return element;
        } else if ("toString".equals(method.getName())) {
            WebDriver webDriver = null;
            if (WrapsElement.class.isAssignableFrom(element.getClass())) {
                webDriver = ((WrapsDriver) ((WrapsElement) element).getWrappedElement()).getWrappedDriver();
            } else if (RemoteWebElement.class.isAssignableFrom(element.getClass())) {
                webDriver = ((RemoteWebElement) element).getWrappedDriver();
            }
            if (webDriver != null) {
                new Actions(webDriver).moveToElement(element).release().build().perform();
            }
        }

        if (objects == null) {
            System.out.println("Invoke {}\n" + method.getName());
        } else {
            System.out.println("Invoke {} with {}\n" + method.getName() + objects);
        }
        try {
            return method.invoke(element, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}
