package qub;

/**
 * A convenience type for working with Intellij workspace.xml documents.
 */
public class IntellijWorkspace
{
    private final static String projectElementName = "project";
    private final static String projectVersionAttributeName = "version";
    private final static String componentElementName = "component";
    private final static String componentNameAttributeName = "name";
    private final static String runManagerAttributeValue = "RunManager";

    private final XMLDocument document;

    private IntellijWorkspace(XMLDocument document)
    {
        PreCondition.assertNotNull(document, "document");
        PreCondition.assertNotNull(document.getRoot(), "document.getRoot()");
        PreCondition.assertEqual(IntellijWorkspace.projectElementName, document.getRoot().getName(), "document.getRoot().getName()");

        this.document = document;
    }

    public static IntellijWorkspace create()
    {
        return IntellijWorkspace.create(XMLDocument.create()
            .setRoot(XMLElement.create(IntellijWorkspace.projectElementName)
                .setAttribute(IntellijWorkspace.projectVersionAttributeName, "4")));
    }

    public static IntellijWorkspace create(XMLDocument document)
    {
        return new IntellijWorkspace(document);
    }

    /**
     * Get the project XML element at the root of the workspace.xml document.
     * @return The project XML element at the root of the workspace.xml document.
     */
    private XMLElement getProjectElement()
    {
        return this.document.getRoot();
    }

    /**
     * Get (or create and add if it doesn't already exist) the RunManager component element under
     * the project element.
     * @return The RunManager component element under the project element.
     */
    private XMLElement getOrCreateRunManagerComponentElement()
    {
        return this.getProjectElement()
            .getFirstOrCreateElementChild(
                (XMLElement elementChild) -> Comparer.equal(elementChild.getName(), IntellijWorkspace.componentElementName) &&
                                             Comparer.equal(elementChild.getAttributeValue(IntellijWorkspace.componentNameAttributeName).catchError().await(), IntellijWorkspace.runManagerAttributeValue),
                () -> XMLElement.create(IntellijWorkspace.componentElementName).setAttribute(IntellijWorkspace.componentNameAttributeName, IntellijWorkspace.runManagerAttributeValue));
    }

    /**
     * Get the run configurations in this workspace.xml document.
     * @return The run configurations in this workspace.xml document.
     */
    public Iterable<IntellijWorkspaceRunConfiguration> getRunConfigurations()
    {
        return this.getOrCreateRunManagerComponentElement()
            .getElementChildren(IntellijWorkspaceRunConfiguration.configurationElementName)
            .map(IntellijWorkspaceRunConfiguration::create);
    }

    /**
     * Add the provided run configuration to this workspace.xml's list of run configurations.
     * @param runConfiguration The run configuration to add.
     * @return This object for method chaining.
     */
    public IntellijWorkspace addRunConfiguration(IntellijWorkspaceRunConfiguration runConfiguration)
    {
        PreCondition.assertNotNull(runConfiguration, "runConfiguration");

        this.getOrCreateRunManagerComponentElement()
            .addChild(runConfiguration.toXml());

        return this;
    }

    /**
     * Remove the provided run configuration from this workspace.xml's list of run configurations.
     * @param runConfiguration The run configuration to remove.
     * @return The result of removing the run configuration.
     */
    public Result<Void> removeRunConfiguration(IntellijWorkspaceRunConfiguration runConfiguration)
    {
        PreCondition.assertNotNull(runConfiguration, "runConfiguration");

        return this.getOrCreateRunManagerComponentElement()
            .removeChild(runConfiguration.toXml());
    }

    public XMLDocument toXml()
    {
        return this.document;
    }

    @Override
    public String toString()
    {
        return this.toString(XMLFormat.consise);
    }

    public String toString(XMLFormat format)
    {
        PreCondition.assertNotNull(format, "format");

        return this.document.toString(format);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof IntellijWorkspace && this.equals((IntellijWorkspace)rhs);
    }

    public boolean equals(IntellijWorkspace rhs)
    {
        return rhs != null && this.document.equals(rhs.document);
    }
}
