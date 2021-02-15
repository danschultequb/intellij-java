package qub;

/**
 * An IntelliJ module (typically parsed from a .iml file).
 */
public class IntellijModule extends XMLDocumentWrapperBase
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

    private IntellijModule(XMLDocument xml)
    {
        super(xml);

        PreCondition.assertNotNull(xml.getRoot(), "xml.getRoot()");
        PreCondition.assertEqual(IntellijModule.moduleElementName, xml.getRoot().getName(), "xml.getRoot().getName()");
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

    public static IntellijModule create(XMLDocument xml)
    {
        return new IntellijModule(xml);
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

    private XMLElement getOrCreateComponentElement()
    {
        return this.toXml().getRoot()
            .getFirstOrCreateElementChild(
                IntellijModule.componentElementName,
                () -> XMLElement.create(IntellijModule.componentElementName)
                    .setAttribute(IntellijModule.componentNameAttributeName, IntellijModule.componentNameAttributeValue));
    }

    public IntellijModule setOutputUrl(String outputUrl)
    {
        PreCondition.assertNotNullAndNotEmpty(outputUrl, "outputUrl");

        this.getOrCreateComponentElement()
            .getFirstOrCreateElementChild(IntellijModule.outputElementName)
            .setAttribute(IntellijModule.outputUrlAttributeName, outputUrl);

        return this;
    }

    public IntellijModule setOutputTestUrl(String outputTestUrl)
    {
        PreCondition.assertNotNullAndNotEmpty(outputTestUrl, "outputTestUrl");

        this.getOrCreateComponentElement()
            .getFirstOrCreateElementChild(IntellijModule.outputTestElementName)
            .setAttribute(IntellijModule.outputTestUrlAttributeName, outputTestUrl);

        return this;
    }

    public IntellijModule setExcludeOutput(boolean excludeOutput)
    {
        final XMLElement componentElement = this.getOrCreateComponentElement();
        if (excludeOutput)
        {
            componentElement.getFirstOrCreateElementChild(IntellijModule.excludeOutputElementName);
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

        final XMLElement contentElement = this.getOrCreateComponentElement()
            .getFirstOrCreateElementChild(
                IntellijModule.contentElementName,
                () -> XMLElement.create(IntellijModule.contentElementName)
                    .setAttribute(IntellijModule.contentUrlAttributeName, IntellijModule.contentUrlAttributeValue));

        contentElement.addChild(sourceFolder.toXml());

        return this;
    }

    public IntellijModule setInheritedJdk(boolean inheritedJdk)
    {
        final XMLElement componentElement = this.getOrCreateComponentElement();
        final XMLElement inheritedJdkElement = componentElement.getFirstElementChild((XMLElement childElement) ->
            IntellijModule.orderEntryElementName.equals(childElement.getName()) &&
            IntellijModule.inheritedJdkAttributeValue.equals(childElement.getAttributeValue(IntellijModule.orderEntryTypeAttributeName).catchError().await()))
            .catchError(NotFoundException.class)
            .await();
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

    public Iterable<IntellijModuleLibrary> getModuleLibraries()
    {
        return this.getOrCreateComponentElement()
            .getElementChildren((XMLElement childElement) ->
                childElement.getName().equals(IntellijModule.orderEntryElementName) &&
                Comparer.equal(
                    childElement.getAttributeValue(IntellijModule.orderEntryTypeAttributeName).catchError().await(),
                    IntellijModuleLibrary.typeAttributeValue))
            .map(IntellijModuleLibrary::create);
    }

    public IntellijModule addModuleLibrary(IntellijModuleLibrary moduleLibrary)
    {
        PreCondition.assertNotNull(moduleLibrary, "moduleLibrary");

        this.getOrCreateComponentElement()
            .addChild(moduleLibrary.toXml());

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
            .removeChild(moduleLibrary.toXml());
    }
}