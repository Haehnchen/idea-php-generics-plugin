package de.espend.idea.php.generics.tests.utils;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.generics.dict.ParameterArrayType;
import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;
import de.espend.idea.php.generics.utils.GenericsUtil;

import java.util.*;

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

    public void testThatParamArrayElementsAreExtracted() {
        PhpDocComment phpDocComment = PhpPsiElementFactory.createPhpPsiFromText(getProject(), PhpDocComment.class, "" +
            "/**\n" +
            "* @psalm-param array{foo: Foo, ?bar: int | string} $foobar\n" +
            "*/\n" +
            "function test($foobar) {}\n"
        );

        PhpDocTag[] tagElementsByName = phpDocComment.getTagElementsByName("@psalm-param");
        String tagValue = tagElementsByName[0].getTagValue();

        Collection<ParameterArrayType> vars = GenericsUtil.getParameterArrayTypes(tagValue, "foobar", tagElementsByName[0]);

        ParameterArrayType foo = Objects.requireNonNull(vars).stream().filter(parameterArrayType -> parameterArrayType.getKey().equalsIgnoreCase("foo")).findFirst().get();
        assertFalse(foo.isOptional());
        assertContainsElements(Collections.singletonList("Foo"), foo.getValues());

        ParameterArrayType foobar = Objects.requireNonNull(vars).stream().filter(parameterArrayType -> parameterArrayType.getKey().equalsIgnoreCase("bar")).findFirst().get();
        assertTrue(foobar.isOptional());
        assertContainsElements(Arrays.asList("string", "int"), foobar.getValues());
    }

    public void testThatReturnElementsAreExtracted() {
        Function function = PhpPsiElementFactory.createPhpPsiFromText(getProject(), Function.class, "" +
            "/**\n" +
            "* @psalm-return array{foo: Foo, ?bar: int | string}\n" +
            "* @return array{foo2: Foo, ?bar2: int | string}\n" +
            "*/" +
            "function test() {}\n"
        );

        Collection<ParameterArrayType> vars = GenericsUtil.getReturnArrayTypes(function);

        ParameterArrayType bar = Objects.requireNonNull(vars).stream().filter(parameterArrayType -> parameterArrayType.getKey().equalsIgnoreCase("bar")).findFirst().get();
        assertTrue(bar.isOptional());
        assertContainsElements(Arrays.asList("string", "int"), bar.getValues());

        ParameterArrayType bar2 = Objects.requireNonNull(vars).stream().filter(parameterArrayType -> parameterArrayType.getKey().equalsIgnoreCase("bar2")).findFirst().get();
        assertTrue(bar2.isOptional());
        assertContainsElements(Arrays.asList("string", "int"), bar2.getValues());
    }
}
