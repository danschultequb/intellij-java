package qub;

public interface IntellijWorkspaceRunConfigurationTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(IntellijWorkspaceRunConfiguration.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create();
                test.assertNotNull(runConfiguration);
                test.assertLinesEqual(
                    Iterable.create(
                        "<configuration type=\"Application\" factoryName=\"Application\">",
                        "  <method v=\"2\">",
                        "    <option name=\"Make\" enabled=\"true\"/>",
                        "  </method>",
                        "</configuration>"),
                    runConfiguration.toString(XMLFormat.pretty));
            });

            runner.testGroup("create(XMLElement)", () ->
            {
                final Action2<XMLElement,Throwable> createErrorTest = (XMLElement xml, Throwable expected) ->
                {
                    runner.test("with " + Objects.toString(xml), (Test test) ->
                    {
                        test.assertThrows(() -> IntellijWorkspaceRunConfiguration.create(xml),
                            expected);
                    });
                };

                createErrorTest.run(null, new PreConditionFailure("xml cannot be null."));
                createErrorTest.run(XMLElement.create("spam"), new PreConditionFailure("xml.getName() (spam) must be configuration."));

                final Action1<XMLElement> createTest = (XMLElement xml) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertNotNull(runConfiguration);
                        test.assertSame(xml, runConfiguration.toXml());
                    });
                };

                createTest.run(XMLElement.create("configuration"));
                createTest.run(XMLElement.create("configuration", true));
                createTest.run(XMLElement.create("configuration").setAttribute("name", "a").setAttribute("type", "b"));
            });

            runner.testGroup("getName()", () ->
            {
                final Action2<XMLElement,String> getNameTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getName());
                    });
                };

                getNameTest.run(XMLElement.create("configuration"), null);
                getNameTest.run(XMLElement.create("configuration").setAttribute("name", ""), "");
                getNameTest.run(XMLElement.create("configuration").setAttribute("name", "hello"), "hello");
            });

            runner.testGroup("setName(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setNameErrorTest = (XMLElement xml, String name, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(name)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialName = runConfiguration.getName();
                        test.assertThrows(() -> runConfiguration.setName(name),
                            expected);
                        test.assertSame(initialName, runConfiguration.getName());
                    });
                };

                setNameErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("name cannot be null."));
                setNameErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("name cannot be empty."));

                final Action2<XMLElement,String> setNameTest = (XMLElement xml, String name) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(name)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setNameResult = runConfiguration.setName(name);
                        test.assertSame(runConfiguration, setNameResult);
                        test.assertEqual(name, runConfiguration.getName());
                    });
                };

                setNameTest.run(XMLElement.create("configuration"), "hello");
                setNameTest.run(XMLElement.create("configuration").setAttribute("name", "hello"), "there");
            });

            runner.testGroup("getType()", () ->
            {
                final Action2<XMLElement,String> getTypeTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getType());
                    });
                };

                getTypeTest.run(XMLElement.create("configuration"), null);
                getTypeTest.run(XMLElement.create("configuration").setAttribute("type", ""), "");
                getTypeTest.run(XMLElement.create("configuration").setAttribute("type", "hello"), "hello");
            });

            runner.testGroup("setType(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setTypeErrorTest = (XMLElement xml, String type, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(type)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialType = runConfiguration.getType();
                        test.assertThrows(() -> runConfiguration.setType(type),
                            expected);
                        test.assertSame(initialType, runConfiguration.getType());
                    });
                };

                setTypeErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("type cannot be null."));
                setTypeErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("type cannot be empty."));

                final Action2<XMLElement,String> setTypeTest = (XMLElement xml, String type) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(type)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setTypeResult = runConfiguration.setType(type);
                        test.assertSame(runConfiguration, setTypeResult);
                        test.assertEqual(type, runConfiguration.getType());
                    });
                };

                setTypeTest.run(XMLElement.create("configuration"), "hello");
                setTypeTest.run(XMLElement.create("configuration").setAttribute("type", "hello"), "there");
            });

            runner.testGroup("getFactoryName()", () ->
            {
                final Action2<XMLElement,String> getFactoryNameTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getFactoryName());
                    });
                };

                getFactoryNameTest.run(XMLElement.create("configuration"), null);
                getFactoryNameTest.run(XMLElement.create("configuration").setAttribute("factoryName", ""), "");
                getFactoryNameTest.run(XMLElement.create("configuration").setAttribute("factoryName", "hello"), "hello");
            });

            runner.testGroup("setFactoryName(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setFactoryNameErrorTest = (XMLElement xml, String factoryName, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(factoryName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialFactoryName = runConfiguration.getFactoryName();
                        test.assertThrows(() -> runConfiguration.setFactoryName(factoryName),
                            expected);
                        test.assertSame(initialFactoryName, runConfiguration.getFactoryName());
                    });
                };

                setFactoryNameErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("factoryName cannot be null."));
                setFactoryNameErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("factoryName cannot be empty."));

                final Action2<XMLElement,String> setFactoryNameTest = (XMLElement xml, String factoryName) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(factoryName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setFactoryNameResult = runConfiguration.setFactoryName(factoryName);
                        test.assertSame(runConfiguration, setFactoryNameResult);
                        test.assertEqual(factoryName, runConfiguration.getFactoryName());
                    });
                };

                setFactoryNameTest.run(XMLElement.create("configuration"), "hello");
                setFactoryNameTest.run(XMLElement.create("configuration").setAttribute("factoryName", "hello"), "there");
            });

            runner.testGroup("getMainClassFullName()", () ->
            {
                final Action2<XMLElement,String> getMainClassFullNameTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getMainClassFullName());
                    });
                };

                getMainClassFullNameTest.run(XMLElement.create("configuration"), null);
                getMainClassFullNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "OTHER_CLASS_NAME").setAttribute("value", "a")),
                    null);
                getMainClassFullNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "MAIN_CLASS_NAME").setAttribute("value", "")),
                    "");
                getMainClassFullNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "MAIN_CLASS_NAME").setAttribute("value", "a")),
                    "a");
            });

            runner.testGroup("setMainClassFullName(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setMainClassFullNameErrorTest = (XMLElement xml, String mainClassFullName, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(mainClassFullName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialMainClassFullName = runConfiguration.getMainClassFullName();
                        test.assertThrows(() -> runConfiguration.setMainClassFullName(mainClassFullName),
                            expected);
                        test.assertEqual(initialMainClassFullName, runConfiguration.getMainClassFullName());
                    });
                };

                setMainClassFullNameErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("mainClassFullName cannot be null."));
                setMainClassFullNameErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("mainClassFullName cannot be empty."));

                final Action2<XMLElement,String> setMainClassFullNameTest = (XMLElement xml, String mainClassFullName) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(mainClassFullName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setMainClassFullNameResult = runConfiguration.setMainClassFullName(mainClassFullName);
                        test.assertSame(runConfiguration, setMainClassFullNameResult);
                        test.assertEqual(mainClassFullName, runConfiguration.getMainClassFullName());
                    });
                };

                setMainClassFullNameTest.run(XMLElement.create("configuration"), "a");
                setMainClassFullNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "MAIN_CLASS_NAME").setAttribute("value", "a")),
                    "b");
            });

            runner.testGroup("getModuleName()", () ->
            {
                final Action2<XMLElement,String> getModuleNameTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getModuleName());
                    });
                };

                getModuleNameTest.run(XMLElement.create("configuration"), null);
                getModuleNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("module").setAttribute("name", "")),
                    "");
                getModuleNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("module").setAttribute("name", "a")),
                    "a");
                getModuleNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("module").setAttribute("name", "a"))
                        .addChild(XMLElement.create("module").setAttribute("name", "b")),
                    "a");
            });

            runner.testGroup("setModuleName(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setModuleNameErrorTest = (XMLElement xml, String moduleName, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(moduleName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialMainClassFullName = runConfiguration.getModuleName();
                        test.assertThrows(() -> runConfiguration.setModuleName(moduleName),
                            expected);
                        test.assertEqual(initialMainClassFullName, runConfiguration.getModuleName());
                    });
                };

                setModuleNameErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("moduleName cannot be null."));
                setModuleNameErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("moduleName cannot be empty."));

                final Action2<XMLElement,String> setModuleNameTest = (XMLElement xml, String moduleName) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(moduleName)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setModuleNameResult = runConfiguration.setModuleName(moduleName);
                        test.assertSame(runConfiguration, setModuleNameResult);
                        test.assertEqual(moduleName, runConfiguration.getModuleName());
                    });
                };

                setModuleNameTest.run(XMLElement.create("configuration"), "a");
                setModuleNameTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("module").setAttribute("name", "a")),
                    "b");
            });

            runner.testGroup("getProgramParameters()", () ->
            {
                final Action2<XMLElement,String> getProgramParametersTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getProgramParameters());
                    });
                };

                getProgramParametersTest.run(XMLElement.create("configuration"), null);
                getProgramParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "PROGRAM_PARAMETERS").setAttribute("value", "")),
                    "");
                getProgramParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "PROGRAM_PARAMETERS").setAttribute("value", "a")),
                    "a");
            });

            runner.testGroup("setProgramParameters(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setProgramParametersErrorTest = (XMLElement xml, String programParameters, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(programParameters)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialProgramParameters = runConfiguration.getProgramParameters();
                        test.assertThrows(() -> runConfiguration.setProgramParameters(programParameters),
                            expected);
                        test.assertEqual(initialProgramParameters, runConfiguration.getProgramParameters());
                    });
                };

                setProgramParametersErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("programParameters cannot be null."));
                setProgramParametersErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("programParameters cannot be empty."));

                final Action2<XMLElement,String> setProgramParametersTest = (XMLElement xml, String programParameters) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(programParameters)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setProgramParametersResult = runConfiguration.setProgramParameters(programParameters);
                        test.assertSame(runConfiguration, setProgramParametersResult);
                        test.assertEqual(programParameters, runConfiguration.getProgramParameters());
                    });
                };

                setProgramParametersTest.run(XMLElement.create("configuration"), "a");
                setProgramParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "PROGRAM_PARAMETERS").setAttribute("value", "a")),
                    "b");
            });

            runner.testGroup("getVmParameters()", () ->
            {
                final Action2<XMLElement,String> getVmParametersTest = (XMLElement xml, String expected) ->
                {
                    runner.test("with " + xml.toString(), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        test.assertEqual(expected, runConfiguration.getVmParameters());
                    });
                };

                getVmParametersTest.run(XMLElement.create("configuration"), null);
                getVmParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "VM_PARAMETERS").setAttribute("value", "")),
                    "");
                getVmParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "VM_PARAMETERS").setAttribute("value", "a")),
                    "a");
            });

            runner.testGroup("setVmParameters(String)", () ->
            {
                final Action3<XMLElement,String,Throwable> setVmParametersErrorTest = (XMLElement xml, String vmParameters, Throwable expected) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(vmParameters)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final String initialVmParameters = runConfiguration.getVmParameters();
                        test.assertThrows(() -> runConfiguration.setVmParameters(vmParameters),
                            expected);
                        test.assertEqual(initialVmParameters, runConfiguration.getVmParameters());
                    });
                };

                setVmParametersErrorTest.run(XMLElement.create("configuration"), null, new PreConditionFailure("vmParameters cannot be null."));
                setVmParametersErrorTest.run(XMLElement.create("configuration"), "", new PreConditionFailure("vmParameters cannot be empty."));

                final Action2<XMLElement,String> setVmParametersTest = (XMLElement xml, String vmParameters) ->
                {
                    runner.test("with " + English.andList(xml.toString(), Strings.escapeAndQuote(vmParameters)), (Test test) ->
                    {
                        final IntellijWorkspaceRunConfiguration runConfiguration = IntellijWorkspaceRunConfiguration.create(xml);
                        final IntellijWorkspaceRunConfiguration setVmParametersResult = runConfiguration.setVmParameters(vmParameters);
                        test.assertSame(runConfiguration, setVmParametersResult);
                        test.assertEqual(vmParameters, runConfiguration.getVmParameters());
                    });
                };

                setVmParametersTest.run(XMLElement.create("configuration"), "a");
                setVmParametersTest.run(
                    XMLElement.create("configuration")
                        .addChild(XMLElement.create("option").setAttribute("name", "VM_PARAMETERS").setAttribute("value", "a")),
                    "b");
            });
        });
    }
}
