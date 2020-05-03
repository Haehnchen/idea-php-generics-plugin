package de.espend.idea.php.generics.tests.indexer;

import de.espend.idea.php.generics.indexer.TemplateAnnotationIndex;
import de.espend.idea.php.generics.indexer.dict.TemplateAnnotationUsage;
import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see TemplateAnnotationIndex
 */
public class TemplateAnnotationIndexTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/indexer/fixtures";
    }

    public void testThatTemplateClassIsInIndex() {
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Foo\\Map.get");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Foo\\PsalmMap.get");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Foo\\Zzz.get");
        assertIndexNotContains(TemplateAnnotationIndex.KEY, "\\Foo\\Bar");

        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Instantiator\\Foobar\\Foobar._barInstantiator");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiator");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorParam");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorReturn");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorPhpStan");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorPhpStanAsObject");

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Instantiator\\Foobar\\Foobar._barInstantiator",
            value -> value.getFqn().equals("\\Instantiator\\Foobar\\Foobar._barInstantiator") && value.getParameterIndex() == 0 && value.getType() == TemplateAnnotationUsage.Type.FUNCTION_CLASS_STRING
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\instantiator",
            value -> value.getFqn().equals("\\instantiator") && value.getParameterIndex() == 0 && value.getType() == TemplateAnnotationUsage.Type.FUNCTION_CLASS_STRING
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\instantiatorPhpStan",
            value -> value.getFqn().equals("\\instantiatorPhpStan") && value.getParameterIndex() == 0 && value.getType() == TemplateAnnotationUsage.Type.FUNCTION_CLASS_STRING
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\instantiatorPhpStanAsObject",
            value -> value.getFqn().equals("\\instantiatorPhpStanAsObject") && value.getParameterIndex() == 1 && value.getType() == TemplateAnnotationUsage.Type.FUNCTION_CLASS_STRING
        );
    }

    public void testThatTemplateMethodWithConstructorTemplateIfInIndex() {
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Template\\MyTemplateImpl.getValue");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Template\\MyTemplateImpl.getValueReturn");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Template\\MyTemplateObject.getValue");

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Template\\MyTemplateImpl.getValue",
            value -> value.getType() == TemplateAnnotationUsage.Type.METHOD_TEMPLATE && "T".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Template\\MyTemplateImpl.getValueReturn",
            value -> value.getType() == TemplateAnnotationUsage.Type.METHOD_TEMPLATE && "T".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Template\\MyTemplateObject.getValue",
            value -> value.getType() == TemplateAnnotationUsage.Type.METHOD_TEMPLATE && "T".equals(value.getContext())
        );
    }

    public void testThatTemplateExtendsClassIsInIndex() {
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Extended\\Classes\\MyExtendsImpl");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Extended\\Classes\\MyExtendsImplPsalm");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Extended\\Classes\\MyExtendsImplPhpStan");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Extended\\Classes\\MyExtendsImplUse");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Extended\\Classes\\MyExtendsImplUseAlias");

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Extended\\Classes\\MyExtendsImpl",
            value -> value.getType() == TemplateAnnotationUsage.Type.EXTENDS && "\\App\\Foo\\Bar\\MyContainer::\\DateTime".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Extended\\Classes\\MyExtendsImplPsalm",
            value -> value.getType() == TemplateAnnotationUsage.Type.EXTENDS && "\\App\\Foo\\Bar\\MyContainer::\\Extended\\Classes\\MyExtendsImplPalm".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Extended\\Classes\\MyExtendsImplPhpStan",
            value -> value.getType() == TemplateAnnotationUsage.Type.EXTENDS && "\\App\\Foo\\Bar\\MyContainer::\\DateTime".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Extended\\Classes\\MyExtendsImplUse",
            value -> value.getType() == TemplateAnnotationUsage.Type.EXTENDS && "\\App\\Foo\\Bar\\MyContainer::\\Extend\\Types\\Foobar".equals(value.getContext())
        );

        assertIndexContainsKeyWithValue(
            TemplateAnnotationIndex.KEY,
            "\\Extended\\Classes\\MyExtendsImplUseAlias",
            value -> value.getType() == TemplateAnnotationUsage.Type.EXTENDS && "\\App\\Foo\\Bar\\MyContainer::\\Extend\\Types\\Foobar".equals(value.getContext())
        );
    }
}
