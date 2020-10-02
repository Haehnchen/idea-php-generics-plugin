package de.espend.idea.php.quality.phpstan;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.phpstan.blacklist.PhpStanValidatorBlackList;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationManager;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationProvider;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorProjectConfiguration;
import de.espend.idea.php.quality.phpstan.form.PhpStanValidatorConfigurable;
import de.espend.idea.php.quality.phpstan.form.PhpStanValidatorConfigurableForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanQualityToolType extends QualityToolType<PhpStanValidatorConfiguration> {
    private static final PhpStanQualityToolType INSTANCE = new PhpStanQualityToolType();

    public static PhpStanQualityToolType getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "PHPStan";
    }

    @Override
    public @NotNull QualityToolBlackList getQualityToolBlackList(@NotNull Project project) {
        return PhpStanValidatorBlackList.getInstance(project);
    }

    @Override
    protected @NotNull QualityToolConfigurationManager<PhpStanValidatorConfiguration> getConfigurationManager(@NotNull Project project) {
        return PhpStanValidatorConfigurationManager.getInstance(project);
    }

    @Override
    protected @NotNull QualityToolValidationInspection getInspection() {
        return new PhpStanFixerValidationInspection();
    }

    @Override
    protected @Nullable QualityToolConfigurationProvider<PhpStanValidatorConfiguration> getConfigurationProvider() {
        return PhpStanValidatorConfigurationProvider.getInstances();
    }

    @Override
    protected @NotNull QualityToolConfigurableForm<PhpStanValidatorConfiguration> createConfigurableForm(@NotNull Project project, PhpStanValidatorConfiguration qualityToolConfiguration) {
        return new PhpStanValidatorConfigurableForm<>(project, qualityToolConfiguration);
    }

    @Override
    protected @NotNull Configurable getToolConfigurable(@NotNull Project project) {
        return new PhpStanValidatorConfigurable(project);
    }

    @Override
    protected @NotNull QualityToolProjectConfiguration<PhpStanValidatorConfiguration> getProjectConfiguration(@NotNull Project project) {
        return new PhpStanValidatorProjectConfiguration();
    }

    @NotNull
    @Override
    protected PhpStanValidatorConfiguration createConfiguration() {
        return new PhpStanValidatorConfiguration();
    }
}
