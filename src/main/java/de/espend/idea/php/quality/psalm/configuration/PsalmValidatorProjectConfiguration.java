package de.espend.idea.php.quality.psalm.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import de.espend.idea.php.quality.psalm.PsalmValidationInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@State(
    name = "PsalmValidatorProjectConfiguration",
    storages = {@Storage("$WORKSPACE_FILE$")}
)
public class PsalmValidatorProjectConfiguration extends QualityToolProjectConfiguration<PsalmValidatorConfiguration> implements PersistentStateComponent<PsalmValidatorProjectConfiguration> {
    public PsalmValidatorProjectConfiguration() {
    }

    public static PsalmValidatorProjectConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, PsalmValidatorProjectConfiguration.class);
    }

    @Nullable
    public PsalmValidatorProjectConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PsalmValidatorProjectConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    protected String getInspectionId() {
        return (new PsalmValidationInspection()).getID();
    }

    @NotNull
    protected String getQualityToolName() {
        return "Psalm Validator";
    }

    @NotNull
    protected PsalmValidatorConfigurationManager getConfigurationManager(@NotNull Project project) {
        return PsalmValidatorConfigurationManager.getInstance(project);
    }
}

