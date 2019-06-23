package de.espend.idea.php.generics.tests.indexer;

import de.espend.idea.php.generics.indexer.TemplateAnnotationIndex;
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
    }
}
