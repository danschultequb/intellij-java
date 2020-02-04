package qub;

public class IntellijModuleLibrary
{
    private static final String typeAttributeName = "type";
    public static final String typeAttributeValue = "module-library";
    private static final String libraryElementName = "library";
    private static final String classesElementName = "CLASSES";
    private static final String rootElementName = "root";
    private static final String urlAttributeName = "url";
    private static final String javadocElementName = "JAVADOC";
    private static final String sourcesElementName = "SOURCES";

    private final XMLElement orderEntryElement;

    private IntellijModuleLibrary(XMLElement orderEntryElement)
    {
        PreCondition.assertNotNull(orderEntryElement, "orderEntryElement");
        PreCondition.assertEqual(IntellijModule.orderEntryElementName, orderEntryElement.getName(), "orderEntryElement.getName()");
        PreCondition.assertEqual(IntellijModuleLibrary.typeAttributeValue, orderEntryElement.getAttributeValue(IntellijModuleLibrary.typeAttributeName).catchError().await(), "orderEntryElement.getAttributeValue(IntellijModuleLibrary.typeAttributeName).await()");

        this.orderEntryElement = orderEntryElement;
        this.getOrCreateClassesElement();
        this.getOrCreateJavadocElement();
        this.getOrCreateSourcesElement();
    }

    public static IntellijModuleLibrary create()
    {
        return IntellijModuleLibrary.create(XMLElement.create(IntellijModule.orderEntryElementName)
            .setAttribute(IntellijModuleLibrary.typeAttributeName, IntellijModuleLibrary.typeAttributeValue));
    }

    public static IntellijModuleLibrary create(XMLElement orderEntryElement)
    {
        return new IntellijModuleLibrary(orderEntryElement);
    }

    private static XMLElement getOrCreateElement(XMLElement parentElement, String childElementName, Action1<XMLElement> setupChildElement)
    {
        PreCondition.assertNotNull(parentElement, "parentElement");
        PreCondition.assertNotNullAndNotEmpty(childElementName, "childElementName");
        PreCondition.assertNotNull(setupChildElement, "setupChildElement");

        XMLElement result = parentElement.getElementChildren(childElementName).first();
        if (result == null)
        {
            result = XMLElement.create(childElementName);
            setupChildElement.run(result);
            parentElement.addChild(result);
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    private static XMLElement getOrCreateElement(XMLElement parentElement, String childElementName)
    {
        PreCondition.assertNotNull(parentElement, "parentElement");
        PreCondition.assertNotNullAndNotEmpty(childElementName, "childElementName");

        return IntellijModuleLibrary.getOrCreateElement(parentElement, childElementName, (XMLElement childElement) -> {});
    }

    private XMLElement getOrCreateLibraryElement()
    {
        return IntellijModuleLibrary.getOrCreateElement(this.orderEntryElement, IntellijModuleLibrary.libraryElementName);
    }

    private XMLElement getOrCreateClassesElement()
    {
        return IntellijModuleLibrary.getOrCreateElement(this.getOrCreateLibraryElement(), IntellijModuleLibrary.classesElementName);
    }

    private XMLElement getOrCreateJavadocElement()
    {
        return IntellijModuleLibrary.getOrCreateElement(this.getOrCreateLibraryElement(), IntellijModuleLibrary.javadocElementName);
    }

    private XMLElement getOrCreateSourcesElement()
    {
        return IntellijModuleLibrary.getOrCreateElement(this.getOrCreateLibraryElement(), IntellijModuleLibrary.sourcesElementName);
    }

    public IntellijModuleLibrary addClassesUrl(String classesUrl)
    {
        PreCondition.assertNotNullAndNotEmpty(classesUrl, "classesUrl");

        this.getOrCreateClassesElement()
            .addChild(XMLElement.create(IntellijModuleLibrary.rootElementName)
                .setAttribute(IntellijModuleLibrary.urlAttributeName, classesUrl));

        return this;
    }

    public IntellijModuleLibrary clearClassesUrls()
    {
        this.getOrCreateClassesElement()
            .clearChildren()
            .setSplit(false);

        return this;
    }

    public IntellijModuleLibrary addSourcesUrl(String sourcesUrl)
    {
        PreCondition.assertNotNullAndNotEmpty(sourcesUrl, "sourcesUrl");

        this.getOrCreateSourcesElement()
            .addChild(XMLElement.create(IntellijModuleLibrary.rootElementName)
                .setAttribute(IntellijModuleLibrary.urlAttributeName, sourcesUrl));

        return this;
    }

    public IntellijModuleLibrary clearSourcesUrls()
    {
        this.getOrCreateSourcesElement()
            .clearChildren()
            .setSplit(false);

        return this;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof IntellijModuleLibrary && this.equals((IntellijModuleLibrary)rhs);
    }

    public boolean equals(IntellijModuleLibrary rhs)
    {
        return rhs != null &&
            this.orderEntryElement.equals(rhs.orderEntryElement);
    }

    @Override
    public String toString()
    {
        return this.toString(XMLFormat.consise);
    }

    public String toString(XMLFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return this.toXML().toString(format);
    }

    public XMLElement toXML()
    {
        return this.orderEntryElement;
    }
}