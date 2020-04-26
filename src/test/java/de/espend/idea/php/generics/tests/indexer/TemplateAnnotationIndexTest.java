package de.espend.idea.php.generics.tests.indexer;

import de.espend.idea.php.generics.indexer.TemplateAnnotationIndex;
import de.espend.idea.php.generics.indexer.dict.TemplateAnnotationUsage;
import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;

public class TemplateAnnotationIndexTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/indexer/fixtures";
    }

    public void testThatTemplateClassIsInIndex() {
        assertIndexContains(TemplateAnnotationIndex.KEY, "Foo\\Map");
        assertIndexContains(TemplateAnnotationIndex.KEY, "Foo\\PsalmMap");
        assertIndexContains(TemplateAnnotationIndex.KEY, "Foo\\Zzz");
        assertIndexNotContains(TemplateAnnotationIndex.KEY, "Foo\\Bar");

        assertIndexContains(TemplateAnnotationIndex.KEY, "\\Instantiator\\Foobar\\Foobar._barInstantiator");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiator");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorParam");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorReturn");
        assertIndexContains(TemplateAnnotationIndex.KEY, "\\instantiatorPhpStan");

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
    }
}
