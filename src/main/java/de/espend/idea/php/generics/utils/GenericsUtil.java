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
import de.espend.idea.php.generics.dict.ParameterArrayType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericsUtil {
    public static boolean isGenericsClass(@NotNull PhpClass phpClass) {
        PhpDocComment phpDocComment = phpClass.getDocComment();
        if(phpDocComment != null) {
            // "@template T"
            // "@psalm-template Foo"
            for (PhpDocTag phpDocTag : getTagElementsByNameForAllFrameworks(phpDocComment, "template")) {
                String tagValue = phpDocTag.getTagValue();
                if (StringUtils.isNotBlank(tagValue) && tagValue.matches("\\w+")) {
                    return true;
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

        // workarounds for inconsistently psi structure
        // https://youtrack.jetbrains.com/issue/WI-47644
        for (PhpDocTag template : getTagElementsByNameForAllFrameworks(docComment, "template")) {
            Matcher matcher = Pattern.compile("([\\w_-]+)\\s+as\\s+([\\w_\\\\-]+)", Pattern.MULTILINE).matcher(template.getText());
            if (!matcher.find()) {
                continue;
            }

            asInstances.put(matcher.group(1), matcher.group(2));
        }

        String instance = null;
        for (PhpDocTag phpDocParamTag : getTagElementsByNameForAllFrameworks(docComment, "param")) {
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

    /**
     * - "@return array{optional?: string, bar: int}"
     * - "@return array{foo: string, bar: int}"
     * - "@psalm-param array{foo: string, bar: int}"
     */
    @NotNull
    public static Collection<ParameterArrayType> getReturnArrayTypes(@NotNull PhpNamedElement phpNamedElement) {
        PhpDocComment docComment = phpNamedElement.getDocComment();
        if (docComment == null) {
            return Collections.emptyList();
        }

        Collection<ParameterArrayType> types = new ArrayList<>();

        // workaround for invalid tags lexer on PhpStorm side
        for (PhpDocTag phpDocTag : getTagElementsByNameForAllFrameworks(docComment, "return")) {
            String text = phpDocTag.getText();
            Matcher arrayElementsMatcher = Pattern.compile("array\\s*\\{(.*)}\\s*", Pattern.MULTILINE).matcher(text);
            if (arrayElementsMatcher.find()) {
                String group = arrayElementsMatcher.group(1);
                types.addAll(GenericsUtil.getParameterArrayTypes(group, phpDocTag));
            }
        }

        return types;
    }

    /**
     * - "@return array{optional?: string, bar: int}"
     * - "@return array{foo: string, bar: int}"
     * - "@return array{foo: string, bar: int}"
     * - "@psalm-param array{foo: Foo, ?bar: int}"
     * - "@param array{foo: Foo, ?bar: int} $options"
     */
    @NotNull
    public static Collection<ParameterArrayType> getParameterArrayTypes(@NotNull String content, @NotNull String parameter, @NotNull PsiElement context) {
        Matcher parameterNameMatcher = Pattern.compile(".*\\$([\\w_-]+)\\s*$", Pattern.MULTILINE).matcher(content);
        if (!parameterNameMatcher.find()) {
            return Collections.emptyList();
        }

        String group = parameterNameMatcher.group(1);
        if (!parameter.equalsIgnoreCase(group)) {
            return Collections.emptyList();
        }

        // array{foo: string, bar: int}
        Matcher arrayElementsMatcher = Pattern.compile("array\\s*\\{(.*)}\\s*", Pattern.MULTILINE).matcher(content);
        if (!arrayElementsMatcher.find()) {
            return Collections.emptyList();
        }

        return getParameterArrayTypes(arrayElementsMatcher.group(1), context);
    }

    @NotNull
    private static Collection<ParameterArrayType> getParameterArrayTypes(@NotNull PhpDocComment phpDocComment, @NotNull String parameterName) {
        Collection<ParameterArrayType> vars = new ArrayList<>();

        for (PhpDocTag phpDocTag : getTagElementsByNameForAllFrameworks(phpDocComment, "param")) {
            String tagValue = phpDocTag.getTagValue();
            vars.addAll(GenericsUtil.getParameterArrayTypes(tagValue, parameterName, phpDocTag));
        }

        // we need a workaround for "@param" as the lexer strips it all of after "array{"
        for (PhpDocTag phpDocTag : phpDocComment.getTagElementsByName("@param")) {
            // {foobar2: string} $foobar
            String tagValue = phpDocTag.getTagValue();

            // extract the parameter name $foobar
            Matcher parameterNameMatcher = Pattern.compile(".*\\$([\\w_-]+)\\s*$", Pattern.MULTILINE).matcher(tagValue);
            if (!parameterNameMatcher.find()) {
                continue;
            }

            // @param array{foobar2: string}
            String text = phpDocTag.getText();

            // try to build a valid string; make in as error prone safe as possible; we need provide as on "@psalm-param":
            // array{foobar2: string} $foobar
            String content = text.replaceAll("\\s*@param\\s*", "") + " $" + parameterNameMatcher.group(1);

            vars.addAll(GenericsUtil.getParameterArrayTypes(content, parameterName, phpDocTag));
        }

        return vars;
    }

    /**
     * - "@return array{optional?: string, bar: int}"
     * - "@return array{foo: string, bar: int}"
     * - "@return array{foo: string, bar: int}"
     * - "@psalm-param array{foo: Foo, ?bar: int}"
     * - "@param array{foo: Foo, ?bar: int} $options"
     */
    @NotNull
    private static Collection<ParameterArrayType> getParameterArrayTypes(@NotNull String array, @NotNull PsiElement context) {
        Collection<ParameterArrayType> parameters = new ArrayList<>();

        for (String s : array.split(",")) {
            String trim = StringUtils.trim(s);
            String[] split = trim.split(":");

            if(split.length != 2) {
                continue;
            }

            // @TODO: class resolve
            Set<String> types = Arrays.stream(split[1].split("\\|"))
                .map(StringUtils::trim)
                .collect(Collectors.toSet());

            boolean isOptional = split[0].startsWith("?") || split[0].endsWith("?");

            parameters.add(new ParameterArrayType(
                isOptional ? StringUtils.strip(split[0], "?") : split[0],
                types,
                isOptional,
                context
            ));
        }

        return parameters;
    }

    /**
     * Resolve the given parameter to find possible psalm docs recursively
     *
     * $foo->foo([])
     *
     * TODO: method search in recursion
     */
    @NotNull
    public static Collection<ParameterArrayType> getTypesForParameter(@NotNull PsiElement psiElement) {
        PsiElement parent = psiElement.getParent();

        if (parent instanceof ParameterList) {
            PsiElement functionReference = parent.getParent();
            if (functionReference instanceof FunctionReference) {
                PsiElement resolve = ((FunctionReference) functionReference).resolve();

                if (resolve instanceof Function) {
                    Parameter[] functionParameters = ((Function) resolve).getParameters();

                    int currentParameterIndex = PhpElementsUtil.getCurrentParameterIndex((ParameterList) parent, psiElement);
                    if (currentParameterIndex >= 0 && functionParameters.length - 1 >= currentParameterIndex) {
                        String name = functionParameters[currentParameterIndex].getName();
                        PhpDocComment docComment = ((Function) resolve).getDocComment();

                        if (docComment != null) {
                            return GenericsUtil.getParameterArrayTypes(docComment, name);
                        }
                    }
                }
            }
        }

        return Collections.emptyList();
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

    @NotNull
    public static PhpDocTag[] getTagElementsByNameForAllFrameworks(@NotNull PhpDocComment phpDocComment, @NotNull String parameterName) {
        return Stream.of(
            phpDocComment.getTagElementsByName("@psalm-" + parameterName),
            phpDocComment.getTagElementsByName("@" + parameterName),
            phpDocComment.getTagElementsByName("@phpstan-" + parameterName)
        ).flatMap(Stream::of).toArray(PhpDocTag[]::new);
    }
}
