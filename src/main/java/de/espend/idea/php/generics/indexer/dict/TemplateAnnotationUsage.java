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

    @Nullable
    private String context;

    public enum Type {
        FUNCTION_CLASS_STRING,
        METHOD_TEMPLATE,
        CONSTRUCTOR,
        EXTENDS
    }

    public TemplateAnnotationUsage(@NotNull String fqn, @NotNull Type type, @Nullable Integer parameterIndex) {
        this.fqn = fqn;
        this.type = type;
        this.parameterIndex = parameterIndex;
    }

    public TemplateAnnotationUsage(@NotNull String fqn, @NotNull Type type, @Nullable Integer parameterIndex, @NotNull String context) {
        this.fqn = fqn;
        this.type = type;
        this.parameterIndex = parameterIndex;
        this.context = context;
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

    @Nullable
    public String getContext() {
        return context;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.fqn)
            .append(this.type.toString())
            .append(this.parameterIndex)
            .append(this.context)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TemplateAnnotationUsage
            && Objects.equals(((TemplateAnnotationUsage) obj).getFqn(), this.fqn)
            && Objects.equals(((TemplateAnnotationUsage) obj).getType(), this.type)
            && Objects.equals(((TemplateAnnotationUsage) obj).getContext(), this.context)
            && Objects.equals(((TemplateAnnotationUsage) obj).getParameterIndex(), this.parameterIndex);
    }
}
