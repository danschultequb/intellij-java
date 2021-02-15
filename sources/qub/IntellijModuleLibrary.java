package qub;

public class IntellijModuleLibrary extends XMLElementWrapperBase
{
    private static final String typeAttributeName = "type";
    public static final String typeAttributeValue = "module-library";
    private static final String libraryElementName = "library";
    private static final String classesElementName = "CLASSES";
    private static final String rootElementName = "root";
    private static final String urlAttributeName = "url";
    private static final String javadocElementName = "JAVADOC";
    private static final String sourcesElementName = "SOURCES";

    private IntellijModuleLibrary(XMLElement xml)
    {
        super(xml);

        PreCondition.assertNotNull(xml, "xml");
        PreCondition.assertEqual(IntellijModule.orderEntryElementName, xml.getName(), "xml.getName()");
        PreCondition.assertEqual(IntellijModuleLibrary.typeAttributeValue, xml.getAttributeValue(IntellijModuleLibrary.typeAttributeName).catchError().await(), "xml.getAttributeValue(IntellijModuleLibrary.typeAttributeName).await()");

        this.getOrCreateClassesElement();
        this.getOrCreateJavadocElement();
        this.getOrCreateSourcesElement();
    }

    public static IntellijModuleLibrary create()
    {
        return IntellijModuleLibrary.create(XMLElement.create(IntellijModule.orderEntryElementName)
            .setAttribute(IntellijModuleLibrary.typeAttributeName, IntellijModuleLibrary.typeAttributeValue));
    }

    public static IntellijModuleLibrary create(XMLElement xml)
    {
        return new IntellijModuleLibrary(xml);
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
        return IntellijModuleLibrary.getOrCreateElement(this.toXml(), IntellijModuleLibrary.libraryElementName);
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

    public Iterable<String> getClassesUrls()
    {
        return this.getOrCreateClassesElement()
            .getElementChildren(IntellijModuleLibrary.rootElementName)
            .map((XMLElement rootElement) -> rootElement.getAttributeValue(IntellijModuleLibrary.urlAttributeName).catchError().await())
            .where((String classesUrl) -> !Strings.isNullOrEmpty(classesUrl));
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

    public Iterable<String> getSourcesUrls()
    {
        return this.getOrCreateSourcesElement()
            .getElementChildren(IntellijModuleLibrary.rootElementName)
            .map((XMLElement rootElement) -> rootElement.getAttributeValue(IntellijModuleLibrary.urlAttributeName).catchError().await())
            .where((String sourcesUrl) -> !Strings.isNullOrEmpty(sourcesUrl));
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
}
