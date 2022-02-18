package qub;

public interface IntellijModuleLibraryTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(IntellijModuleLibrary.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                test.assertEqual(
                    XMLElement.create("orderEntry").setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES"))
                            .addChild(XMLElement.create("JAVADOC"))
                            .addChild(XMLElement.create("SOURCES"))),
                    moduleLibrary.toXml());
            });

            runner.testGroup("create(XMLElement)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> IntellijModuleLibrary.create(null),
                        new PreConditionFailure("xml cannot be null."));
                });

                runner.test("with non-orderEntry element", (Test test) ->
                {
                    test.assertThrows(() -> IntellijModuleLibrary.create(XMLElement.create("hello")),
                        new PreConditionFailure("xml.getName() (hello) must be orderEntry."));
                });

                runner.test("with no type attribute", (Test test) ->
                {
                    test.assertThrows(() -> IntellijModuleLibrary.create(XMLElement.create("orderEntry")),
                        new PreConditionFailure("xml.getAttributeValue(IntellijModuleLibrary.typeAttributeName).await() (null) must be module-library."));
                });

                runner.test("with non-module-library type attribute", (Test test) ->
                {
                    test.assertThrows(() -> IntellijModuleLibrary.create(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "chocolate")),
                        new PreConditionFailure("xml.getAttributeValue(IntellijModuleLibrary.typeAttributeName).await() (chocolate) must be module-library."));
                });

                runner.test("with valid element", (Test test) ->
                {
                    final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "module-library"));
                    test.assertNotNull(moduleLibrary);
                    test.assertEqual(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "module-library")
                            .addChild(XMLElement.create("library")
                                .addChild(XMLElement.create("CLASSES"))
                                .addChild(XMLElement.create("JAVADOC"))
                                .addChild(XMLElement.create("SOURCES"))),
                        moduleLibrary.toXml());
                });
            });

            runner.testGroup("getClassesUrls(XMLElement,Iterable<String>)", () ->
            {
                final Action2<XMLElement,Iterable<String>> getClassesUrlsTest = (XMLElement xml, Iterable<String> expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create(xml);
                        test.assertEqual(expected, moduleLibrary.getClassesUrls());
                    });
                };

                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library"),
                    Iterable.create());
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")),
                    Iterable.create());
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES"))),
                    Iterable.create());
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES")
                                .addChild(XMLElement.create("root")))),
                    Iterable.create());
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "")))),
                    Iterable.create());
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "a")))),
                    Iterable.create("a"));
                getClassesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("CLASSES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "a"))
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "b")))),
                    Iterable.create("a", "b"));
            });

            runner.testGroup("addClassesUrl(String)", () ->
            {
                final Action2<String,Throwable> addClassesUrlErrorTest = (String classesUrl, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(classesUrl), (Test test) ->
                    {
                        final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                        test.assertThrows(() -> moduleLibrary.addClassesUrl(classesUrl),
                            expected);
                        test.assertEqual(Iterable.create(), moduleLibrary.getClassesUrls());
                    });
                };

                addClassesUrlErrorTest.run(null, new PreConditionFailure("classesUrl cannot be null."));
                addClassesUrlErrorTest.run("", new PreConditionFailure("classesUrl cannot be empty."));

                final Action1<String> addClassesUrlTest = (String classesUrl) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(classesUrl), (Test test) ->
                    {
                        final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                        final IntellijModuleLibrary addClassesUrlResult = moduleLibrary.addClassesUrl(classesUrl);
                        test.assertSame(moduleLibrary, addClassesUrlResult);
                        test.assertEqual(Iterable.create(classesUrl), moduleLibrary.getClassesUrls());
                        test.assertEqual(Iterable.create(), moduleLibrary.getSourcesUrls());
                    });
                };

                addClassesUrlTest.run("a");
                addClassesUrlTest.run("jar:///qub/folder/thing");
            });

            runner.testGroup("clearClassesUrls()", () ->
            {
                final Action2<IntellijModuleLibrary,IntellijModuleLibrary> clearClassesUrlsTest = (IntellijModuleLibrary moduleLibrary, IntellijModuleLibrary expected) ->
                {
                    runner.test("with " + moduleLibrary, (Test test) ->
                    {
                        final IntellijModuleLibrary clearClassesUrlsResult = moduleLibrary.clearClassesUrls();
                        test.assertSame(moduleLibrary, clearClassesUrlsResult);
                        test.assertEqual(expected, moduleLibrary);
                    });
                };

                clearClassesUrlsTest.run(IntellijModuleLibrary.create(), IntellijModuleLibrary.create());
                clearClassesUrlsTest.run(
                    IntellijModuleLibrary.create()
                        .addClassesUrl("hello")
                        .addClassesUrl("there")
                        .addSourcesUrl("friend"),
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("friend"));
            });

            runner.testGroup("getSourcesUrls(XMLElement,Iterable<String>)", () ->
            {
                final Action2<XMLElement,Iterable<String>> getSourcesUrlsTest = (XMLElement xml, Iterable<String> expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create(xml);
                        test.assertEqual(expected, moduleLibrary.getSourcesUrls());
                    });
                };

                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library"),
                    Iterable.create());
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")),
                    Iterable.create());
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("SOURCES"))),
                    Iterable.create());
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("SOURCES")
                                .addChild(XMLElement.create("root")))),
                    Iterable.create());
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("SOURCES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "")))),
                    Iterable.create());
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("SOURCES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "a")))),
                    Iterable.create("a"));
                getSourcesUrlsTest.run(
                    XMLElement.create("orderEntry")
                        .setAttribute("type", "module-library")
                        .addChild(XMLElement.create("library")
                            .addChild(XMLElement.create("SOURCES")
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "a"))
                                .addChild(XMLElement.create("root")
                                    .setAttribute("url", "b")))),
                    Iterable.create("a", "b"));
            });

            runner.testGroup("addSourcesUrl(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                    test.assertThrows(() -> moduleLibrary.addSourcesUrl(null),
                        new PreConditionFailure("sourcesUrl cannot be null."));
                    test.assertEqual(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "module-library")
                            .addChild(XMLElement.create("library")
                                .addChild(XMLElement.create("CLASSES"))
                                .addChild(XMLElement.create("JAVADOC"))
                                .addChild(XMLElement.create("SOURCES"))),
                        moduleLibrary.toXml());
                });

                runner.test("with empty", (Test test) ->
                {
                    final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                    test.assertThrows(() -> moduleLibrary.addSourcesUrl(""),
                        new PreConditionFailure("sourcesUrl cannot be empty."));
                    test.assertEqual(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "module-library")
                            .addChild(XMLElement.create("library")
                                .addChild(XMLElement.create("CLASSES"))
                                .addChild(XMLElement.create("JAVADOC"))
                                .addChild(XMLElement.create("SOURCES"))),
                        moduleLibrary.toXml());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final IntellijModuleLibrary moduleLibrary = IntellijModuleLibrary.create();
                    final IntellijModuleLibrary addSourcesUrlResult = moduleLibrary.addSourcesUrl("hello");
                    test.assertSame(moduleLibrary, addSourcesUrlResult);
                    test.assertEqual(
                        XMLElement.create("orderEntry")
                            .setAttribute("type", "module-library")
                            .addChild(XMLElement.create("library")
                                .addChild(XMLElement.create("CLASSES"))
                                .addChild(XMLElement.create("JAVADOC"))
                                .addChild(XMLElement.create("SOURCES")
                                    .addChild(XMLElement.create("root")
                                        .setAttribute("url", "hello")))),
                        moduleLibrary.toXml());
                });
            });

            runner.testGroup("clearSourcesUrls()", () ->
            {
                final Action2<IntellijModuleLibrary,IntellijModuleLibrary> clearSourcesUrlTest = (IntellijModuleLibrary moduleLibrary, IntellijModuleLibrary expected) ->
                {
                    runner.test("with " + moduleLibrary, (Test test) ->
                    {
                        final IntellijModuleLibrary clearSourcesUrlsResult = moduleLibrary.clearSourcesUrls();
                        test.assertSame(moduleLibrary, clearSourcesUrlsResult);
                        test.assertEqual(expected, moduleLibrary);
                    });
                };

                clearSourcesUrlTest.run(IntellijModuleLibrary.create(), IntellijModuleLibrary.create());
                clearSourcesUrlTest.run(
                    IntellijModuleLibrary.create()
                        .addClassesUrl("hello")
                        .addClassesUrl("there")
                        .addSourcesUrl("friend"),
                    IntellijModuleLibrary.create()
                        .addClassesUrl("hello")
                        .addClassesUrl("there"));
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<IntellijModuleLibrary,Object,Boolean> equalsTest = (IntellijModuleLibrary moduleLibrary, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(moduleLibrary, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, moduleLibrary.equals(rhs));
                    });
                };

                equalsTest.run(IntellijModuleLibrary.create(), null, false);
                equalsTest.run(IntellijModuleLibrary.create(), "hello", false);
                equalsTest.run(IntellijModuleLibrary.create(), IntellijModuleLibrary.create(), true);
                equalsTest.run(
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("there"),
                    false);
                equalsTest.run(
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    true);
            });

            runner.testGroup("equals(IntellijModuleLibrary)", () ->
            {
                final Action3<IntellijModuleLibrary,IntellijModuleLibrary,Boolean> equalsTest = (IntellijModuleLibrary moduleLibrary, IntellijModuleLibrary rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(moduleLibrary, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, moduleLibrary.equals(rhs));
                    });
                };

                equalsTest.run(IntellijModuleLibrary.create(), null, false);
                equalsTest.run(IntellijModuleLibrary.create(), IntellijModuleLibrary.create(), true);
                equalsTest.run(
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("there"),
                    false);
                equalsTest.run(
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    IntellijModuleLibrary.create()
                        .addSourcesUrl("hello"),
                    true);
            });
        });
    }
}
