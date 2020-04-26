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

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TemplateAnnotationTypeProvider implements PhpTypeProvider4 {
    private static final Key<CachedValue<Map<String, Collection<TemplateAnnotationUsage>>>> PHP_GENERICS_TEMPLATES = new Key<>("PHP_GENERICS_TEMPLATES");

    @Override
    public char getKey() {
        return '\u0197';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (psiElement instanceof FunctionReference) {
            String subject = ((FunctionReference) psiElement).getSignature();
            String parameters = StringUtils.join(PhpTypeProviderUtil.getReferenceSignatures((FunctionReference) psiElement), '\u0199');

            // done also by PhpStorm; is this suitable? reduce parameters maybe to limit to one on longer values?
            if (subject.length() <= 200 && parameters.length() <= 300) {
                return new PhpType().add("#" + this.getKey() + subject + '\u0198' + parameters);
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

        PhpType types = null;

        // split for "subject" and its "parameters"
        String[] subjectAndParameters = s.substring(2).split(String.valueOf('\u0198'));

        // PhpStorm split on multiple types too
        String[] signatures = subjectAndParameters[0].split("\\|");
        for (String signature : signatures) {
            for (PhpNamedElement phpNamedElement : PhpIndex.getInstance(project).getBySignature(signature)) {
                String fqn = phpNamedElement.getFQN();

                Map<String, Collection<TemplateAnnotationUsage>> map = getTemplateAnnotationUsagesMap(project);
                Collection<TemplateAnnotationUsage> templateAnnotationUsages = map.get(fqn);
                if (templateAnnotationUsages == null || templateAnnotationUsages.size() == 0) {
                    continue;
                }

                List<String> parameters = Arrays.asList(subjectAndParameters[1].split(String.valueOf('\u0199')));
                for (TemplateAnnotationUsage usage : templateAnnotationUsages) {
                    Integer parameterIndex = usage.getParameterIndex();
                    if (parameterIndex == null) {
                        continue;
                    }

                    String s1 = parameters.get(parameterIndex);
                    if (s1 != null) {
                        // init the return type for types
                        if (types == null) {
                            types = new PhpType();
                        }

                        types.add("#" + this.getKey() + s1);
                    }
                }
            }
        }
        return types;
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
}
