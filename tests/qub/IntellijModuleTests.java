package qub;

public interface IntellijModuleTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(IntellijModule.class, () ->
        {
            runner.testGroup("toXML()", () ->
            {
                final Action2<IntellijModule,XMLDocument> toXMLTest = (IntellijModule module, XMLDocument expected) ->
                {
                    runner.test("with " + module, (Test test) ->
                    {
                        test.assertEqual(expected, module.toXML());
                    });
                };

                toXMLTest.run(
                    IntellijModule.create(),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager"))));

                toXMLTest.run(
                    IntellijModule.create()
                        .setInheritedJdk(true),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "inheritedJdk")))));

                toXMLTest.run(
                    IntellijModule.create()
                        .setSourceFolderForTests(false),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "sourceFolder")
                                    .setAttribute("forTests", "false")))));

                toXMLTest.run(
                    IntellijModule.create()
                        .setSourceFolderForTests(true),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "sourceFolder")
                                    .setAttribute("forTests", "true")))));

                toXMLTest.run(
                    IntellijModule.create()
                        .setOutputUrl("file://$MODULE_DIR$/outputs"),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output")
                                    .setAttribute("url", "file://$MODULE_DIR$/outputs")))));
                toXMLTest.run(
                    IntellijModule.create()
                        .setOutputTestUrl("file://$MODULE_DIR$/outputs"),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output-test")
                                    .setAttribute("url", "file://$MODULE_DIR$/outputs")))));
                toXMLTest.run(
                    IntellijModule.create()
                        .setExcludeOutput(true),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("exclude-output")))));
                toXMLTest.run(
                    IntellijModule.create()
                        .addSourceFolder(IntellijSourceFolder.create("file://$MODULE_DIR$/sources")
                            .setIsTestSource(false))
                        .addSourceFolder(IntellijSourceFolder.create("file://$MODULE_DIR$/tests")
                            .setIsTestSource(true)),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("content")
                                    .setAttribute("url", "file://$MODULE_DIR$")
                                    .addChild(XMLElement.create("sourceFolder")
                                        .setAttribute("url", "file://$MODULE_DIR$/sources")
                                        .setAttribute("isTestSource", "false"))
                                    .addChild(XMLElement.create("sourceFolder")
                                        .setAttribute("url", "file://$MODULE_DIR$/tests")
                                        .setAttribute("isTestSource", "true"))))));
                toXMLTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "module-library")
                                    .addChild(XMLElement.create("library")
                                        .addChild(XMLElement.create("CLASSES"))
                                        .addChild(XMLElement.create("JAVADOC"))
                                        .addChild(XMLElement.create("SOURCES")))))));
                toXMLTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()
                            .addClassesUrl("jar://C:/qub/qub/qub-java/164/qub-java.jar!/")),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "module-library")
                                    .addChild(XMLElement.create("library")
                                        .addChild(XMLElement.create("CLASSES")
                                            .addChild(XMLElement.create("root")
                                                .setAttribute("url", "jar://C:/qub/qub/qub-java/164/qub-java.jar!/")))
                                        .addChild(XMLElement.create("JAVADOC"))
                                        .addChild(XMLElement.create("SOURCES")))))));

                toXMLTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()
                            .addClassesUrl("jar://C:/qub/qub/qub-java/164/qub-java.jar!/")
                            .addClassesUrl("jar://C:/qub/qub/qub-java/165/qub-java.jar!/")),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "module-library")
                                    .addChild(XMLElement.create("library")
                                        .addChild(XMLElement.create("CLASSES")
                                            .addChild(XMLElement.create("root")
                                                .setAttribute("url", "jar://C:/qub/qub/qub-java/164/qub-java.jar!/"))
                                            .addChild(XMLElement.create("root")
                                                .setAttribute("url", "jar://C:/qub/qub/qub-java/165/qub-java.jar!/")))
                                        .addChild(XMLElement.create("JAVADOC"))
                                        .addChild(XMLElement.create("SOURCES")))))));
                toXMLTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()
                            .addSourcesUrl("jar://C:/qub/qub/qub-java/164/qub-java.sources.jar!/")),
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "module-library")
                                    .addChild(XMLElement.create("library")
                                        .addChild(XMLElement.create("CLASSES"))
                                        .addChild(XMLElement.create("JAVADOC"))
                                        .addChild(XMLElement.create("SOURCES")
                                            .addChild(XMLElement.create("root")
                                                .setAttribute("url", "jar://C:/qub/qub/qub-java/164/qub-java.sources.jar!/"))))))));
            });
        });
    }
}