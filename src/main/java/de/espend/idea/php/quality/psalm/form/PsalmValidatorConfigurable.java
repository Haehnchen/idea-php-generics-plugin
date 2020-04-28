package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import de.espend.idea.php.quality.psalm.PsalmValidationInspection;
import de.espend.idea.php.quality.psalm.blacklist.PsalmValidatorIgnoredFilesConfigurable;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorProjectConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {
    public PsalmValidatorConfigurable(@NotNull Project project) {
        super(project);
    }

    protected QualityToolProjectConfiguration getProjectConfiguration() {
        return PsalmValidatorProjectConfiguration.getInstance(this.myProject);
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
    protected String getInspectionShortName() {
        return new PsalmValidationInspection().getShortName();
    }

    @NotNull
    protected QualityToolConfigurationComboBox createConfigurationComboBox() {
        return new PsalmValidatorConfigurationComboBox(this.myProject);
    }

    protected QualityToolsIgnoreFilesConfigurable getIgnoredFilesConfigurable() {
        return new PsalmValidatorIgnoredFilesConfigurable(this.myProject);
    }
}

