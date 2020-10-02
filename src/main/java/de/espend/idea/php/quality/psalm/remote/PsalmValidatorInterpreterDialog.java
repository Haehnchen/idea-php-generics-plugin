package de.espend.idea.php.quality.psalm.remote;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterDialog;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorInterpreterDialog extends QualityToolByInterpreterDialog<PsalmValidatorConfiguration, PsalmValidatorRemoteConfiguration> {
    protected PsalmValidatorInterpreterDialog(@Nullable Project project, @NotNull List<PsalmValidatorConfiguration> settings) {
        super(project, settings, "Psalm", PsalmValidatorRemoteConfiguration.class);
    }

    @Nullable
    protected String getInterpreterId(@NotNull PsalmValidatorRemoteConfiguration configuration) {
        return configuration.getInterpreterId();
    }
}
