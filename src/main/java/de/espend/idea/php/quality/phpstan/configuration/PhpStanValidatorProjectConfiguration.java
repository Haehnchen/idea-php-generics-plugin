package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import de.espend.idea.php.quality.phpstan.PhpStanFixerValidationInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@State(
    name = "PhpStanValidatorProjectConfiguration",
    storages = {@Storage("$WORKSPACE_FILE$")}
)
public class PhpStanValidatorProjectConfiguration extends QualityToolProjectConfiguration<PhpStanValidatorConfiguration> implements PersistentStateComponent<PhpStanValidatorProjectConfiguration> {
    public PhpStanValidatorProjectConfiguration() {
    }

    public static PhpStanValidatorProjectConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, PhpStanValidatorProjectConfiguration.class);
    }

    @Nullable
    public PhpStanValidatorProjectConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PhpStanValidatorProjectConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    protected String getInspectionId() {
        return (new PhpStanFixerValidationInspection()).getID();
    }

    @NotNull
    protected String getQualityToolName() {
        return "PhpStan Validator";
    }

    @NotNull
    protected PhpStanValidatorConfigurationManager getConfigurationManager(@NotNull Project project) {
        return PhpStanValidatorConfigurationManager.getInstance(project);
    }
}

