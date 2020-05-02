package de.espend.idea.php.generics.type;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.generics.indexer.TemplateAnnotationIndex;
import de.espend.idea.php.generics.indexer.dict.TemplateAnnotationUsage;
import de.espend.idea.php.generics.utils.PhpTypeProviderUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TemplateAnnotationTypeProvider implements PhpTypeProvider4 {
    private static final Key<CachedValue<Map<String, Collection<TemplateAnnotationUsage>>>> PHP_GENERICS_TEMPLATES = new Key<>("PHP_GENERICS_TEMPLATES");

    /**
     * #M#C\Class\Foo.getMethod
     */
    private static final Pattern METHOD_CALL_SIGNATURE_MATCHER = Pattern.compile("#M#C([^.]+)\\.(.*)");

    /**
     * Separator for the parameter types
     */
    private static final char PARAMETER_SEPARATOR = '\u0199';

    @Override
    public char getKey() {
        return '\u0197';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (psiElement instanceof FunctionReference) {
            String subject = ((FunctionReference) psiElement).getSignature();
            String parameters = StringUtils.join(PhpTypeProviderUtil.getReferenceSignatures((FunctionReference) psiElement), PARAMETER_SEPARATOR);

            // done also by PhpStorm; is this suitable? reduce parameters maybe to limit to one on longer values?
            if (subject.length() <= 200 && parameters.length() <= 300) {
                return new PhpType().add("#" + this.getKey() + subject + '\u0198' + parameters);
            } else if(subject.length() <= 200) {
                // fallback on long parameter; to support at least some other features
                return new PhpType().add("#" + this.getKey() + subject + '\u0198');
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        if (!s.startsWith("#" + this.getKey())) {
            return null;
        }

        Collection<String> types = new HashSet<>();

        String[] subjectAndParameters = s.substring(2).split(String.valueOf('\u0198'));

        // "@template for parameters"
        // split for "subject" and its "parameters"
        // PhpStorm split on multiple types too
        String[] signatures = subjectAndParameters[0].split("\\|");
        for (String signature : signatures) {

            for (PhpNamedElement phpNamedElement : PhpIndex.getInstance(project).getBySignature(signature)) {
                String fqn = phpNamedElement.getFQN();

                Collection<TemplateAnnotationUsage> templateAnnotationUsages = getTemplateAnnotationUsagesMap(project, fqn);
                if (templateAnnotationUsages.size() == 0) {
                    continue;
                }

                if (subjectAndParameters.length >= 2) {
                    visitParameterTypes(types, subjectAndParameters[1], templateAnnotationUsages);
                }

                visitTemplateAnnotatedMethod(project, types, signature, phpNamedElement, templateAnnotationUsages);
            }
        }

        if (types.size() == 0) {
            return null;
        }

        PhpType phpType = new PhpType();
        types.forEach(phpType::add);
        return phpType;
    }

    @NotNull
    private static Collection<TemplateAnnotationUsage> getTemplateAnnotationUsagesMap(@NotNull Project project, @NotNull String fqn) {
        Map<String, Collection<TemplateAnnotationUsage>> map = getTemplateAnnotationUsagesMap(project);
        return map.getOrDefault(fqn, Collections.emptyList());
    }

    @NotNull
    private static Map<String, Collection<TemplateAnnotationUsage>> getTemplateAnnotationUsagesMap(@NotNull Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, PHP_GENERICS_TEMPLATES, () -> {
            Map<String, Collection<TemplateAnnotationUsage>> map = new HashMap<>();

            FileBasedIndex instance = FileBasedIndex.getInstance();
            GlobalSearchScope scope = PhpIndex.getInstance(project).getSearchScope();

            instance.processAllKeys(TemplateAnnotationIndex.KEY, (key) -> {
                map.putIfAbsent(key, new HashSet<>());
                map.get(key).addAll(instance.getValues(TemplateAnnotationIndex.KEY, key, scope));
                return true;
            }, project);

            return CachedValueProvider.Result.create(map, getModificationTracker(project));
        }, false);
    }

    @NotNull
    private static ModificationTracker getModificationTracker(@NotNull Project project) {
        return () -> FileBasedIndex.getInstance().getIndexModificationStamp(TemplateAnnotationIndex.KEY, project);
    }

    /**
     * Supports "@extends" and "@implements"
     *
     * - "@extends \Extended\Implementations\MyContainer<\Extended\Implementations\Foobar>"
     */
    private void visitTemplateAnnotatedMethod(@NotNull Project project, @NotNull Collection<String> types, @NotNull String signature, @NotNull PhpNamedElement phpNamedElement, @NotNull Collection<TemplateAnnotationUsage> usages) {
        if (!(phpNamedElement instanceof Method)) {
            return;
        }

        for (TemplateAnnotationUsage usage : usages) {
            if (usage.getType() == TemplateAnnotationUsage.Type.METHOD_TEMPLATE) {
                // it class does not implement the method we got into "parent" class; here we get the orgin class name
                Matcher matcher = METHOD_CALL_SIGNATURE_MATCHER.matcher(signature);
                if (!matcher.find()) {
                    continue;
                }

                // Find class "@extends" tag and the origin class
                String group = matcher.group(1);
                for (TemplateAnnotationUsage origin : getTemplateAnnotationUsagesMap(project, group)) {
                    if (origin.getType() == TemplateAnnotationUsage.Type.EXTENDS) {
                        String context = origin.getContext();
                        if (context != null) {
                            String[] split = context.split("::");
                            if (split.length > 1) {
                                types.add("#" + this.getKey() + "#K#C" + split[1] + ".class");
                            }
                        }
                    }
                }
            }
        }
    }

    private void visitParameterTypes(@NotNull Collection<String> types, @NotNull String subjectAndParameter, @NotNull Collection<TemplateAnnotationUsage> templateAnnotationUsages) {
        List<String> parameters = Arrays.asList(subjectAndParameter.split(String.valueOf(PARAMETER_SEPARATOR)));
        for (TemplateAnnotationUsage usage : templateAnnotationUsages) {
            Integer parameterIndex = usage.getParameterIndex();
            if (parameterIndex == null) {
                continue;
            }

            String s1 = parameters.get(parameterIndex);
            if (s1 != null) {
                types.add("#" + this.getKey() + s1);
            }
        }
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> set, int i, Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, expression);
        if(resolvedParameter == null) {
            return null;
        }

        return phpIndex.getAnyByFQN(resolvedParameter);
    }
}
