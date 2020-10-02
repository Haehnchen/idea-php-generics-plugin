package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.phpstan.PhpStanQualityToolType;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpStanValidatorConfigurableForm<C extends PhpStanValidatorConfiguration> extends QualityToolConfigurableForm<C> {
    public PhpStanValidatorConfigurableForm(@NotNull Project project, @NotNull C configuration) {
        super(project, configuration, "PhpStan", "phpstan");
    }

    @Override
    public QualityToolType getQualityToolType() {
        return PhpStanQualityToolType.getInstance();
    }

    @Nls
    public String getDisplayName() {
        return "PhpStan";
    }

    @Nullable
    public String getHelpTopic() {
        return "settings.phpstan.codeStyle";
    }

    @NotNull
    public String getId() {
        return PhpStanValidatorConfigurableForm.class.getName();
    }

    @NotNull
    public Pair<Boolean, String> validateMessage(String message) {
        return message.contains("PHPStan")
            ? Pair.create(true, "OK, " + message)
            : Pair.create(false, message);
    }

    public boolean isValidToolFile(VirtualFile file) {
        return file.getName().startsWith("phpstan");
    }
}
