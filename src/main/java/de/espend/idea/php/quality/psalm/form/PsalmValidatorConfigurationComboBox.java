package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorConfigurationComboBox extends QualityToolConfigurationComboBox<PsalmValidatorConfiguration> {
    public PsalmValidatorConfigurationComboBox(@Nullable Project project) {
        super(project);
    }

    protected QualityToolConfigurableList<PsalmValidatorConfiguration> getQualityToolConfigurableList(@NotNull Project project, @Nullable String item) {
        return new PsalmValidatorQualityToolConfigurableList(project, item);
    }

    protected QualityToolConfigurationManager<PsalmValidatorConfiguration> getConfigurationManager(@NotNull Project project) {
        return PsalmValidatorConfigurationManager.getInstance(project);
    }
}
