package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import de.espend.idea.php.quality.phpstan.PhpStanQualityToolType;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurationComboBox extends QualityToolConfigurationComboBox<PhpStanValidatorConfiguration> {
    public PhpStanValidatorConfigurationComboBox(@Nullable Project project) {
        super(project, PhpStanQualityToolType.getInstance());
    }
}
