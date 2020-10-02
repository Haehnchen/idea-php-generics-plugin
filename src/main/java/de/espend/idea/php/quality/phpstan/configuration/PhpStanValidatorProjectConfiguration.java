package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.php.tools.quality.QualityToolProjectConfiguration;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.phpstan.PhpStanQualityToolType;
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

    @Override
    protected QualityToolType<PhpStanValidatorConfiguration> getQualityToolType() {
        return PhpStanQualityToolType.getInstance();
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
}

