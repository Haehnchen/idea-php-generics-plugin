package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurationComboBox extends QualityToolConfigurationComboBox<PhpStanValidatorConfiguration> {
    public PhpStanValidatorConfigurationComboBox(@Nullable Project project) {
        super(project);
    }

    protected QualityToolConfigurableList<PhpStanValidatorConfiguration> getQualityToolConfigurableList(@NotNull Project project, @Nullable String item) {
        return new PhpStanValidatorQualityToolConfigurableList(project, item);
    }

    protected QualityToolConfigurationManager<PhpStanValidatorConfiguration> getConfigurationManager(@NotNull Project project) {
        return PhpStanValidatorConfigurationManager.getInstance(project);
    }
}
