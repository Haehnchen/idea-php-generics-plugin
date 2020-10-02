package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolConfigurationComboBox;
import com.jetbrains.php.tools.quality.QualityToolProjectConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.phpstan.PhpStanQualityToolType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurable extends QualityToolProjectConfigurableForm implements Configurable.NoScroll {
    public PhpStanValidatorConfigurable(@NotNull Project project) {
        super(project);
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
    protected QualityToolConfigurationComboBox createConfigurationComboBox() {
        return new PhpStanValidatorConfigurationComboBox(this.myProject);
    }

    @Override
    protected QualityToolType getQualityToolType() {
        return PhpStanQualityToolType.getInstance();
    }
}

