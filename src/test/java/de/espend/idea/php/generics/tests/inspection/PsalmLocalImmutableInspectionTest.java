package de.espend.idea.php.generics.tests.inspection;

import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;

/*
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmLocalImmutableInspectionTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("PsalmLocalImmutableInspection.php"));
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/inspection/fixtures";
    }

    public void testThatPsalmReadOnlyAssignmentExpressionIsMarked() {
        assertLocalInspectionContains(
            "test.php",
            "<?php\n(new PsalmReadOnly())->read<caret>Only = 'test'",
            "[psalm] property marked as readonly"
        );

        assertLocalInspectionIsEmpty(
            "test.php",
            "<?php\n(new PsalmReadOnly())->wr<caret>ite = 'test'"
        );

        assertLocalInspectionIsEmpty(
            "test.php",
            "<?php\n" +
                "new class extends PsalmReadOnly {\n" +
                "    public function __construct()\n" +
                "    {\n" +
                "        $this->read<caret>Only = 'test';\n" +
                "    }\n" +
                "\n" +
                "};"
        );
    }

    public void testThatPsalmImmutableAssignmentExpressionIsMarked() {
        assertLocalInspectionContains(
            "test.php",
            "<?php\n(new PsalmImmutable())->read<caret>Only = 'test'",
            "[psalm] property marked as readonly"
        );

        assertLocalInspectionContains(
            "test.php",
            "<?php\n(new PsalmImmutable())->wri<caret>te = 'test'",
            "[psalm] property marked as readonly"
        );
    }
}
