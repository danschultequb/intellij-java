package qub;

public class IntellijSourceFolder
{
    private static final String sourceFolderElementName = "sourceFolder";
    private static final String urlAttributeName = "url";
    private static final String isTestSourceAttributeName = "isTestSource";

    private final XMLElement element;

    private IntellijSourceFolder(XMLElement element)
    {
        PreCondition.assertNotNull(element, "element");

        this.element = element;
    }

    public static IntellijSourceFolder create(String url)
    {
        PreCondition.assertNotNullAndNotEmpty(url, "url");

        return IntellijSourceFolder.create(XMLElement.create(IntellijSourceFolder.sourceFolderElementName))
            .setUrl(url);
    }

    public static IntellijSourceFolder create(XMLElement sourceFolderElement)
    {
        PreCondition.assertNotNull(sourceFolderElement, "sourceFolderElement");
        PreCondition.assertEqual(IntellijSourceFolder.sourceFolderElementName, sourceFolderElement.getName(), "sourceFolderElement.getName()");

        return new IntellijSourceFolder(sourceFolderElement);
    }

    public String getUrl()
    {
        return this.element.getAttributeValue(IntellijSourceFolder.urlAttributeName)
            .catchError(NotFoundException.class)
            .await();
    }

    public IntellijSourceFolder setUrl(String url)
    {
        PreCondition.assertNotNullAndNotEmpty(url, "url");

        this.element.setAttribute(IntellijSourceFolder.urlAttributeName, url);
        return this;
    }

    public boolean getIsTestSource()
    {
        return this.element.getAttributeValue(IntellijSourceFolder.isTestSourceAttributeName)
            .then((String isTestSourceAttributeValue) -> Booleans.parse(isTestSourceAttributeValue).await())
            .catchError(() -> false)
            .await();
    }

    public IntellijSourceFolder setIsTestSource(boolean isTestSource)
    {
        this.element.setAttribute(IntellijSourceFolder.isTestSourceAttributeName, Booleans.toString(isTestSource));
        return this;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof IntellijSourceFolder && this.equals((IntellijSourceFolder)rhs);
    }

    public boolean equals(IntellijSourceFolder rhs)
    {
        return rhs != null &&
            this.element.equals(rhs.element);
    }

    @Override
    public String toString()
    {
        return this.toString(XMLFormat.consise);
    }

    public String toString(XMLFormat format)
    {
        return this.toXML().toString(format);
    }

    public XMLElement toXML()
    {
        return this.element;
    }
}
