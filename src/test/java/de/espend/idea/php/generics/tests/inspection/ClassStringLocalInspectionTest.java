package de.espend.idea.php.generics.tests.inspection;

import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;

public class ClassStringLocalInspectionTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("fixtures.php"));
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/inspection/fixtures";
    }

    public void testThatClassStringParameterProvideInspections() {
        assertLocalInspectionContains("test.php", "<?php\n" +
                "C::a('t<caret>est')",
            "expects class-string<Exception>, parent type string(test) provided"
        );

        assertLocalInspectionContains("test.php", "<?php\n" +
                "C::a(\\Foobar\\Foo::cl<caret>ass)",
            "expects class-string<Exception>, Foobar\\Foo::class provided"
        );
    }
}
