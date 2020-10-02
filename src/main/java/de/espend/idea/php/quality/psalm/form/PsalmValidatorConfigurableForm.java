package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.psalm.PsalmQualityToolType;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsalmValidatorConfigurableForm<C extends PsalmValidatorConfiguration> extends QualityToolConfigurableForm<C> {
    public PsalmValidatorConfigurableForm(@NotNull Project project, @NotNull C configuration) {
        super(project, configuration, "Psalm", "psalm");
    }

    @Override
    public QualityToolType getQualityToolType() {
        return PsalmQualityToolType.getInstance();
    }

    @Nls
    public String getDisplayName() {
        return "Psalm";
    }

    @Nullable
    public String getHelpTopic() {
        return "settings.psalm.codeStyle";
    }

    @NotNull
    public String getId() {
        return PsalmValidatorConfigurableForm.class.getName();
    }

    @NotNull
    public Pair<Boolean, String> validateMessage(String message) {
        return message.toLowerCase().contains("psalm")
            ? Pair.create(true, "OK, " + StringUtils.abbreviate(message, 100))
            : Pair.create(false, message);
    }

    public boolean isValidToolFile(VirtualFile file) {
        return file.getName().startsWith("psalm");
    }
}
