package qub;

/**
 * A convenience type for working with Intellij workspace.xml documents.
 */
public class IntellijWorkspace extends XMLDocumentWrapperBase
{
    private final static String projectElementName = "project";
    private final static String projectVersionAttributeName = "version";
    private final static String componentElementName = "component";
    private final static String componentNameAttributeName = "name";
    private final static String runManagerAttributeValue = "RunManager";

    private IntellijWorkspace(XMLDocument xml)
    {
        super(xml);

        PreCondition.assertNotNull(xml.getRoot(), "xml.getRoot()");
        PreCondition.assertEqual(IntellijWorkspace.projectElementName, xml.getRoot().getName(), "xml.getRoot().getName()");
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
        return this.toXml().getRoot();
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
}
