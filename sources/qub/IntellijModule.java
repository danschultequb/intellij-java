package qub;

/**
 * An IntelliJ module (typically parsed from a .iml file).
 */
public class IntellijModule
{
    private static final String moduleElementName = "module";
    private static final String moduleTypeAttributeName = "type";
    private static final String moduleTypeAttributeValue = "JAVA_MODULE";
    private static final String moduleVersionAttributeName = "version";
    private static final String moduleVersionAttributeValue = "4";
    private static final String componentElementName = "component";
    private static final String componentNameAttributeName = "name";
    private static final String componentNameAttributeValue = "NewModuleRootManager";
    private static final String outputElementName = "output";
    private static final String outputUrlAttributeName = "url";
    private static final String outputTestElementName = "output-test";
    private static final String outputTestUrlAttributeName = "url";
    private static final String excludeOutputElementName = "exclude-output";
    private static final String contentElementName = "content";
    private static final String contentUrlAttributeName = "url";
    private static final String contentUrlAttributeValue = "file://$MODULE_DIR$";
    private static final String sourceFolderElementName = "sourceFolder";
    private static final String sourceFolderUrlAttributeName = "url";
    private static final String sourceFolderIsTestSourceAttributeName = "isTestSource";
    public static final String orderEntryElementName = "orderEntry";
    private static final String orderEntryTypeAttributeName = "type";
    private static final String inheritedJdkAttributeValue = "inheritedJdk";
    private static final String sourceFolderAttributeValue = "sourceFolder";
    private static final String orderEntryForTestsAttributeName = "forTests";

    private final XMLDocument document;

    private IntellijModule(XMLDocument document)
    {
        PreCondition.assertNotNull(document, "document");
        PreCondition.assertNotNull(document.getRoot(), "document.getRoot()");
        PreCondition.assertEqual(IntellijModule.moduleElementName, document.getRoot().getName(), "document.getRoot().getName()");

        this.document = document;
    }

    public static IntellijModule create()
    {
        final IntellijModule result = IntellijModule.create(XMLDocument.create()
            .setDeclaration(XMLDeclaration.create()
                .setVersion("1.0")
                .setEncoding("UTF-8"))
            .setRoot(XMLElement.create(IntellijModule.moduleElementName)
                .setAttribute(IntellijModule.moduleTypeAttributeName, IntellijModule.moduleTypeAttributeValue)
                .setAttribute(IntellijModule.moduleVersionAttributeName, IntellijModule.moduleVersionAttributeValue)));

        result.getOrCreateComponentElement();

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    public static IntellijModule create(XMLDocument document)
    {
        return new IntellijModule(document);
    }

    public static Result<IntellijModule> parse(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return XML.parse(file)
            .then((XMLDocument xmlDocument) -> IntellijModule.create(xmlDocument));
    }

    public static Result<IntellijModule> parse(String text)
    {
        PreCondition.assertNotNull(text, "text");

        return XML.parse(text)
            .then((XMLDocument xmlDocument) -> IntellijModule.create(xmlDocument));
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

        return IntellijModule.getOrCreateElement(parentElement, childElementName, (XMLElement childElement) -> {});
    }

    private XMLElement getOrCreateComponentElement()
    {
        return IntellijModule.getOrCreateElement(this.document.getRoot(), IntellijModule.componentElementName, (XMLElement componentElement) ->
        {
            componentElement.setAttribute(IntellijModule.componentNameAttributeName, IntellijModule.componentNameAttributeValue);
        });
    }

    public IntellijModule setOutputUrl(String outputUrl)
    {
        PreCondition.assertNotNullAndNotEmpty(outputUrl, "outputUrl");

        IntellijModule.getOrCreateElement(this.getOrCreateComponentElement(), IntellijModule.outputElementName)
            .setAttribute(IntellijModule.outputUrlAttributeName, outputUrl);

        return this;
    }

    public IntellijModule setOutputTestUrl(String outputTestUrl)
    {
        IntellijModule.getOrCreateElement(this.getOrCreateComponentElement(), IntellijModule.outputTestElementName)
            .setAttribute(IntellijModule.outputTestUrlAttributeName, outputTestUrl);

        return this;
    }

    public IntellijModule setExcludeOutput(boolean excludeOutput)
    {
        final XMLElement componentElement = this.getOrCreateComponentElement();
        if (excludeOutput)
        {
            IntellijModule.getOrCreateElement(componentElement, IntellijModule.excludeOutputElementName);
        }
        else
        {
            componentElement.removeElementChildren(IntellijModule.excludeOutputElementName)
                .catchError(NotFoundException.class)
                .await();
        }

        return this;
    }

    public IntellijModule addSourceFolder(IntellijSourceFolder sourceFolder)
    {
        PreCondition.assertNotNull(sourceFolder, "sourceFolder");

        final XMLElement contentElement = IntellijModule.getOrCreateElement(this.getOrCreateComponentElement(), IntellijModule.contentElementName, (XMLElement createdContentElement) ->
        {
            createdContentElement.setAttribute(IntellijModule.contentUrlAttributeName, IntellijModule.contentUrlAttributeValue);
        });
        contentElement.addChild(sourceFolder.toXML());

        return this;
    }

    public IntellijModule setInheritedJdk(boolean inheritedJdk)
    {
        final XMLElement componentElement = this.getOrCreateComponentElement();
        final XMLElement inheritedJdkElement = componentElement.getElementChildren((XMLElement childElement) ->
            IntellijModule.orderEntryElementName.equals(childElement.getName()) &&
            IntellijModule.inheritedJdkAttributeValue.equals(childElement.getAttributeValue(IntellijModule.orderEntryTypeAttributeName).catchError().await()))
            .first();
        if (inheritedJdk && inheritedJdkElement == null)
        {
            componentElement.addChild(XMLElement.create(IntellijModule.orderEntryElementName)
                .setAttribute(IntellijModule.orderEntryTypeAttributeName, IntellijModule.inheritedJdkAttributeValue));
        }
        else if (!inheritedJdk && inheritedJdkElement != null)
        {
            componentElement.removeChild(inheritedJdkElement);
        }

        return this;
    }

    public IntellijModule setSourceFolderForTests(boolean sourceFolderForTests)
    {
        final XMLElement componentElement = this.getOrCreateComponentElement();
        XMLElement sourceFolderElement = componentElement.getElementChildren((XMLElement childElement) ->
            IntellijModule.orderEntryElementName.equals(childElement.getName()) &&
            IntellijModule.orderEntryTypeAttributeName.equals(childElement.getAttributeValue(IntellijModule.sourceFolderAttributeValue).catchError().await()))
            .first();
        if (sourceFolderElement == null)
        {
            sourceFolderElement = XMLElement.create(IntellijModule.orderEntryElementName)
                .setAttribute(IntellijModule.orderEntryTypeAttributeName, IntellijModule.sourceFolderAttributeValue);
            componentElement.addChild(sourceFolderElement);
        }
        sourceFolderElement.setAttribute(IntellijModule.orderEntryForTestsAttributeName, Booleans.toString(sourceFolderForTests));

        return this;
    }

    public IntellijModule addModuleLibrary(IntellijModuleLibrary moduleLibrary)
    {
        PreCondition.assertNotNull(moduleLibrary, "moduleLibrary");

        this.getOrCreateComponentElement()
            .addChild(moduleLibrary.toXML());

        return this;
    }

    public IntellijModule clearModuleLibraries()
    {
        this.getOrCreateComponentElement()
            .removeElementChildren((XMLElement childElement) ->
                childElement.getName().equals(IntellijModule.orderEntryElementName) &&
                Comparer.equal(
                    childElement.getAttributeValue(IntellijModule.orderEntryTypeAttributeName).catchError().await(),
                    IntellijModuleLibrary.typeAttributeValue));

        return this;
    }

    public Result<Void> removeModuleLibrary(IntellijModuleLibrary moduleLibrary)
    {
        PreCondition.assertNotNull(moduleLibrary, "moduleLibrary");

        return this.getOrCreateComponentElement()
            .removeChild(moduleLibrary.toXML());
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

    /**
     * Get the XML representation of this object.
     * @return The XML representation of this object.
     */
    public XMLDocument toXML()
    {
        return this.document;
    }
}