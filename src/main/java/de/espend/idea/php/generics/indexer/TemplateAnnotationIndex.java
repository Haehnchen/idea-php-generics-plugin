package de.espend.idea.php.generics.indexer;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.annotation.util.AnnotationUtil;
import de.espend.idea.php.generics.indexer.dict.TemplateAnnotationUsage;
import de.espend.idea.php.generics.indexer.externalizer.ObjectStreamDataExternalizer;
import de.espend.idea.php.generics.utils.GenericsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TemplateAnnotationIndex extends FileBasedIndexExtension<String, TemplateAnnotationUsage> {
    public static final ID<String, TemplateAnnotationUsage> KEY = ID.create("de.espend.idea.php.generics.templates");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();
    private static ObjectStreamDataExternalizer<TemplateAnnotationUsage> EXTERNALIZER = new ObjectStreamDataExternalizer<>();

    @NotNull
    @Override
    public ID<String, TemplateAnnotationUsage> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, TemplateAnnotationUsage, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, TemplateAnnotationUsage> map = new HashMap<>();

            PsiFile psiFile = inputData.getPsiFile();
            if (!(psiFile instanceof PhpFile)) {
                return map;
            }

            if (!AnnotationUtil.isValidForIndex(inputData)) {
                return map;
            }

            psiFile.accept(new MyPsiRecursiveElementWalkingVisitor(map));

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<TemplateAnnotationUsage> getValueExternalizer() {
        return EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 3;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return virtualFile -> virtualFile.getFileType() == PhpFileType.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    private static class MyPsiRecursiveElementWalkingVisitor extends PsiRecursiveElementWalkingVisitor {

        /**
         * Matches: "\App\Foo\Bar\MyContainer<\DateTime>"
         */
        private static final Pattern CLASS_EXTENDS_MATCHER = Pattern.compile("\\s*([^<]+)\\s*<\\s*([^>]+)\\s*>");

        private final Map<String, TemplateAnnotationUsage> map;

        private MyPsiRecursiveElementWalkingVisitor(Map<String, TemplateAnnotationUsage> map) {
            this.map = map;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {
            if (element instanceof PhpClass) {
                visitPhpClass((PhpClass) element);
            } else if (element instanceof Function) {
                visitPhpFunctionOrMethod((Function) element);
            }

            super.visitElement(element);
        }

        private void visitPhpClass(@NotNull PhpClass phpClass) {
            String fqn = phpClass.getFQN();
            if(!fqn.startsWith("\\")) {
                fqn = "\\" + fqn;
            }

            PhpDocComment phpDocComment = phpClass.getDocComment();
            if (phpDocComment != null) {
                for (PhpDocTag phpDocTag : GenericsUtil.getTagElementsByNameForAllFrameworks(phpDocComment, "extends")) {
                    String tagValue = phpDocTag.getTagValue();

                    Matcher matcher = CLASS_EXTENDS_MATCHER.matcher(tagValue);
                    if (!matcher.find()) {
                        continue;
                    }

                    String extendsClass = matcher.group(1);
                    String type = matcher.group(2);

                    if (!extendsClass.startsWith("\\")) {
                        extendsClass = StringUtils.substringBeforeLast(fqn, "\\") + "\\" + extendsClass;
                    }

                    if (!type.startsWith("\\")) {
                        type = StringUtils.substringBeforeLast(fqn, "\\") + "\\" + type;
                    }

                    // @TODO: implement class resolving based on use statement
                    map.put(fqn, new TemplateAnnotationUsage(fqn, TemplateAnnotationUsage.Type.EXTENDS, 0, extendsClass + "::" + type));
                }
            }

        }

        private void visitPhpFunctionOrMethod(@NotNull Function function) {
            PhpDocComment phpDocComment = function.getDocComment();
            if (phpDocComment == null) {
                return;
            }

            /*
             *
             */
            for (PhpDocTag phpDocTag : GenericsUtil.getTagElementsByNameForAllFrameworks(phpDocComment, "template")) {
                // @template T
                String templateName = extractTemplateName(phpDocTag);
                if (templateName == null) {
                    continue;
                }

                // return doctag must match: "@return T"
                if (!hasReturnTypeTemplate(phpDocComment, templateName)) {
                    continue;
                }

                // get possible tags
                for (PhpDocTag docTag : GenericsUtil.getTagElementsByNameForAllFrameworks(phpDocComment, "param")) {
                    String psalmParamTag = docTag.getTagValue();
                    Pattern pattern = Pattern.compile("class-string<" + Pattern.quote(templateName) + ">.*\\$([\\w-]+)");

                    Matcher matcher = pattern.matcher(psalmParamTag);
                    if (!matcher.find()) {
                        continue;
                    }

                    String parameterName = matcher.group(1);
                    Parameter[] parameters = function.getParameters();

                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        String name = parameter.getName();
                        if (name.equalsIgnoreCase(parameterName)) {
                            String fqn = function.getFQN();

                            map.put(fqn, new TemplateAnnotationUsage(fqn, TemplateAnnotationUsage.Type.FUNCTION_CLASS_STRING, i));
                            return;
                        }
                    }
                }
            }

            /*
             *
             */
            if (function instanceof Method) {
                for (String docTagValue : GenericsUtil.getReturnTypeTagValues(phpDocComment)) {
                    String templateName = extractTemplateName(docTagValue);
                    if (templateName == null) {
                        continue;
                    }

                    PhpClass containingClass = ((Method) function).getContainingClass();
                    if (containingClass == null) {
                        continue;
                    }

                    PhpDocComment docComment = containingClass.getDocComment();
                    if (docComment == null) {
                        continue;
                    }

                    for (PhpDocTag template : GenericsUtil.getTagElementsByNameForAllFrameworks(docComment, "template")) {
                        String templateNameClassLevel = extractTemplateName(template);
                        if (StringUtils.isBlank(templateNameClassLevel)) {
                            continue;
                        }

                        if (templateNameClassLevel.equals(templateName)) {
                            String fqn = function.getFQN();
                            map.put(fqn, new TemplateAnnotationUsage(fqn, TemplateAnnotationUsage.Type.METHOD_TEMPLATE, 0, templateName));
                        }
                    }
                }
            }
        }

        /**
         * Extract the "T"
         *
         * "T"
         * "T as object"
         */
        @Nullable
        private static String extractTemplateName(@NotNull PhpDocTag phpDocTag) {
            String templateTagValue = phpDocTag.getTagValue();
            if (StringUtils.isBlank(templateTagValue)) {
                return null;
            }

            return extractTemplateName(templateTagValue);
        }

        /**
         * Extract the "T"
         *
         * "T"
         * "T as object"
         */
        @Nullable
        private static String extractTemplateName(@NotNull String tagValue) {
            Matcher matcher = Pattern.compile("^([\\w]+)\\s*").matcher(tagValue);
            if (!matcher.find()) {
                return null;
            }

            return StringUtils.trim(matcher.group(1));
        }

        /**
         * For for the given template name as a return value
         *
         * "@return T"
         * "@psalm-return T"
         * "@return T as object"
         */
        private boolean hasReturnTypeTemplate(@NotNull PhpDocComment phpDocComment, @NotNull String templateName) {
            return GenericsUtil.getReturnTypeTagValues(phpDocComment)
                .stream()
                .map(MyPsiRecursiveElementWalkingVisitor::extractTemplateName)
                .anyMatch(templateName::equals);
        }
    }
}
