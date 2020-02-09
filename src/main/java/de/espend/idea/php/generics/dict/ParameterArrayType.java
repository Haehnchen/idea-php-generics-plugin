package de.espend.idea.php.generics.dict;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ParameterArrayType {
    final private boolean isOptional;

    @NotNull
    final private PsiElement context;
    final private String key;

    @NotNull
    final private Collection<String> values;

    public ParameterArrayType(@NotNull String key, @NotNull Collection<String> values, boolean isOptional, @NotNull PsiElement context) {
        this.key = key;
        this.values = values;
        this.isOptional = isOptional;
        this.context = context;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String getKey() {
        return key;
    }

    @NotNull
    public Collection<String> getValues() {
        return values;
    }


    @NotNull
    public PsiElement getContext() {
        return context;
    }
}
