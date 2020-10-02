package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import de.espend.idea.php.quality.psalm.PsalmQualityToolType;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorConfigurationComboBox extends QualityToolConfigurationComboBox<PsalmValidatorConfiguration> {
    public PsalmValidatorConfigurationComboBox(@Nullable Project project) {
        super(project, PsalmQualityToolType.getInstance());
    }
}
