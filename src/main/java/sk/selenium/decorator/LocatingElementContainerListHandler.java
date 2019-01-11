package sk.selenium.decorator;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class LocatingElementContainerListHandler<T> implements InvocationHandler {

    private final ClassLoader loader;

    private final Class<T> clazz;

    private final ElementLocator locator;

    private List<T> cachedObjects = new ArrayList<>();

    public LocatingElementContainerListHandler(ElementLocator locator, ClassLoader loader, Class<T> clazz) {
        this.locator = locator;
        this.loader = loader;
        this.clazz = clazz;
    }

    public <T> T create(final Class<T> componentClass, final WebElement wrappedElement) throws Exception {
        try {
            Constructor<T> declaredConstructor = componentClass.getDeclaredConstructor(WebElement.class);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return declaredConstructor.newInstance(wrappedElement);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String message = String.format(
                "Failed to create the instance of the component '%s' for the base element'%s'\n Exception:'%s'",
                componentClass.getSimpleName(), wrappedElement.toString(), e);
            System.out.println(message);
            throw new Exception("exception");
        }
    }

    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        final List<WebElement> wElements = getElements();

        /* If the new list of items has not changed in size, then immediately return the cached objects */
        if (cachedObjects.size() != wElements.size()) {

            /* else create a new list of objects */
            cachedObjects.clear();
            for (int i = 0; i < wElements.size(); i++) {
                cachedObjects.add(create(clazz, proxyWebElementForList(loader, locator, i)));
            }
        }

        try {
            return method.invoke(cachedObjects, objects);
        } catch (InvocationTargetException e) {
            cachedObjects.clear();
            String message = String.format(
                "[PROXY] Can not call the method '%s' for the base element with the locator '%s' \n Exception: '%s'",
                method.getName(), locator.toString(), e.getCause());
            System.out.println(message);
            return false;
        }
    }

    /**
     * Proxy of one element from the list of elements. Complex logic. Each time the method is invoked,
     * all elements are searched, followed by an element with the specified index and proxy. Its proxy
     * is returned.
     */
    @SuppressWarnings("squid:CallToDeprecatedMethod")
    private WebElement proxyWebElementForList(ClassLoader loader, ElementLocator locator, int index) {
        InvocationHandler handler = (proxy, method, args) -> {
            try {
                if ("getWrappedElement".equals(method.getName())) {
                    return getElements().get(index);
                }
                return method.invoke(getElements().get(index), args);
            } catch (InvocationTargetException e) {
                String message = String.format(
                    "[PROXY] Can not call the method '%s' for the base element with the locator '%s' \n Exception: '%s'",
                    method.getName(), locator.toString(), e.getCause());
                return false;
            }
        };
        return (WebElement) Proxy.newProxyInstance(loader, new Class[]{WebElement.class, WrapsElement.class}, handler);
    }

    private List<WebElement> getElements() {
        return locator.findElements();
    }
}
