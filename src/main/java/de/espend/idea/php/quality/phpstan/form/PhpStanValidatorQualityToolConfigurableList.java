package de.espend.idea.php.quality.phpstan.form;

import com.intellij.openapi.project.Project;
import com.intellij.util.ObjectUtils;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationManager;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorQualityToolConfigurableList extends QualityToolConfigurableList<PhpStanValidatorConfiguration> {
    public PhpStanValidatorQualityToolConfigurableList(@NotNull Project project, @Nullable String initialElement) {
        super(project, PhpStanValidatorConfigurationManager.getInstance(project), PhpStanValidatorConfiguration::new, PhpStanValidatorConfiguration::clone, (settings) -> {
            PhpStanValidatorConfigurationProvider provider = PhpStanValidatorConfigurationProvider.getInstances();
            if (provider != null) {
                QualityToolConfigurableForm form = provider.createConfigurationForm(project, settings);
                if (form != null) {
                    return form;
                }
            }

            return new PhpStanValidatorConfigurableForm<>(project, settings);
        }, initialElement);
        this.setSubjectDisplayName("phpstan");
    }

    @Nullable
    protected PhpStanValidatorConfiguration getConfiguration(@Nullable QualityToolConfiguration configuration) {
        return ObjectUtils.tryCast(configuration, PhpStanValidatorConfiguration.class);
    }

    @Nullable
    @Override
    protected QualityToolConfigurationProvider<PhpStanValidatorConfiguration> getConfigurationProvider() {
        return PhpStanValidatorConfigurationProvider.getInstances();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PhpStan";
    }
}
