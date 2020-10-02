package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.psalm.PsalmQualityToolType;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {
    public PsalmValidatorConfigurable(@NotNull Project project) {
        super(project);
    }

    @Nls
    public String getDisplayName() {
        return "Psalm";
    }

    public String getHelpTopic() {
        return "settings.psalm.validator";
    }

    @NotNull
    public String getId() {
        return PsalmValidatorConfigurable.class.getName();
    }

    @NotNull
    protected QualityToolConfigurationComboBox<PsalmValidatorConfiguration> createConfigurationComboBox() {
        return new PsalmValidatorConfigurationComboBox(this.myProject);
    }

    @Override
    protected QualityToolType<PsalmValidatorConfiguration> getQualityToolType() {
        return PsalmQualityToolType.getInstance();
    }
}

