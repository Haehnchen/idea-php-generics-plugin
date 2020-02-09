package de.espend.idea.php.generics.tests;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class CompletionNavigationProviderTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("CompletionNavigationProvider.php"));
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/fixtures";
    }

    public void testThatArrayProvidesPsalmTypesCompletion() {
        assertCompletionContains("test.php",
            "<?php\n" +
                "(new Bar())->foobar(['<caret>'])",
            "bar", "foo", "foobar", "foobar2", "foo--__---2FOO2122"
        );
    }

    public void testThatArrayProvidesPsalmTypesNavigation() {
        assertNavigationMatch("test.php",
            "<?php\n" +
                "(new Bar())->foobar(['foo<caret>bar'])",
            PlatformPatterns.psiElement(PhpDocTag.class)
        );
    }
}
