package sk.selenium.decorator;

import java.lang.reflect.*;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import sk.selenium.elements.Element;
import sk.selenium.elements.ElementContainer;

public class CustomFieldDecorator extends DefaultFieldDecorator {

    public CustomFieldDecorator(SearchContext searchContext) {
        super(new DefaultElementLocatorFactory(searchContext));
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if (Element.class.isAssignableFrom(field.getType())) {
            return decorateElement(loader, field);
        }

        if (ElementContainer.class.isAssignableFrom(field.getType())) {
            return decorateContainer(loader, field);
        }

        if (isDecoratedList(field, Element.class)) {
            return decorateElementList(loader, field);
        }

        if (isDecoratedList(field, ElementContainer.class)) {
            return decorateElementsContainerList(loader, field);
        }

        return super.decorate(loader, field);
    }

    public <E> E create(final Class<E> elementClass, final WebElement wrappedElement){
        try {
            return elementClass.getDeclaredConstructor(WebElement.class).newInstance(wrappedElement);
        } catch (InstantiationException var4) {
            throw new RuntimeException(var4);
        } catch (IllegalAccessException var5) {
            throw new RuntimeException(var5);
        } catch (InvocationTargetException var6) {
            throw new RuntimeException(var6);
        } catch (NoSuchMethodException var7) {
            throw new RuntimeException(var7);
        }
    }

    private boolean isDecoratedList(Field field, Class<?> paramType) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        Class<?> clazzList = (Class<?>) (((ParameterizedType) genericType).getActualTypeArguments()[0]);

        if (!paramType.isAssignableFrom(clazzList)) {
            return false;
        }

        return field.getAnnotation(FindBy.class) != null
                || field.getAnnotation(FindBys.class) != null
                || field.getAnnotation(FindAll.class) != null;
    }

    private Object decorateElement(final ClassLoader loader, final Field field) {
        final WebElement wrappedElement = proxyForLocator(loader, createLocator(field));
        return create((Class<? extends Element>) field.getType(), wrappedElement);
    }

    private Object decorateContainer(final ClassLoader loader, final Field field) {
        final WebElement wrappedElement = proxyForLocator(loader, createLocator(field));
        ElementContainer container = create((Class<? extends ElementContainer>) field.getType(), wrappedElement);
        return container;
    }

    @Override
    protected WebElement proxyForLocator(ClassLoader loader, ElementLocator locator) {
        InvocationHandler handler = new LocatingElementHandler(locator);
        WebElement proxy;
        proxy = (WebElement) Proxy.newProxyInstance(loader, new Class[] {WebElement.class, WrapsElement.class, Locatable.class}, handler);
        return proxy;
    }

    private Object decorateElementList(final ClassLoader loader, final Field field) {
        Class<? extends Element> clazz = (Class<? extends Element>) (((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        return proxyForListElement(loader, createLocator(field), clazz);
    }

    private Object decorateElementsContainerList(final ClassLoader loader, final Field field) {
        Class<? extends ElementContainer> clazz = (Class<? extends ElementContainer>) (((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        return proxyForElementsContainerList(loader, createLocator(field), clazz);
    }

    private <T extends ElementContainer> List<T> proxyForElementsContainerList(ClassLoader loader, ElementLocator locator, Class<T> clazz) {
        final InvocationHandler handler = new LocatingElementContainerListHandler(locator, loader, clazz);
        return (List<T>) Proxy.newProxyInstance(loader, new Class[] {List.class}, handler);
    }

    private <T extends Element> List<T> proxyForListElement(ClassLoader loader, ElementLocator locator, Class<T> clazz) {
        final InvocationHandler handler = new LocatingElementListHandler(locator, loader, clazz);
        return (List<T>) Proxy.newProxyInstance(loader, new Class[] {List.class}, handler);
    }

    private ElementLocator createLocator(final Field field) {
        return factory.createLocator(field);
    }
}
