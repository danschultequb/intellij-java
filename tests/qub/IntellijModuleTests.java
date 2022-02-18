package qub;

public interface IntellijModuleTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(IntellijModule.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final IntellijModule module = IntellijModule.create();
                test.assertNotNull(module);
                test.assertEqual(
                    XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create()
                            .setVersion("1.0")
                            .setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager"))),
                    module.toXml());
            });

            runner.testGroup("create(XMLDocument)", () ->
            {
                final Action2<XMLDocument,Throwable> createErrorTest = (XMLDocument xml, Throwable expected) ->
                {
                    runner.test("with " + Objects.toString(xml), (Test test) ->
                    {
                        test.assertThrows(() -> IntellijModule.create(xml),
                            expected);
                    });
                };

                createErrorTest.run(null, new PreConditionFailure("xml cannot be null."));
                createErrorTest.run(XMLDocument.create(), new PreConditionFailure("xml.getRoot() cannot be null."));
                createErrorTest.run(
                    XMLDocument.create()
                        .setRoot(XMLElement.create("spam")),
                    new PreConditionFailure("xml.getRoot().getName() (spam) must be module."));

                final Action1<XMLDocument> createTest = (XMLDocument xml) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijModule module = IntellijModule.create(xml);
                        test.assertNotNull(module);
                        test.assertEqual(xml, module.toXml());
                    });
                };

                createTest.run(
                    XMLDocument.create()
                        .setRoot(XMLElement.create("module")));
            });

            runner.testGroup("parse(File)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> IntellijModule.parse((File)null),
                        new PreConditionFailure("file cannot be null."));
                });

                runner.test("with non-existing root", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    test.assertThrows(() -> IntellijModule.parse(workspaceXMLFile).await(),
                        new RootNotFoundException("/"));
                });

                runner.test("with non-existing file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    fileSystem.createRoot("/").await();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    test.assertThrows(() -> IntellijModule.parse(workspaceXMLFile).await(),
                        new FileNotFoundException(workspaceXMLFile));
                });

                runner.test("with empty file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    fileSystem.createRoot("/").await();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    workspaceXMLFile.create().await();
                    test.assertThrows(() -> IntellijModule.parse(workspaceXMLFile).await(),
                        new PreConditionFailure("xml.getRoot() cannot be null."));
                });

                runner.test("with non-XML file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    fileSystem.createRoot("/").await();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    workspaceXMLFile.setContentsAsString("hello there").await();
                    test.assertThrows(() -> IntellijModule.parse(workspaceXMLFile).await(),
                        new ParseException("Expected only whitespace and elements at the root of the document."));
                });

                runner.test("with non-matching XML file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    fileSystem.createRoot("/").await();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    workspaceXMLFile.setContentsAsString("<a/>").await();
                    test.assertThrows(() -> IntellijModule.parse(workspaceXMLFile).await(),
                        new PreConditionFailure("xml.getRoot().getName() (a) must be module."));
                });

                runner.test("with matching XML file", (Test test) ->
                {
                    final InMemoryFileSystem fileSystem = InMemoryFileSystem.create();
                    fileSystem.createRoot("/").await();
                    final File workspaceXMLFile = fileSystem.getFile("/workspace.xml").await();
                    workspaceXMLFile.setContentsAsString("<module/>").await();
                    final IntellijModule module = IntellijModule.parse(workspaceXMLFile).await();
                    test.assertNotNull(module);
                    test.assertEqual(Iterable.create(), module.getModuleLibraries());
                });
            });

            runner.testGroup("parse(String)", () ->
            {
                final Action2<String,Throwable> parseErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> IntellijModule.parse(text).await(), expected);
                    });
                };

                parseErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseErrorTest.run("", new PreConditionFailure("xml.getRoot() cannot be null."));
                parseErrorTest.run("hello there", new ParseException("Expected only whitespace and elements at the root of the document."));
                parseErrorTest.run("<a/>", new PreConditionFailure("xml.getRoot().getName() (a) must be module."));

                final Action2<String,IntellijModule> parseTest = (String text, IntellijModule expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertEqual(expected, IntellijModule.parse(text).await());
                    });
                };

                parseTest.run(
                    "<module/>",
                    IntellijModule.create(XMLDocument.create()
                        .setRoot(XMLElement.create("module"))));
            });

            runner.testGroup("setOutputUrl(String)", () ->
            {
                final Action3<IntellijModule,String,Throwable> setOutputUrlErrorTest = (IntellijModule module, String outputUrl, Throwable expected) ->
                {
                    runner.test("with " + English.andList(module, Strings.escapeAndQuote(outputUrl)), (Test test) ->
                    {
                        test.assertThrows(() -> module.setOutputUrl(outputUrl), expected);
                    });
                };

                setOutputUrlErrorTest.run(IntellijModule.create(), null, new PreConditionFailure("outputUrl cannot be null."));
                setOutputUrlErrorTest.run(IntellijModule.create(), "", new PreConditionFailure("outputUrl cannot be empty."));

                final Action3<IntellijModule,String,IntellijModule> setOutputUrlTest = (IntellijModule module, String outputUrl, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, Strings.escapeAndQuote(outputUrl)), (Test test) ->
                    {
                        final IntellijModule setOutputUrlResult = module.setOutputUrl(outputUrl);
                        test.assertSame(module, setOutputUrlResult);
                        test.assertEqual(expected, module);
                    });
                };

                setOutputUrlTest.run(
                    IntellijModule.create(),
                    "hello",
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output")
                                    .setAttribute("url", "hello"))))));
                setOutputUrlTest.run(
                    IntellijModule.create(),
                    "file://$MODULE_DIR$/outputs",
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output")
                                    .setAttribute("url", "file://$MODULE_DIR$/outputs"))))));
            });

            runner.testGroup("setOutputTestUrl(String)", () ->
            {
                final Action3<IntellijModule,String,Throwable> setOutputTestUrlErrorTest = (IntellijModule module, String outputTestUrl, Throwable expected) ->
                {
                    runner.test("with " + English.andList(module, Strings.escapeAndQuote(outputTestUrl)), (Test test) ->
                    {
                        test.assertThrows(() -> module.setOutputTestUrl(outputTestUrl), expected);
                    });
                };

                setOutputTestUrlErrorTest.run(IntellijModule.create(), null, new PreConditionFailure("outputTestUrl cannot be null."));
                setOutputTestUrlErrorTest.run(IntellijModule.create(), "", new PreConditionFailure("outputTestUrl cannot be empty."));

                final Action3<IntellijModule,String,IntellijModule> setOutputTestUrlTest = (IntellijModule module, String outputTestUrl, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, Strings.escapeAndQuote(outputTestUrl)), (Test test) ->
                    {
                        final IntellijModule setOutputUrlResult = module.setOutputTestUrl(outputTestUrl);
                        test.assertSame(module, setOutputUrlResult);
                        test.assertEqual(expected, module);
                    });
                };

                setOutputTestUrlTest.run(
                    IntellijModule.create(),
                    "hello",
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output-test")
                                    .setAttribute("url", "hello"))))));
                setOutputTestUrlTest.run(
                    IntellijModule.create(),
                    "file://$MODULE_DIR$/outputs",
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("output-test")
                                    .setAttribute("url", "file://$MODULE_DIR$/outputs"))))));
            });

            runner.testGroup("setExcludeOutput(boolean)", () ->
            {
                final Action3<IntellijModule,Boolean,IntellijModule> setExcludeOutputTest = (IntellijModule module, Boolean excludeOutput, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, excludeOutput), (Test test) ->
                    {
                        final IntellijModule setExcludeOutputResult = module.setExcludeOutput(excludeOutput);
                        test.assertSame(module, setExcludeOutputResult);
                        test.assertEqual(expected, module);
                    });
                };

                setExcludeOutputTest.run(
                    IntellijModule.create(),
                    false,
                    IntellijModule.create());
                setExcludeOutputTest.run(
                    IntellijModule.create(),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("exclude-output"))))));
                setExcludeOutputTest.run(
                    IntellijModule.create().setExcludeOutput(true),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("exclude-output"))))));
                setExcludeOutputTest.run(
                    IntellijModule.create().setExcludeOutput(true),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")))));
            });

            runner.testGroup("addSourceFolder(IntellijSourceFolder)", () ->
            {
                final Action3<IntellijModule,IntellijSourceFolder,Throwable> addSourceFolderErrorTest = (IntellijModule module, IntellijSourceFolder sourceFolder, Throwable expected) ->
                {
                    runner.test("with " + English.andList(module, sourceFolder), (Test test) ->
                    {
                        test.assertThrows(() -> module.addSourceFolder(sourceFolder), expected);
                    });
                };

                addSourceFolderErrorTest.run(IntellijModule.create(), null, new PreConditionFailure("sourceFolder cannot be null."));

                final Action3<IntellijModule,IntellijSourceFolder,IntellijModule> addSourceFolderTest = (IntellijModule module, IntellijSourceFolder sourceFolder, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, Strings.escapeAndQuote(sourceFolder)), (Test test) ->
                    {
                        final IntellijModule setOutputUrlResult = module.addSourceFolder(sourceFolder);
                        test.assertSame(module, setOutputUrlResult);
                        test.assertEqual(expected, module);
                    });
                };

                addSourceFolderTest.run(
                    IntellijModule.create(),
                    IntellijSourceFolder.create("hello"),
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("content")
                                    .setAttribute("url", "file://$MODULE_DIR$")
                                    .addChild(XMLElement.create("sourceFolder")
                                        .setAttribute("url", "hello")))))));
                addSourceFolderTest.run(
                    IntellijModule.create().addSourceFolder(IntellijSourceFolder.create("hello")),
                    IntellijSourceFolder.create("there"),
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("content")
                                    .setAttribute("url", "file://$MODULE_DIR$")
                                    .addChild(XMLElement.create("sourceFolder")
                                        .setAttribute("url", "hello"))
                                    .addChild(XMLElement.create("sourceFolder")
                                        .setAttribute("url", "there")))))));
            });

            runner.testGroup("setInheritedJdk(boolean)", () ->
            {
                final Action3<IntellijModule,Boolean,IntellijModule> setInheritedJdkTest = (IntellijModule module, Boolean inheritedJdk, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, inheritedJdk), (Test test) ->
                    {
                        final IntellijModule setInheritedJdkResult = module.setInheritedJdk(inheritedJdk);
                        test.assertSame(module, setInheritedJdkResult);
                        test.assertEqual(expected, module);
                    });
                };

                setInheritedJdkTest.run(
                    IntellijModule.create(),
                    false,
                    IntellijModule.create());
                setInheritedJdkTest.run(
                    IntellijModule.create(),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "inheritedJdk"))))));
                setInheritedJdkTest.run(
                    IntellijModule.create().setInheritedJdk(true),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "inheritedJdk"))))));
                setInheritedJdkTest.run(
                    IntellijModule.create().setInheritedJdk(true),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")))));
                setInheritedJdkTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))))),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))))));
                setInheritedJdkTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))))),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "inheritedJdk"))))));
                setInheritedJdkTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))))),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))))));
                setInheritedJdkTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))))),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "inheritedJdk"))))));
            });

            runner.testGroup("setSourceFolderForTests(boolean)", () ->
            {
                final Action3<IntellijModule,Boolean,IntellijModule> setSourceFolderForTestsTest = (IntellijModule module, Boolean sourceFolderForTests, IntellijModule expected) ->
                {
                    runner.test("with " + English.andList(module, sourceFolderForTests), (Test test) ->
                    {
                        final IntellijModule setSourceFolderForTestsResult = module.setSourceFolderForTests(sourceFolderForTests);
                        test.assertSame(module, setSourceFolderForTestsResult);
                        test.assertEqual(expected, module);
                    });
                };

                setSourceFolderForTestsTest.run(
                    IntellijModule.create(),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "false"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create(),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "true"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create().setSourceFolderForTests(true),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component")
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "true"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create().setSourceFolderForTests(true),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "false"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))))),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "false"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))))),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("notOrderEntry").setAttribute("type", "inheritedJdk"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "true"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))))),
                    false,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "false"))))));
                setSourceFolderForTestsTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))))),
                    true,
                    IntellijModule.create(XMLDocument.create()
                        .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                        .setRoot(XMLElement.create("module")
                            .setAttribute("type", "JAVA_MODULE")
                            .setAttribute("version", "4")
                            .addChild(XMLElement.create("component", true)
                                .setAttribute("name", "NewModuleRootManager")
                                .addChild(XMLElement.create("orderEntry").setAttribute("notType", "spam"))
                                .addChild(XMLElement.create("orderEntry").setAttribute("type", "sourceFolder").setAttribute("forTests", "true"))))));
            });

            runner.testGroup("getModuleLibraries()", () ->
            {
                final Action2<IntellijModule,Iterable<IntellijModuleLibrary>> getModuleLibrariesTest = (IntellijModule module, Iterable<IntellijModuleLibrary> expected) ->
                {
                    runner.test("with " + module.toString(), (Test test) ->
                    {
                        test.assertEqual(expected, module.getModuleLibraries());
                    });
                };

                getModuleLibrariesTest.run(
                    IntellijModule.create(),
                    Iterable.create());
                getModuleLibrariesTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setRoot(XMLElement.create("module")
                            .addChild(XMLElement.create("component")
                                .addChild(XMLElement.create("notOrderEntry"))))),
                    Iterable.create());
                getModuleLibrariesTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setRoot(XMLElement.create("module")
                            .addChild(XMLElement.create("component")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("notType", "module-library"))))),
                    Iterable.create());
                getModuleLibrariesTest.run(
                    IntellijModule.create(XMLDocument.create()
                        .setRoot(XMLElement.create("module")
                            .addChild(XMLElement.create("component")
                                .addChild(XMLElement.create("orderEntry")
                                    .setAttribute("type", "not-module-library"))))),
                    Iterable.create());
                getModuleLibrariesTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()),
                    Iterable.create(IntellijModuleLibrary.create()));
                getModuleLibrariesTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create(XMLElement.create("orderEntry").setAttribute("type", "module-library").setAttribute("a", "b"))),
                    Iterable.create(IntellijModuleLibrary.create(XMLElement.create("orderEntry").setAttribute("type", "module-library").setAttribute("a", "b"))));
                getModuleLibrariesTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()
                            .addSourcesUrl("sources-url-1"))
                        .addModuleLibrary(IntellijModuleLibrary.create()
                            .addSourcesUrl("sources-url-2")),
                    Iterable.create(
                        IntellijModuleLibrary.create().addSourcesUrl("sources-url-1"),
                        IntellijModuleLibrary.create().addSourcesUrl("sources-url-2")));
            });

            runner.testGroup("addModuleLibrary(IntellijModuleLibrary)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final IntellijModule module = IntellijModule.create();
                    test.assertThrows(() -> module.addModuleLibrary(null),
                        new PreConditionFailure("moduleLibrary cannot be null."));
                    test.assertEqual(
                        XMLDocument.create()
                            .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                            .setRoot(XMLElement.create("module").setAttribute("type", "JAVA_MODULE").setAttribute("version", "4")
                                .addChild(XMLElement.create("component").setAttribute("name", "NewModuleRootManager"))),
                        module.toXml());
                });

                runner.test("with non-null", (Test test) ->
                {
                    final IntellijModule module = IntellijModule.create();
                    final IntellijModule addModuleLibraryResult = module.addModuleLibrary(IntellijModuleLibrary.create());
                    test.assertSame(module, addModuleLibraryResult);
                    test.assertEqual(
                        XMLDocument.create()
                            .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                            .setRoot(XMLElement.create("module").setAttribute("type", "JAVA_MODULE").setAttribute("version", "4")
                                .addChild(XMLElement.create("component").setAttribute("name", "NewModuleRootManager")
                                    .addChild(XMLElement.create("orderEntry").setAttribute("type", "module-library")
                                        .addChild(XMLElement.create("library")
                                            .addChild(XMLElement.create("CLASSES"))
                                            .addChild(XMLElement.create("JAVADOC"))
                                            .addChild(XMLElement.create("SOURCES")))))),
                        module.toXml());
                });
            });

            runner.testGroup("clearModuleLibraries()", () ->
            {
                final Action2<IntellijModule,IntellijModule> clearModuleLibrariesTest = (IntellijModule module, IntellijModule expected) ->
                {
                    runner.test("with " + module.toString(), (Test test) ->
                    {
                        final IntellijModule clearModuleLibrariesResult = module.clearModuleLibraries();
                        test.assertSame(module, clearModuleLibrariesResult);
                        test.assertEqual(expected, module);
                    });
                };

                clearModuleLibrariesTest.run(
                    IntellijModule.create(),
                    IntellijModule.create());
                clearModuleLibrariesTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create()),
                    IntellijModule.create(
                        XMLDocument.create()
                            .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                            .setRoot(XMLElement.create("module").setAttribute("type", "JAVA_MODULE").setAttribute("version", "4")
                                .addChild(XMLElement.create("component", true).setAttribute("name", "NewModuleRootManager")))));
                clearModuleLibrariesTest.run(
                    IntellijModule.create()
                        .addModuleLibrary(IntellijModuleLibrary.create())
                        .addModuleLibrary(IntellijModuleLibrary.create()),
                    IntellijModule.create(
                        XMLDocument.create()
                            .setDeclaration(XMLDeclaration.create().setVersion("1.0").setEncoding("UTF-8"))
                            .setRoot(XMLElement.create("module").setAttribute("type", "JAVA_MODULE").setAttribute("version", "4")
                                .addChild(XMLElement.create("component", true).setAttribute("name", "NewModuleRootManager")))));
            });

            runner.testGroup("toXml()", () ->
            {
                final Action2<IntellijModule,XMLDocument> toXmlTest = (IntellijModule module, XMLDocument expected) ->
                {
                    runner.test("with " + module, (Test test) ->
                    {
                        test.assertEqual(expected, module.toXml());
                    });
                };

                toXmlTest.run(
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

                toXmlTest.run(
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

                toXmlTest.run(
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

                toXmlTest.run(
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

                toXmlTest.run(
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
                toXmlTest.run(
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
                toXmlTest.run(
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
                toXmlTest.run(
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
                toXmlTest.run(
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
                toXmlTest.run(
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

                toXmlTest.run(
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
                toXmlTest.run(
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