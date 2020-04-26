package de.espend.idea.php.generics.indexer.dict;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TemplateAnnotationUsage implements Serializable {
    @NotNull
    private final String fqn;

    @NotNull
    private final Type type;

    @Nullable
    private final Integer parameterIndex;

    public enum Type {
        FUNCTION_CLASS_STRING,
        CONSTRUCTOR
    }

    public TemplateAnnotationUsage(@NotNull String fqn, @NotNull Type type, @Nullable Integer parameterIndex) {
        this.fqn = fqn;
        this.type = type;
        this.parameterIndex = parameterIndex;
    }

    @NotNull
    public String getFqn() {
        return fqn;
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @Nullable
    public Integer getParameterIndex() {
        return parameterIndex;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.fqn)
            .append(this.type.toString())
            .append(this.parameterIndex)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TemplateAnnotationUsage
            && Objects.equals(((TemplateAnnotationUsage) obj).getFqn(), this.fqn)
            && Objects.equals(((TemplateAnnotationUsage) obj).getType(), this.type)
            && Objects.equals(((TemplateAnnotationUsage) obj).getParameterIndex(), this.parameterIndex);
    }
}
