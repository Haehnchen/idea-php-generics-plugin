package de.espend.idea.php.generics;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.generics.dict.ParameterArrayType;
import de.espend.idea.php.generics.utils.GenericsUtil;
import de.espend.idea.php.generics.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class CompletionNavigationProvider {
    public static class Completion extends CompletionContributor {
        public Completion() {
            extend(CompletionType.BASIC, PhpElementsUtil.getParameterListArrayValuePattern(), new ArrayParameterCompletionProvider());
        }
    }

    public static class GotoDeclaration implements GotoDeclarationHandler {
        @Nullable
        @Override
        public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
            if (sourceElement == null) {
                return new PsiElement[0];
            }

            Collection<PsiElement> psiElements = new HashSet<>();

            PsiElement parent = sourceElement.getParent();
            if (parent instanceof StringLiteralExpression && PhpElementsUtil.getParameterListArrayValuePattern().accepts(sourceElement)) {
                psiElements.addAll(collectArrayKeyParameterTargets(parent));
            }

            return psiElements.toArray(new PsiElement[0]);
        }

        private Collection<PsiElement> collectArrayKeyParameterTargets(PsiElement psiElement) {
            Collection<PsiElement> psiElements = new HashSet<>();

            ArrayCreationExpression parentOfType = PsiTreeUtil.getParentOfType(psiElement, ArrayCreationExpression.class);

            if (parentOfType != null) {
                String contents = ((StringLiteralExpression) psiElement).getContents();

                psiElements.addAll(GenericsUtil.getTypesForParameter(parentOfType).stream()
                    .filter(parameterArrayType -> parameterArrayType.getKey().equalsIgnoreCase(contents))
                    .map(ParameterArrayType::getContext).collect(Collectors.toSet())
                );
            }

            return psiElements;
        }
    }

    /**
     * foo(['<caret>'])
     * foo(['<caret>' => 'foobar'])
     */
    private static class ArrayParameterCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();

            ArrayCreationExpression parentOfType = PsiTreeUtil.getParentOfType(position, ArrayCreationExpression.class);
            if (parentOfType != null) {
                result.addAllElements(GenericsUtil.getTypesForParameter(parentOfType).stream()
                    .map(CompletionNavigationProvider::createParameterArrayTypeLookupElement)
                    .collect(Collectors.toSet())
                );
            }
        }
    }

    @NotNull
    private static LookupElement createParameterArrayTypeLookupElement(@NotNull ParameterArrayType type) {
        LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(type.getKey())
            .withIcon(PhpIcons.FIELD);

        String types = StringUtils.join(type.getValues(), '|');
        if (type.isOptional()) {
            types = "(optional) " + types;
        }

        return lookupElementBuilder.withTypeText(types);
    }
}
