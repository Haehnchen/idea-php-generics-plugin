package de.espend.idea.php.generics.tests.utils;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;
import de.espend.idea.php.generics.utils.GenericsUtil;

public class GenericsUtilTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("fixtures.php"));
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/utils/fixtures";
    }

    public void testThatTypeTemplateIsExtracted() {
        MethodReference methodReference = PhpPsiElementFactory.createMethodReference(getProject(), "C::a('foo', 'foobar')");

        PsiElement[] parameters = methodReference.getParameterList().getParameters();

        assertEquals("Exception", GenericsUtil.getExpectedParameterInstanceOf(parameters[0]));
        assertNull(GenericsUtil.getExpectedParameterInstanceOf(parameters[1]));
    }

    public void testThatTypeTemplateIsExtractedForImports() {
        MethodReference methodReference = PhpPsiElementFactory.createMethodReference(getProject(), "C::b('foo', 'foobar')");

        PsiElement[] parameters = methodReference.getParameterList().getParameters();

        assertEquals("Foobar\\Foo", GenericsUtil.getExpectedParameterInstanceOf(parameters[0]));
        assertEquals("Foobar\\Foo", GenericsUtil.getExpectedParameterInstanceOf(parameters[1]));
    }
}
