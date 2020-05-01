package de.espend.idea.php.quality.psalm.remote;

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
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationManager;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfigurationProvider;
import de.espend.idea.php.quality.psalm.form.PsalmValidatorConfigurableForm;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorRemoteConfigurationProvider extends PsalmValidatorConfigurationProvider {
    public String getConfigurationName(@Nullable String interpreterName) {
        return PsalmValidatorRemoteConfiguration.getDefaultName(interpreterName);
    }

    public boolean canLoad(@NotNull String tagName) {
        return StringUtil.equals(tagName, "psalm_by_interpreter");
    }

    @Nullable
    public PsalmValidatorConfiguration load(@NotNull Element element) {
        return XmlSerializer.deserialize(element, PsalmValidatorRemoteConfiguration.class);
    }

    @Nullable
    public QualityToolConfigurableForm<PsalmValidatorRemoteConfiguration> createConfigurationForm(@NotNull Project project, @NotNull PsalmValidatorConfiguration settings) {
        if (settings instanceof PsalmValidatorRemoteConfiguration) {
            PsalmValidatorRemoteConfiguration remoteConfiguration = (PsalmValidatorRemoteConfiguration)settings;
            PsalmValidatorConfigurableForm<PsalmValidatorRemoteConfiguration> delegate = new PsalmValidatorConfigurableForm<>(project, remoteConfiguration);
            return new QualityToolByInterpreterConfigurableForm<>(project, remoteConfiguration, delegate);
        } else {
            return null;
        }
    }

    public PsalmValidatorConfiguration createNewInstance(@Nullable Project project, @NotNull List<PsalmValidatorConfiguration> existingSettings) {
        PsalmValidatorInterpreterDialog dialog = new PsalmValidatorInterpreterDialog(project, existingSettings);
        if (dialog.showAndGet()) {
            String id = PhpInterpretersManagerImpl.getInstance(project).findInterpreterId(dialog.getSelectedInterpreterName());
            if (StringUtil.isNotEmpty(id)) {
                PsalmValidatorRemoteConfiguration settings = new PsalmValidatorRemoteConfiguration();
                settings.setInterpreterId(id);
                PhpSdkAdditionalData data = PhpInterpretersManagerImpl.getInstance(project).findInterpreterDataById(id);
                PhpRemoteInterpreterManager manager = PhpRemoteInterpreterManager.getInstance();
                if (manager != null && data != null) {
                    PathMappingSettings mappings = manager.createPathMappings(project, data);
                    if (project != null) {
                        this.fillSettingsByDefaultValue(settings, PsalmValidatorConfigurationManager.getInstance(project).getLocalSettings(), (localPath) -> localPath == null ? null : mappings.convertToRemote(localPath));
                    }
                }

                return settings;
            }
        }

        return null;
    }

    public PsalmValidatorConfiguration createConfigurationByInterpreter(@NotNull PhpInterpreter interpreter) {
        PsalmValidatorRemoteConfiguration settings = new PsalmValidatorRemoteConfiguration();
        settings.setInterpreterId(interpreter.getId());
        return settings;
    }
}
