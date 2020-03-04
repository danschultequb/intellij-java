package qub;

/**
 * A convenience type for working with Intellij workspace.xml run configurations.
 */
public class IntellijWorkspaceRunConfiguration
{
    public final static String configurationElementName = "configuration";
    private final static String configurationNameAttributeName = "name";
    private final static String configurationTypeAttributeName = "type";
    public final static String applicationAttributeValue = "Application";
    private final static String configurationFactoryNameAttributeName = "factoryName";
    private final static String optionElementName = "option";
    private final static String optionNameAttributeName = "name";
    private final static String mainClassNameAttributeValue = "MAIN_CLASS_NAME";
    private final static String optionValueAttributeName = "value";
    private final static String moduleElementName = "module";
    private final static String programParametersAttributeValue = "PROGRAM_PARAMETERS";
    private final static String vmParametersAttributeValue="VM_PARAMETERS";
    private final static String methodElementName = "method";
    private final static String methodVAttributeName = "v";
    private final static String methodVAttributeValue = "2";
    private final static String optionEnabledAttributeName = "enabled";

    private final XMLElement xmlElement;

    private IntellijWorkspaceRunConfiguration(XMLElement xmlElement)
    {
        PreCondition.assertNotNull(xmlElement, "xmlElement");
        PreCondition.assertEqual(IntellijWorkspaceRunConfiguration.configurationElementName, xmlElement.getName(), "xmlElement.getName()");

        this.xmlElement = xmlElement;
    }

    public static IntellijWorkspaceRunConfiguration create()
    {
        return IntellijWorkspaceRunConfiguration.create(
            XMLElement.create(IntellijWorkspaceRunConfiguration.configurationElementName)
                .setAttribute(IntellijWorkspaceRunConfiguration.configurationTypeAttributeName, IntellijWorkspaceRunConfiguration.applicationAttributeValue)
                .setAttribute(IntellijWorkspaceRunConfiguration.configurationFactoryNameAttributeName, IntellijWorkspaceRunConfiguration.applicationAttributeValue)
                .addChild(XMLElement.create(IntellijWorkspaceRunConfiguration.methodElementName)
                    .setAttribute(IntellijWorkspaceRunConfiguration.methodVAttributeName, IntellijWorkspaceRunConfiguration.methodVAttributeValue)
                    .addChild(XMLElement.create(IntellijWorkspaceRunConfiguration.optionElementName)
                        .setAttribute(IntellijWorkspaceRunConfiguration.optionNameAttributeName, "Make")
                        .setAttribute(IntellijWorkspaceRunConfiguration.optionEnabledAttributeName, "true"))));
    }

    public static IntellijWorkspaceRunConfiguration create(XMLElement xmlElement)
    {
        return new IntellijWorkspaceRunConfiguration(xmlElement);
    }

    /**
     * Get the name of this run configuration.
     * @return The name of this run configuration.
     */
    public String getName()
    {
        return this.xmlElement.getAttributeValue(IntellijWorkspaceRunConfiguration.configurationNameAttributeName)
            .catchError(NotFoundException.class)
            .await();
    }

    /**
     * Set the name of this run configuration.
     * @param name The new name of this run configuration.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setName(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        this.xmlElement.setAttribute(IntellijWorkspaceRunConfiguration.configurationNameAttributeName, name);

        return this;
    }

    /**
     * Get the type of this run configuration.
     * @return The type of this run configuration.
     */
    public String getType()
    {
        return this.xmlElement.getAttributeValue(IntellijWorkspaceRunConfiguration.configurationTypeAttributeName)
            .catchError(NotFoundException.class)
            .await();
    }

    /**
     * Set the type of this run configuration.
     * @param type The new type of this run configuration.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setType(String type)
    {
        PreCondition.assertNotNullAndNotEmpty(type, "type");

        this.xmlElement.setAttribute(IntellijWorkspaceRunConfiguration.configurationTypeAttributeName, type);

        return this;
    }

    /**
     * Get the factory name of this run configuration.
     * @return The factory name of this run configuration.
     */
    public String getFactoryName()
    {
        return this.xmlElement.getAttributeValue(IntellijWorkspaceRunConfiguration.configurationFactoryNameAttributeName)
            .catchError(NotFoundException.class)
            .await();
    }

    /**
     * Set the factory name of this run configuration.
     * @param factoryName The new factory name of this run configuration.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setFactoryName(String factoryName)
    {
        PreCondition.assertNotNullAndNotEmpty(factoryName, "factoryName");

        this.xmlElement.setAttribute(IntellijWorkspaceRunConfiguration.configurationFactoryNameAttributeName, factoryName);

        return this;
    }

    private XMLElement getOptionElementChild(String attributeName, String attributeValue)
    {
        return this.xmlElement.getFirstElementChild(
            (XMLElement childElement) -> Comparer.equal(childElement.getName(), IntellijWorkspaceRunConfiguration.optionElementName) &&
                                         Comparer.equal(childElement.getAttributeValue(attributeName).catchError().await(), attributeValue))
            .catchError(NotFoundException.class)
            .await();
    }

    private XMLElement getOrCreateOptionElementChild(String attributeName, String attributeValue)
    {
        return this.xmlElement.getFirstOrCreateElementChild(
            (XMLElement childElement) -> Comparer.equal(childElement.getName(), IntellijWorkspaceRunConfiguration.optionElementName) &&
                                         Comparer.equal(childElement.getAttributeValue(attributeName).catchError().await(), attributeValue),
            () -> XMLElement.create(IntellijWorkspaceRunConfiguration.optionElementName)
                .setAttribute(attributeName, attributeValue));
    }

    /**
     * Get the full name of the main class of this run configuration.
     * @return The full name of the main class of this run configuration.
     */
    public String getMainClassFullName()
    {
        final XMLElement mainClassNameElement = this.getOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.mainClassNameAttributeValue);
        return mainClassNameElement == null
            ? null
            : mainClassNameElement.getAttributeValue(IntellijWorkspaceRunConfiguration.optionValueAttributeName).catchError().await();
    }

    /**
     * Set the full name of the main class of this run configuration.
     * @param mainClassFullName The new full name of the main class of this run configuration.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setMainClassFullName(String mainClassFullName)
    {
        PreCondition.assertNotNullAndNotEmpty(mainClassFullName, "mainClassFullName");

        this.getOrCreateOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.mainClassNameAttributeValue)
            .setAttribute(IntellijWorkspaceRunConfiguration.optionValueAttributeName, mainClassFullName);

        return this;
    }

    /**
     * Get the name of the Intellij module that contains this run configuration.
     * @return The name of the Intellij module that contains this run configuration.
     */
    public String getModuleName()
    {
        return this.xmlElement.getFirstElementChild(IntellijWorkspaceRunConfiguration.moduleElementName)
            .then((XMLElement moduleElement) -> moduleElement.getAttributeValue(IntellijWorkspaceRunConfiguration.optionNameAttributeName).await())
            .catchError(NotFoundException.class)
            .await();
    }

    /**
     * Set the name of the Intellij module that contains this run configuration.
     * @param moduleName The new name of the Intellij module that contains this run configuration.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setModuleName(String moduleName)
    {
        PreCondition.assertNotNullAndNotEmpty(moduleName, "moduleName");

        this.xmlElement.getFirstOrCreateElementChild(IntellijWorkspaceRunConfiguration.moduleElementName)
            .setAttribute(IntellijWorkspaceRunConfiguration.optionNameAttributeName, moduleName);

        return this;
    }

    /**
     * Get the parameters that will be passed to the program/application when this run
     * configuration is run.
     * @return The parameters that will be passed to the program/application when this run
     * configuration is run.
     */
    public String getProgramParameters()
    {
        final XMLElement programParametersElement = this.getOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.programParametersAttributeValue);
        return programParametersElement == null
            ? null
            : programParametersElement.getAttributeValue(IntellijWorkspaceRunConfiguration.optionValueAttributeName)
                .catchError(NotFoundException.class)
                .await();
    }

    /**
     * Set the parameters that will be passed to the program/application when this run
     * configuration is run.
     * @param programParameters The new program parameters that will be passed to the program/
     *                          application when this run configuration is run.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setProgramParameters(String programParameters)
    {
        PreCondition.assertNotNullAndNotEmpty(programParameters, "programParameters");

        this.getOrCreateOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.programParametersAttributeValue)
            .setAttribute(IntellijWorkspaceRunConfiguration.optionValueAttributeName, programParameters);

        return this;
    }

    /**
     * Get the parameters that will be passed to the JVM when this run configuration is run.
     * @return The parameters that will be passed to the JVM when this run configuration is run.
     */
    public String getVmParameters()
    {
        final XMLElement vmParametersElement = this.getOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.vmParametersAttributeValue);
        return vmParametersElement == null
            ? null
            : vmParametersElement.getAttributeValue(IntellijWorkspaceRunConfiguration.optionValueAttributeName)
                .catchError(NotFoundException.class)
                .await();
    }

    /**
     * Set the parameters that will be passed to the JVM when this run configuration is run.
     * @param vmParameters The new parameters that will be passed to the JVM when this run
     *                     configuration is run.
     * @return This object for method chaining.
     */
    public IntellijWorkspaceRunConfiguration setVmParameters(String vmParameters)
    {
        PreCondition.assertNotNullAndNotEmpty(vmParameters, "vmParameters");

        this.getOrCreateOptionElementChild(
            IntellijWorkspaceRunConfiguration.optionNameAttributeName,
            IntellijWorkspaceRunConfiguration.vmParametersAttributeValue)
            .setAttribute(IntellijWorkspaceRunConfiguration.optionValueAttributeName, vmParameters);

        return this;
    }

    /**
     * Get the XML representation of this run configuration.
     * @return The XML representation of this run configuration.
     */
    public XMLElement toXml()
    {
        return this.xmlElement;
    }

    @Override
    public String toString()
    {
        return this.toString(XMLFormat.consise);
    }

    public String toString(XMLFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return this.xmlElement.toString(format);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof IntellijWorkspaceRunConfiguration && this.equals((IntellijWorkspaceRunConfiguration)rhs);
    }

    public boolean equals(IntellijWorkspaceRunConfiguration rhs)
    {
        return rhs != null && this.xmlElement.equals(rhs.xmlElement);
    }
}
