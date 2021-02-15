package qub;

public interface IntellijSourceFolderTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(IntellijSourceFolder.class, () ->
        {
            runner.testGroup("create(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> IntellijSourceFolder.create((String)null),
                        new PreConditionFailure("url cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    test.assertThrows(() -> IntellijSourceFolder.create(""),
                        new PreConditionFailure("url cannot be empty."));
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    test.assertNotNull(sourceFolder, "sourceFolder");
                    test.assertEqual("hello", sourceFolder.getUrl());
                    test.assertFalse(sourceFolder.getIsTestSource());
                    test.assertEqual(
                        XMLElement.create("sourceFolder").setAttribute("url", "hello"),
                        sourceFolder.toXml());
                });
            });

            runner.testGroup("create(XMLElement)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> IntellijSourceFolder.create((XMLElement)null),
                        new PreConditionFailure("xml cannot be null."));
                });

                runner.test("with non-sourceFolder element", (Test test) ->
                {
                    test.assertThrows(() -> IntellijSourceFolder.create(XMLElement.create("hello")),
                        new PreConditionFailure("xml.getName() (hello) must be sourceFolder."));
                });

                runner.test("with empty sourceFolder element", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create(XMLElement.create("sourceFolder"));
                    test.assertNotNull(sourceFolder);
                    test.assertNull(sourceFolder.getUrl());
                    test.assertFalse(sourceFolder.getIsTestSource());
                    test.assertEqual(
                        XMLElement.create("sourceFolder"),
                        sourceFolder.toXml());
                });

                runner.test("with expected attributes", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create(XMLElement.create("sourceFolder")
                        .setAttribute("url", "hello")
                        .setAttribute("isTestSource", "true"));
                    test.assertNotNull(sourceFolder);
                    test.assertEqual("hello", sourceFolder.getUrl());
                    test.assertTrue(sourceFolder.getIsTestSource());
                    test.assertEqual(
                        XMLElement.create("sourceFolder")
                            .setAttribute("url", "hello")
                            .setAttribute("isTestSource", "true"),
                        sourceFolder.toXml());
                });
                runner.test("with extra attributes", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create(XMLElement.create("sourceFolder")
                        .setAttribute("url", "there")
                        .setAttribute("isTestSource", "false")
                        .setAttribute("name", "foo"));
                    test.assertNotNull(sourceFolder);
                    test.assertEqual("there", sourceFolder.getUrl());
                    test.assertFalse(sourceFolder.getIsTestSource());
                    test.assertEqual(
                        XMLElement.create("sourceFolder")
                            .setAttribute("url", "there")
                            .setAttribute("isTestSource", "false")
                            .setAttribute("name", "foo"),
                        sourceFolder.toXml());
                });
            });

            runner.testGroup("setUrl(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    test.assertThrows(() -> sourceFolder.setUrl(null),
                        new PreConditionFailure("url cannot be null."));
                    test.assertEqual("hello", sourceFolder.getUrl());
                });

                runner.test("with empty", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    test.assertThrows(() -> sourceFolder.setUrl(""),
                        new PreConditionFailure("url cannot be empty."));
                    test.assertEqual("hello", sourceFolder.getUrl());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    final IntellijSourceFolder setUrlResult = sourceFolder.setUrl("there");
                    test.assertSame(sourceFolder, setUrlResult);
                    test.assertEqual("there", sourceFolder.getUrl());
                });
            });

            runner.testGroup("setIsTestSource(boolean)", () ->
            {
                runner.test("with false", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    final IntellijSourceFolder setIsTestSourceResult = sourceFolder.setIsTestSource(false);
                    test.assertSame(sourceFolder, setIsTestSourceResult);
                    test.assertFalse(sourceFolder.getIsTestSource());
                });

                runner.test("with true", (Test test) ->
                {
                    final IntellijSourceFolder sourceFolder = IntellijSourceFolder.create("hello");
                    final IntellijSourceFolder setIsTestSourceResult = sourceFolder.setIsTestSource(true);
                    test.assertSame(sourceFolder, setIsTestSourceResult);
                    test.assertTrue(sourceFolder.getIsTestSource());
                });
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<IntellijSourceFolder,Object,Boolean> equalsTest = (IntellijSourceFolder sourceFolder, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(sourceFolder, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, sourceFolder.equals(rhs));
                    });
                };

                equalsTest.run(IntellijSourceFolder.create("hello"), null, false);
                equalsTest.run(IntellijSourceFolder.create("hello"), "there", false);
                equalsTest.run(IntellijSourceFolder.create("hello"), IntellijSourceFolder.create("hello"), true);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    IntellijSourceFolder.create("hello"),
                    false);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(true),
                    IntellijSourceFolder.create("hello"),
                    false);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    true);
            });

            runner.testGroup("equals(IntellijSourceFolder)", () ->
            {
                final Action3<IntellijSourceFolder,IntellijSourceFolder,Boolean> equalsTest = (IntellijSourceFolder sourceFolder, IntellijSourceFolder rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(sourceFolder, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, sourceFolder.equals(rhs));
                    });
                };

                equalsTest.run(IntellijSourceFolder.create("hello"), null, false);
                equalsTest.run(IntellijSourceFolder.create("hello"), IntellijSourceFolder.create("hello"), true);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    IntellijSourceFolder.create("hello"),
                    false);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(true),
                    IntellijSourceFolder.create("hello"),
                    false);
                equalsTest.run(
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    IntellijSourceFolder.create("hello")
                        .setIsTestSource(false),
                    true);
            });
        });
    }
}
