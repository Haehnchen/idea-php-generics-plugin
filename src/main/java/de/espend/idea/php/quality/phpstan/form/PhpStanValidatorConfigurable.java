package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import com.jetbrains.php.tools.quality.messDetector.*;
import de.espend.idea.php.quality.phpstan.PhpStanFixerValidationInspection;
import de.espend.idea.php.quality.phpstan.blacklist.PhpStanValidatorIgnoredFilesConfigurable;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorProjectConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {
    public PhpStanValidatorConfigurable(@NotNull Project project) {
        super(project);
    }

    protected QualityToolProjectConfiguration getProjectConfiguration() {
        return PhpStanValidatorProjectConfiguration.getInstance(this.myProject);
    }

    @Nls
    public String getDisplayName() {
        return "PhpStan";
    }

    public String getHelpTopic() {
        return "settings.phpstan.validator";
    }

    @NotNull
    public String getId() {
        return PhpStanValidatorConfigurable.class.getName();
    }

    @NotNull
    protected String getInspectionShortName() {
        return new PhpStanFixerValidationInspection().getShortName();
    }

    @NotNull
    protected QualityToolConfigurationComboBox createConfigurationComboBox() {
        return new PhpStanValidatorConfigurationComboBox(this.myProject);
    }

    protected QualityToolsIgnoreFilesConfigurable getIgnoredFilesConfigurable() {
        return new PhpStanValidatorIgnoredFilesConfigurable(this.myProject);
    }
}

