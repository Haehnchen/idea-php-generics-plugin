package de.espend.idea.php.generics.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.generics.tests.AnnotationLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TemplateAnnotationTypeProviderTest extends AnnotationLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/generics/tests/type/fixtures";
    }

    public void testTypesForFunctionWithClassString() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n instantiator(\\Foobar\\Foobar::class)->get<caret>Foo();\n",
            PlatformPatterns.psiElement(Method.class).withName("getFoo")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n instantiator2('', \\Foobar\\Foobar::class, '')->get<caret>Foo();\n",
            PlatformPatterns.psiElement(Method.class).withName("getFoo")
        );

        assertPhpReferenceNotResolveTo(PhpFileType.INSTANCE,
            "<?php\n instantiator2('', '', \\Foobar\\Foobar::class)->get<caret>Foo();\n",
            PlatformPatterns.psiElement(Method.class).withName("getFoo")
        );
    }

    public void testTypesForMethodWithClassString() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php\n (new \\Instantiator\\Foobar\\Foobar())->_barInstantiator(\\Foobar\\Foobar::class)->get<caret>Foo();\n",
            PlatformPatterns.psiElement(Method.class).withName("getFoo")
        );
    }
}
