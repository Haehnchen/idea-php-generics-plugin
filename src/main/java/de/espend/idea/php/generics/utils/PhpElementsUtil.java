package de.espend.idea.php.generics.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpElementsUtil {
    /**
     * Provide array key pattern. we need incomplete array key support, too.
     *
     * foo(['<caret>'])
     * foo(['<caret>' => 'foobar'])
     */
    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getParameterListArrayValuePattern() {
        return PlatformPatterns.psiElement()
            .withParent(PlatformPatterns.psiElement(StringLiteralExpression.class).withParent(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement().withElementType(PhpElementTypes.ARRAY_VALUE)
                        .withParent(PlatformPatterns.psiElement(ArrayCreationExpression.class)
                            .withParent(ParameterList.class)
                        ),

                    PlatformPatterns.psiElement().withElementType(PhpElementTypes.ARRAY_KEY)
                        .withParent(PlatformPatterns.psiElement(ArrayHashElement.class)
                            .withParent(PlatformPatterns.psiElement(ArrayCreationExpression.class)
                                .withParent(ParameterList.class)
                            )
                        )
                ))
            );
    }

    public static int getCurrentParameterIndex(@NotNull ParameterList parameterList, @NotNull PsiElement psiElement) {
        PsiElement[] parameters = parameterList.getParameters();

        int i;
        for(i = 0; i < parameters.length; i++) {
            if(parameters[i].equals(psiElement)) {
                return i;
            }
        }

        return -1;
    }
}
