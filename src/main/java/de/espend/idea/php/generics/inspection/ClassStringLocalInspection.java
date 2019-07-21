package de.espend.idea.php.generics.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.generics.utils.GenericsUtil;
import org.jetbrains.annotations.NotNull;

/**
 * "@template T as Exception"
 * "@param T::class $type"
 * "@return T"
 */
public class ClassStringLocalInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if(element instanceof StringLiteralExpression || element instanceof ClassConstantReference) {
                    PsiElement parent = element.getParent();
                    if (parent instanceof ParameterList) {
                        invoke(element, holder);
                    }
                }

                super.visitElement(element);
            }
        };
    }

    private void invoke(@NotNull final PsiElement element, @NotNull ProblemsHolder holder) {
        String expectedParameterInstanceOf = GenericsUtil.getExpectedParameterInstanceOf(element);
        if (expectedParameterInstanceOf == null) {
            return;
        }

        String content = GenericsUtil.getStringValue(element);

        if (!(element instanceof ClassConstantReference)) {
            holder.registerProblem(
                element,
                String.format("expects class-string<%s>, parent type string(%s) provided", expectedParameterInstanceOf, content != null ? content : "n/a"),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING
            );

            return;
        }

        if (content == null) {
            return;
        }

        PhpClass givenClass = GenericsUtil.findClass(element.getProject(), content);
        if (givenClass == null) {
            return;
        }

        PhpClass expectedClass = GenericsUtil.findClass(element.getProject(), expectedParameterInstanceOf);
        if (expectedClass == null) {
            return;
        }

        if (GenericsUtil.isInstanceOf(givenClass, expectedClass)) {
            return;
        }

        holder.registerProblem(
            element,
            String.format("expects class-string<%s>, %s::class provided", expectedParameterInstanceOf, content),
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING
        );
    }
}
