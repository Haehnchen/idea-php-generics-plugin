package de.espend.idea.php.generics.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

/**
 * Check property assign "$this->a = 'test'" of a readonly property
 *
 * Supported tags:
 *  - "@psalm-readonly"
 *  - "@psalm-immutable"
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmLocalImmutableInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                // $this->a = 'foobar';
                if (element instanceof FieldReference && element.getParent() instanceof AssignmentExpression) {
                    Function function = PsiTreeUtil.getParentOfType(element, Function.class);

                    // not invalid in constructors
                    if (this.isInvalidWriteScope((FieldReference) element)) {
                        PsiElement resolve = ((FieldReference) element).resolve();

                        // find fields reference with a psalm tag
                        boolean isReadOnly = false;

                        if (resolve instanceof Field) {
                            // search for "@psalm-readonly" on property level
                            PhpDocComment docComment = ((Field) resolve).getDocComment();
                            if (docComment != null) {
                                PhpDocTag[] tagElementsByName = docComment.getTagElementsByName("@psalm-readonly");
                                isReadOnly = tagElementsByName.length > 0;
                            }

                            // search for "@psalm-immutable" on class level if not already given on property level
                            if (!isReadOnly) {
                                PhpClass containingClass = ((Field) resolve).getContainingClass();
                                if (containingClass != null) {
                                    PhpDocComment phpDocComment = containingClass.getDocComment();
                                    if (phpDocComment != null) {
                                        PhpDocTag[] tagElementsByName = phpDocComment.getTagElementsByName("@psalm-immutable");
                                        isReadOnly = tagElementsByName.length > 0;
                                    }
                                }
                            }

                            if (isReadOnly) {
                                holder.registerProblem(
                                    element,
                                    "[psalm] property marked as readonly",
                                    ProblemHighlightType.GENERIC_ERROR
                                );
                            }
                        }
                    }
                }

                super.visitElement(element);
            }

            /**
             * Check if value write is disallowed to property eg. inside a class constructor
             */
            private boolean isInvalidWriteScope(@NotNull FieldReference fieldReference) {
                // @TODO: this should maybe optimized eg hint: anonymous classes
                Function function = PsiTreeUtil.getParentOfType(fieldReference, Function.class);
                return function == null || !"__construct".equalsIgnoreCase(function.getName());
            }
        };
    }
}
