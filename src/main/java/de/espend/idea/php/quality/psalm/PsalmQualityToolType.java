package de.espend.idea.php.quality.psalm;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.phpstan.PhpStanFixerValidationInspection;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorProjectConfiguration;
import de.espend.idea.php.quality.psalm.blacklist.PsalmValidatorBlackList;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationManager;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationProvider;
import de.espend.idea.php.quality.psalm.form.PsalmValidatorConfigurable;
import de.espend.idea.php.quality.psalm.form.PsalmValidatorConfigurableForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmQualityToolType extends QualityToolType<PsalmValidatorConfiguration> {
    private static final PsalmQualityToolType INSTANCE = new PsalmQualityToolType();
    
    public static PsalmQualityToolType getInstance(){
        return INSTANCE;
    }
    
    @NotNull
    @Override
    public String getDisplayName() {
        return "Psalm";
    }

    @Override
    public @NotNull QualityToolBlackList getQualityToolBlackList(@NotNull Project project) {
        return PsalmValidatorBlackList.getInstance(project);
    }

    @Override
    protected @NotNull QualityToolConfigurationManager<PsalmValidatorConfiguration> getConfigurationManager(@NotNull Project project) {
        return PsalmValidatorConfigurationManager.getInstance(project);
    }

    @Override
    protected @NotNull QualityToolValidationInspection getInspection() {
        return new PhpStanFixerValidationInspection();
    }

    @Override
    protected @Nullable QualityToolConfigurationProvider<PsalmValidatorConfiguration> getConfigurationProvider() {
        return PsalmValidatorConfigurationProvider.getInstances();
    }

    @Override
    protected @NotNull QualityToolConfigurableForm<PsalmValidatorConfiguration> createConfigurableForm(@NotNull Project project, PsalmValidatorConfiguration psalmValidatorConfiguration) {
        return new PsalmValidatorConfigurableForm<>(project, psalmValidatorConfiguration);
    }

    @Override
    protected @NotNull Configurable getToolConfigurable(@NotNull Project project) {
        return new PsalmValidatorConfigurable(project);
    }

    @Override
    protected @NotNull QualityToolProjectConfiguration getProjectConfiguration(@NotNull Project project) {
        return new PhpStanValidatorProjectConfiguration();
    }

    @Override
    protected @NotNull PsalmValidatorConfiguration createConfiguration() {
        return new PsalmValidatorConfiguration();
    }
}
