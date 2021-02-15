package qub;

public class IntellijSourceFolder extends XMLElementWrapperBase
{
    private static final String sourceFolderElementName = "sourceFolder";
    private static final String urlAttributeName = "url";
    private static final String isTestSourceAttributeName = "isTestSource";

    private IntellijSourceFolder(XMLElement xml)
    {
        super(xml);
    }

    public static IntellijSourceFolder create(String url)
    {
        PreCondition.assertNotNullAndNotEmpty(url, "url");

        return IntellijSourceFolder.create(XMLElement.create(IntellijSourceFolder.sourceFolderElementName))
            .setUrl(url);
    }

    public static IntellijSourceFolder create(XMLElement xml)
    {
        PreCondition.assertNotNull(xml, "xml");
        PreCondition.assertEqual(IntellijSourceFolder.sourceFolderElementName, xml.getName(), "xml.getName()");

        return new IntellijSourceFolder(xml);
    }

    public String getUrl()
    {
        return this.toXml().getAttributeValue(IntellijSourceFolder.urlAttributeName)
            .catchError(NotFoundException.class)
            .await();
    }

    public IntellijSourceFolder setUrl(String url)
    {
        PreCondition.assertNotNullAndNotEmpty(url, "url");

        this.toXml().setAttribute(IntellijSourceFolder.urlAttributeName, url);
        return this;
    }

    public boolean getIsTestSource()
    {
        return this.toXml().getAttributeValue(IntellijSourceFolder.isTestSourceAttributeName)
            .then((String isTestSourceAttributeValue) -> Booleans.parse(isTestSourceAttributeValue).await())
            .catchError(() -> false)
            .await();
    }

    public IntellijSourceFolder setIsTestSource(boolean isTestSource)
    {
        this.toXml().setAttribute(IntellijSourceFolder.isTestSourceAttributeName, Booleans.toString(isTestSource));
        return this;
    }
}
