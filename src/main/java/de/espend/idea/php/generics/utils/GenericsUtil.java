package de.espend.idea.php.generics.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericsUtil {
    public static boolean isGenericsClass(@NotNull PhpClass phpClass) {
        PhpDocComment phpDocComment = phpClass.getDocComment();
        if(phpDocComment != null) {
            // "@template T"
            // "@psalm-template Foo"
            for (String docBlock : Arrays.asList("@template", "@psalm-template")) {
                for (PhpDocTag phpDocTag : phpDocComment.getTagElementsByName(docBlock)) {
                    String tagValue = phpDocTag.getTagValue();
                    if (StringUtils.isNotBlank(tagValue) && tagValue.matches("\\w+")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    public static String getExpectedParameterInstanceOf(@NotNull PsiElement psiElement) {
        PsiElement parameterList = psiElement.getParent();
        if (!(parameterList instanceof ParameterList)) {
            return null;
        }

        PsiElement functionReference = parameterList.getParent();
        if (!(functionReference instanceof FunctionReference)) {
            return null;
        }

        Integer currentParameterIndex = getCurrentParameterIndex(psiElement);
        if (currentParameterIndex == null) {
            return null;
        }

        PsiElement resolve = ((FunctionReference) functionReference).resolve();
        if (!(resolve instanceof Function)) {
            return null;
        }

        Parameter[] parameters = ((Function) resolve).getParameters();
        if (parameters.length <= currentParameterIndex) {
            return null;
        }

        PhpDocComment docComment = ((Function) resolve).getDocComment();
        if (docComment == null) {
            return null;
        }

        Map<String, String> asInstances = new HashMap<>();

        Collection<PhpDocTag> templates = new HashSet<>();
        templates.addAll(Arrays.asList(docComment.getTagElementsByName("@template")));
        templates.addAll(Arrays.asList(docComment.getTagElementsByName("@psalm-template")));

        // workarounds for inconsistently psi structure
        // https://youtrack.jetbrains.com/issue/WI-47644
        for (PhpDocTag template : templates) {
            Matcher matcher = Pattern.compile("([\\w_-]+)\\s+as\\s+([\\w_\\\\-]+)", Pattern.MULTILINE).matcher(template.getText());
            if (!matcher.find()) {
                continue;
            }

            asInstances.put(matcher.group(1), matcher.group(2));
        }

        Collection<PhpDocTag> phpDocParamTags = new HashSet<>();
        phpDocParamTags.addAll(Arrays.asList(docComment.getTagElementsByName("@param")));
        phpDocParamTags.addAll(Arrays.asList(docComment.getTagElementsByName("@psalm-param")));

        String instance = null;
        for (PhpDocTag phpDocParamTag : phpDocParamTags) {
            String tagText = phpDocParamTag.getText();
            if (!tagText.contains("$" + parameters[currentParameterIndex].getName())) {
                continue;
            }

            Matcher matcher = Pattern.compile("\\s*([\\w_-]+)::class\\s*", Pattern.MULTILINE).matcher(tagText);
            if (!matcher.find()) {
                continue;
            }

            String group = matcher.group(1);
            if (!asInstances.containsKey(group)) {
                continue;
            }

            instance = asInstances.get(group);
            break;
        }

        if (instance == null) {
            return null;
        }

        Map<String, String> useImportMap = getUseImportMap(docComment);
        if (useImportMap.containsKey(instance)) {
            return StringUtils.stripStart(useImportMap.get(instance), "\\");
        }

        return instance;
    }

    @Nullable
    private static Integer getCurrentParameterIndex(PsiElement parameter) {
        PsiElement parameterList = parameter.getContext();
        if(!(parameterList instanceof ParameterList)) {
            return null;
        }

        PsiElement[] parameters = ((ParameterList) parameterList).getParameters();

        int i;
        for(i = 0; i < parameters.length; i = i + 1) {
            if(parameters[i].equals(parameter)) {
                return i;
            }
        }

        return null;
    }

    /*
     * Collect file use imports and resolve alias with their class name
     *
     * @param PhpDocComment current doc scope
     * @return map with class names as key and fqn on value
     */
    @NotNull
    private static Map<String, String> getUseImportMap(@Nullable PhpDocComment phpDocComment) {
        if(phpDocComment == null) {
            return Collections.emptyMap();
        }

        PhpPsiElement scope = PhpCodeInsightUtil.findScopeForUseOperator(phpDocComment);
        if(scope == null) {
            return Collections.emptyMap();
        }

        Map<String, String> useImports = new HashMap<>();

        for (PhpUseList phpUseList : PhpCodeInsightUtil.collectImports(scope)) {
            for(PhpUse phpUse : phpUseList.getDeclarations()) {
                String alias = phpUse.getAliasName();
                if (alias != null) {
                    useImports.put(alias, phpUse.getFQN());
                } else {
                    useImports.put(phpUse.getName(), phpUse.getFQN());
                }
            }
        }

        return useImports;
    }

    /**
     * Resolve string definition in a recursive way
     *
     * $foo = Foo::class
     * $this->foo = Foo::class
     * $this->foo1 = $this->foo
     */
    @Nullable
    public static String getStringValue(@Nullable PsiElement psiElement) {
        return getStringValue(psiElement, 0);
    }

    @Nullable
    private static String getStringValue(@Nullable PsiElement psiElement, int depth) {
        if(psiElement == null || ++depth > 5) {
            return null;
        }

        if(psiElement instanceof StringLiteralExpression) {
            String resolvedString = ((StringLiteralExpression) psiElement).getContents();
            if(StringUtils.isEmpty(resolvedString)) {
                return null;
            }

            return resolvedString;
        } else if(psiElement instanceof Field) {
            return getStringValue(((Field) psiElement).getDefaultValue(), depth);
        } else if(psiElement instanceof ClassConstantReference && "class".equals(((ClassConstantReference) psiElement).getName())) {
            // Foobar::class
            return getClassConstantPhpFqn((ClassConstantReference) psiElement);
        } else if(psiElement instanceof PhpReference) {
            PsiReference psiReference = psiElement.getReference();
            if(psiReference == null) {
                return null;
            }

            PsiElement ref = psiReference.resolve();
            if(ref instanceof PhpReference) {
                return getStringValue(psiElement, depth);
            }

            if(ref instanceof Field) {
                return getStringValue(((Field) ref).getDefaultValue());
            }
        }

        return null;
    }


    /**
     * Foo::class to its class fqn include namespace
     */
    public static String getClassConstantPhpFqn(@NotNull ClassConstantReference classConstant) {
        PhpExpression classReference = classConstant.getClassReference();
        if(!(classReference instanceof PhpReference)) {
            return null;
        }

        String typeName = ((PhpReference) classReference).getFQN();
        return StringUtils.isNotBlank(typeName) ? StringUtils.stripStart(typeName, "\\") : null;
    }


    /**
     * @param subjectClass eg DateTime
     * @param expectedClass eg DateTimeInterface
     */
    public static boolean isInstanceOf(@NotNull PhpClass subjectClass, @NotNull PhpClass expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), PhpIndex.getInstance(subjectClass.getProject()));
    }

    /**
     * @param subjectClass eg DateTime
     * @param expectedClass eg DateTimeInterface
     */
    public static boolean isInstanceOf(@NotNull PhpClass subjectClass, @NotNull String expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), PhpIndex.getInstance(subjectClass.getProject()));
    }

    /**
     * @param subjectClass eg DateTime
     * @param expectedClass eg DateTimeInterface
     */
    public static boolean isInstanceOf(@NotNull Project project, @NotNull String subjectClass, @NotNull String expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), PhpIndex.getInstance(project));
    }

    @Nullable
    static public PhpClass findClass(Project project, @NotNull String className) {
        Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getAnyByFQN(className);
        return phpClasses.size() == 0 ? null : phpClasses.iterator().next();
    }
}
