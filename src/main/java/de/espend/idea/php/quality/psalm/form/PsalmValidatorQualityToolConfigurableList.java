package de.espend.idea.php.quality.psalm.form;

import com.intellij.openapi.project.Project;
import com.intellij.util.ObjectUtils;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import com.jetbrains.php.tools.quality.QualityToolConfigurableList;
import com.jetbrains.php.tools.quality.QualityToolConfiguration;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationManager;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorQualityToolConfigurableList extends QualityToolConfigurableList<PsalmValidatorConfiguration> {
    public PsalmValidatorQualityToolConfigurableList(@NotNull Project project, @Nullable String initialElement) {
        super(project, PsalmValidatorConfigurationManager.getInstance(project), PsalmValidatorConfiguration::new, PsalmValidatorConfiguration::clone, (settings) -> {
            PsalmValidatorConfigurationProvider provider = PsalmValidatorConfigurationProvider.getInstances();
            if (provider != null) {
                QualityToolConfigurableForm form = provider.createConfigurationForm(project, settings);
                if (form != null) {
                    return form;
                }
            }

            return new PsalmValidatorConfigurableForm<>(project, settings);
        }, initialElement);
        this.setSubjectDisplayName("psalm");
    }

    @Nullable
    protected PsalmValidatorConfiguration getConfiguration(@Nullable QualityToolConfiguration configuration) {
        return ObjectUtils.tryCast(configuration, PsalmValidatorConfiguration.class);
    }

    @Nullable
    @Override
    protected QualityToolConfigurationProvider<PsalmValidatorConfiguration> getConfigurationProvider() {
        return PsalmValidatorConfigurationProvider.getInstances();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Psalm";
    }
}
