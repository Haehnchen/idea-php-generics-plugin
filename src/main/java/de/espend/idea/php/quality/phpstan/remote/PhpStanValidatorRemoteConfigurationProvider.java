package de.espend.idea.php.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathMappingSettings;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.config.interpreters.PhpSdkAdditionalData;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterConfigurableForm;
import com.jetbrains.php.run.remote.PhpRemoteInterpreterManager;
import com.jetbrains.php.tools.quality.QualityToolConfigurableForm;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationManager;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfigurationProvider;
import de.espend.idea.php.quality.phpstan.form.PhpStanValidatorConfigurableForm;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorRemoteConfigurationProvider extends PhpStanValidatorConfigurationProvider {
    public static PhpStanValidatorRemoteConfigurationProvider INSTANCE = new PhpStanValidatorRemoteConfigurationProvider();

    public PhpStanValidatorRemoteConfigurationProvider() {
    }

    public String getConfigurationName(@Nullable String interpreterName) {
        return PhpStanValidatorRemoteConfiguration.getDefaultName(interpreterName);
    }

    public boolean canLoad(@NotNull String tagName) {
        return StringUtil.equals(tagName, "phpstan_by_interpreter");
    }

    @Nullable
    public PhpStanValidatorConfiguration load(@NotNull Element element) {
        return XmlSerializer.deserialize(element, PhpStanValidatorRemoteConfiguration.class);
    }

    @Nullable
    public QualityToolConfigurableForm<PhpStanValidatorRemoteConfiguration> createConfigurationForm(@NotNull Project project, @NotNull PhpStanValidatorConfiguration settings) {
        if (settings instanceof PhpStanValidatorRemoteConfiguration) {
            PhpStanValidatorRemoteConfiguration remoteConfiguration = (PhpStanValidatorRemoteConfiguration)settings;
            PhpStanValidatorConfigurableForm<PhpStanValidatorRemoteConfiguration> delegate = new PhpStanValidatorConfigurableForm<>(project, remoteConfiguration);
            return new QualityToolByInterpreterConfigurableForm<>(project, remoteConfiguration, delegate);
        } else {
            return null;
        }
    }

    public PhpStanValidatorConfiguration createNewInstance(@Nullable Project project, @NotNull List<PhpStanValidatorConfiguration> existingSettings) {
        PhpStanValidatorInterpreterDialog dialog = new PhpStanValidatorInterpreterDialog(project, existingSettings);
        if (dialog.showAndGet()) {
            String id = PhpInterpretersManagerImpl.getInstance(project).findInterpreterId(dialog.getSelectedInterpreterName());
            if (StringUtil.isNotEmpty(id)) {
                PhpStanValidatorRemoteConfiguration settings = new PhpStanValidatorRemoteConfiguration();
                settings.setInterpreterId(id);
                PhpSdkAdditionalData data = PhpInterpretersManagerImpl.getInstance(project).findInterpreterDataById(id);
                PhpRemoteInterpreterManager manager = PhpRemoteInterpreterManager.getInstance();
                if (manager != null && data != null) {
                    PathMappingSettings mappings = manager.createPathMappings(project, data);
                    if (project != null) {
                        this.fillSettingsByDefaultValue(settings, PhpStanValidatorConfigurationManager.getInstance(project).getLocalSettings(), (localPath) -> localPath == null ? null : mappings.convertToRemote(localPath));
                    }
                }

                return settings;
            }
        }

        return null;
    }

    public PhpStanValidatorConfiguration createConfigurationByInterpreter(@NotNull PhpInterpreter interpreter) {
        PhpStanValidatorRemoteConfiguration settings = new PhpStanValidatorRemoteConfiguration();
        settings.setInterpreterId(interpreter.getId());
        return settings;
    }
}
