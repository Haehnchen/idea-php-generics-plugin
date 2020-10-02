package de.espend.idea.php.quality.phpstan.remote;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.remote.tools.quality.QualityToolByInterpreterDialog;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorInterpreterDialog extends QualityToolByInterpreterDialog<PhpStanValidatorConfiguration, PhpStanValidatorRemoteConfiguration> {
    protected PhpStanValidatorInterpreterDialog(@Nullable Project project, @NotNull List<PhpStanValidatorConfiguration> settings) {
        super(project, settings, "PHPStan", PhpStanValidatorRemoteConfiguration.class);
    }

    @Nullable
    protected String getInterpreterId(@NotNull PhpStanValidatorRemoteConfiguration configuration) {
        return configuration.getInterpreterId();
    }
}
